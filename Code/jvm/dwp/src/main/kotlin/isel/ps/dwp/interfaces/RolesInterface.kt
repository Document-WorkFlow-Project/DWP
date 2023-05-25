package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Role

interface RolesInterface {

    fun createRole(name: String, description: String): Int

    fun deleteRole(roleName: String)

    fun roleDetails(roleName: String): Role

    fun getRoles(): List<String>

    fun getRoleUsers(roleName: String): List<String>

    fun addRoleToUser(roleName: String, userEmail: String)

    fun removeRoleFromUser(roleName: String, userEmail: String)
}