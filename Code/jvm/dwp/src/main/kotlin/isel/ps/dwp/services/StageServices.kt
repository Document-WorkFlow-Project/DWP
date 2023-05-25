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

    /**
     * Assinar etapa, marcando como completa se todos os responsáveis já assinaram
     * Cancela notificações recorrentes associadas
     */
    override fun signStage(stageId: String, approve: Boolean) {
        transactionManager.run {
            it.stagesRepository.checkStage(stageId)
            it.stagesRepository.signStage(stageId, approve)
        }

        val notificationIds: List<String>
        // TODO get email of user who signed
        val userEmail = ""

        if (approve) {
            // Selecionar apenas a notificação associada ao user que assinou
            notificationIds = transactionManager.run {
                it.stagesRepository.getStageNotifications(stageId, userEmail)
            }

            // Verificar se todos os responsáveis já assinaram
            // Se sim, marcar etapa como completa e prosseguir para a etapa seguinte
            // Se não existirem mais etapas o processo é terminado
            if (transactionManager.run { it.stagesRepository.verifySignatures(stageId) }) {

                //Se não existir etapa seguinte, o processo acaba e deve ser enviado email informativo ao autor
                startNextStage(stageId) ?: run {
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
            }
        } else {
            // Selecionar todas as notificações associadas à etapa
            notificationIds = transactionManager.run {
                it.stagesRepository.getStageNotifications(stageId, null)
            }
        }

        // Cancelar emails agendados
        notificationIds.forEach {
            notificationServices.cancelScheduledEmails(it)
        }
    }

    /**
     * Inicia a próxima etapa pendente do processo associado
     * Notifica utilizadores resposáveis pela etapa e agendando notificações recorrentes
     */
    override fun startNextStage(stageId: String): String? {
        // Ver qual a próxima etapa e atualizar com data inicio
        val nextStageId = transactionManager.run {
            it.stagesRepository.startNextStage(stageId)
        }

        if (nextStageId != null) {
            // Notificar utilizadores da próxima etapa e agendar notificações recorrentes de acordo com NOTIFICATION_FREQUENCY
            transactionManager.run {
                it.stagesRepository.stageResponsible(nextStageId)
            }.forEach {
                val msgBody = "Olá $it, \nTem uma tarefa pendente. \nObrigado"
                notificationServices.scheduleEmail(EmailDetails(it, msgBody, "Tarefa pendente"), NOTIFICATION_FREQUENCY)
            }
        }

        return nextStageId
    }

    override fun createStage(processId: String, index: Int, name: String, description: String, mode: String, responsible: List<String>, duration: Int): String {
        processServices.checkProcess(processId)

        if (name.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Stage Name can't be blank.")
        if (mode.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Responsable can't be blank.")

        // TODO: Mais Averiguações

        val responsibleEmails: MutableList<String> = mutableListOf()

        responsible.forEach { resp ->
            // A responsible can be a single email
            if (resp.contains('@'))
                responsibleEmails.add(resp)
            // Or a group of emails
            else {
                val roleEmails = transactionManager.run {
                    it.rolesRepository.getRoleUsers(resp)
                }
                responsibleEmails.addAll(roleEmails)
            }
        }

        return transactionManager.run {
            it.stagesRepository.createStage(processId, index, name, description, mode, responsibleEmails, duration)
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