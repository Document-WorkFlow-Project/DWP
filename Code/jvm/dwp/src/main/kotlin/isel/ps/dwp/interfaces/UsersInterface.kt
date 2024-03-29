package isel.ps.dwp.interfaces

import isel.ps.dwp.model.User
import isel.ps.dwp.model.UserAuth

interface UsersInterface {

    fun checkBearerToken(bearerToken: String): UserAuth

    fun usersList(): List<String>

    fun login(email: String, password: String): String?

    /**
     * Apagar utilizador (função de administrador)
     */
    fun deleteUser(email: String)

    /**
     * Detalhes de um utilizador
     */
    fun userDetails(email: String): UserAuth

    /**
     * Atualizar perfil (nome, email, password) (função de administrador ou utilizador associado)
     */
    fun updateCredentials(email: String, oldPass: String, newPass: String)

    fun checkUser(email: String): User?
}
