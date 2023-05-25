package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.model.RoleModel
import isel.ps.dwp.services.RoleServices
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/roles")
class RolesController(
    private val roleServices: RoleServices = RoleServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

    @PostMapping
    fun createRole(@RequestBody role: RoleModel): ResponseEntity<*> {
        val res = roleServices.createRole(role.name, role.description)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @DeleteMapping("/{roleName}")
    fun deleteRole(@PathVariable roleName: String): ResponseEntity<*> {
        val res = roleServices.deleteRole(roleName)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @GetMapping("/{roleName}")
    fun roleDetails(@PathVariable roleName: String): ResponseEntity<*> {
        val roleDetails = roleServices.roleDetails(roleName)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleDetails)
    }

    @GetMapping
    fun rolesList(): ResponseEntity<*> {
        val roles = roleServices.getRoles()
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(roles)
    }

    @GetMapping("/{roleName}/users")
    fun usersWithRole(@PathVariable roleName: String): ResponseEntity<*> {
        val users = roleServices.getRoleUsers(roleName)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(users)
    }

    @PutMapping("/{roleName}/{userEmail}")
    fun addRoleToUser(@PathVariable roleName: String, @PathVariable userEmail: String): ResponseEntity<*> {
        val res = roleServices.addRoleToUser(roleName, userEmail)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @DeleteMapping("/{roleName}/{userEmail}")
    fun removeRoleFromUser(@PathVariable roleName: String, @PathVariable userEmail: String): ResponseEntity<*> {
        val res = roleServices.removeRoleFromUser(roleName, userEmail)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }
}