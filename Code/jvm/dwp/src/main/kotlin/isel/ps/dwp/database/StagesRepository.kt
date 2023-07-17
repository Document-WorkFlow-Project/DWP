package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.*
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp
import java.util.*

class StagesRepository(private val handle: Handle) : StagesInterface {

    /** --------------------------- Stages -------------------------------**/

    /**
     * Retorna os detalhes de uma etapa
     */
    override fun stageDetails(stageId: String, userAuth: UserAuth): Stage {
        if (!userAuth.roles.contains("admin") && !isUserInStage(
                stageId, userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")
        return handle.createQuery("SELECT * FROM Etapa WHERE id = :stageId")
            .bind("stageId", stageId)
            .mapTo(Stage::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.StageNotFound("Etapa não encontrada.")
    }

    /**
     * Retorna a próxima etapa pendente de um processo ou null caso não existam mais etapas pendentes
     */
    private fun nextPendingStage(processId: String): StageInfo? {
        return handle.createQuery("select id, prazo from etapa where id_processo = :processId and estado = 'PENDING' order by indice")
            .bind("processId", processId)
            .mapTo<StageInfo>()
            .firstOrNull()
    }

    private fun processEnded(processId: String): Boolean {
        return handle.createQuery("select estado from processo where id = :processId")
            .bind("processId", processId)
            .mapTo<String>()
            .first() != "PENDING"
    }

    /**
     * Inicia a próxima etapa pendente de um processo, atualizando a sua data de inicio.
     * Caso não existam mais etapas pendentes retorna null.
     */
    override fun startNextPendingStage(stageId: String): StageInfo? {
        val processId = findProcessFromStage(stageId)

        if (processEnded(processId))
            throw ExceptionControllerAdvice.InvalidParameterException("Este processo já terminou.")

        val nextStage = nextPendingStage(processId)

        // Não existem mais etapas pendentes, fim do processo
        if (nextStage == null) {
            handle.createUpdate("update processo set estado = 'APPROVED', data_fim = :endDate where id = :processId")
                .bind("endDate", Timestamp(System.currentTimeMillis()))
                .bind("processId", processId)
                .execute()

            return null
        }

        handle.createUpdate("UPDATE etapa SET data_inicio = :startDate WHERE id = :stageId")
            .bind("startDate", Timestamp(System.currentTimeMillis()))
            .bind("stageId", nextStage.id)
            .execute()

        return nextStage
    }

    /**
     * Guarda id de notificação na base de dados
     */
    fun addNotificationId(userEmail: String, notificationId: String, stageId: String, startNotificationDate: Timestamp) {
        handle.createUpdate(
            "UPDATE utilizador_etapa " +
                    "SET id_notificacao = :notificationId, data_inicio_notif = :startDate " +
                    "WHERE id_etapa = :stageId and email_utilizador = :email "
        )
            .bind("notificationId", notificationId)
            .bind("stageId", stageId)
            .bind("startDate", startNotificationDate)
            .bind("email", userEmail)
            .execute()
    }

    fun findProcessFromStage(stageId: String): String {
        return handle.createQuery("select id_processo from etapa where id = :stageId")
            .bind("stageId", stageId)
            .mapTo<String>()
            .firstOrNull() ?: throw ExceptionControllerAdvice.StageNotFound("Processo não encontrado.")
    }

    /**
     * Retorna a lista de emails dos utilizadores resposáveis por esta etapa
     */
    fun stageResponsible(stageId: String): List<String> {
        return handle.createQuery("select email_utilizador from utilizador_etapa where id_etapa = :stageId")
            .bind("stageId", stageId)
            .mapTo<String>()
            .list()
    }

    /**
     * Retorna a lista de ids de todas as notificações agendadas
     */
    fun getStageNotifications(stageId: String, email: String?, userAuth: UserAuth): List<String> {
        return if (email == null) {
            // Retornar o id de todas as notificações ativas associadas a esta etapa
            handle.createQuery("select id_notificacao from utilizador_etapa where id_etapa = :stageId")
                .bind("stageId", stageId)
                .mapTo<String>()
                .list()
        } else {
            // Retornar o id das notificações ativas associadas a esta etapa, de um utilizador em especifico
            handle.createQuery("select id_notificacao from utilizador_etapa where id_etapa = :stageId and email_utilizador = :email")
                .bind("stageId", stageId)
                .bind("email", email)
                .mapTo<String>()
                .list()
        }
    }

    fun getAllActiveStageNotifications(): List<StageNotification> {
        return handle.createQuery(
            "SELECT ue.id_notificacao, ue.data_inicio_notif, ue.email_utilizador, ue.id_etapa " +
                    "FROM utilizador_etapa ue " +
                    "join etapa e on ue.id_etapa = e.id " +
                    "WHERE (ue.assinatura is null AND ue.id_notificacao is not null and e.estado = 'PENDING')"
        )
            .mapTo<StageNotification>()
            .list()
    }

    override fun signStage(stageId: String, approve: Boolean, userAuth: UserAuth) {
        if (!isUserInStage(
                stageId,
                userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não está associado à etapa")

        if (handle.createQuery("select assinatura from utilizador_etapa where email_utilizador = :email")
                .bind("email", userAuth.email)
                .mapTo<Boolean>()
                .singleOrNull() != null
        )
            throw ExceptionControllerAdvice.InvalidParameterException("O utilizador já participou nesta etapa.")

        val processId = findProcessFromStage(stageId)

        if (processEnded(processId))
            throw ExceptionControllerAdvice.InvalidParameterException("Este processo já terminou.")

        val nextPendingStage = nextPendingStage(processId) ?: throw ExceptionControllerAdvice.InvalidParameterException("Este processo já terminou.")

        if (nextPendingStage.id != stageId)
            throw ExceptionControllerAdvice.InvalidParameterException("Esta não é a etapa currente, ainda não pode assinar.")

        val date = Timestamp(System.currentTimeMillis())

        handle.createUpdate(
            "UPDATE utilizador_etapa SET assinatura = :value, data_assinatura = :signDate WHERE email_utilizador = :email AND id_etapa = :stageId"
        )
            .bind("email", userAuth.email)
            .bind("value", approve)
            .bind("signDate", date)
            .bind("stageId", stageId)
            .execute()

        // Em caso de reprovação as etapas seguintes e o processo é reprovado
        if (!approve) {
            handle.createUpdate("UPDATE etapa SET estado = 'DISAPPROVED', data_fim = :endDate WHERE id = :stageId")
                .bind("endDate", date)
                .bind("stageId", stageId)
                .execute()

            handle.createUpdate("UPDATE processo SET estado = 'DISAPPROVED', data_fim = :endDate WHERE id = :processId")
                .bind("endDate", date)
                .bind("processId", processId)
                .execute()
        }
    }

    override fun stageSignatures(stageId: String, userAuth: UserAuth): List<Signature> {
        if (!userAuth.roles.contains("admin") && !isUserInStage(
                stageId, userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")
        return handle.createQuery(
            "select email_utilizador, assinatura, data_assinatura from utilizador_etapa where id_etapa = :stageId order by data_assinatura"
        )
            .bind("stageId", stageId)
            .mapTo<Signature>()
            .list()
    }

    /**
     * Verificar se todos os responsáveis já assinaram de acordo com o modo selecionado.
     * Se sim, marcar etapa como completa e prosseguir para a etapa seguinte.
     */
    fun verifySignatures(stageId: String): Boolean {
        val mode = handle.createQuery("select modo from etapa where id = :stageId")
            .bind("stageId", stageId)
            .mapTo<String>()
            .one()

        if (mode == "Unanimous") {
            // Se nenhuma assinatura estiver por preencher, todos os responsáveis já assinaram
            if (handle.createQuery("select email_utilizador from utilizador_etapa where id_etapa = :stageId and assinatura is null")
                    .bind("stageId", stageId)
                    .mapTo<String>()
                    .singleOrNull() == null
            ) {
                handle.createUpdate("UPDATE etapa SET estado = 'APPROVED', data_fim = :endDate WHERE id = :stageId")
                    .bind("endDate", Timestamp(System.currentTimeMillis()))
                    .bind("stageId", stageId)
                    .execute()

                return true
            }
        } else if (mode == "Majority") {
            // Se mais de metade das assinaturas já estiverem preenchidas, o workflow pode prosseguir
            if (handle.createQuery(
                    "with counted_values as (" +
                            "select count(*) as total_count, count(assinatura) as non_null_count " +
                            "from utilizador_etapa " +
                            "where id_etapa = :stageId )" +
                        "select non_null_count > total_count / 2 " +
                            "from counted_values"
                )
                    .bind("stageId", stageId)
                    .mapTo<Boolean>()
                    .one()
            ) {
                handle.createUpdate("UPDATE etapa SET estado = 'APPROVED', data_fim = :endDate WHERE id = :stageId")
                    .bind("endDate", Timestamp(System.currentTimeMillis()))
                    .bind("stageId", stageId)
                    .execute()

                return true
            }
        } else
            throw ExceptionControllerAdvice.InvalidParameterException("Modo de assinatura inválido.")

        return false
    }

    override fun createStage(
        processId: String,
        index: Int,
        name: String,
        description: String,
        mode: String,
        responsible: List<String>,
        duration: Int,
        userAuth: UserAuth
    ): String {
        val stageId = UUID.randomUUID().toString()

        handle.createUpdate(
            "insert into etapa (id, id_processo, indice, modo, nome, descricao, prazo, estado) " +
                    "VALUES (:id, :id_processo, :idx, :modo, :name, :description, :prazo, 'PENDING')"
        )
            .bind("id", stageId)
            .bind("id_processo", processId)
            .bind("idx", index)
            .bind("modo", mode)
            .bind("name", name)
            .bind("description", description)
            .bind("prazo", duration)
            .execute()

        responsible.forEach { resp ->
            handle.createUpdate("insert into utilizador_etapa (email_utilizador, id_etapa) values (:email, :stageId)")
                .bind("email", resp)
                .bind("stageId", stageId)
                .execute()
        }

        return stageId
    }

    override fun stagesOfState(
        state: State,
        userAuth: UserAuth,
        limit: Int?,
        skip: Int?,
        userEmail: String?
    ): TaskPage {
        val email = userEmail ?: userAuth.email

        val queryLimit = limit?.plus(1) ?: Int.MAX_VALUE

        val query = if (state == State.PENDING)
            "SELECT e.id, e.nome, e.data_inicio, e.data_fim, p.nome as processo_nome, e.id_processo, e.descricao, e.estado " +
                    "FROM utilizador_etapa ue join etapa e on ue.id_etapa = e.id join processo p on p.id = e.id_processo " +
                    "WHERE (ue.assinatura is null AND ue.id_notificacao is not null AND ue.email_utilizador = :email and e.estado = 'PENDING') " +
                    "order by e.data_inicio desc limit :limit offset :offset"
            else
            "SELECT e.id, e.nome, e.data_inicio, e.data_fim, p.nome as processo_nome, e.id_processo, e.descricao, e.estado " +
                    "FROM utilizador_etapa ue join etapa e on ue.id_etapa = e.id join processo p on p.id = e.id_processo " +
                    "WHERE (ue.email_utilizador = :email and (e.estado = 'APPROVED' or e.estado = 'DISAPPROVED')) " +
                    "order by e.data_fim desc limit :limit offset :offset"

        val list = handle.createQuery(query)
            .bind("email", email)
            .bind("limit", queryLimit)
            .bind("offset", skip)
            .mapTo(StageDetails::class.java)
            .list()

        // Check if there is a previous page
        val hasPreviousPage = skip?.let { it > 0 } ?: false

        // Check if there is a next page
        val hasNextPage = list.size == queryLimit

        val pageList = list.take(limit ?: list.size)

        return TaskPage(hasPreviousPage, hasNextPage, pageList)
    }

    override fun stageUsers(stageId: String, userAuth: UserAuth): List<UserDetails> {
        if (!userAuth.roles.contains("admin") && !isUserInStage(
                stageId, userAuth.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")

        return handle.createQuery("SELECT Utilizador.email, Utilizador.nome FROM Utilizador JOIN Utilizador_Etapa ON Utilizador.email = Utilizador_Etapa.email_utilizador JOIN Etapa ON Etapa.id = Utilizador_Etapa.id_etapa WHERE Etapa.id = :stageId")
            .bind("stageId", stageId)
            .mapTo(UserDetails::class.java)
            .list()
    }


    /** --------------------------- Comments -------------------------------**/
    override fun addComment(stageId: String, comment: String, user: UserAuth): String {
        if (!user.roles.contains("admin") && !isUserInStage(
                stageId, user.email
            )
        ) throw ExceptionControllerAdvice.UserNotAuthorizedException("Utilizador não é Admin nem está associado à etapa")
        val commentId = UUID.randomUUID().toString()

        handle.createUpdate("INSERT INTO Comentario (id, id_etapa, data, texto, remetente) VALUES (:id, :stageId, :date, :text, :authorEmail)")
            .bind("id", commentId)
            .bind("stageId", stageId)
            .bind("date", Timestamp(System.currentTimeMillis()))
            .bind("text", comment)
            .bind("authorEmail", user.email)
            .execute()

        return commentId
    }

    override fun deleteComment(commentId: String, user: UserAuth) {
        if (!user.roles.contains("admin")) {
            handle.createQuery("SELECT * FROM Comentario WHERE id = :commentId and remetente=:email")
                .bind("commentId", commentId)
                .bind("email", user.email)
                .mapTo(Comment::class.java)
                .singleOrNull()
                ?: throw ExceptionControllerAdvice.CommentNotYours("Comentário não é seu, portanto não o pode apagar.")
        }
        handle.createUpdate("DELETE FROM Comentario WHERE id = :commentId")
            .bind("commentId", commentId)
            .execute()
    }

    override fun stageComments(stageId: String, userAuth: UserAuth): List<Comment> {
        return handle.createQuery("SELECT * FROM Comentario WHERE id_etapa = :stageId order by data desc")
            .bind("stageId", stageId)
            .mapTo(Comment::class.java)
            .list()
    }

    /**
     * Checks if comment exists
     */
    fun checkComment(commentId: String) {
        handle.createQuery("SELECT * FROM Comentario WHERE id = :commentId")
            .bind("commentId", commentId)
            .mapTo(Comment::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.CommentNotFound("Comentário não encontrado.")
    }

    private fun isUserInStage(stageId: String, userEmail: String): Boolean {
        val sql = """SELECT CASE
                WHEN COUNT(*) > 0 THEN true
        ELSE false
        END AS IsUserInStage
        FROM Utilizador_Etapa
                WHERE email_utilizador = :userEmail AND id_etapa = :stageId;""".trimIndent()

        // Execute the query, assuming handle is a Handle instance.
        val result =
            handle.createQuery(sql)
                .bind("userEmail", userEmail)
                .bind("stageId", stageId)
                .mapTo(Boolean::class.java)
                .one()

        // Convert result to Boolean and return.
        return result
    }

    private fun ProcessIdFromStage(stageId: String, userEmail: String): String {
        val sql = """ """.trimIndent()

        // Execute the query, assuming handle is a Handle instance.
        val result =
            handle.createQuery(sql)
                .bind("userEmail", userEmail)
                .bind("stageId", stageId)
                .mapTo(String::class.java)
                .one()

        // Convert result to Boolean and return.
        return result
    }

    fun userAdminOrInStage(stageId: String, userAuth: UserAuth): Boolean {
        return !userAuth.roles.contains("admin") && !isUserInStage(
            stageId, userAuth.email
        )
    }


}
