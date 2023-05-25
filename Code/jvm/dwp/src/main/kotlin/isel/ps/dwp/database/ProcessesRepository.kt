package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import org.jdbi.v3.core.Handle
import org.springframework.web.multipart.MultipartFile
import java.util.*


class ProcessesRepository(private val handle: Handle) : ProcessesInterface {

    override fun checkProcess(id: String): Process? {
        return handle.createQuery("SELECT * FROM processo WHERE id = :id")
            .bind("id", id)
            .mapTo(Process::class.java)
            .singleOrNull()
    }

    override fun getProcesses(type: String?): List<String> {
        //TODO email must be provided, to know the processes the user created, unless user is admin
        return if (type != null)
            handle.createQuery("select id from processo where template_processo = :type")
            .bind("type", type)
            .mapTo(String::class.java)
            .list()
        else
            handle.createQuery("select id from processo where autor = :email")
                .bind("email", "") //TODO email must be provided
                .mapTo(String::class.java)
                .list()
    }

    override fun pendingProcesses(userEmail: String?): List<String> {
        //TODO email must be provided unless user is admin
        return if (userEmail != null) handle.createQuery(
            "select id from processo where autor = :email and estado = 'PENDING'"
        )
            .bind("email", userEmail)
            .mapTo(String::class.java)
            .list() ?: throw ExceptionControllerAdvice.UserNotFoundException("User not found")
        else
            handle.createQuery("select id from processo where estado = 'PENDING'")
            .mapTo(String::class.java)
            .list()
    }

    override fun finishedProcesses(userEmail: String?): List<String> {
        //TODO email must be provided unless user is admin
        return if (userEmail != null) handle.createQuery(
            "select id from processo where autor = :email and (estado = 'APPROVED' or estado = 'DISAPPROVED')"
        )
            .bind("email", userEmail)
            .mapTo(String::class.java)
            .list() ?: throw ExceptionControllerAdvice.UserNotFoundException("User not found")
        else
            handle.createQuery("select id from processo where estado = 'APPROVED' or estado = 'DISAPPROVED'")
                .mapTo(String::class.java)
                .list()
    }

    override fun processStages(processId: String): List<String> {
        val stages = handle.createQuery(
                "select id from etapa where id_processo = :processId order by indice"
        )
            .bind("processId", processId)
            .mapTo(String::class.java)
            .list()
        return stages.ifEmpty { throw ExceptionControllerAdvice.ProcessNotFound("Processo $processId não encontrado.") }
    }

    override fun processDetails(processId: String): Process {
        return handle.createQuery("select * from processo where id = :processId")
            .bind("processId", processId)
            .mapTo(Process::class.java)
            .firstOrNull() ?: throw ExceptionControllerAdvice.ProcessNotFound("Processo $processId não encontrado.")
    }

    override fun newProcess(templateName: String, name: String, description: String, files: List<MultipartFile>): String {
        val uuid = UUID.randomUUID().toString()
        //TODO get email from requesting user
        val userEmail = "example@gmail.com"

        handle.createUpdate(
                "insert into processo(id, nome, autor, descricao, data_inicio, estado, template_processo) values (:uuid,:name,:author,:description,:startDate, 'PENDING', :template)"
        )
                .bind("uuid", uuid)
                .bind("name", name)
                .bind("author", userEmail)
                .bind("description", description)
                .bind("startDate", Date())
                .bind("template", templateName)
                .execute()

        return uuid
    }

    fun associateDocToProcess(docId: String, processId: String) {
        handle.createUpdate("insert into documento_processo values (:docId, :processId)")
            .bind("docId", docId)
            .bind("processId", processId)
            .execute()
    }

    override fun deleteProcess(processId: String) {
        handle.createUpdate(
            "delete from processo where id = :processId"
        )
            .bind("processId", processId)
            .execute()
    }

    override fun cancelProcess(processId: String) {
        handle.createUpdate(
            "update processo set estado = 'CANCELLED' where id = :processId"
        )
            .bind("processId", processId)
            .execute()
    }
}