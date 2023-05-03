package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.Stage
import isel.ps.dwp.model.saveInFilesystem
import isel.ps.dwp.templatesFolderPath
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

@Component
class ProcessServices(private val transactionManager: TransactionManager): ProcessesInterface {

    override fun addTemplate(templateFile: MultipartFile) {
        //TODO instead of saving file by name, save using an id to avoid replacements on collision

        if (templateFile.contentType != "application/json")
            throw ExceptionControllerAdvice.DataTransferError("Invalid template file format.")

        // Save template file in filesystem
        saveInFilesystem(templateFile, "$templatesFolderPath/${templateFile.originalFilename}")

        // Save template file description in database
        return transactionManager.run {
            it.processesRepository.addTemplate(templateFile)
        }
    }

    override fun deleteTemplate(templateName: String) {
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.processesRepository.deleteTemplate(templateName)
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

    override fun getProcesses(type: String): List<String> {
        if (type.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template type.")

        return transactionManager.run {
            it.processesRepository.getProcesses(type)
        }
    }

    override fun processUsers(processId: String): List<String> {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run {
            it.processesRepository.processUsers(processId)
        }
    }

    override fun userProcesses(userId: String): List<Process> {
        if (userId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user id.")

        return transactionManager.run {
            it.processesRepository.userProcesses(userId)
        }
    }

    override fun processStages(processId: String): List<Stage> {
        if (processId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing process id.")

        return transactionManager.run {
            it.processesRepository.processStages(processId)
        }
    }

    override fun addUserToTemplate(userId: String, templateName: String) {
        if (userId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user id.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.processesRepository.addUserToTemplate(userId, templateName)
        }
    }

    override fun removeUserFromTemplate(userId: String, templateName: String) {
        if (userId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user id.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.processesRepository.removeUserFromTemplate(userId, templateName)
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