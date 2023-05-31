package isel.ps.dwp.model

import java.sql.Timestamp

data class SignInModel(val email: String, val password: String)

data class RegisterModel(val email: String, val name: String)

data class Token(val token: String)

data class UserDetails(val nome: String, val email: String)

data class UserDetailsWithRoles(val email: String, val nome: String,  val roles : String)

data class RoleModel(val name: String, val description: String)

data class ProcessModel(val nome: String, val id: String)

data class UserAuth(
        val email: String,
        val roles: List<String>
)

data class NewComment(val text: String)

data class Signature(val email_utilizador: String, val assinatura: Boolean?, val data_assinatura: Timestamp?)
