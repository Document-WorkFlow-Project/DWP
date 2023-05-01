package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage
import isel.ps.dwp.model.User

interface StagesInterface {

    fun createStage(processId: Int, nome: String, responsavel: String, descricao: String, data_inicio: String, data_fim: String, prazo: String, estado: String)

    fun deleteStage(stageId: String)

    fun editStage(stageId: String, nome: String, descricao: String, data_inicio: String, data_fim: String, prazo: String, estado: String)

    fun viewStages(processId : String): List<Stage>

    fun approveStage(stageId: String)

    fun disaproveStage(stageId: String)

    /**
     * Obter lista de utilizadores associados a uma etapa (função de administrador ou utilizador associado à etapa)
     */
    fun stageUsers(stageId: String): List<User>

    /**
     * Obter lista de etapas pendentes (função de administrador ou utilizador associado à etapa)
     */
    fun pendingStages(processId: String): List<Stage>


    /**
     * Editar detalhes da etapa (função de administrador)
     */

    /**
     * Detalhes de uma etapa (função de utilizador associado ao processo)
     */
    fun stageDetails(stageId: String): Stage

    /**
     * Adicionar comentário (função de administrador ou utilizador associado ao processo)
     */
    fun addComment(id: String, stageId: String, date: String, text: String, authorEmail : String): String

    /**
     * Remover comentário (função de administrador ou utilizador associado ao comentário)
     */
    fun deleteComment(commentId: String)

    /**
     * Obter lista de comentários associados a uma etapa (função de administrador ou utilizador associado ao processo)
     */
    fun stageComments(stageId: String): List<Comment>

    /**
     * Aprovar/reprovar etapa (função dos responsáveis da etapa) (implementar aprovação por maioria ou totalidade dos participantes)
     */

}