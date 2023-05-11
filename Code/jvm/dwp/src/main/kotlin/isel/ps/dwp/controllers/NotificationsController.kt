package isel.ps.dwp.controllers

import isel.ps.dwp.interfaces.NotificationsServicesInterface
import isel.ps.dwp.model.EmailDetails
import isel.ps.dwp.model.EmailSchedule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
class EmailController {

    @Autowired
    lateinit var emailService: NotificationsServicesInterface

    @PostMapping("/sendEmail")
    fun sendMail(@RequestBody details: EmailDetails): ResponseEntity<*> {
        val result = emailService.sendSimpleMail(details)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(result)
    }

    @PostMapping("/scheduleEmail")
    fun scheduleMail(@RequestBody schedule: EmailSchedule): ResponseEntity<*> {
        val notificationId = emailService.scheduleEmail(schedule.email, Duration.ofDays(schedule.period))
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(notificationId)
    }

    @PostMapping("/cancelSchedule/{notificationId}")
    fun cancelSchedule(@PathVariable notificationId: String): ResponseEntity<*> {
        emailService.cancelScheduledEmails(notificationId)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$notificationId cancelled")
    }
}

