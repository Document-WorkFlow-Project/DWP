package isel.ps.dwp.interfaces

import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.model.Template
import isel.ps.dwp.model.UserAuth
import org.springframework.web.multipart.MultipartFile

interface TemplatesInterface {

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
     * Remover template de um processo (função de administrador)
     */
    fun deleteTemplate(templateName: String)

}