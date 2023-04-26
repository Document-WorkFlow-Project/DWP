package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.services.DocumentServices
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream

@RestController
@RequestMapping("/docs")
class DocumentsController (
    private val documentServices: DocumentServices = DocumentServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

    //TODO associate with process
    @PostMapping("")
    fun fileUpload(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        val savedFileId = documentServices.uploadDoc(file)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(savedFileId)
    }

    @GetMapping("/{id}")
    fun fileDownload(@PathVariable id: String, response: HttpServletResponse): ResponseEntity<out Any> {
        /*return when (val res = documentServices.downloadDoc(id).getOrNull()) {
            is Resource ->
                ResponseEntity.status(200)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${id}")
                    .body<Resource>(res)
            else ->
                throw ExceptionControllerAdvice.DocumentNotFoundException("Document $id not found.")
        }
        */
        val docDetails = documentServices.documentDetails(id) ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Document $id not found.")
        val fileObj = File(docDetails.localizacao)

        return if (fileObj.exists()) {
            response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
            response.setHeader("Content-Disposition", "attachment; filename=${fileObj.name}")

            val inputStream = FileInputStream(fileObj)
            FileCopyUtils.copy(inputStream, response.outputStream)
            inputStream.close()

            response.flushBuffer()
            ResponseEntity.ok().build()
        } else
            throw ExceptionControllerAdvice.DocumentNotFoundException("Document $id not found.")
    }

    @GetMapping("/{id}/details")
    fun documentDetails(@PathVariable id: String): ResponseEntity<*> {
        val details = documentServices.documentDetails(id)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(details)
    }
}