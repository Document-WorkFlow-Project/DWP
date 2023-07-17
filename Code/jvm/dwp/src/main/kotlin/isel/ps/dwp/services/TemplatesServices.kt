package isel.ps.dwp.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.TemplatesInterface
import isel.ps.dwp.model.*
import org.springframework.stereotype.Service

@Service
class TemplatesServices(
    private val transactionManager: TransactionManager,
    private val objectMapper: ObjectMapper
): TemplatesInterface {

    override fun allTemplates(): List<TemplateWStatus> {
        return transactionManager.run {
            it.templatesRepository.allTemplates()
        }
    }

    override fun availableTemplates(user: UserAuth): List<String> {
        return transactionManager.run {
            it.templatesRepository.availableTemplates(user)
        }
    }

    override fun templateUsers(templateName: String): List<String> {
        return transactionManager.run {
            it.templatesRepository.templateUsers(templateName)
        }
    }

    fun addTemplate(template: ProcessTemplate) {
        // Convert stages objects to json
        val stagesJson = objectMapper.writeValueAsString(template.stages)

        transactionManager.run {
            it.templatesRepository.addTemplate(template.name, template.description, stagesJson)
        }
    }

    override fun addUsersToTemplate(templateName: String, email: String) {
        if (email.isEmpty())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user emails.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.usersRepository.checkUser(email) ?: throw ExceptionControllerAdvice.UserNotFound("Utilizador não encontrado.")
            it.templatesRepository.addUsersToTemplate(templateName, email)
        }
    }

    override fun removeUserFromTemplate(templateName: String, email: String) {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user email.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.usersRepository.checkUser(email) ?: throw ExceptionControllerAdvice.UserNotFound("Utilizador não encontrado.")
            it.templatesRepository.removeUserFromTemplate(templateName, email)
        }
    }

    fun getTemplate(templateName: String, user: UserAuth): TemplateResponse  {
        if (!availableTemplates(user).contains(templateName))
            throw ExceptionControllerAdvice.InvalidParameterException("O utilizador não tem acesso a este template.")

        return transactionManager.run {
            val templateDetails = it.templatesRepository.getTemplate(templateName)
            val stages = objectMapper.readValue<List<StageTemplate>>(templateDetails.etapas)
            TemplateResponse(templateDetails.nome, templateDetails.descricao, stages)
        }
    }

    override fun setTemplateAvailability(active: Boolean, templateName: String) {
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.templatesRepository.setTemplateAvailability(active, templateName)
        }
    }
}