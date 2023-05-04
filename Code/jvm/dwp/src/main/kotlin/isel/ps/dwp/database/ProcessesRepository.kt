package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.Stage
import isel.ps.dwp.templatesFolderPath
import org.jdbi.v3.core.Handle
import org.springframework.web.multipart.MultipartFile

class ProcessesRepository(private val handle: Handle) : ProcessesInterface {

    override fun addTemplate(templateFile: MultipartFile): String {
        val fileNameWithType = templateFile.originalFilename
        val fileName = fileNameWithType!!.substringBeforeLast(".json")
        //TODO add description
        val description = "sample description"
        val filePath = "$templatesFolderPath/$fileNameWithType"

        if (handle.createQuery("select * from template_processo where nome = :nome")
            .bind("nome", fileName)
            .mapTo(String::class.java)
            .firstOrNull() != null)
            throw ExceptionControllerAdvice.InvalidParameterException("Template $fileName already exists.")

        handle.createUpdate(
                "insert into template_processo(nome, descricao, path) values (:name,:description,:path)"
        )
                .bind("name", fileName)
                .bind("description", description)
                .bind("path", filePath)
                .execute()
        return fileName
    }

    override fun deleteTemplate(templateName: String) {
        handle.createUpdate(
            "delete from template_processo where nome = :name"
        )
            .bind("name", templateName)
            .execute()
    }

    fun findTemplatePathByName(templateName: String): String {
        return handle.createQuery(
            "select path from template_processo where nome = :name"
        )
            .bind("name", templateName)
            .mapTo(String::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Template $templateName not found.")
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

    // TODO obter por ordem
    override fun processStages(processId: String): List<String> {
        val stages = handle.createQuery("select id_etapa from etapa_processo where id_processo = :processId")
            .bind("processId", processId)
            .mapTo(String::class.java)
            .list()
        return stages.ifEmpty { throw ExceptionControllerAdvice.ProcessNotFound("Process $processId not found.") }
    }

    override fun addUsersToTemplate(templateName: String, email: String) {
        if (handle.createQuery("select * from acesso_template where nome_template = :template and utilizador = :email")
                .bind("template", templateName)
                .bind("email", email)
                .mapTo(String::class.java)
                .firstOrNull() != null)
            throw ExceptionControllerAdvice.InvalidParameterException("User $email already added to $templateName.")

        handle.createUpdate(
            "insert into acesso_template(nome_template, utilizador) values (:template,:email)"
        )
            .bind("template", templateName)
            .bind("email", email)
            .execute()
    }

    override fun removeUserFromTemplate(templateName: String, email: String) {
        handle.createUpdate(
            "delete from acesso_template where nome_template = :template and utilizador = :email"
        )
            .bind("template", templateName)
            .bind("email", email)
            .execute()
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

}