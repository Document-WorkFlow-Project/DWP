package isel.ps.dwp.services

import isel.ps.dwp.NOTIFICATION_FREQUENCY
import isel.ps.dwp.REDIRECT_URL
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.NotificationsServicesInterface
import isel.ps.dwp.model.EmailDetails
import isel.ps.dwp.model.StageInfo
import isel.ps.dwp.notificationsSwitch
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ScheduledFuture

@Service
class NotificationServices(
    private val javaMailSender: JavaMailSender,
    private val taskScheduler: TaskScheduler,
    private val transactionManager: TransactionManager
): NotificationsServicesInterface {

    @Value("\${spring.mail.username}")
    private val sender: String? = null

    private val scheduledTasks: MutableMap<String, ScheduledFuture<*>?> = mutableMapOf()

    /**
     * Executado no inicio da aplicação para reagendar notificações armazenadas na base de dados
     */
    @PostConstruct
    fun init() {
        // Obter todas as notificações ativas
        val notifications = transactionManager.run {
            it.stagesRepository.getAllActiveStageNotifications()
        }

        notifications.forEach { notification ->
            // Calcular delay entre data agendada e a data currente
            var delayMillis = notification.data_inicio_notif.time - System.currentTimeMillis()

            // Se já foi ultrapassada a data agendada a notificação tem efeito imediato
            // TODO opcional: guardar data da ultima notificacao enviada para calcular um delay mais correto
            if (delayMillis < 0) delayMillis = 0

            val delay = Duration.ofMillis(delayMillis)

            val emailDetails = stageNotificationEmail(notification.id_etapa, notification.email_utilizador)

            val scheduledFuture = taskScheduler.scheduleAtFixedRate(
                    { sendSimpleMail(emailDetails) },
                    Instant.now() + delay,
                    Duration.ofDays(NOTIFICATION_FREQUENCY)
            )

            scheduledTasks[notification.id_notificacao] = scheduledFuture
        }
    }

    override fun sendSimpleMail(details: EmailDetails): String {
        return try {
            if (notificationsSwitch) {
                val mailMessage = SimpleMailMessage()

                mailMessage.from = sender
                mailMessage.setTo(details.recipient)
                mailMessage.text = details.msgBody
                mailMessage.subject = details.subject

                javaMailSender.send(mailMessage)
            }
            "Mail Sent Successfully"
        }
        catch (e: Exception) {
            "Error while Sending Mail"
        }
    }

    fun stageNotificationEmail(stageId: String, email: String): EmailDetails {
        val message = "Olá $email,\n\nTem uma tarefa pendente.\nVer detalhes:\n$REDIRECT_URL/stage/$stageId\n\nObrigado,\nDWP"
        return EmailDetails(email, message, "Tarefa pendente")
    }

    override fun scheduleStageNotifications(stageInfo: StageInfo, stageResponsible: List<String>) {
        // First scheduled email is one day before deadline
        // If deadline is in one day, only send emails after deadline
        val delay = if (stageInfo.prazo == 1) Duration.ofDays(stageInfo.prazo.toLong()) else Duration.ofDays((stageInfo.prazo - 1).toLong())

        stageResponsible.forEach { resp ->
            // Send email immediately
            sendSimpleMail(stageNotificationEmail(stageInfo.id, resp))

            val notificationId = UUID.randomUUID().toString()

            // Schedule subsequent emails to specified delay
            val scheduledFuture = taskScheduler.scheduleAtFixedRate(
                { sendSimpleMail(stageNotificationEmail(stageInfo.id, resp)) },
                Instant.now() + delay,
                Duration.ofDays(NOTIFICATION_FREQUENCY)
            )

            scheduledTasks[notificationId] = scheduledFuture

            // Add notification information on database
            transactionManager.run {
                it.stagesRepository.addNotificationId(resp, notificationId, stageInfo.id, Timestamp.from(Instant.now() + delay))
            }
        }
    }

    override fun cancelScheduledNotification(notificationId: String) {
        scheduledTasks[notificationId]?.cancel(true)
    }
}

