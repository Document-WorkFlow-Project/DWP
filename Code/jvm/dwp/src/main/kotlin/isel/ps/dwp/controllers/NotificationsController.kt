package isel.ps.dwp.controllers

import isel.ps.dwp.interfaces.NotificationsService
import isel.ps.dwp.model.EmailDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailController {

    @Autowired
    lateinit var emailService: NotificationsService

    @PostMapping("/sendMail")
    fun sendMail(@RequestBody details: EmailDetails): ResponseEntity<*> {
        val result = emailService.sendSimpleMail(details)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(result)
    }
}

