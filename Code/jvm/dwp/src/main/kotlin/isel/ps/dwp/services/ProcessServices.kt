package isel.ps.dwp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Document
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.ProcessTemplate
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

    override fun getProcesses(type: String?): List<String> {
        return transactionManager.run {
            it.processesRepository.getProcesses(type)
        }
    }

    override fun pendingProcesses(userEmail: String?): List<String> {
        return transactionManager.run {
            it.processesRepository.pendingProcesses(userEmail)
        }
    }

    override fun finishedProcesses(userEmail: String?): List<String> {
        return transactionManager.run {
            it.processesRepository.finishedProcesses(userEmail)
        }
    }

    override fun processStages(processId: String): List<String> {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run {
            it.processesRepository.processStages(processId)
        }
    }

    override fun processDetails(processId: String): Process {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run {
            it.processesRepository.processDetails(processId)
        }
    }

    override fun processDocs(processId: String): List<Document> {
        return transactionManager.run {
            it.processesRepository.processDocs(processId)
        }
    }

    override fun newProcess(templateName: String, name: String, description: String, files: List<MultipartFile>): String {
        return transactionManager.run {
            // Create process
            val processId = it.processesRepository.newProcess(templateName, name, description, files)

            // Save uploaded files associated to this process
            files.forEach{ file ->
                val docId = UUID.randomUUID().toString()

                // Save file in filesystem
                saveInFilesystem(file, "$uploadsFolderPath/$docId-${file.originalFilename}")

                it.documentsRepository.saveDocReference(file, docId)
                it.processesRepository.associateDocToProcess(docId, processId)
            }

            // Get template stages to be initialized
            val templateDetails = it.templatesRepository.templateDetails(templateName)
            val template = objectMapper.readValue<ProcessTemplate>(File(templateDetails.path))

            // Create process stages
            template.stages.forEachIndexed { index, stage ->

                // Translate groups into email, using a hash set to avoid duplicates
                val responsibleSet = HashSet<String>()
                stage.responsible.forEach { resp ->
                    if (resp.contains('@')) {
                        responsibleSet.add(resp)
                    } else {
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

    override fun deleteProcess(processId: String) {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        transactionManager.run {
            it.processesRepository.deleteProcess(processId)
        }
    }

    override fun cancelProcess(processId: String) {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        transactionManager.run {
            it.processesRepository.cancelProcess(processId)
        }
    }

}