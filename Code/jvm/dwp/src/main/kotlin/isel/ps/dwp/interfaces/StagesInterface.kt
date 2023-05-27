package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage
import isel.ps.dwp.model.StageInfo
import isel.ps.dwp.model.UserDetails

interface StagesInterface {

    fun createStage(processId: String, index: Int, name: String, description: String, mode: String, responsible: List<String>, duration: Int): String

    fun startNextPendingStage(stageId: String): String?

    fun viewStages(processId : String): List<Stage>

    /**
     * Os responsáveis pela etapa devem assinar para o workflow avançar (true para aprovação e false para reprovação)
     * Se a etapa for reprovada, o processo é terminado
     */
    fun signStage(stageId: String, approve: Boolean)

    /**
     * Obter lista de utilizadores associados a uma etapa (função de administrador ou utilizador associado à etapa)
     */
    fun stageUsers(stageId: String): List<UserDetails>

    /**
     * Obter lista de etapas pendentes que o utilizador necessita de assinar (função de administrador ou utilizador associado à etapa)
     */
    fun pendingStages(userEmail: String?): List<StageInfo>

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

}