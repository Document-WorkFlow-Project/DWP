package isel.ps.dwp.interfaces

import isel.ps.dwp.model.TemplateWStatus
import isel.ps.dwp.model.UserAuth

interface TemplatesInterface {

    fun allTemplates(): List<TemplateWStatus>

    /**
     * Obter lista de templates disponiveis para um utilizador
     */
    fun availableTemplates(user: UserAuth): List<String>

    /**
     * Retorna a lista de utilizadores que têm acesso ao template passado como parâmetro
     */
    fun templateUsers(templateName: String): List<String>

    /**
     * Adicionar utilizadores que podem usar template (função de administrador)
     */
    fun addUsersToTemplate(templateName: String, email: String)

    /**
     * Remover utilizadores que podem usar template (função de administrador)
     */
    fun removeUserFromTemplate(templateName: String, email: String)

    /**
     * Ativar/desativar template de um processo (função de administrador)
     */
    fun setTemplateAvailability(active: Boolean, templateName: String)

}