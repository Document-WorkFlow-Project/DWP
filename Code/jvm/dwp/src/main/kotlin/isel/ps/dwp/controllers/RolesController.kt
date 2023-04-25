package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.services.RoleServices
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roles")
class RolesController (
    private val roleServices: RoleServices = RoleServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

}