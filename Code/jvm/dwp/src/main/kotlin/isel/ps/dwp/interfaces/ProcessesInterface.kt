package isel.ps.dwp.interfaces

import isel.ps.dwp.model.*
import org.springframework.web.multipart.MultipartFile

interface ProcessesInterface {

    fun getProcesses(userAuth: UserAuth, type: String?): List<String>

    /**
     * Obter lista de processos pendentes ou terminados (função de administrador ou utilizador associado ao processo)
     */
    fun processesOfState(state: State, userAuth: UserAuth, limit: Int?, skip: Int?, userEmail: String?): ProcessPage

    /**
     * Obter a lista de etapas associadas a um processo (função de administrador ou utilizador associado ao processo)
     */
    fun processStages(userAuth: UserAuth, processId: String): List<StageModel>

    /**
     * Detalhes de um processo (função de utilizadores associados ao processo)
     */
    fun processDetails(userAuth: UserAuth, processId: String): Process

    /**
     * Retorna os detalhes de todos os documentos associados a um processo
     */
    fun processDocs(userAuth: UserAuth, processId: String): List<Document>

    fun processDocsDetails(userAuth: UserAuth, processId: String): ProcessDocInfo

    /**
     * Criar processo (função de administrador ou utilizador autorizado)
     */
    fun newProcess(
        templateName: String,
        name: String,
        description: String,
        files: List<MultipartFile>,
        userAuth: UserAuth
    ): String

    /**
     * Remover processo (função de administrador ou utilizador autorizado)
     */
    fun deleteProcess(userAuth: UserAuth, processId: String)

    /**
     * Cancelar processo (função de administrador ou utilizador que criou processo)
     */
    fun cancelProcess(userAuth: UserAuth, processId: String)

}