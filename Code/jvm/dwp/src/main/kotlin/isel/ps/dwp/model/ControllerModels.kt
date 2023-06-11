package isel.ps.dwp.model

import java.sql.Timestamp

data class SignInModel(val email: String, val password: String)

data class RegisterModel(val email: String, val name: String)

data class UserDetails(val nome: String, val email: String)

data class RoleModel(val name: String, val description: String)

data class ProcessModel(
        val nome: String,
        val id: String,
        val data_inicio: Timestamp,
        val data_fim: Timestamp?,
        val estado: String
)

data class UserAuth(
        val email: String,
        val nome: String,
        val roles: List<String>
)

data class NewComment(val text: String)

data class Signature(val email_utilizador: String, val assinatura: Boolean?, val data_assinatura: Timestamp?)

data class StageModel(val nome: String, val id: String, val estado: String)

data class ProcessDocInfo(val names: List<String>, val size: Int)
