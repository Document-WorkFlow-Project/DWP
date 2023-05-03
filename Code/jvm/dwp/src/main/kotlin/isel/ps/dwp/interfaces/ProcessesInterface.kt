package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Process
import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.model.Stage
import org.springframework.web.multipart.MultipartFile

interface ProcessesInterface {

    /** Importar template de um processo à aplicação a partir de um ficheiro json (função de administrador)
     */
    fun addTemplate(templateFile: MultipartFile): String

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

    /**
     * Obter lista de processos de um certo tipo de template
     */
    fun getProcesses(type: String): List<String>

    /**
     * Obter lista de processos pendentes (função de administrador ou utilizador associado ao processo)
     */
    fun pendingProcesses(userEmail: String?): List<String>

    /**
     * Obter lista de processos terminados (função de administrador ou utilizador associado ao processo)
     */
    fun finishedProcesses(userEmail: String?): List<String>

    /**
     * Obter a lista de etapas associadas a um processo (função de administrador ou utilizador associado ao processo)
     */
    fun processStages(processId: String): List<Stage>

    /**
    * Detalhes de um processo (função de utilizadores associados ao processo)
     */
    fun processDetails(processId: String): Process

    /**
     * Criar processo (função de administrador ou utilizador autorizado)
     */
    fun newProcessFromTemplate(templateName: String): String

    /**
     * Remover processo (função de administrador ou utilizador autorizado)
     */
    fun deleteProcess(processId: String)

    /**
     * Cancelar processo (função de administrador ou utilizador que criou processo)
     */
    fun cancelProcess(processId: String)
}