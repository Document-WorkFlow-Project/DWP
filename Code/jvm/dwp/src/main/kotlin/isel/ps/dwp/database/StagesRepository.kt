package isel.ps.dwp.database

import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage
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
     * Atualiza a etapa com data de inicio
     */
    fun startStage(stageId: String) {
        handle.createUpdate("UPDATE etapa SET data_inicio = :startDate WHERE id = :stageId")
                .bind("startDate", Date())
                .bind("stageId", stageId)
                .execute()
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

        // Em caso de reprovação a etapa e todo o processo é reprovado, sendo o seu fim
        if (!approve) {
            handle.createUpdate("UPDATE etapa SET estado = 'DISAPPROVED' and data_fim = :endDate WHERE id = :stageId")
                .bind("endDate", date)
                .bind("stageId", stageId)
                .execute()

            val processId = handle.createQuery("select id_processo from etapa_processo where id_etapa = :stageId")
                .bind("stageId", stageId)
                .mapTo<String>()
                .one()

            handle.createUpdate("UPDATE processo SET estado = 'DISAPPROVED' and data_fim = :endDate WHERE id = :processId")
                .bind("endDate", date)
                .bind("processId", processId)
                .execute()
        }
    }


    // TODO implementar aprovação de etapa de acordo com o modo
    fun verifySignatures(stageId: String): Boolean {
        // Todos os responsáveis já assinaram
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
        return false
    }


    override fun createStage(processId: Int, nome: String, modo:String, responsavel: String, descricao: String, data_inicio: String, data_fim: String?, prazo: String, estado: String) {
        handle.createUpdate("INSERT INTO Etapa (id_processo, nome, responsavel, descricao, data_inicio,modo, data_fim, prazo, estado) VALUES (:id_processo, :nome, :responsavel, :descricao, :data_inicio, :modo, :data_fim, :prazo, :estado)")
            .bind("id_processo", processId)
            .bind("nome", nome)
            .bind("modo",modo)
            .bind("responsavel", responsavel)
            .bind("descricao", descricao)
            .bind("data_inicio", data_inicio)
            .bind("data_fim", data_fim)
            .bind("prazo", prazo)
            .bind("estado", estado)
            .execute()
    }

    override fun deleteStage(stageId: String) {
        handle.createUpdate("DELETE FROM etapa WHERE id = :stageId")
            .bind("stageId", stageId)
            .execute()
    }

    override fun editStage(stageId: String, nome: String, modo: String, descricao: String, data_inicio: String, data_fim: String, prazo: String, estado: String) {
        handle.createUpdate(
            "UPDATE etapa SET nome = :nome, responsavel = :responsavel, descricao = :descricao, data_inicio = :dataInicio, modo =:modo, data_fim = :dataFim, prazo = :prazo, estado = :estado WHERE id = :stageId"
        )
            .bind("stageId", stageId)
            .bind("nome", nome)
            .bind("modo",modo)
            .bind("descricao", descricao)
            .bind("data_inicio", data_inicio)
            .bind("data_fim", data_fim)
            .bind("prazo", prazo)
            .bind("estado", estado)
            .execute()
    }


    override fun viewStages(processId : String): List<Stage> {
        return handle.createQuery("SELECT * FROM etapa WHERE id_processo = :processId order by indice")
            .bind("processId", processId)
            .mapTo(Stage::class.java)
            .list()
    }

    override fun pendingStages(processId: String): List<Stage> {
        return handle.createQuery("SELECT * FROM etapa WHERE id_processo = :processId AND estado = 'Pending'")
            .bind("processId", processId)
            .mapTo(Stage::class.java)
            .list()
    }

    override fun stageUsers(stageId: String): List<User> {
        return handle.createQuery("SELECT Utilizador.email, Utilizador.nome FROM Utilizador JOIN Utilizador_Etapa ON Utilizador.email = Utilizador_Etapa.email_utilizador JOIN Etapa ON Etapa.id = Utilizador_Etapa.id_etapa WHERE Etapa.id = :stageId")
            .bind("stageId", stageId)
            .mapTo(User::class.java)
            .list()
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

    override fun checkStage(stageId: String): Stage? {
        return handle.createQuery("SELECT * FROM Etapa WHERE id_etapa = :stageId")
            .bind("stageId", stageId)
            .mapTo(Stage::class.java)
            .list()
            .singleOrNull()
    }

    override fun checkComment(commentId: String) : Comment? {
        return handle.createQuery("SELECT * FROM Comentario WHERE id = :commentId")
            .bind("commentId", commentId)
            .mapTo(Comment::class.java)
            .list()
            .singleOrNull()
    }


}
