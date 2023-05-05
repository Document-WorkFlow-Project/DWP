package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.services.StageServices
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
    @PostMapping("/new")
    fun newTemplate(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        val templateName = templatesServices.addTemplate(file)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(templateName)
    }

    @PutMapping("{templateName}")
    fun addUserToTemplate(@PathVariable templateName: String, @RequestBody email: String): ResponseEntity<*> {
        templatesServices.addUsersToTemplate(templateName, email)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Emails added to $templateName template.")
    }

    @DeleteMapping("{templateName}")
    fun removeUserFromTemplate(@PathVariable templateName: String, @RequestBody email: String): ResponseEntity<*> {
        templatesServices.removeUserFromTemplate(templateName, email)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$email removed from $templateName template.")
    }

    @DeleteMapping("{name}")
    fun deleteTemplate(@PathVariable name: String): ResponseEntity<*> {
        templatesServices.deleteTemplate(name)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Template $name deleted")
    }

    @PostMapping("/{json}")
    fun insertDataFromTemplate(@RequestBody template: ProcessTemplate){
        val result = templatesServices.insertDataFromTemplate(template)
    }

}