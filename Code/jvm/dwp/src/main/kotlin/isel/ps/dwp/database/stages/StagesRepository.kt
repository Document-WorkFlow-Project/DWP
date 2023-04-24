package isel.ps.dwp.database.stages

import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage

interface StagesRepository {

    /**
     * Obter lista de etapas pendentes (função de administrador ou utilizador associado à etapa)
     */
    fun pendingStages(processId: String): List<Stage>

    /**
     * Obter lista de utilizadores associados a uma etapa (função de administrador ou utilizador associado à etapa)
     */
    fun stageUsers(stageId: String): List<String>

    /**
     * Adicionar etapa a um template (função de administrador)
     */
    fun addStageToTemplate(stage: Stage, stageIndex: Int?, templateId: String)

    /**
     * Remover etapa de um template (função de administrador)
     */
    fun removeStageFromTemplate(stageIndex: Int, templateId: String)

    /**
     * Editar detalhes da etapa (função de administrador)
     */
    fun editStage(stageId: String, editedStage: Stage)

    /**
     * Detalhes de uma etapa (função de utilizador associado ao processo)
     */
    fun stageDetails(stageId: String): Stage

    /**
     * Adicionar comentário (função de administrador ou utilizador associado ao processo)
     */
    fun addComment(comment: Comment, stageId: String): String

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
    fun approveStage(value: Boolean)
}