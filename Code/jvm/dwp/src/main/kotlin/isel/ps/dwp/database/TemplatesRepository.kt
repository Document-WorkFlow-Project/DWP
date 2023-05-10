package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.interfaces.TemplatesInterface
import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.templatesFolderPath
import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

class TemplatesRepository(private val handle: Handle) : TemplatesInterface {

    fun findTemplatePathByName(templateName: String): String {
        return handle.createQuery(
            "select path from template_processo where nome = :name"
        )
            .bind("name", templateName)
            .mapTo(String::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Template $templateName not found.")
    }

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


}