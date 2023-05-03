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
        return handle.createQuery("select id from processo where tipo = :type")
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

    override fun processStages(processId: String): List<Stage> {
        TODO("Not yet implemented")
    }

    override fun addUsersToTemplate(templateName: String, email: String) {
        TODO("Not yet implemented")
    }

    override fun removeUserFromTemplate(templateName: String, email: String) {
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