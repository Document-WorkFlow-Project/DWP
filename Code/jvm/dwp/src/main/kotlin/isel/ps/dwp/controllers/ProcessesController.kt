package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.services.ProcessServices
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/process")
class ProcessesController (
    private val processesServices: ProcessServices = ProcessServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

}