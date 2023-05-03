package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.services.ProcessServices
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/processes")
class ProcessesController (
    private val processesServices: ProcessServices = ProcessServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

    @PostMapping("/templates")
    fun newTemplate(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        val templateName = processesServices.addTemplate(file)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body(templateName)
    }

    @PutMapping("/templates/{templateName}")
    fun addUserToTemplate(@PathVariable templateName: String, @RequestBody email: String): ResponseEntity<*> {
        processesServices.addUsersToTemplate(templateName, email)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Emails added to $templateName template.")
    }

    @DeleteMapping("/templates/{templateName}")
    fun removeUserFromTemplate(@PathVariable templateName: String, @RequestBody email: String): ResponseEntity<*> {
        processesServices.removeUserFromTemplate(templateName, email)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("$email removed from $templateName template.")
    }

    @DeleteMapping("/templates/{name}")
    fun deleteTemplate(@PathVariable name: String): ResponseEntity<*> {
        processesServices.deleteTemplate(name)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Template $name deleted")
    }

    @GetMapping("/{type}")
    fun userProcesses(@PathVariable type: String): ResponseEntity<*> {
        val processes = processesServices.getProcesses(type)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(processes)
    }

    @GetMapping("/pending")
    fun pendingProcesses(@RequestBody email: String): ResponseEntity<*> {
        val pending = processesServices.pendingProcesses(email)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(pending)
    }

    @GetMapping("/finished")
    fun finishedProcesses(@RequestBody email: String): ResponseEntity<*> {
        val finished = processesServices.finishedProcesses(email)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(finished)
    }

    @GetMapping("{processId}/stages")
    fun processStages(@PathVariable processId: String): ResponseEntity<*> {
        val stages = processesServices.processStages(processId)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(stages)
    }

    @GetMapping("{processId}")
    fun processDetails(@PathVariable processId: String): ResponseEntity<*> {
        val details = processesServices.processDetails(processId)
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(details)
    }

    @PostMapping("{templateName}")
    fun newProcess(@PathVariable templateName: String): ResponseEntity<*> {
        val processId = processesServices.newProcessFromTemplate(templateName)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body(processId)
    }

    @DeleteMapping("{processId}")
    fun deleteProcess(@PathVariable processId: String): ResponseEntity<*> {
        processesServices.deleteProcess(processId)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Process $processId deleted")
    }

    @PutMapping("{processId}")
    fun cancelProcess(@PathVariable processId: String): ResponseEntity<*> {
        processesServices.cancelProcess(processId)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Process $processId cancelled")
    }

}