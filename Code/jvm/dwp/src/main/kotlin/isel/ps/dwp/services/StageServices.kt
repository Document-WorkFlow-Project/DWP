package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.REDIRECT_URL
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.NotificationsServicesInterface
import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.*
import org.springframework.stereotype.Service

@Service
class StageServices(
    private val transactionManager: TransactionManager,
    private val notificationServices: NotificationsServicesInterface,
    private val userServices: UserServices
) : StagesInterface {

    override fun stageDetails(stageId: String,userAuth: UserAuth): Stage {
        return transactionManager.run {
            if (!it.stagesRepository.userAdminOrInStage(
                    stageId,
                    userAuth
                )
            ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")
            it.stagesRepository.stageDetails(stageId,userAuth)
        }
    }

    private fun finalStageEmail(stageId: String, userAuth: UserAuth) {
        val processDetails = transactionManager.run {
            val processId = it.stagesRepository.findProcessFromStage(stageId)
            it.processesRepository.processDetails(userAuth, processId)
        }

        val state = if (processDetails.estado == "APPROVED") "aprovado" else "não aprovado"

        val emailDetails = EmailDetails(
                processDetails.autor,
                "O processo ${processDetails.nome} foi concluído com estado $state.\nVer detalhes:\n$REDIRECT_URL/process/${processDetails.id}\n\nObrigado,\nDWP",
                "Processo ${processDetails.nome} concluído"
        )
        notificationServices.sendSimpleMail(emailDetails)
    }

    /**
     * Assinar etapa, marcando como completa se todos os responsáveis já assinaram
     * Cancela notificações recorrentes associadas
     */
    override fun signStage(stageId: String, approve: Boolean, userAuth: UserAuth) {
        transactionManager.run {
            it.stagesRepository.stageUsers(stageId).find {
                user -> user.email == userAuth.email
            } ?: throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não faz parte da etapa.")

            it.stagesRepository.signStage(stageId, approve, userAuth)
        }

        val notificationIds: List<String>
        val userEmail = userAuth.email

        if (approve) {
            // Selecionar apenas a notificação associada ao user que assinou
            notificationIds = transactionManager.run {
                it.stagesRepository.getStageNotifications(stageId, userEmail, userAuth)
            }

            // Verificar se todos os responsáveis já assinaram
            // Se sim, marcar etapa como completa e prosseguir para a etapa seguinte
            // Se não existirem mais etapas o processo é terminado
            if (transactionManager.run {
                    it.stagesRepository.verifySignatures(stageId)
                }) {
                //Se não existir etapa seguinte, o processo acaba e deve ser enviado email informativo ao autor
                startNextPendingStage(stageId) ?: run {
                    finalStageEmail(stageId, userAuth)
                }
            }
        } else {
            // Selecionar todas as notificações associadas à etapa
            notificationIds = transactionManager.run {
                it.stagesRepository.getStageNotifications(stageId, null, userAuth)
            }
            finalStageEmail(stageId, userAuth)
        }

        // Cancelar emails agendados
        notificationIds.forEach {
            notificationServices.cancelScheduledNotification(it)
        }
    }

    override fun stageSignatures(stageId: String,userAuth: UserAuth): List<Signature> {
        return transactionManager.run {
            if (!it.stagesRepository.userAdminOrInStage(
                    stageId,
                    userAuth
                )
            ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")
            it.stagesRepository.stageDetails(stageId,userAuth)
            it.stagesRepository.stageSignatures(stageId,userAuth)
        }
    }

    /**
     * Inicia a próxima etapa pendente do processo associado
     * Notifica utilizadores resposáveis pela etapa e agendando notificações recorrentes
     */
    override fun startNextPendingStage(stageId: String): StageInfo? {
        // Ver qual a próxima etapa e atualizar com data inicio
        val nextStage = transactionManager.run {
            it.stagesRepository.startNextPendingStage(stageId)
        }

        if (nextStage != null) {
            // Notificar utilizadores da próxima etapa e agendar notificações recorrentes
            transactionManager.run {
                val stageResponsible = it.stagesRepository.stageResponsible(nextStage.id)
                notificationServices.scheduleStageNotifications(nextStage, stageResponsible)
            }
        }

        return nextStage
    }

    override fun createStage(
        processId: String,
        index: Int,
        name: String,
        description: String,
        mode: String,
        responsible: List<String>,
        duration: Int,
        userAuth: UserAuth
    ): String {
        return transactionManager.run {
            it.processesRepository.processDetails(userAuth, processId)
            it.stagesRepository.createStage(processId, index, name, description, mode, responsible, duration,userAuth)
        }
    }

    override fun stageUsers(stageId: String,userAuth: UserAuth): List<UserDetails> {
        return transactionManager.run {
            if (!it.stagesRepository.userAdminOrInStage(
                    stageId,
                    userAuth
                )
            ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")
            it.stagesRepository.stageDetails(stageId,userAuth)
            it.stagesRepository.stageUsers(stageId,userAuth)
        }
    }

    override fun stagesOfState(state: State, userAuth: UserAuth, limit: Int?, skip: Int?, userEmail: String?): TaskPage {
        return transactionManager.run {
            it.stagesRepository.stagesOfState(state, userAuth, limit, skip, userEmail)
        }
    }

    /** --------------------------- Comments -------------------------------**/

    override fun addComment(stageId: String, comment: String, user: UserAuth): String {
        userServices.checkUser(user.email) ?: throw ExceptionControllerAdvice.UserNotFound("Utilizador não encontrado.")

        if (comment.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("text can't be blank.")
        if (comment.length > 150)
            throw ExceptionControllerAdvice.InvalidParameterException("text length can't be bigger than 150 chars.")

        return transactionManager.run {
            if (!it.stagesRepository.userAdminOrInStage(
                    stageId,
                    user
                )
            ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")
            it.stagesRepository.stageDetails(stageId, user)
            it.stagesRepository.addComment(stageId, comment, user)
        }
    }

    override fun deleteComment(commentId: String, user: UserAuth) {
        return transactionManager.run {
            it.stagesRepository.checkComment(commentId)
            it.stagesRepository.deleteComment(commentId, user)
        }
    }

    override fun stageComments(stageId: String, userAuth: UserAuth): List<Comment> {
        return transactionManager.run {
            if (!it.stagesRepository.userAdminOrInStage(
                    stageId,
                    userAuth
                )
            ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")
            it.stagesRepository.stageDetails(stageId,userAuth)
            it.stagesRepository.stageComments(stageId,userAuth)
        }
    }

}