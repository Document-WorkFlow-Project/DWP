package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.services.DocumentServices
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/docs")
class DocumentsController (
    private val documentServices: DocumentServices = DocumentServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

}