package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.RolesInterface
import isel.ps.dwp.model.Role
import org.springframework.stereotype.Component

@Component
class RoleServices(private val transactionManager: TransactionManager) : RolesInterface {
    override fun createRole(name: String, description: String): String {
        if (name.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Role Name can't be blank.")
        if (description.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Description can't be blank.")
        if (name.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Role Name length can't be bigger than 32 chars.")
        if (description.length > 140)
            throw ExceptionControllerAdvice.InvalidParameterException("Description length can't be bigger than 140 chars.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.createRole(name, description)
        }
    }

    override fun deleteRole(roleId: String) {
        if (roleId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.deleteRole(roleId)
        }
    }

    override fun editRole(roleId: String, name: String, description: String) {
        if (roleId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")
        if (name.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Role Name can't be blank.")
        if (description.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Description can't be blank.")
        if (name.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("New Role Name length can't be bigger than 32 chars.")
        if (description.length > 140)
            throw ExceptionControllerAdvice.InvalidParameterException("New Description length can't be bigger than 140 chars.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.editRole(roleId, name, description)
        }
    }

    override fun roleDetails(roleId: String): Role {
        if (roleId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.roleDetails(roleId)
        }
    }

    override fun getRoles(): List<Role> {
        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.getRoles()
        }
    }

    override fun getRoleUsers(roleId: String): List<String> {
        if (roleId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.getRoleUsers(roleId)
        }
    }

    override fun addRoleToUser(roleId: String, userId: String) {
        if (roleId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")
        if (userId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("UserId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.addRoleToUser(roleId, userId)
        }
    }

    override fun removeRoleFromUser(roleId: String, userId: String) {
        if (roleId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")
        if (userId.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("UserId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.removeRoleFromUser(roleId, userId)
        }
    }
}