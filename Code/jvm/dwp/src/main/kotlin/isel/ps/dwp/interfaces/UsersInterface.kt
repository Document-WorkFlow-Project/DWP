package isel.ps.dwp.interfaces

import isel.ps.dwp.model.User
import isel.ps.dwp.model.UserDetails

interface UsersInterface {

    fun checkBearerToken(bearerToken: String): String?

    fun usersList(): List<String>

    fun login(email: String, password: String): String?

    /**
     * Apagar utilizador (função de administrador)
     */
    fun deleteUser(email: String)

    /**
     * Detalhes de um utilizador
     */
    fun userDetails(email: String): UserDetails

    /**
     * Atualizar perfil (nome, email, password) (função de administrador ou utilizador associado)
     */
    fun updateProfile(email: String, hashPassword: String, newPass: String)
    fun checkUser(email: String): User?
}
