package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Role

interface RolesInterface {

    fun createRole(name: String, description: String): Int

    fun deleteRole(roleId: String)

    fun editRole(roleId: String, name: String, description: String)

    fun roleDetails(roleId: String): Role

    fun getRoles(): List<Role>

    fun getRoleUsers(roleId: String): List<String>

    fun addRoleToUser(roleId: String, userId: String)

    fun removeRoleFromUser(roleId: String, userId: String)
}