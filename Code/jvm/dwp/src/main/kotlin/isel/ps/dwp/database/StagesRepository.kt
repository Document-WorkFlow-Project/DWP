package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage
import isel.ps.dwp.model.StageInfo
import isel.ps.dwp.model.User
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.util.*

class StagesRepository(private val handle: Handle) : StagesInterface {

    /** --------------------------- Stages -------------------------------**/

    override fun stageDetails(stageId: String): Stage {
        return handle.createQuery("SELECT * FROM Etapa WHERE id = :stageId")
            .bind("stageId", stageId)
            .mapTo(Stage::class.java)
            .one()
    }

    /**
     * Inicia a próxima etapa pendente de um processo, atualizando a sua data de inicio.
     * Caso não existam mais etapas pendentes retorna null.
     */
    override fun startNextStage(stageId: String): String? {
        val processId = findProcessFromStage(stageId)

        val nextStage = handle.createQuery("select id from etapa where id_processo = :processId and estado = 'PENDING' order by indice")
                .bind("processId", processId)
                .mapTo<String>()
                .firstOrNull()

        if (nextStage == null) {
            handle.createUpdate("update processo set estado = 'APPROVED' and data_fim = :endDate where id = :processId")
                .bind("endDate", Date())
                .bind("processId", processId)
                .execute()

            return null
        }

        handle.createUpdate("UPDATE etapa SET data_inicio = :startDate WHERE id = :stageId")
                .bind("startDate", Date())
                .bind("stageId", nextStage)
                .execute()

        return nextStage
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
    fun getStageNotifications(stageId: String, email: String?): List<String> {
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

    override fun signStage(stageId: String, approve: Boolean) {
        val date = Date()

        handle.createUpdate(
            "UPDATE utilizador_etapa SET assinatura = :value and data_assinatura = :signDate WHERE id_etapa = :stageId"
        )
            .bind("value", approve)
            .bind("signDate", date)
            .bind("stageId", stageId)
            .execute()

        // Em caso de reprovação a etapa, o processo é reprovado
        if (!approve) {
            handle.createUpdate("UPDATE etapa SET estado = 'DISAPPROVED' and data_fim = :endDate WHERE id = :stageId")
                .bind("endDate", date)
                .bind("stageId", stageId)
                .execute()

            val processId = findProcessFromStage(stageId)

            handle.createUpdate("UPDATE processo SET estado = 'DISAPPROVED' and data_fim = :endDate WHERE id = :processId")
                .bind("endDate", date)
                .bind("processId", processId)
                .execute()
        }
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
                            .one() == null
            ) {
                handle.createUpdate("UPDATE etapa SET estado = 'APPROVED' and data_fim = :endDate WHERE id = :stageId")
                        .bind("endDate", Date())
                        .bind("stageId", stageId)
                        .execute()

                return true
            }
        } else if (mode == "Majority") {
            // Se mais de metade das assinaturas já estiverem preenchidas, o workflow pode prosseguir
            if (handle.createQuery(
                "with counted_values as (select assinatura, count(*) as total_count, count(*) filter (where assinatura is not null) as non_null_count from utilizador_etapa where id = :stageId)" +
                        "select non_null_count > total_count / 2 from counted_values"
                )
                    .bind("stageId", stageId)
                    .mapTo<Boolean>()
                    .one()
            ) {
                handle.createUpdate("UPDATE etapa SET estado = 'APPROVED' and data_fim = :endDate WHERE id = :stageId")
                        .bind("endDate", Date())
                        .bind("stageId", stageId)
                        .execute()

                return true
            }
        } else
            throw ExceptionControllerAdvice.InvalidParameterException("Modo de assinatura inválido.")

        return false
    }

    override fun createStage(processId: String, index: Int, name: String, description: String, mode: String, responsible: List<String>, duration: Int): String {
        val stageId = UUID.randomUUID().toString()

        handle.createUpdate(
        "insert into etapa (id, id_processo, indice, modo, nome, descricao, data_inicio, data_fim, prazo, estado) " +
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

    override fun viewStages(processId : String): List<Stage> {
        return handle.createQuery("SELECT * FROM etapa WHERE id_processo = :processId order by indice")
            .bind("processId", processId)
            .mapTo(Stage::class.java)
            .list()
    }

    override fun pendingStages(userEmail: String?): List<StageInfo> {
        return handle.createQuery(
            "SELECT id_etapa, nome FROM utilizador_etapa join etapa on id_etapa = id WHERE assinatura is null AND id_notificacao is not null AND email_utilizador = :email"
        )
            .bind("email", userEmail)
            .mapTo(StageInfo::class.java)
            .list()
    }

    override fun stageUsers(stageId: String): List<User> {
        return handle.createQuery("SELECT Utilizador.email, Utilizador.nome FROM Utilizador JOIN Utilizador_Etapa ON Utilizador.email = Utilizador_Etapa.email_utilizador JOIN Etapa ON Etapa.id = Utilizador_Etapa.id_etapa WHERE Etapa.id = :stageId")
            .bind("stageId", stageId)
            .mapTo(User::class.java)
            .list()
    }

    /**
     * Checks if stage exists
     */
    fun checkStage(stageId: String) {
        handle.createQuery("SELECT * FROM Etapa WHERE id_etapa = :stageId")
                .bind("stageId", stageId)
                .mapTo(Stage::class.java)
                .list()
                .singleOrNull() ?: throw ExceptionControllerAdvice.StageNotFound("Etapa não encontrada.")
    }


    /** --------------------------- Comments -------------------------------**/
    override fun addComment(id: String, stageId: String, date: String, text: String, authorEmail : String): String {
        handle.createUpdate("INSERT INTO Comentario (id, id_etapa, data, texto, remetente) VALUES (:id, :stageId, :date, :text, :authorEmail)")
            .bind("id", id)
            .bind("stageId", stageId)
            .bind("date", date)
            .bind("text", text)
            .bind("authorEmail", authorEmail)
          //  .bind("createdAt", now())
            .execute()

        return id
    }

    override fun deleteComment(commentId: String) {
        handle.createUpdate("DELETE FROM Comentario WHERE id = :commentId")
            .bind("commentId", commentId)

    }

    override fun stageComments(stageId: String): List<Comment> {
        return handle.createQuery("SELECT * FROM Comentario WHERE id_etapa = :stageId")
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
            .list()
            .singleOrNull() ?: throw ExceptionControllerAdvice.CommentNotFound("Comentário não encontrado.")
    }

}
