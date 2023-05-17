package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.services.TemplatesServices
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/templates")
class TemplatesController (
    private val templatesServices: TemplatesServices = TemplatesServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

    @GetMapping
    fun availableTemplates(): ResponseEntity<*> {
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(templatesServices.availableTemplates())
    }

    @PostMapping
    fun newTemplate(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        val templateName = templatesServices.addTemplate(file)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(templateName)
    }

    @PutMapping("/{templateName}/{email}")
    fun addUserToTemplate(@PathVariable templateName: String, @PathVariable email: String): ResponseEntity<*> {
        templatesServices.addUsersToTemplate(templateName, email)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Emails added to $templateName template.")
    }

    @DeleteMapping("/{templateName}/{email}")
    fun removeUserFromTemplate(@PathVariable templateName: String, @PathVariable email: String): ResponseEntity<*> {
        templatesServices.removeUserFromTemplate(templateName, email)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$email removed from $templateName template.")
    }

    @DeleteMapping("/{templateName}")
    fun deleteTemplate(@PathVariable templateName: String): ResponseEntity<*> {
        templatesServices.deleteTemplate(templateName)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Template $templateName deleted")
    }

    /* TODO create process should do this
    @PostMapping("/{json}")
    fun insertDataFromTemplate(@RequestBody template: ProcessTemplate){
        val result = templatesServices.insertDataFromTemplate(template)
    }
     */

}