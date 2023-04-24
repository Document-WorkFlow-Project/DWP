package isel.ps.dwp.database.processes

import isel.ps.dwp.model.Process
import isel.ps.dwp.model.Stage

interface ProcessRepository {

    /** Criar template de um processo (função de administrador)
     */
    fun createTemplate()

    /**
     * Remover template de um processo (função de administrador)
     */
    fun deleteTemplate()

    /**
     * Obter lista de processos pendentes (função de administrador ou utilizador associado ao processo)
     */
    fun pendingProcesses(): List<Process>

    /**
     * Obter lista de processos terminados (função de administrador ou utilizador associado ao processo)
     */
    fun finishedProcesses(): List<Process>

    /**
     * Obter lista de processos de um certo tipo de template
     */
    fun getProcesses(type: String): List<Process>

    /**
     * Obter lista de utilizadores associados a um processo (função de administrador ou utilizador associado ao processo)
     */
    fun processUsers(processId: String): List<String>

    /**
     * Obter lista de processos de um utilizador (responsável ou participante)
     */
    fun userProcesses(userId: String): List<Process>

    /**
     * Obter a lista de etapas associadas a um processo (função de administrador ou utilizador associado ao processo)
     */
    fun processStages(processId: String): List<Stage>

    /**
     * Adicionar utilizadores que podem usar template (função de administrador)
     */
    fun assUserToTemplate(userId: String, templateId: String)

    /**
     * Remover utilizadores que podem usar template (função de administrador)
     */
    fun removeUserFromTemplate(userId: String, templateId: String)

    /**
    * Detalhes de um processo (função de utilizadores associados ao processo)
     */
    fun processDetails(processId: String): Process

    /**
     * Criar processo (função de administrador ou utilizador autorizado)
     */
    fun newProcess(): String

    /**
     * Remover processo (função de administrador ou utilizador autorizado)
     */
    fun deleteProcess(processId: String)

    /**
     * Cancelar processo (função de administrador ou utilizador que criou processo)
     */
    fun cancelProcess(processId: String)
}