package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.*
import org.jdbi.v3.core.Handle
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.*


class ProcessesRepository(private val handle: Handle) : ProcessesInterface {

    override fun getProcesses(type: String?): List<String> {
        //TODO email must be provided, to know the processes the user created, unless user is admin
        return if (type != null)
            handle.createQuery("select id from processo where template_processo = :type")
            .bind("type", type)
            .mapTo(String::class.java)
            .list()
        else
            handle.createQuery("select id from processo where autor = :email")
                .bind("email", "") //TODO email must be provided
                .mapTo(String::class.java)
                .list()
    }

    // Retorna lista de processos pendentes do user que fez o pedido
    // Se for fornecido email, tem de ser pedido feito por parte do administrador, só este tipo de user pode aceder processos que não lhe pertencem
    override fun processesOfState(
        state: State,
        userAuth: UserAuth,
        limit: Int?,
        skip: Int?,
        userEmail: String?
    ): ProcessPage {
        val email = userEmail ?: userAuth.email

        val queryLimit = limit?.plus(1) ?: Int.MAX_VALUE

        val query = if (state == State.PENDING)
            "select id, nome, data_inicio, data_fim, estado " +
                    "from processo where autor = :email and estado = 'PENDING' " +
                    "order by data_inicio desc limit :limit offset :offset"
        else
            "select id, nome, data_inicio, data_fim, estado " +
                    "from processo where autor = :email and (estado = 'APPROVED' or estado = 'DISAPPROVED') " +
                    "order by data_fim desc limit :limit offset :offset"

        val list = handle.createQuery(query)
            .bind("email", email)
            .bind("limit", queryLimit)
            .bind("offset", skip)
            .mapTo(ProcessModel::class.java)
            .list()

        // Check if there is a previous page
        val hasPreviousPage = skip?.let { it > 0 } ?: false

        // Check if there is a next page
        val hasNextPage = list.size == queryLimit

        val pageList = list.take(limit ?: list.size)

        return ProcessPage(hasPreviousPage, hasNextPage, pageList)
    }

    override fun processStages(processId: String): List<StageModel> {
        return handle.createQuery(
                "select nome, id, estado from etapa where id_processo = :processId order by indice"
        )
            .bind("processId", processId)
            .mapTo(StageModel::class.java)
            .list()
            .ifEmpty { throw ExceptionControllerAdvice.ProcessNotFound("Processo $processId não encontrado.") }
    }

    override fun processDetails(processId: String): Process {
        return handle.createQuery("select * from processo where id = :processId")
            .bind("processId", processId)
            .mapTo(Process::class.java)
            .firstOrNull() ?: throw ExceptionControllerAdvice.ProcessNotFound("Processo $processId não encontrado.")
    }

    override fun processDocs(processId: String): List<Document> {
        return handle.createQuery(
            "select id, nome, tipo, tamanho, localizacao " +
                "from documento d join documento_processo dp on d.id = dp.id_documento " +
                "where dp.id_processo = :processId"
        )
                .bind("processId", processId)
                .mapTo(Document::class.java)
                .list()
                .ifEmpty { throw ExceptionControllerAdvice.DocumentNotFoundException("Nenhum documento encontrado para o processo $processId") }
    }

    override fun processDocsDetails(processId: String): ProcessDocInfo {
        val sql = """SELECT d.nome, SUM(d.tamanho) AS total_size
                     FROM documento_processo dp
                     JOIN documento d ON dp.id_documento = d.id
                     WHERE dp.id_processo = :processId
                     GROUP BY d.nome
                  """.trimIndent()

        val result = handle.createQuery(sql)
            .bind("processId", processId)
            .map { rs, _ -> rs.getString("nome") to rs.getInt("total_size") }
            .list()

        val names = result.map { it.first }
        val size = result.sumOf { it.second }

        return ProcessDocInfo(names, size)
    }

    override fun newProcess(templateName: String, name: String, description: String, files: List<MultipartFile>, userAuth: UserAuth): String {
        val uuid = UUID.randomUUID().toString()
        val userEmail = userAuth.email

        handle.createUpdate(
                "insert into processo(id, nome, autor, descricao, data_inicio, estado, template_processo) values (:uuid,:name,:author,:description,:startDate, 'PENDING', :template)"
        )
                .bind("uuid", uuid)
                .bind("name", name)
                .bind("author", userEmail)
                .bind("description", description)
                .bind("startDate", Timestamp(System.currentTimeMillis()))
                .bind("template", templateName)
                .execute()

        return uuid
    }

    fun associateDocToProcess(docId: String, processId: String) {
        handle.createUpdate("insert into documento_processo values (:docId, :processId)")
            .bind("docId", docId)
            .bind("processId", processId)
            .execute()
    }

    override fun deleteProcess(processId: String) {
        handle.createUpdate(
            "delete from processo where id = :processId"
        )
            .bind("processId", processId)
            .execute()
    }

    override fun cancelProcess(processId: String) {
        handle.createUpdate(
            "update processo set estado = 'CANCELLED' where id = :processId"
        )
            .bind("processId", processId)
            .execute()
    }
}