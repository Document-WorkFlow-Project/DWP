package isel.ps.dwp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.*
import isel.ps.dwp.uploadsFolderPath
import isel.ps.dwp.utils.saveInFilesystem
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.IsolationLevel
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Service
class ProcessServices(
        private val transactionManager: TransactionManager,
        private val stageServices: StageServices,
        private val objectMapper: ObjectMapper
): ProcessesInterface {

    override fun getProcesses(type: String?): List<String> {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.getProcesses(type)
        }
    }

    override fun pendingProcesses(userAuth: UserAuth, userEmail: String?): List<ProcessModel> {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.pendingProcesses(userAuth, userEmail)
        }
    }

    override fun finishedProcesses(userAuth: UserAuth, userEmail: String?): List<ProcessModel> {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.finishedProcesses(userAuth, userEmail)
        }
    }

    override fun processStages(processId: String): List<StageModel> {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processStages(processId)
        }
    }

    override fun processDetails(processId: String): Process {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processDetails(processId)
        }
    }

    override fun processDocs(processId: String): List<Document> {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processDocs(processId)
        }
    }

    override fun processDocsDetails(processId: String): ProcessDocInfo {
        return transactionManager.run(TransactionIsolationLevel.READ_COMMITTED) {
            it.processesRepository.processDocsDetails(processId)
        }
    }

    override fun newProcess(templateName: String, name: String, description: String, files: List<MultipartFile>, userAuth: UserAuth): String {
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

                val stageId = it.stagesRepository.createStage(processId, index, stage.name, stage.description, stage.mode, responsibleSet.toList(), stage.duration)
                // Start the first stage
                if (index == 0)
                    stageServices.startNextPendingStage(stageId)
            }

            // Save uploaded files associated to this process
            files.forEach{ file ->
                val docId = UUID.randomUUID().toString()

                // Save file in filesystem
                saveInFilesystem(file, "$uploadsFolderPath/$docId-${file.originalFilename}")

                it.documentsRepository.saveDocReference(file, docId)
                it.processesRepository.associateDocToProcess(docId, processId)
            }

            processId
        }
    }

    override fun deleteProcess(processId: String) {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        transactionManager.run(TransactionIsolationLevel.REPEATABLE_READ) {
            it.processesRepository.deleteProcess(processId)
        }
    }

    override fun cancelProcess(processId: String) {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        transactionManager.run(TransactionIsolationLevel.REPEATABLE_READ) {
            it.processesRepository.cancelProcess(processId)
        }
    }

}