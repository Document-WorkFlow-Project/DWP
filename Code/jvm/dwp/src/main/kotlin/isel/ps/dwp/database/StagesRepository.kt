package isel.ps.dwp.database

import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage
import isel.ps.dwp.model.User
import org.jdbi.v3.core.Handle

class StagesRepository(private val handle: Handle) : StagesInterface {

    /** --------------------------- Stages -------------------------------**/

    override fun stageDetails(stageId: String): Stage {
        return handle.createQuery("SELECT * FROM Etapa WHERE id = :stageId")
            .bind("stageId", stageId)
            .mapTo(Stage::class.java)
            .one()
    }


    override fun approveStage(stageId: String) {
        handle.createUpdate("UPDATE Stage SET estado = 'Aprovado' WHERE id = :stageId")
            .bind("stageId", stageId)
            .execute()
    }

    override fun disaproveStage(stageId: String) {
        handle.createUpdate("UPDATE Stage SET estado = 'Rejeitado' WHERE id = :stageId")
            .bind("stageId", stageId)
            .execute()
    }


    override fun createStage(processId: Int, nome: String, responsavel: String, descricao: String, data_inicio: String, data_fim: String?, prazo: String, estado: String) {
        handle.createUpdate("INSERT INTO Etapa (id_processo, nome, responsavel, descricao, data_inicio, data_fim, prazo, estado) VALUES (:id_processo, :nome, :responsavel, :descricao, :data_inicio, :data_fim, :prazo, :estado)")
            .bind("id_processo", processId)
            .bind("nome", nome)
            .bind("responsavel", responsavel)
            .bind("descricao", descricao)
            .bind("data_inicio", data_inicio)
            .bind("data_fim", data_fim)
            .bind("prazo", prazo)
            .bind("estado", estado)
            .execute()
    }

    override fun deleteStage(stageId: String) {
        handle.createUpdate("DELETE FROM Stage WHERE id = :stageId")
            .bind("stageId", stageId)
            .execute()
    }

    override fun editStage(stageId: String, nome: String, descricao: String, data_inicio: String, data_fim: String, prazo: String, estado: String) {
        handle.createUpdate(
            "UPDATE Stage SET nome = :nome, responsavel = :responsavel, descricao = :descricao, data_inicio = :dataInicio, data_fim = :dataFim, prazo = :prazo, estado = :estado WHERE id = :stageId"
        )
            .bind("stageId", stageId)
            .bind("nome", nome)
            .bind("descricao", descricao)
            .bind("data_inicio", data_inicio)
            .bind("data_fim", data_fim)
            .bind("prazo", prazo)
            .bind("estado", estado)
            .execute()
    }


    override fun viewStages(processId : String): List<Stage> {
        return handle.createQuery("SELECT * FROM stages WHERE id_processo = :processId ")
            .bind("processId", processId)
            .mapTo(Stage::class.java)
            .list()
    }

    override fun pendingStages(processId: String): List<Stage> {
        return handle.createQuery("SELECT * FROM stages WHERE id_processo = :processId AND status = 'Pending'")
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


}