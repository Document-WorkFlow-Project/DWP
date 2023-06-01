package isel.ps.dwp.controllers

import isel.ps.dwp.http.pipeline.Admin
import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.services.TemplatesServices
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/templates")
class TemplatesController (
    private val templatesServices: TemplatesServices
) {

    @GetMapping
    fun availableTemplates(user: UserAuth): ResponseEntity<*> {
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(templatesServices.availableTemplates(user))
    }

    @PostMapping
    @Admin
    fun newTemplate(@RequestParam("name") name: String, @RequestParam("description") description: String, @RequestParam("file") file: MultipartFile, user: UserAuth): ResponseEntity<*> {
        templatesServices.addTemplate(name, description, file)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Template $name criado.")
    }

    @GetMapping("/{templateName}/users")
    fun templateUsers(@PathVariable templateName: String, user: UserAuth): ResponseEntity<*> {
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(templatesServices.templateUsers(templateName))
    }

    @PutMapping("/{templateName}/{email}")
    @Admin
    fun addUserToTemplate(@PathVariable templateName: String, @PathVariable email: String, user: UserAuth): ResponseEntity<*> {
        templatesServices.addUsersToTemplate(templateName, email)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Emails added to $templateName template.")
    }

    @DeleteMapping("/{templateName}/{email}")
    @Admin
    fun removeUserFromTemplate(@PathVariable templateName: String, @PathVariable email: String, user: UserAuth): ResponseEntity<*> {
        templatesServices.removeUserFromTemplate(templateName, email)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$email removed from $templateName template.")
    }

    @GetMapping("/{templateName}")
    fun getTemplate(@PathVariable templateName: String, user: UserAuth): ResponseEntity<*> {
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(templatesServices.getTemplate(templateName))
    }

    @DeleteMapping("/{templateName}")
    @Admin
    fun deleteTemplate(@PathVariable templateName: String, user: UserAuth): ResponseEntity<*> {
        templatesServices.deleteTemplate(templateName)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Template $templateName deleted")
    }

}