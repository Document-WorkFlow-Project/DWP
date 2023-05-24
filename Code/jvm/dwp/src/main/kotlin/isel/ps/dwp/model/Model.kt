package isel.ps.dwp.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class User (
    val email: String,
    val name: String,
    val token: String
)

data class Role (
    val roleId: Int,
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
    val endDate: Date,   /*TODO: Pode ser Date?, nullable*/    // date of conclusion
    val duration: Int
)

data class Stage (
    val stageId: String,
    val name: String,
    val mode: String,
    val description: String,
    val state: String,
    val responsible: String, // stage responsibles
    val duration: Int // stage duration in days
)

data class Comment (
    val commentId: String,
    val stageId: String,
    val date: Date,
    val text: String,
    val sender: String
)

// Representação de um documento guardado na base de dados
data class Document (
    val id: String,
    val nome: String,
    val tipo: String,
    val tamanho: Long,
    val localizacao: String
)

// Representação de um template guardado na base de dados
data class Template (
    val nome: String,
    val descricao: String,
    val path: String
)

data class EmailDetails (
    val recipient: String,
    val msgBody: String,
    val subject: String
)

data class EmailSchedule (
    val email: EmailDetails,
    val period: Long
)

data class StageInfo (
    val id: String,
    val name: String
)

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ProcessTemplate(
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("stages") val stages: List<StageTemplate>
)

data class StageTemplate(
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("responsibles") val responsible: List<String>,
    @JsonProperty("duration") val duration: Int,
    @JsonProperty("mode") val mode: String
)


