package isel.ps.dwp.database

import isel.ps.dwp.interfaces.RolesInterface
import isel.ps.dwp.model.Role
import org.jdbi.v3.core.Handle

class RolesRepository(handle: Handle) : RolesInterface {
    override fun createRole(roleId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteRole(roleId: String) {
        TODO("Not yet implemented")
    }

    override fun editRole(roleId: String) {
        TODO("Not yet implemented")
    }

    override fun roleDetails(roleId: String): Role {
        TODO("Not yet implemented")
    }

    override fun getRoles(): List<Role> {
        TODO("Not yet implemented")
    }

    override fun getRoleUsers(roleId: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun addRoleToUser(roleId: String, userId: String) {
        TODO("Not yet implemented")
    }

    override fun removeRoleFromUser(roleId: String, userId: String) {
        TODO("Not yet implemented")
    }

}