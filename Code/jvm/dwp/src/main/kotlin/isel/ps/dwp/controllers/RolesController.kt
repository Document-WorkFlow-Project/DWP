package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
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

    @PostMapping("/")
    fun createRole(@RequestBody role: RoleCreateModel, user: UserDetails): ResponseEntity<*> {
        val res = roleServices.createRole(role.name, role.description)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @DeleteMapping("/")
    fun deleteRole(@RequestParam roleId: String, user: UserDetails): ResponseEntity<*> {
        val res = roleServices.deleteRole(roleId)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @GetMapping("/details")
    fun roleDetails(@RequestParam roleId: String, user: UserDetails): ResponseEntity<*> {
        val res = roleServices.roleDetails(roleId)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @GetMapping("/")
    fun rolesList(user: UserDetails): ResponseEntity<*> {
        val res = roleServices.getRoles()
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @GetMapping("/role")
    fun usersWithRole(@RequestParam roleId: String, user: UserDetails): ResponseEntity<*> {
        val res = roleServices.getRoleUsers(roleId)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @PutMapping("/user")
    fun addRoleToUser(@RequestBody roleToAdd: RoleInPlayerModel, user: UserDetails): ResponseEntity<*> {
        val res = roleServices.addRoleToUser(roleToAdd.roleId, roleToAdd.userId)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }

    @DeleteMapping("/user")
    fun removeRoleFromUser(@RequestBody roleToDelete: RoleInPlayerModel, user: UserDetails): ResponseEntity<*> {
        val res = roleServices.removeRoleFromUser(roleToDelete.roleId, roleToDelete.userId)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res)
    }
}