package isel.ps.dwp.database

import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage
import org.jdbi.v3.core.Handle

class StagesRepository(handle: Handle) : StagesInterface {

    /** --------------------------- Stages -------------------------------**/

    override fun stageDetails(stageId: String): Stage {
        return handle.createQuery("SELECT * FROM Etapa WHERE id = :stageId")
            .bind("stageId", stageId)
            .mapTo(Stage::class.java)
            .one()
    }

    override fun approveStage(value: Boolean) {
        // TODO:
    }

    override fun disaproveStage(value: Boolean) {
        // TODO:
    }

    override fun createStage(value: Boolean) {
        // TODO:
    }

    override fun deleteStage(value: Boolean) {
        // TODO:
    }

    override fun editStage(stageId: String, editedStage: Stage) {
        //TODO:
        handle.createUpdate("UPDATE stages SET name = :name, description = :description, updated_at = :updatedAt WHERE id = :stageId")
            .bindBean(
                editedStage.copy(
                    id = stageId,
                    processId = null,
                    createdAt = null,
                    stageIndex = null,
                    status = null
                )
            )
            .bind("stageId", stageId)
            .execute()
    }


    override fun viewStages(processId : String): List<Stage> {
        return handle.createQuery("SELECT * FROM stages WHERE id_processo = :processId ")
            .bind("processId", processId)
            .mapTo(Stage::class.java)
            .list()
    }

    override fun pendingStages(stageId: String): List<Stage> {
        return handle.createQuery("SELECT * FROM stages WHERE id = :stageId AND status = 'Pending'")
            .bind("stageId", stageId)
            .mapTo(Stage::class.java)
            .list()
    }

    override fun stageUsers(stageId: String): List<String> {
        return handle.createQuery("SELECT * FROM Utilizador_Etapa WHERE id_etapa = :stageId")
            .bind("stageId", stageId)
            .mapTo(User::class.java)
            .list()
    }



    /** --------------------------- Comments -------------------------------**/
    override fun addComment(id: String, stageId: String, date: String, text: String, authorEmail : String): String {
        val id = generateId()

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