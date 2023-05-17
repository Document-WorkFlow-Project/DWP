package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.*
import isel.ps.dwp.templatesFolderPath
import org.springframework.stereotype.Component
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

    override fun checkProcess(id: String): Process? {
        return transactionManager.run {
            it.processesRepository.checkProcess(id)
        } ?: throw ExceptionControllerAdvice.ProcessNotFound("Process not found. Incorrect id.")
    }

}