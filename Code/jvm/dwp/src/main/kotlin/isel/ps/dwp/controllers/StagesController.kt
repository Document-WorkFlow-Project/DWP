package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.services.StageServices
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stages")
class StagesController (
    private val stageServices: StageServices = StageServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

}