package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.NOTIFICATION_FREQUENCY
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.NotificationsServicesInterface
import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.*
import org.springframework.stereotype.Service

@Service
class StageServices (
    private val transactionManager: TransactionManager,
    private val notificationServices: NotificationsServicesInterface
) : StagesInterface {

    private val userServices: UserServices = UserServices(transactionManager)

    override fun stageDetails(stageId: String): Stage {
        return transactionManager.run {
            it.stagesRepository.stageDetails(stageId)
        }
    }

    private fun finalStageEmail(stageId: String) {
        val processDetails = transactionManager.run {
            val processId = it.stagesRepository.findProcessFromStage(stageId)
            it.processesRepository.processDetails(processId)
        }

        val emailDetails = EmailDetails(
                processDetails.autor,
                "O processo ${processDetails.id} foi concluido com estado ${processDetails.estado}.",
                "Processo ${processDetails.id} concluído"
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
                det -> det.email == userAuth.email
            } ?: throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não faz parte da etapa.")
            //it.stagesRepository.checkStage(stageId)
            it.stagesRepository.signStage(stageId, approve, userAuth)
        }

        val notificationIds: List<String>
        val userEmail = userAuth.email

        if (approve) {
            // Selecionar apenas a notificação associada ao user que assinou
            notificationIds = transactionManager.run {
                it.stagesRepository.getStageNotifications(stageId, userEmail)
            }

            // Verificar se todos os responsáveis já assinaram
            // Se sim, marcar etapa como completa e prosseguir para a etapa seguinte
            // Se não existirem mais etapas o processo é terminado
            if (transactionManager.run {
                it.stagesRepository.verifySignatures(stageId)
            }) {
                //Se não existir etapa seguinte, o processo acaba e deve ser enviado email informativo ao autor
                startNextPendingStage(stageId) ?: run {
                    finalStageEmail(stageId)
                }
            }
        } else {
            // Selecionar todas as notificações associadas à etapa
            notificationIds = transactionManager.run {
                it.stagesRepository.getStageNotifications(stageId, null)
            }
            finalStageEmail(stageId)
        }

        // Cancelar emails agendados
        notificationIds.forEach {
            notificationServices.cancelScheduledEmails(it)
        }
    }

    override fun stageSignatures(stageId: String): List<Signature> {
        return transactionManager.run {
            it.stagesRepository.stageDetails(stageId)
            it.stagesRepository.stageSignatures(stageId)
        }
    }

    /**
     * Inicia a próxima etapa pendente do processo associado
     * Notifica utilizadores resposáveis pela etapa e agendando notificações recorrentes
     */
    override fun startNextPendingStage(stageId: String): String? {
        // Ver qual a próxima etapa e atualizar com data inicio
        val nextStageId = transactionManager.run {
            it.stagesRepository.startNextPendingStage(stageId)
        }

        if (nextStageId != null) {
            // Notificar utilizadores da próxima etapa e agendar notificações recorrentes de acordo com NOTIFICATION_FREQUENCY
            transactionManager.run {
                it.stagesRepository.stageResponsible(nextStageId).forEach { resp ->
                    val message = "Olá $resp,\n\nTem uma tarefa pendente.\nVer detalhes:\nhttp://localhost:3000/stage/$nextStageId\n\nObrigado,\nDWP"
                    val email = EmailDetails(resp, message, "Tarefa pendente")
                    val notificationId = notificationServices.scheduleEmail(email, NOTIFICATION_FREQUENCY)
                    it.stagesRepository.addNotificationId(resp, notificationId, nextStageId)
                }
            }
        }

        return nextStageId
    }

    override fun createStage(processId: String, index: Int, name: String, description: String, mode: String, responsible: List<String>, duration: Int): String {
        return transactionManager.run {
            it.processesRepository.processDetails(processId)
            it.stagesRepository.createStage(processId, index, name, description, mode, responsible, duration)
        }
    }

    override fun stageUsers(stageId: String): List<UserDetails> {
        return transactionManager.run {
            it.stagesRepository.stageDetails(stageId)
            it.stagesRepository.stageUsers(stageId)
        }
    }

    override fun pendingStages(userAuth: UserAuth, userEmail: String?): List<StageInfo> {
        return transactionManager.run {
            it.stagesRepository.pendingStages(userAuth, userEmail)
        }
    }

    override fun finishedStages(userAuth: UserAuth, userEmail: String?): List<StageInfo> {
        return transactionManager.run {
            it.stagesRepository.finishedStages(userAuth, userEmail)
        }
    }

    /** --------------------------- Comments -------------------------------**/

    override fun addComment(stageId: String, comment: String, user: UserAuth): String {
        userServices.checkUser(user.email)

        if (comment.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("text can't be blank.")
        if (comment.length > 150)
            throw ExceptionControllerAdvice.InvalidParameterException("text length can't be bigger than 150 chars.")

        return transactionManager.run {
            it.stagesRepository.stageDetails(stageId)
            it.stagesRepository.addComment(stageId, comment, user)
        }
    }

    override fun deleteComment(commentId: String) {
        return transactionManager.run {
            it.stagesRepository.checkComment(commentId)
            it.stagesRepository.deleteComment(commentId)
        }
    }

    override fun stageComments(stageId: String): List<Comment> {
        return transactionManager.run {
            it.stagesRepository.stageDetails(stageId)
            it.stagesRepository.stageComments(stageId)
        }
    }

}