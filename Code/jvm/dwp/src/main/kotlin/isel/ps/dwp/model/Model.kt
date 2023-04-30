package isel.ps.dwp.model

import java.util.*

data class User (
    val email: String,
    val name: String,
    val token: String
)

data class Role (
    val roleId: String,
    val name: String,
    val description: String
)

data class Process (
    val processId: String,
    val name: String,
    val description: String,
    val type: String, // type of process template
    var state: String, // pending, finished with success/failure
    val responsible: String, // author of the process
    val startDate: Date, // date of creation
    var endDate: Date, // date of conclusion
    val duration: Int
)

data class Stage (
    val stageId: String,
    var name: String,
    var description: String,
    var state: String,
    val responsible: String, // stage responsibles
    var duration: Int // stage duration in days
)

data class Comment (
    val commentId: String,
    val stageId: String,
    val date: Date,
    val text: String,
    val sender: String
)

data class Document (
    val id: String,
    val nome: String,
    val tipo: String,
    val tamanho: Long,
    val localizacao: String
)

data class EmailDetails (
    val recipient: String,
    val msgBody: String,
    val subject: String
)

data class ProcessTemplate (
    var nome: String,
    val descricao: String,
    val path: String
)