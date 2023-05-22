package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.TemplatesInterface
import isel.ps.dwp.model.Document
import isel.ps.dwp.model.Template
import isel.ps.dwp.templatesFolderPath
import org.jdbi.v3.core.Handle
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

    override fun availableTemplates(): List<String> {
        //TODO get email from accessing user, and return available templates to this user

        return handle.createQuery(
                "select nome from template_processo"
        )
                .mapTo(String::class.java)
                .list() ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Não existem templates disponiveis.")
    }

    override fun addTemplate(templateFile: MultipartFile): String {
        val fileNameWithType = templateFile.originalFilename
        val fileName = fileNameWithType!!.substringBeforeLast(".json")
        val description = "sample description" //TODO get description from json
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

    fun templateDetails(templateName: String): Template {
        return handle.createQuery("select * from template_processo where nome = :name")
                .bind("name", templateName)
                .mapTo(Template::class.java)
                .singleOrNull() ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Template não encontrado.")
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