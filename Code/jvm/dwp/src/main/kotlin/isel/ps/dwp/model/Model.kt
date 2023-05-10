package isel.ps.dwp.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonGetter
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

data class EmailDetails (
    val recipient: String,
    val msgBody: String,
    val subject: String
)

// Representação de um ficheiro template json na base de dados
data class ProcessTemplateFile (
    val nome: String,
    val descricao: String,
    val path: String
)

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ProcessTemplate(
    @JsonProperty("nome") val nome: String,
    @JsonProperty("autor") val autor: String,
    @JsonProperty("descricao") val descricao: String,
    @JsonProperty("data_inicio") val data_inicio: String,
    @JsonProperty("data_fim") val data_fim: String?,
    @JsonProperty("prazo") val prazo: String,
    @JsonProperty("estado") val estado: String,
    @JsonProperty("template_processo") val template_processo: String,
    @JsonProperty("etapas") val etapas: List<StageTemplate> = listOf()
)

data class StageTemplate(
    @JsonProperty("nome") val nome: String ,
    @JsonProperty("modo") val modo: String,
    @JsonProperty("responsavel") val responsavel: List<String> = listOf(),
    @JsonProperty("descricao") val descricao: String ,
    @JsonProperty("data_inicio") val data_inicio: String ,
    @JsonProperty("data_fim") val data_fim: String? ,
    @JsonProperty("prazo") val prazo: String,
    @JsonProperty("estado") val estado: String
)


