package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import isel.ps.dwp.uploadsFolderPath
import isel.ps.dwp.utils.saveInFilesystem
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ProcessServices(private val transactionManager: TransactionManager): ProcessesInterface {

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
        // Guardar documentos anexados ao processo
        files.forEach{ saveInFilesystem(it, "$uploadsFolderPath/${it.originalFilename}") }

        // Criar processo e respetivas etapas na bd
        return transactionManager.run {
            it.processesRepository.newProcess(templateName, name, description, files)
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