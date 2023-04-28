package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.model.Stage
import isel.ps.dwp.templatesFolderPath
import isel.ps.dwp.uploadsFolderPath
import org.jdbi.v3.core.Handle
import org.springframework.web.multipart.MultipartFile
import java.util.*

class ProcessesRepository(private val handle: Handle) : ProcessesInterface {

    override fun addTemplate(templateFile: MultipartFile) {
        val fileName = templateFile.originalFilename
        //TODO add description
        val description = "sample description"
        val filePath = "$templatesFolderPath/$fileName"

        handle.createUpdate(
                "insert into template_processo(nome, descricao, path) values (:name,:description,:path)"
        )
                .bind("name", fileName)
                .bind("description", description)
                .bind("path", filePath)
                .execute()
    }

    override fun deleteTemplate(templateName: String) {
        handle.createUpdate(
            "delete from template_processo where nome = :name"
        )
            .bind("name", templateName)
            .execute()
    }

    override fun pendingProcesses(userEmail: String?): List<String> {
        //TODO email must be provided unless user is admin
        return if (userEmail != null) handle.createQuery(
            "select id from processo where responsavel = :email and estado = 'PENDING'"
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
            "select id from processo where responsavel = :email and (estado = 'SUCCESS' or estado = 'FAILURE')"
        )
            .bind("email", userEmail)
            .mapTo(String::class.java)
            .list() ?: throw ExceptionControllerAdvice.UserNotFoundException("User not found")
        else
            handle.createQuery("select id from processo where estado = 'SUCCESS' or estado = 'FAILURE'")
                .mapTo(String::class.java)
                .list()
    }

    override fun getProcesses(type: String): List<String> {
        return handle.createQuery("select id from processo where tipo = :type")
            .bind("type", type)
            .mapTo(String::class.java)
            .list()
    }

    //TODO acho que não deve ser relação N:N porque apenas um utilizador cria varios processos. Existem varios utilizadores em cada etapa
    override fun processUsers(processId: String): List<String> {
        return handle.createQuery("select email_utilizador from utilizador_processo where id_processo = :id")
            .bind("id", processId)
            .mapTo(String::class.java)
            .list() //TODO exception on empty
    }

    override fun userProcesses(userEmail: String): List<Process> {
        TODO("Not yet implemented")
    }

    override fun processStages(processId: String): List<Stage> {
        TODO("Not yet implemented")
    }

    override fun addUserToTemplate(userId: String, templateName: String) {
        TODO("Not yet implemented")
    }

    override fun removeUserFromTemplate(userId: String, templateName: String) {
        TODO("Not yet implemented")
    }

    override fun processDetails(processId: String): Process {
        TODO("Not yet implemented")
    }

    override fun newProcessFromTemplate(templateName: String): String {
        TODO("Not yet implemented")
    }

    override fun deleteProcess(processId: String) {
        TODO("Not yet implemented")
    }

    override fun cancelProcess(processId: String) {
        TODO("Not yet implemented")
    }

}