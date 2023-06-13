package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.ProcessesInterface
import isel.ps.dwp.model.*
import org.jdbi.v3.core.Handle
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.*


class ProcessesRepository(private val handle: Handle) : ProcessesInterface {

    override fun getProcesses(userAuth: UserAuth, type: String?): List<String> {
        if (userAuth.roles.contains("admin")) {
            return if (type != null) handle.createQuery("select id from processo where template_processo = :type")
                .bind("type", type).mapTo(String::class.java).list()
            else handle.createQuery("select id from processo")
                .mapTo(String::class.java).list()
        } else {
            // Se é utilizador normal vê os processos em que participa ou que criou pelo type
            val userProcessesByType = userProcessesByType(userAuth.email, type)
            val userAuthoredProcessesByType = userProcessesAuthoredByType(userAuth.email, type)

            return (userProcessesByType + userAuthoredProcessesByType).toSet().toList()
        }
    }

    // Retorna lista de processos pendentes do user que fez o pedido
    // Se for fornecido email, tem de ser pedido feito por parte do administrador, só este tipo de user pode aceder processos que não lhe pertencem
    override fun pendingProcesses(userAuth: UserAuth, userEmail: String?): List<ProcessModel> {
        if (userAuth.roles.contains("admin")) {
            return handle.createQuery(
                "select id, nome, data_inicio, data_fim, estado from processo where estado = 'PENDING' order by data_fim desc"
            ).bind("email", userAuth.email).mapTo(ProcessModel::class.java).list()
                .ifEmpty { throw ExceptionControllerAdvice.UserNotFoundException("Nenhum processo encontrado") }
        }

        // Se é utilizador normal vê os processos em que participou e já acabaram + aqueles que acabaram e ele era o autor
        val userProcessesPending = userProcessesThatHaveNotEnded(userAuth.email)
        val userAuthoredProcessesPending = processesWhereUserIsAuthorAndHaveNotEnded(userAuth.email)

        return (userProcessesPending + userAuthoredProcessesPending).toSet().toList()

    }

    // Retorna lista de processos terminados do user que fez o pedido
    // Se for fornecido email, tem de ser pedido feito por parte do administrador, só este tipo de user pode aceder processos que não lhe pertencem
    override fun finishedProcesses(userAuth: UserAuth, userEmail: String?): List<ProcessModel> {
        // Se é admin vé todos
        if (userAuth.roles.contains("admin")) {
            return handle.createQuery(
                "select id, nome, data_inicio, data_fim, estado " + "from processo where (estado = 'APPROVED' or estado = 'DISAPPROVED') order by data_fim desc"
            ).bind("email", userAuth.email).mapTo(ProcessModel::class.java).list()
                .ifEmpty { throw ExceptionControllerAdvice.UserNotFoundException("Nenhum processo encontrado") }
        }

        // Se é utilizador normal vê os processos em que participou e já acabaram + aqueles que acabaram e ele era o autor
        val userProcessesEnded = userProcessesThatHaveEnded(userAuth.email)
        val userAuthoredProcessesEnded = processesWhereUserIsAuthorAndHaveEnded(userAuth.email)

        return (userProcessesEnded + userAuthoredProcessesEnded).toSet().toList()

    }

    override fun processStages(userAuth: UserAuth, processId: String): List<StageModel> {
        if (!userAuth.roles.contains("admin") && !isUserInProcess(
                processId,
                userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado ao processo")

        return handle.createQuery(
            "select nome, id, estado from etapa where id_processo = :processId order by indice"
        ).bind("processId", processId).mapTo(StageModel::class.java).list()
            .ifEmpty { throw ExceptionControllerAdvice.ProcessNotFound("Processo $processId não encontrado.") }
    }

    override fun processDetails(userAuth: UserAuth, processId: String): Process {
        if (!userAuth.roles.contains("admin") && !isUserInProcess(
                processId,
                userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado ao processo")
        return handle.createQuery("select * from processo where id = :processId").bind("processId", processId)
            .mapTo(Process::class.java).firstOrNull()
            ?: throw ExceptionControllerAdvice.ProcessNotFound("Processo $processId não encontrado.")
    }

    override fun processDocs(userAuth: UserAuth, processId: String): List<Document> {
        if (!userAuth.roles.contains("admin") && !isUserInProcess(
                processId,
                userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado ao processo")
        return handle.createQuery(
            "select id, nome, tipo, tamanho, localizacao " + "from documento d join documento_processo dp on d.id = dp.id_documento " + "where dp.id_processo = :processId"
        ).bind("processId", processId).mapTo(Document::class.java).list()
            .ifEmpty { throw ExceptionControllerAdvice.DocumentNotFoundException("Nenhum documento encontrado para o processo $processId") }
    }

    override fun processDocsDetails(userAuth: UserAuth, processId: String): ProcessDocInfo {
        if (!userAuth.roles.contains("admin") && !isUserInProcess(
                processId,
                userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado ao processo")

        val sql = """SELECT d.nome, SUM(d.tamanho) AS total_size
                     FROM documento_processo dp
                     JOIN documento d ON dp.id_documento = d.id
                     WHERE dp.id_processo = :processId
                     GROUP BY d.nome
                  """.trimIndent()

        val result = handle.createQuery(sql).bind("processId", processId)
            .map { rs, _ -> rs.getString("nome") to rs.getInt("total_size") }.list()

        val names = result.map { it.first }
        val size = result.sumOf { it.second }

        return ProcessDocInfo(names, size)
    }

    override fun newProcess(
        templateName: String, name: String, description: String, files: List<MultipartFile>, userAuth: UserAuth
    ): String {
        val uuid = UUID.randomUUID().toString()
        val userEmail = userAuth.email

        handle.createUpdate(
            "insert into processo(id, nome, autor, descricao, data_inicio, estado, template_processo) values (:uuid,:name,:author,:description,:startDate, 'PENDING', :template)"
        ).bind("uuid", uuid).bind("name", name).bind("author", userEmail).bind("description", description)
            .bind("startDate", Timestamp(System.currentTimeMillis())).bind("template", templateName).execute()

        return uuid
    }

    fun associateDocToProcess(userAuth: UserAuth, docId: String, processId: String) {
        if (!userAuth.roles.contains("admin") && !isUserProcessAuthor(
                processId, userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado ao processo")

        handle.createUpdate("insert into documento_processo values (:docId, :processId)").bind("docId", docId)
            .bind("processId", processId).execute()
    }

    override fun deleteProcess(userAuth: UserAuth, processId: String) {
        if (!userAuth.roles.contains("admin") && !isUserProcessAuthor(
                processId,
                userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado ao processo")

        handle.createUpdate(
            "delete from processo where id = :processId"
        ).bind("processId", processId).execute()
    }

    override fun cancelProcess(userAuth: UserAuth, processId: String) {
        if (!userAuth.roles.contains("admin") && !isUserProcessAuthor(
                processId, userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado ao processo")

        handle.createUpdate(
            "update processo set estado = 'CANCELLED' where id = :processId"
        ).bind("processId", processId).execute()
    }

    private fun isUserProcessAuthor(processId: String, userEmail: String): Boolean {
        val sql = """SELECT CASE
                WHEN COUNT(*) > 0 THEN 'True'
        ELSE 'False'
        END AS IsUserAuthor
        FROM Processo
                WHERE autor = :userEmail AND id = :processId;""".trimIndent()

        // Execute the query, assuming handle is a Handle instance.
        val result =
            handle.createQuery(sql).bind("userEmail", userEmail).bind("processId", processId)
                .mapTo(String::class.java)
                .firstOrNull()
                ?: throw ExceptionControllerAdvice.UserNotAuthorizedException("O Utilizador não é o Autor do Processo")

        // Convert result to Boolean and return.
        return result.toBoolean()
    }

    private fun isUserInProcess(processId: String, userEmail: String): Boolean {
        val sql = """SELECT CASE 
                    WHEN COUNT(*) > 0 THEN 'True' 
                    ELSE 'False' 
                    END AS IsUserInProcess
                FROM Utilizador_Etapa ue 
                INNER JOIN Etapa e ON ue.id_etapa = e.id 
                WHERE ue.email_utilizador = :userEmail AND e.id_processo = :processId;""".trimIndent()

        // Execute the query, assuming handle is a Handle instance.
        val result =
            handle.createQuery(sql).bind("userEmail", userEmail).bind("processId", processId)
                .mapTo(String::class.java)
                .firstOrNull()
                ?: throw ExceptionControllerAdvice.UserNotAuthorizedException("O Utilizador não está no processo")

        // Convert result to Boolean and return.
        return result.toBoolean()
    }

    private fun userProcessesThatHaveNotEnded(userEmail: String): List<ProcessModel> {

        val sql = """SELECT DISTINCT p.nome, p.id, p.data_inicio, p.data_fim, p.estado
             FROM Processo p
             INNER JOIN Etapa e ON p.id = e.id_processo
             INNER JOIN Utilizador_Etapa ue ON e.id = ue.id_etapa
             WHERE ue.email_utilizador = :userEmail AND p.data_fim IS NULL;""".trimIndent()

        // Execute the query, assuming handle is a Handle instance.
        return handle.createQuery(sql)
            .bind("userEmail", userEmail)
            .mapTo(ProcessModel::class.java)
            .list() ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não está em nenhum processo")
    }

    private fun userProcessesThatHaveEnded(userEmail: String): List<ProcessModel> {

        val sql = """SELECT DISTINCT p.nome, p.id, p.data_inicio, p.data_fim, p.estado
             FROM Processo p
             INNER JOIN Etapa e ON p.id = e.id_processo
             INNER JOIN Utilizador_Etapa ue ON e.id = ue.id_etapa
             WHERE ue.email_utilizador = :userEmail AND p.data_fim IS NOT NULL;""".trimIndent()


        // Execute the query, assuming handle is a Handle instance.
        return handle.createQuery(sql)
            .bind("userEmail", userEmail)
            .mapTo(ProcessModel::class.java)
            .list() ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não está em nenhum processo")
    }

    private fun processesWhereUserIsAuthor(userEmail: String): List<ProcessModel> {

        val sql = """SELECT nome,id, data_inicio, data_fim, estado
            FROM Processo 
            WHERE autor = :userEmail;
            """.trimIndent()

        // Execute the query, assuming handle is a Handle instance.
        return handle.createQuery(sql)
            .bind("userEmail", userEmail)
            .mapTo(ProcessModel::class.java)
            .list() ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não é Autor de nenhum Processo")
    }

    private fun processesWhereUserIsAuthorAndHaveEnded(userEmail: String): List<ProcessModel> {

        val sql = """SELECT nome,id, data_inicio, data_fim, estado
            FROM Processo 
            WHERE autor = :userEmail AND data_fim IS NOT NULL;
            """.trimIndent()

        // Execute the query, assuming handle is a Handle instance.
        return handle.createQuery(sql)
            .bind("userEmail", userEmail)
            .mapTo(ProcessModel::class.java)
            .list() ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não é Autor de nenhum Processo")
    }

    private fun processesWhereUserIsAuthorAndHaveNotEnded(userEmail: String): List<ProcessModel> {

        val sql = """SELECT nome,id, data_inicio, data_fim, estado
            FROM Processo 
            WHERE autor = :userEmail AND data_fim IS NULL;
            """.trimIndent()

        // Execute the query, assuming handle is a Handle instance.
        return handle.createQuery(sql)
            .bind("userEmail", userEmail)
            .mapTo(ProcessModel::class.java)
            .list() ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não é Autor de nenhum Processo")
    }

    private fun userProcessesByType(email: String, type: String?): List<String> {
        val sql: String
        if (type != null) {
            sql = """"SELECT DISTINCT p.id
             FROM Processo p
             INNER JOIN Etapa e ON p.id = e.id_processo
             INNER JOIN Utilizador_Etapa ue ON e.id = ue.id_etapa
             WHERE ue.email_utilizador = :userEmail AND p.template_processo = :type;""".trimIndent()
            return handle.createQuery(sql)
                .bind("userEmail", email)
                .bind("type", type)
                .mapTo(String::class.java)
                .list()
                ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não está em nenhum Processo do $type")
        } else {
            sql = """"SELECT DISTINCT p.id
             FROM Processo p
             INNER JOIN Etapa e ON p.id = e.id_processo
             INNER JOIN Utilizador_Etapa ue ON e.id = ue.id_etapa
             WHERE ue.email_utilizador = :userEmail""".trimIndent()
            return handle.createQuery(sql)
                .bind("userEmail", email)
                .mapTo(String::class.java)
                .list()
                ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não está em nenhum Processo")
        }
    }

    private fun userProcessesAuthoredByType(email: String, type: String?): List<String> {
        val sql: String
        if (type == null) {
            sql = """select id from processo where autor = :email""".trimIndent()
            return handle.createQuery(sql)
                .bind("email", email)
                .mapTo(String::class.java)
                .list()
                ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não é Autor de nenhum Processo")
        } else {
            sql = """select id from processo where autor = :email AND template_processo = :type;""".trimIndent()
            return handle.createQuery(sql)
                .bind("email", email)
                .bind("type", type)
                .mapTo(String::class.java)
                .list()
                ?: throw ExceptionControllerAdvice.ProcessNotFound("O Utilizador não está em nenhum Processo do $type")
        }
    }

}