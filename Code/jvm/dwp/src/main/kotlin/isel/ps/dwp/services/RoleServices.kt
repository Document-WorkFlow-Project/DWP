package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.RolesInterface
import isel.ps.dwp.model.Role
import org.springframework.stereotype.Service

@Service
class RoleServices(private val transactionManager: TransactionManager) : RolesInterface {
    override fun createRole(name: String, description: String): Int {
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

    override fun deleteRole(roleName: String) {
        if (roleName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.deleteRole(roleName)
        }
    }

    override fun roleDetails(roleName: String): Role {
        if (roleName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.roleDetails(roleName)
        }
    }

    override fun getRoles(): List<String> {
        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.getRoles()
        }
    }

    override fun getRoleUsers(roleName: String): List<String> {
        if (roleName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.getRoleUsers(roleName)
        }
    }

    override fun addRoleToUser(roleName: String, userEmail: String) {
        if (roleName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")
        if (userEmail.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("UserId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.addRoleToUser(roleName, userEmail)
        }
    }

    override fun removeRoleFromUser(roleName: String, userEmail: String) {
        if (roleName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("RoleId can't be blank.")
        if (userEmail.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("UserId can't be blank.")

        return transactionManager.run {
            val rolesRepo = it.rolesRepository
            rolesRepo.removeRoleFromUser(roleName, userEmail)
        }
    }
}