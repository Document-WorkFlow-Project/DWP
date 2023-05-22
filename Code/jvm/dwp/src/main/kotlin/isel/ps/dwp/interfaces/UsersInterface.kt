package isel.ps.dwp.interfaces

import isel.ps.dwp.controllers.UserDetails
import isel.ps.dwp.model.User

interface UsersInterface {

    fun checkBearerToken(bearerToken: String): String?

    fun login(email: String, password: String): String?

    /**
     * Criar utilizador (função de administrador)
     */
    fun register(email: String, name: String, password: String): String

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
