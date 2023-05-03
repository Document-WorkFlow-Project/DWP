package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.Stage
import isel.ps.dwp.model.deleteFromFilesystem
import isel.ps.dwp.model.saveInFilesystem
import isel.ps.dwp.templatesFolderPath
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class ProcessServices(private val transactionManager: TransactionManager): ProcessesInterface {

    override fun addTemplate(templateFile: MultipartFile): String {
        if (templateFile.contentType != "application/json")
            throw ExceptionControllerAdvice.DataTransferError("Invalid template file format.")

        // Save template file in filesystem
        saveInFilesystem(templateFile, "$templatesFolderPath/${templateFile.originalFilename}")

        // Save template file description in database
        return transactionManager.run {
            it.processesRepository.addTemplate(templateFile)
        }
    }

    override fun addUsersToTemplate(templateName: String, email: String) {
        if (email.isEmpty())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user emails.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.processesRepository.addUsersToTemplate(templateName, email)
        }
    }

    override fun removeUserFromTemplate(templateName: String, email: String) {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user email.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.processesRepository.removeUserFromTemplate(templateName, email)
        }
    }

    override fun deleteTemplate(templateName: String) {
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        val templatePath = transactionManager.run {
            it.processesRepository.findTemplatePathByName(templateName)
        }

        // Delete template file from filesystem
        deleteFromFilesystem(templatePath)

        transactionManager.run {
            it.processesRepository.deleteTemplate(templateName)
        }
    }

    override fun getProcesses(type: String): List<String> {
        if (type.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template type.")

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

    override fun processStages(processId: String): List<Stage> {
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


    override fun newProcessFromTemplate(templateName: String): String {
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        //TODO fill new process params

        return transactionManager.run {
            it.processesRepository.newProcessFromTemplate(templateName)
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