package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Process

interface ProcessesInterface {

    fun getProcesses(type: String?): List<String>

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
    fun processStages(processId: String): List<String>

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


    fun checkProcess(id: String): Process?
}