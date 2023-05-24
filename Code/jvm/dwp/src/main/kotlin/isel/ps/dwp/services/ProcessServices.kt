package isel.ps.dwp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.uploadsFolderPath
import isel.ps.dwp.utils.saveInFilesystem
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class ProcessServices(private val transactionManager: TransactionManager): ProcessesInterface {

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

    override fun newProcess(templateName: String, name: String, description: String, files: List<MultipartFile>): String {
        // Save uploaded files associated to this process
        files.forEach{ saveInFilesystem(it, "$uploadsFolderPath/${it.originalFilename}") }
        // TODO fill documento_processo

        return transactionManager.run {
            // Create process
            val processId = it.processesRepository.newProcess(templateName, name, description, files)

            // Get template stages to be initialized
            val templateDetails = it.templatesRepository.templateDetails(templateName)
            val template = objectMapper.readValue<ProcessTemplate>(File(templateDetails.path))

            // Create process stages
            template.stages.forEachIndexed { index, stage ->
                it.stagesRepository.createStage(processId, index, stage.name, stage.description, stage.mode, stage.responsible, stage.duration)
            }

            //TODO start first stage of new process

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

    override fun checkProcess(id: String): Process? {
        return transactionManager.run {
            it.processesRepository.checkProcess(id)
        } ?: throw ExceptionControllerAdvice.ProcessNotFound("Process not found. Incorrect id.")
    }

}