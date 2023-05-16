package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.RolesInterface
import isel.ps.dwp.model.Role
import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Repository
import java.util.UUID

class RolesRepository(private val handle: Handle) : RolesInterface {


    override fun createRole(name: String, description: String): Int {
        return handle.createQuery(
            "insert into papel(nome,descricao) values (:nome,:descricao) returning id"
        )
            .bind("nome", name)
            .bind("descricao", description)
            .mapTo(Int::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.InvalidParameterException("Role Name already in use.")
    }

    override fun deleteRole(roleId: String) {
        handle.createUpdate("delete from papel where id=:roleId")
            .bind("roleId", roleId)
            .execute()
            .also { if (it == 0) throw ExceptionControllerAdvice.InvalidParameterException("Role does not exist.") }
    }

    override fun editRole(roleId: String, name: String, description: String) {
        handle.createUpdate("update papel set nome=:nome, descricao=:descricao where id=:roleId")
            .bind("nome", name)
            .bind("descricao", description)
            .bind("roleId", roleId)
            .execute()
            .also { if (it == 0) throw ExceptionControllerAdvice.InvalidParameterException("Role does not exist.") }
    }

    override fun roleDetails(roleId: String): Role {
        return handle.createQuery(
            "select * from papel where id=$roleId"
        )
            .mapTo(Role::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.InvalidParameterException("Role does not exist.")
    }

    override fun getRoles(): List<Role> {
        return handle.createQuery(
            "select * from papel"
        )
            .mapTo(Role::class.java)
            .list()
    }

    override fun getRoleUsers(roleId: String): List<String> {
        return handle.createQuery(
            "select email_utilizador from Utilizador_Papel where id_papel=$roleId"
        )
            .mapTo(String::class.java)
            .list()
    }

    override fun addRoleToUser(roleId: String, userId: String) {
        handle.createUpdate(
            "insert into Utilizador_Papel(id_papel,email_utilizador) values (:id_papel,:email_utilizador)"
        )
            .bind("id_papel", roleId)
            .bind("email_utilizador", userId)
            .execute()
            .also { if (it == 0) throw ExceptionControllerAdvice.InvalidParameterException("Role does not exist to be added to user.") }

    }

    override fun removeRoleFromUser(roleId: String, userId: String) {
        handle.createUpdate(
            "delete from Utilizador_Papel where id_papel=$roleId and email_utilizador='$userId'"
        )
            .execute()
            .also { if (it == 0) throw ExceptionControllerAdvice.InvalidParameterException("Role does not exist to be removed from user.") }
    }

}