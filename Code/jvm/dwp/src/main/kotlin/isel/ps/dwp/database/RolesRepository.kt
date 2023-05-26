package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.RolesInterface
import isel.ps.dwp.model.Role
import org.jdbi.v3.core.Handle

class RolesRepository(private val handle: Handle) : RolesInterface {


    override fun createRole(name: String, description: String): Int {
        return handle.createUpdate(
            "insert into papel (nome, descricao) values (:nome, :descricao)"
        )
            .bind("nome", name)
            .bind("descricao", description)
            .execute()
            .also { if (it == 0) throw ExceptionControllerAdvice.InvalidParameterException("Nome do papel já existe.") }
    }

    override fun deleteRole(roleName: String) {
        handle.createUpdate("delete from papel where nome = :roleName")
            .bind("roleName", roleName)
            .execute()
            .also { if (it == 0) throw ExceptionControllerAdvice.InvalidParameterException("Papel não existe.") }
    }

    override fun roleDetails(roleName: String): Role {
        return handle.createQuery(
            "select * from papel where nome = :roleName"
        )
            .bind("roleName", roleName)
            .mapTo(Role::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.InvalidParameterException("Papel não existe.")
    }

    override fun getRoles(): List<String> {
        return handle.createQuery("select nome from papel")
            .mapTo(String::class.java)
            .list()
    }

    override fun getRoleUsers(roleName: String): List<String> {
        // Check if role exists
        roleDetails(roleName)
        
        return handle.createQuery(
            "select email_utilizador from Utilizador_Papel where papel = :roleName"
        )
            .bind("roleName", roleName)
            .mapTo(String::class.java)
            .list()
    }

    override fun addRoleToUser(roleName: String, userEmail: String) {
        // Check if role exists
        roleDetails(roleName)

        handle.createUpdate(
            "insert into Utilizador_Papel (papel, email_utilizador) values (:id_papel, :email_utilizador)"
        )
            .bind("id_papel", roleName)
            .bind("email_utilizador", userEmail)
            .execute()
            .also { if (it == 0) throw ExceptionControllerAdvice.InvalidParameterException("Erro ao adicionar papel.") }
    }

    override fun removeRoleFromUser(roleName: String, userEmail: String) {
        // Check if role exists
        roleDetails(roleName)

        handle.createUpdate(
            "delete from Utilizador_Papel where papel = :roleName and email_utilizador = :userEmail"
        )
            .bind("roleName", roleName)
            .bind("userEmail", userEmail)
            .execute()
            .also { if (it == 0) throw ExceptionControllerAdvice.InvalidParameterException("Erro ao remover papel.") }
    }

}