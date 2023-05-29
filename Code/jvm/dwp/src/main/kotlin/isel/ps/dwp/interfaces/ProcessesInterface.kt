package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Document
import isel.ps.dwp.model.Process
import isel.ps.dwp.model.ProcessModel
import isel.ps.dwp.model.UserAuth
import org.springframework.web.multipart.MultipartFile

interface ProcessesInterface {

    fun getProcesses(type: String?): List<String>

    /**
     * Obter lista de processos pendentes (função de administrador ou utilizador associado ao processo)
     */
    fun pendingProcesses(userAuth: UserAuth, userEmail: String?): List<ProcessModel>

    /**
     * Obter lista de processos terminados (função de administrador ou utilizador associado ao processo)
     */
    fun finishedProcesses(userAuth: UserAuth, userEmail: String?): List<ProcessModel>

    /**
     * Obter a lista de etapas associadas a um processo (função de administrador ou utilizador associado ao processo)
     */
    fun processStages(processId: String): List<String>

    /**
    * Detalhes de um processo (função de utilizadores associados ao processo)
     */
    fun processDetails(processId: String): Process

    /**
     * Retorna os detalhes de todos os documentos associados a um processo
     */
    fun processDocs(processId: String): List<Document>

    /**
     * Criar processo (função de administrador ou utilizador autorizado)
     */
    fun newProcess(templateName: String, name: String, description: String, files: List<MultipartFile>, userAuth: UserAuth): String

    /**
     * Remover processo (função de administrador ou utilizador autorizado)
     */
    fun deleteProcess(processId: String)

    /**
     * Cancelar processo (função de administrador ou utilizador que criou processo)
     */
    fun cancelProcess(processId: String)

}