package isel.ps.dwp.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp
import java.util.*

data class User(
    val email: String,
    val nome: String,
    val authToken: String,
    val pass: String
)

data class UserAuth(
    val email: String,
    val role: String
)

data class Role(
    val nome: String,
    val descricao: String
)

data class Process(
    val id: String,
    val nome: String,
    val descricao: String,
    val template_processo: String, // type of process template
    var estado: String,    // 'PENDING', 'APPROVED', 'DISAPPROVED'
    val autor: String,   // author of the process
    val data_inicio: Timestamp,  // date of creation
    val data_fim: Timestamp?    // date of conclusion
)

data class Stage(
    val id: String,
    val id_processo: String,
    val indice: Int,
    val modo: String,
    val nome: String,
    val descricao: String,
    val data_inicio: Timestamp?,     // date of creation
    val data_fim: Timestamp?,      // date of conclusion
    val estado: String,
    val prazo: Int        // stage duration in days
)

data class Comment(
    val id: String,
    val id_etapa: String,
    val data: Timestamp,
    val texto: String,
    val remetente: String
)

// Representação de um documento guardado na base de dados
data class Document(
    val id: String,
    val nome: String,
    val tipo: String,
    val tamanho: Long,
    val localizacao: String
)

// Representação de um template guardado na base de dados
data class Template(
    val nome: String,
    val descricao: String,
    val path: String
)

data class EmailDetails(
    val recipient: String,
    val msgBody: String,
    val subject: String
)

data class EmailSchedule(
    val email: EmailDetails,
    val period: Long
)

data class StageInfo(
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


