package isel.ps.dwp.interfaces

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
     * Importar template de um processo à aplicação a partir de um ficheiro json (função de administrador)
     */
    fun addTemplate(templateName: String, templateDescription: String, templateFile: MultipartFile): String

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