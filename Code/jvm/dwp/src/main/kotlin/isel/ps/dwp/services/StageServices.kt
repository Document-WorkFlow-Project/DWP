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

    private val userServices: UserServices = UserServices(
        transactionManager)

    private val processServices : ProcessServices = ProcessServices(
        transactionManager
    )


    override fun stageDetails(stageId: String): Stage {
        checkStage(stageId)

        return transactionManager.run {
            it.stagesRepository.stageDetails(stageId)
        }
    }

    override fun signStage(stageId: String, approve: Boolean) {

        checkStage(stageId)

        transactionManager.run {
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

    override fun createStage(processId: Int, nome: String, modo: String, responsavel: String, descricao: String, data_inicio: String, data_fim: String?, prazo: String, estado: String){

        processServices.checkProcess(processId.toString())


        if (nome.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Stage Name can't be blank.")
        if (responsavel.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Responsable can't be blank.")
        if (descricao.length > 100)
            throw ExceptionControllerAdvice.InvalidParameterException("Descrição length can't be bigger than 100 chars.")

        /* Verificar se o modo é válido */
        if (modo != "Unanimous" && modo != "Majority" && modo != "Unilateral") {
            throw ExceptionControllerAdvice.InvalidParameterException("Invalid value for parameter 'modo'. Must be 'Unanimos', 'Majority' or 'Unilateral'.")
        }


        /*TODO: Mais Averiguações*/

        return transactionManager.run {
            it.stagesRepository.createStage(processId, nome, modo, responsavel,descricao,data_inicio,null,prazo,estado)
        }

    }

    fun checkStage(stageId: String): Stage? {
        return transactionManager.run {
            val stageRepo = it.stagesRepository
            stageRepo.checkStage(stageId)
        } ?: throw ExceptionControllerAdvice.StageNotFound("Stage not found. Incorrect Stage ID.")
    }

    override fun checkComment(commentId: String): Comment?{
        return transactionManager.run {
            val stageRepo = it.stagesRepository
            stageRepo.checkComment(commentId)
        } ?: throw ExceptionControllerAdvice.CommentNotFound("Comment not found. Incorrect Comment ID.")
    }

    override fun stageUsers(stageId: String): List<User> {
        checkStage(stageId)

        return transactionManager.run {
            it.stagesRepository.stageUsers(stageId)
        }
    }

    override fun deleteStage(stageId: String) {
        TODO("Necessary?")
    }

    override fun editStage(stageId: String, nome: String, modo:String, descricao: String, data_inicio: String, data_fim: String, prazo: String, estado: String) {
        checkStage(stageId)
        if (nome.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Stage Name can't be blank.")
        if (descricao.length > 100)
            throw ExceptionControllerAdvice.InvalidParameterException("Descrição length can't be bigger than 100 chars.")

        /* Verificar se o modo é válido */
        if (modo != "Unanimous" && modo != "Majority" && modo != "Unilateral") {
            throw ExceptionControllerAdvice.InvalidParameterException("Invalid value for parameter 'modo'. Must be 'Unanimos', 'Majority' or 'Unilateral'.")
        }
        return transactionManager.run {
            it.stagesRepository.editStage(stageId,nome,modo,descricao,data_inicio,data_fim,prazo,estado)
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


    override fun addComment(id: String, stageId: String, date: String, text: String, authorEmail : String): String {
        checkComment(id)

        checkStage(stageId)

        userServices.checkUser(authorEmail)

        if (date.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("date can't be blank.")
        if (text.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("text can't be blank.")
        if (text.length > 150)
            throw ExceptionControllerAdvice.InvalidParameterException("text length can't be bigger than 150 chars.")

        return transactionManager.run {
            it.stagesRepository.addComment(id,stageId,date,text,authorEmail)
        }

    }

    override fun deleteComment(commentId: String) {
        checkComment(commentId)

        return transactionManager.run {
            it.stagesRepository.deleteComment(commentId)
        }
    }

    override fun stageComments(stageId: String): List<Comment> {
        checkStage(stageId)

        return transactionManager.run {
            it.stagesRepository.stageComments(stageId)
        }
    }


}