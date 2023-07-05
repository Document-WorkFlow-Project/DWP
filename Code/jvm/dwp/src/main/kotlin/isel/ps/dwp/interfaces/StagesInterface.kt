package isel.ps.dwp.interfaces

import isel.ps.dwp.model.*

interface StagesInterface {

    fun createStage(processId: String, index: Int, name: String, description: String, mode: String, responsible: List<String>, duration: Int): String

    fun startNextPendingStage(stageId: String): StageInfo?

    /**
     * Os responsáveis pela etapa devem assinar para o workflow avançar (true para aprovação e false para reprovação)
     * Se a etapa for reprovada, o processo é terminado
     */
    fun signStage(stageId: String, approve: Boolean, userAuth: UserAuth)

    /**
     * Retorna a lista de todas as assinaturas respetivas a uma etapa
     */
    fun stageSignatures(stageId: String): List<Signature>

    /**
     * Obter lista de utilizadores associados a uma etapa (função de administrador ou utilizador associado à etapa)
     */
    fun stageUsers(stageId: String): List<UserDetails>

    /**
     * Obter lista de etapas pendentes ou terminadas (função de administrador ou utilizador associado à etapa)
     */
    fun stagesOfState(state: State, userAuth: UserAuth, limit: Int?, skip: Int?, userEmail: String?): TaskPage

    /**
     * Detalhes de uma etapa (função de utilizador associado ao processo)
     */
    fun stageDetails(stageId: String): Stage

    /**
     * Adicionar comentário (função de administrador ou utilizador associado ao processo)
     */
    fun addComment(stageId: String, comment: String, user: UserAuth): String

    /**
     * Remover comentário (função de administrador ou utilizador associado ao comentário)
     */
    fun deleteComment(commentId: String, user: UserAuth)

    /**
     * Obter lista de comentários associados a uma etapa (função de administrador ou utilizador associado ao processo)
     */
    fun stageComments(stageId: String): List<Comment>

}