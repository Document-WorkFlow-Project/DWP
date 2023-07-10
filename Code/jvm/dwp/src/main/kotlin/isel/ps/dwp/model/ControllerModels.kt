package isel.ps.dwp.model

import java.sql.Timestamp

data class SignInModel(val email: String, val password: String)

data class RegisterModel(val email: String, val name: String)

data class UpdateCredentialsModel(val password: String, val newPassword: String)

data class UserDetails(val nome: String, val email: String)

data class RoleModel(val name: String, val description: String)

data class ProcessPage(
        val hasPrevious: Boolean,
        val hasNext: Boolean,
        val list: List<ProcessModel>
)

data class TaskPage(
        val hasPrevious: Boolean,
        val hasNext: Boolean,
        val list: List<StageDetails>
)

enum class State { PENDING, FINISHED }

data class ProcessModel(
        val nome: String,
        val id: String,
        val data_inicio: Timestamp,
        val data_fim: Timestamp?,
        val descricao: String
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

data class TemplateResponse(
        val name: String,
        val description: String,
        val stages: List<StageTemplate>
)

data class TemplateWStatus(
        val nome: String,
        val ativo: Boolean
)
