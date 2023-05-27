package isel.ps.dwp.controllers

import isel.ps.dwp.services.ProcessServices
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/processes")
class ProcessesController (
    private val processesServices: ProcessServices
) {

    @GetMapping
    fun userProcesses(@RequestParam type: String?): ResponseEntity<*> {
        val processes = processesServices.getProcesses(type)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(processes)
    }

    @GetMapping("/pending")
    fun pendingProcesses(@RequestBody email: String?): ResponseEntity<*> {
        val pending = processesServices.pendingProcesses(email)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(pending)
    }

    @GetMapping("/finished")
    fun finishedProcesses(@RequestBody email: String?): ResponseEntity<*> {
        val finished = processesServices.finishedProcesses(email)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(finished)
    }

    @GetMapping("/{processId}/stages")
    fun processStages(@PathVariable processId: String): ResponseEntity<*> {
        val stages = processesServices.processStages(processId)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(stages)
    }

    @GetMapping("/{processId}")
    fun processDetails(@PathVariable processId: String): ResponseEntity<*> {
        val details = processesServices.processDetails(processId)
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(details)
    }

    @PostMapping
    fun newProcess(@RequestParam templateName: String, @RequestParam name: String, @RequestParam description: String, @RequestParam("file") files: List<MultipartFile>): ResponseEntity<*> {
        val processId = processesServices.newProcess(templateName, name, description, files)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body(processId)
    }

    @DeleteMapping("/{processId}")
    fun deleteProcess(@PathVariable processId: String): ResponseEntity<*> {
        processesServices.deleteProcess(processId)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Process $processId deleted")
    }

    @PutMapping("/{processId}")
    fun cancelProcess(@PathVariable processId: String): ResponseEntity<*> {
        processesServices.cancelProcess(processId)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Process $processId cancelled")
    }

}

//TODO averiguar se o utilizador não assina uma etapa que ainda não começou
//TODO pending processes não funciona se fornecer email