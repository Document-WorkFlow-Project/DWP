package isel.ps.dwp.services

import isel.ps.dwp.interfaces.NotificationsServicesInterface
import isel.ps.dwp.model.EmailDetails
import isel.ps.dwp.notificationsSwitch
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import java.util.concurrent.ScheduledFuture

@Service
class NotificationServices(
    private val javaMailSender: JavaMailSender,
    private val taskScheduler: TaskScheduler
): NotificationsServicesInterface {

    @Value("\${spring.mail.username}")
    private val sender: String? = null

    private var scheduledTasks: MutableMap<String, ScheduledFuture<*>?> = mutableMapOf()

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

    override fun scheduleEmail(details: EmailDetails, period: Long): String {
        val notificationId = UUID.randomUUID().toString()
        if (notificationsSwitch) {
            val scheduledFuture = taskScheduler.scheduleAtFixedRate(
                    { sendSimpleMail(details) },
                    Duration.ofDays(period)
            )
            scheduledTasks[notificationId] = scheduledFuture
        }
        return notificationId
    }

    override fun cancelScheduledEmails(notificationId: String) {
        if (notificationsSwitch)
            scheduledTasks[notificationId]?.cancel(true)
    }
}

