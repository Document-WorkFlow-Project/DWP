package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.User
import org.jdbi.v3.core.Handle


class ProcessesRepository(private val handle: Handle) : ProcessesInterface {

    override fun checkProcess(id: String): Process? {
        return handle.createQuery("SELECT * FROM processo WHERE id = :id")
            .bind("id", id)
            .mapTo(Process::class.java)
            .singleOrNull()
    }

    override fun getProcesses(type: String): List<String> {
        return handle.createQuery("select id from processo where template_processo = :type")
            .bind("type", type)
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

    // TODO obter por ordem
    override fun processStages(processId: String): List<String> {
        val stages = handle.createQuery("select id_etapa from etapa_processo where id_processo = :processId")
            .bind("processId", processId)
            .mapTo(String::class.java)
            .list()
        return stages.ifEmpty { throw ExceptionControllerAdvice.ProcessNotFound("Process $processId not found.") }
    }

    override fun processDetails(processId: String): Process {
        return handle.createQuery("select * from processo where id = :processId")
            .bind("processId", processId)
            .mapTo(Process::class.java)
            .firstOrNull() ?: throw ExceptionControllerAdvice.ProcessNotFound("Process $processId not found.")
    }

    override fun newProcessFromTemplate(templateName: String): String {
        TODO("Not yet implemented")
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

    fun createProcess(nome: String, autor: String, descricao: String, data_inicio: String, data_fim: String?, prazo: String, estado: String, template_processo:String): Int? {
        val result = handle.createUpdate("INSERT INTO Processo (nome, autor, descricao, data_inicio, data_fim, prazo, estado, template_processo) VALUES (:nome, :autor, :responsavel, :descricao, :data_inicio, :data_fim, :prazo, :estado, :template_processo)")
            .bind("nome", nome)
            .bind("autor", autor)
            .bind("descricao", descricao)
            .bind("data_inicio", data_inicio)
            .bind("data_fim", data_fim)
            .bind("prazo", prazo)
            .bind("estado", estado)
            .bind("template_processo", template_processo)
            .executeAndReturnGeneratedKeys()
        return result.mapTo(Int::class.java).one()
    }
}