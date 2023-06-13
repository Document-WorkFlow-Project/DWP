package isel.ps.dwp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.*
import isel.ps.dwp.uploadsFolderPath
import isel.ps.dwp.utils.saveInFilesystem
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Service
class ProcessServices(
        private val transactionManager: TransactionManager,
        private val stageServices: StageServices
): ProcessesInterface {

    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun getProcesses(userAuth: UserAuth,type: String?): List<String> {
        return transactionManager.run {
            it.processesRepository.getProcesses(userAuth,type)
        }
    }

    override fun pendingProcesses(userAuth: UserAuth, userEmail: String?): List<ProcessModel> {
        return transactionManager.run {
            it.processesRepository.pendingProcesses(userAuth, userEmail)
        }
    }

    override fun finishedProcesses(userAuth: UserAuth, userEmail: String?): List<ProcessModel> {
        return transactionManager.run {
            it.processesRepository.finishedProcesses(userAuth, userEmail)
        }
    }

    override fun processStages(userAuth: UserAuth,processId: String): List<StageModel> {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run {
            it.processesRepository.processStages(userAuth,processId)
        }
    }

    override fun processDetails(userAuth: UserAuth,processId: String): Process {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run {
            it.processesRepository.processDetails(userAuth,processId)
        }
    }

    override fun processDocs(userAuth: UserAuth,processId: String): List<Document> {
        return transactionManager.run {
            it.processesRepository.processDocs(userAuth,processId)
        }
    }

    override fun processDocsDetails(userAuth: UserAuth,processId: String): ProcessDocInfo {
        return transactionManager.run {
            it.processesRepository.processDocsDetails(userAuth,processId)
        }
    }

    override fun newProcess(templateName: String, name: String, description: String, files: List<MultipartFile>, userAuth: UserAuth): String {
        return transactionManager.run {
            // Create process
            val processId = it.processesRepository.newProcess(templateName, name, description, files, userAuth)

            // Save uploaded files associated to this process
            files.forEach{ file ->
                val docId = UUID.randomUUID().toString()

                // Save file in filesystem
                saveInFilesystem(file, "$uploadsFolderPath/$docId-${file.originalFilename}")

                it.documentsRepository.saveDocReference(file, docId)
                it.processesRepository.associateDocToProcess(userAuth,docId, processId)
            }

            // Get template stages to be initialized
            val templateDetails = it.templatesRepository.templateDetails(templateName)
            val template = objectMapper.readValue<ProcessTemplate>(File(templateDetails.path))

            // Create process stages
            template.stages.forEachIndexed { index, stage ->

                // Translate groups into email, using a hash set to avoid duplicates
                val responsibleSet = HashSet<String>()
                stage.responsible.forEach { resp ->
                    // A responsible can be a single email
                    if (resp.contains('@'))
                        responsibleSet.add(resp)
                    // Or a role, a group of emails
                    else {
                        val emails = it.rolesRepository.getRoleUsers(resp)
                        responsibleSet.addAll(emails)
                    }
                }

                val stageId = it.stagesRepository.createStage(processId, index, stage.name, stage.description, stage.mode, responsibleSet.toList(), stage.duration)
                // Start the first stage
                if (index == 0)
                    stageServices.startNextPendingStage(stageId)
            }

            processId
        }
    }

    override fun deleteProcess(userAuth: UserAuth,processId: String) {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        transactionManager.run {
            it.processesRepository.deleteProcess(userAuth,processId)
        }
    }

    override fun cancelProcess(userAuth: UserAuth,processId: String) {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        transactionManager.run {
            it.processesRepository.cancelProcess(userAuth,processId)
        }
    }

}