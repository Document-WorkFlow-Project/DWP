package isel.ps.dwp.controllers

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.http.pipeline.Admin
import isel.ps.dwp.model.RoleModel
import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.services.RoleServices
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/roles")
class RolesController(
    private val roleServices: RoleServices
) {

    @PostMapping
    @Admin
    fun createRole(@RequestBody role: RoleModel, user: UserAuth): ResponseEntity<*> {
        val res = roleServices.createRole(role.name, role.description)
        return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res)
    }

    @DeleteMapping("/{roleName}")
    @Admin
    fun deleteRole(@PathVariable roleName: String, user: UserAuth): ResponseEntity<*> {
        val res = roleServices.deleteRole(roleName)
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(res)
    }

    @GetMapping("/{roleName}")
    fun roleDetails(@PathVariable roleName: String, user: UserAuth): ResponseEntity<*> {
        val roleDetails = roleServices.roleDetails(roleName)
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(roleDetails)
    }

    @GetMapping
    @Admin
    fun rolesList(user: UserAuth): ResponseEntity<*> {
        val roles = roleServices.getRoles()
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(roles)
    }

    @GetMapping("/{roleName}/users")
    @Admin
    fun usersWithRole(@PathVariable roleName: String, user: UserAuth): ResponseEntity<*> {
        val users = roleServices.getRoleUsers(roleName)
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(users)
    }

    @PutMapping("/{roleName}/{userEmail}")
    @Admin
    fun addRoleToUser(
        @PathVariable roleName: String, @PathVariable userEmail: String, user: UserAuth
    ): ResponseEntity<*> {
        val res = roleServices.addRoleToUser(roleName, userEmail)
        return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res)
    }

    @DeleteMapping("/{roleName}/{userEmail}")
    @Admin
    fun removeRoleFromUser(
        @PathVariable roleName: String, @PathVariable userEmail: String, user: UserAuth
    ): ResponseEntity<*> {
        val res = roleServices.removeRoleFromUser(roleName, userEmail)
        return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(res)
    }
}