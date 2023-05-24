package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.NotificationsServicesInterface
import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class StageServices(private val transactionManager: TransactionManager): StagesInterface {

    private val NOTIFICATION_FREQUENCY: Long = 2

    @Autowired
    @Qualifier("notificationsService")
    lateinit var notificationServices: NotificationsServicesInterface

    private val userServices: UserServices = UserServices(transactionManager)

    private val processServices : ProcessServices = ProcessServices(transactionManager)


    override fun stageDetails(stageId: String): Stage {
        return transactionManager.run {
            it.stagesRepository.checkStage(stageId)
            it.stagesRepository.stageDetails(stageId)
        }
    }

    override fun signStage(stageId: String, approve: Boolean) {

        transactionManager.run {
            it.stagesRepository.checkStage(stageId)
            it.stagesRepository.signStage(stageId, approve)
        }

        val notificationIds: List<String>
        // TODO get email of user who signed
        val userEmail = ""

        if (approve) {
            notificationIds = transactionManager.run {
                it.stagesRepository.getStageNotifications(stageId, userEmail)
            }

            // Verificar se todos os responsáveis já assinaram, se sim marcar etapa como completa e prosseguir para a etapa seguinte
            if (transactionManager.run { it.stagesRepository.verifySignatures(stageId) })
                startNextStage(stageId)

        } else {
            notificationIds = transactionManager.run {
                it.stagesRepository.getStageNotifications(stageId, null)
            }
        }

        // Cancelar emails agendados, referentes a esta etapa
        notificationIds.forEach {
            notificationServices.cancelScheduledEmails(it)
        }
    }

    fun startNextStage(stageId: String) {
        // TODO get id from next pending stage
        val nextStage = ""

        // Atualizar etapa seguinte com data inicio
        transactionManager.run {
            it.stagesRepository.startStage(nextStage)
        }

        // Notificar utilizadores da próxima etapa e agendar notificações recorrentes de acorodo com NOTIFICATION_FREQUENCY
        transactionManager.run {
            it.stagesRepository.stageResponsible(nextStage)
        }.forEach {
            val msgBody = "Olá $it, \nTem uma tarefa pendente. \nObrigado"
            notificationServices.scheduleEmail(EmailDetails(it, msgBody, "Tarefa pendente"), NOTIFICATION_FREQUENCY)
        }
    }

    override fun createStage(processId: String, index: Int, name: String, description: String, mode: String, responsible: List<String>, duration: Int) {
        processServices.checkProcess(processId)

        if (name.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Stage Name can't be blank.")
        if (mode.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Responsable can't be blank.")

        //TODO: Mais Averiguações

        return transactionManager.run {
            it.stagesRepository.createStage(processId, index, name, description, mode, responsible, duration)
        }

    }

    override fun stageUsers(stageId: String): List<User> {
        //checkStage(stageId)

        return transactionManager.run {
            it.stagesRepository.stageUsers(stageId)
        }
    }

    override fun viewStages(processId: String): List<Stage> {
        processServices.checkProcess(processId)

        return transactionManager.run {
            it.stagesRepository.viewStages(processId)
        }
    }

    override fun pendingStages(userEmail: String?): List<StageInfo> {
        return transactionManager.run {
            it.stagesRepository.pendingStages(userEmail)
        }
    }

    /** --------------------------- Comments -------------------------------**/

    override fun addComment(id: String, stageId: String, date: String, text: String, authorEmail : String): String {
        userServices.checkUser(authorEmail)

        if (date.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("date can't be blank.")
        if (text.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("text can't be blank.")
        if (text.length > 150)
            throw ExceptionControllerAdvice.InvalidParameterException("text length can't be bigger than 150 chars.")

        return transactionManager.run {
            it.stagesRepository.checkStage(stageId)
            it.stagesRepository.addComment(id,stageId,date,text,authorEmail)
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
            it.stagesRepository.checkStage(stageId)
            it.stagesRepository.stageComments(stageId)
        }
    }

}