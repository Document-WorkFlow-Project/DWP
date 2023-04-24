package isel.ps.dwp.database.stages

import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage

class JdbiStagesRepository: StagesRepository {
    override fun pendingStages(processId: String): List<Stage> {
        TODO("Not yet implemented")
    }

    override fun stageUsers(stageId: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun addStageToTemplate(stage: Stage, stageIndex: Int?, templateId: String) {
        TODO("Not yet implemented")
    }

    override fun removeStageFromTemplate(stageIndex: Int, templateId: String) {
        TODO("Not yet implemented")
    }

    override fun editStage(stageId: String, editedStage: Stage) {
        TODO("Not yet implemented")
    }

    override fun stageDetails(stageId: String): Stage {
        TODO("Not yet implemented")
    }

    override fun addComment(comment: Comment, stageId: String): String {
        TODO("Not yet implemented")
    }

    override fun deleteComment(commentId: String) {
        TODO("Not yet implemented")
    }

    override fun stageComments(stageId: String): List<Comment> {
        TODO("Not yet implemented")
    }

    override fun approveStage(value: Boolean) {
        TODO("Not yet implemented")
    }
}