package isel.ps.dwp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.*
import isel.ps.dwp.uploadsFolderPath
import isel.ps.dwp.utils.deleteFromFilesystem
import isel.ps.dwp.utils.saveInFilesystem
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.IsolationLevel
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ProcessServices(
    private val transactionManager: TransactionManager,
    private val stageServices: StageServices,
    private val objectMapper: ObjectMapper
) : ProcessesInterface {

    override fun getProcesses(userAuth: UserAuth, type: String?): List<String> {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.getProcesses(userAuth, type)
        }
    }

    override fun processesOfState(
        state: State,
        userAuth: UserAuth,
        limit: Int?,
        skip: Int?,
        userEmail: String?
    ): ProcessPage {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processesOfState(state, userAuth, limit, skip, userEmail)
        }
    }

    override fun processStages(userAuth: UserAuth, processId: String): List<StageModel> {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processStages(userAuth, processId)
        }
    }

    override fun processDetails(userAuth: UserAuth, processId: String): Process {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processDetails(userAuth, processId)
        }
    }

    override fun processDocs(userAuth: UserAuth, processId: String): List<Document> {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processDocs(userAuth, processId)
        }
    }

    override fun processDocsDetails(userAuth: UserAuth, processId: String): ProcessDocInfo {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processDocsDetails(userAuth, processId)
        }
    }

    override fun newProcess(
        templateName: String,
        name: String,
        description: String,
        files: List<MultipartFile>,
        userAuth: UserAuth
    ): String {
        return transactionManager.run(TransactionIsolationLevel.REPEATABLE_READ) {
            // Create process
            val processId = it.processesRepository.newProcess(templateName, name, description, files, userAuth)

            // Get template stages to be initialized
            val templateDetails = it.templatesRepository.getTemplate(templateName)
            val stages = objectMapper.readValue<List<StageTemplate>>(templateDetails.etapas)

            // Create process stages
            stages.forEachIndexed { index, stage ->

                // Translate stage responsible into emails, using a hash set to avoid duplicates
                val responsibleSet = HashSet<String>()

                stage.responsible.forEach { resp ->
                    // A responsible can be a single email
                    if (resp.contains('@'))
                        responsibleSet.add(resp)
                    // Or a role, a group of emails
                    else {
                        val emails = it.rolesRepository.getRoleUsers(resp)
                        if (emails.isEmpty())
                            throw ExceptionControllerAdvice.UserNotFound("Não existem utilizadores para o papel $resp.")

                        responsibleSet.addAll(emails)
                    }
                }

                val stageId = it.stagesRepository.createStage(
                    processId,
                    index,
                    stage.name,
                    stage.description,
                    stage.mode,
                    responsibleSet.toList(),
                    stage.duration,
                    userAuth
                )
                // Start the first stage
                if (index == 0)
                    stageServices.startNextPendingStage(stageId)
            }

            val addedFilesPaths = mutableListOf<String>()

            try {
                // Save uploaded files associated to this process
                files.forEach { file ->
                    val docId = UUID.randomUUID().toString()
                    val path = "$uploadsFolderPath/$docId-${file.originalFilename}"

                    // Save file in filesystem
                    saveInFilesystem(file, path)

                    // Save path in case of exception
                    addedFilesPaths.add(path)

                    it.documentsRepository.saveDocReference(file, docId)
                    it.processesRepository.associateDocToProcess(userAuth,docId, processId)
                }
            } catch (exception: Exception) {
                addedFilesPaths.forEach { path ->
                    deleteFromFilesystem(path)
                }
                throw ExceptionControllerAdvice.DataTransferError("Erro na escrita de ficheiros.")
            }

            processId
        }
    }

    override fun deleteProcess(userAuth: UserAuth, processId: String) {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        transactionManager.run(TransactionIsolationLevel.REPEATABLE_READ) {
            it.processesRepository.deleteProcess(userAuth, processId)
        }
    }

    override fun cancelProcess(userAuth: UserAuth, processId: String) {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        transactionManager.run(TransactionIsolationLevel.REPEATABLE_READ) {
            it.processesRepository.cancelProcess(userAuth, processId)
        }
    }

}