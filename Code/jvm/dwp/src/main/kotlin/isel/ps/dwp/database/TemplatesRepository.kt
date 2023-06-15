package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.TemplatesInterface
import isel.ps.dwp.model.Template
import isel.ps.dwp.model.UserAuth
import org.jdbi.v3.core.Handle

class TemplatesRepository(private val handle: Handle) : TemplatesInterface {

    override fun availableTemplates(user: UserAuth): List<String> {
        // Admin has access to all templates
        return if (user.roles.contains("admin"))
            handle.createQuery("select nome from template_processo")
                    .mapTo(String::class.java)
                    .list() ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Não existem templates disponiveis.")
        else
            handle.createQuery("select nome_template from acesso_template where utilizador = :email")
                    .bind("email", user.email)
                    .mapTo(String::class.java)
                    .list() ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Não existem templates disponiveis.")
    }

    override fun templateUsers(templateName: String): List<String> {
        return handle.createQuery(
                "select email from acesso_template at join utilizador u on u.email = at.utilizador where nome_template = :templateName"
        )
                .bind("templateName", templateName)
                .mapTo(String::class.java)
                .list()
    }

    fun addTemplate(templateName: String, templateDescription: String, stages: String) {
        if (handle.createQuery("select * from template_processo where nome = :nome")
                .bind("nome", templateName)
                .mapTo(String::class.java)
                .firstOrNull() != null)
            throw ExceptionControllerAdvice.InvalidParameterException("Template $templateName já existe.")

        handle.createUpdate(
            "insert into template_processo (nome, descricao, etapas) values (:name, :description, cast(:etapas as json))"
        )
            .bind("name", templateName)
            .bind("description", templateDescription)
            .bind("etapas", stages)
            .execute()
    }

    fun getTemplate(templateName: String): Template {
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