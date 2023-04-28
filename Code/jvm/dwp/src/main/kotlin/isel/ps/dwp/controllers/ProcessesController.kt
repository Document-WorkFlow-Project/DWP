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
@RequestMapping("/process")
class ProcessesController (
    private val processesServices: ProcessServices = ProcessServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

    @PostMapping("/template")
    fun newTemplate(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        val savedFileId = processesServices.addTemplate(file)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedFileId)
    }

    @DeleteMapping("/template/{name}")
    fun deleteTemplate(@PathVariable name: String): ResponseEntity<*> {
        val res = processesServices.deleteTemplate(name)
        return ResponseEntity
                .status(201)
                .contentType(MediaType.APPLICATION_JSON)
                .body(res)
    }
}