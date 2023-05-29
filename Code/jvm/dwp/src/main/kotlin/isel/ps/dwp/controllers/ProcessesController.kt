package isel.ps.dwp.controllers

import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.services.ProcessServices
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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

    @GetMapping("/{processId}/docs")
    fun processDocuments(@PathVariable processId: String, response: HttpServletResponse): ResponseEntity<out Any> {
        val docDetailsList = processesServices.processDocs(processId)

        // Create temporary zip file to hold all the files
        val zipFile = File.createTempFile("${processId}_documents", ".zip")
        val zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))

        try {
            for (document in docDetailsList) {
                val file = File(document.localizacao)
                val fileInputStream = FileInputStream(file)

                // Add each file to the zip file
                val entry = ZipEntry(document.nome)
                zipOutputStream.putNextEntry(entry)

                // Write the file content to the zip
                val buffer = ByteArray(1024)
                var len: Int
                while (fileInputStream.read(buffer).also { len = it } > 0) {
                    zipOutputStream.write(buffer, 0, len)
                }

                zipOutputStream.closeEntry()
                fileInputStream.close()
            }

            // Close the zip stream
            zipOutputStream.close()

            val resource = ByteArrayResource(Files.readAllBytes(zipFile.toPath()))

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_OCTET_STREAM
            headers.contentLength = resource.contentLength()
            headers.add("Content-Disposition", "attachment; filename=\"${processId}_documents.zip\"")

            return ResponseEntity(resource, headers, HttpStatus.OK)
        } finally {
            // Delete the temporary zip file
            zipFile.delete()
        }
    }

    @PostMapping
    fun newProcess(
            @RequestParam templateName: String,
            @RequestParam name: String,
            @RequestParam description: String,
            @RequestParam("file") files: List<MultipartFile>,
            user: UserAuth
    ): ResponseEntity<*> {
        val processId = processesServices.newProcess(templateName, name, description, files, user)
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

//TODO pending processes não funciona se fornecer email
//TODO sign stage tem de distinguir quem assinou para não ficarem logo todas assinadas ou não falsificarem assinatura
