package isel.ps.dwp.services

import isel.ps.dwp.interfaces.NotificationsService
import isel.ps.dwp.model.EmailDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl : NotificationsService {

    @Autowired
    lateinit var javaMailSender: JavaMailSender

    @Value("\${spring.mail.username}")
    private val sender: String? = null

    override fun sendSimpleMail(details: EmailDetails): String {
        return try {
            val mailMessage = SimpleMailMessage()

            mailMessage.from = sender
            mailMessage.setTo(details.recipient)
            mailMessage.text = details.msgBody
            mailMessage.subject = details.subject

            javaMailSender.send(mailMessage)
            "Mail Sent Successfully..."
        }
        catch (e: Exception) {
            "Error while Sending Mail"
        }
    }

    override fun scheduleEmail(details: EmailDetails, delay: Long): String {
        TODO("Not yet implemented")
    }
}

