package isel.ps.dwp.controllers

import isel.ps.dwp.http.pipeline.authorization.Admin
import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.services.TemplatesServices
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    fun newTemplate(@RequestBody template: ProcessTemplate, user: UserAuth): ResponseEntity<*> {
        templatesServices.addTemplate(template)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Template ${template.name} criado.")
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
        val template = templatesServices.getTemplate(templateName, user)
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(template)
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