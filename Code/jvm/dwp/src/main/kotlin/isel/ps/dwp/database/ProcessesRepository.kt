package isel.ps.dwp.database

import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.Stage
import org.jdbi.v3.core.Handle

class ProcessesRepository(handle: Handle) : ProcessesInterface {
    override fun createTemplate() {
        TODO("Not yet implemented")
    }

    override fun deleteTemplate() {
        TODO("Not yet implemented")
    }

    override fun pendingProcesses(): List<Process> {
        TODO("Not yet implemented")
    }

    override fun finishedProcesses(): List<Process> {
        TODO("Not yet implemented")
    }

    override fun getProcesses(type: String): List<Process> {
        TODO("Not yet implemented")
    }

    override fun processUsers(processId: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun userProcesses(userId: String): List<Process> {
        TODO("Not yet implemented")
    }

    override fun processStages(processId: String): List<Stage> {
        TODO("Not yet implemented")
    }

    override fun assUserToTemplate(userId: String, templateId: String) {
        TODO("Not yet implemented")
    }

    override fun removeUserFromTemplate(userId: String, templateId: String) {
        TODO("Not yet implemented")
    }

    override fun processDetails(processId: String): Process {
        TODO("Not yet implemented")
    }

    override fun newProcess(): String {
        TODO("Not yet implemented")
    }

    override fun deleteProcess(processId: String) {
        TODO("Not yet implemented")
    }

    override fun cancelProcess(processId: String) {
        TODO("Not yet implemented")
    }

}