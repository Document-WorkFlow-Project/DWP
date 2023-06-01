package isel.ps.dwp.controllers

import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.NewComment
import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.services.StageServices
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stages")
class StagesController(
    private val stageServices: StageServices
) {
    /** --------------------------- Stages -------------------------------**/
    @GetMapping("/{stageId}")
    fun stageDetails(@PathVariable stageId: String, user: UserAuth): ResponseEntity<*> {
        val stage = stageServices.stageDetails(stageId)
        return ResponseEntity.ok(stage)
    }

    @PutMapping("sign/{stageId}")
    fun signStage(
        @PathVariable stageId: String,
        @RequestParam(required = true) approve: Boolean,
        user: UserAuth
    ): ResponseEntity<String> {
        stageServices.signStage(stageId, approve, user)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("A Etapa foi aprovada=$approve com sucesso")
    }

    // Etapa pendentes que um responsável tem que assinar
    @GetMapping("/pending")
    fun pendingStages(userEmail: String?, user: UserAuth): ResponseEntity<List<*>> {
        val stages = stageServices.pendingStages(user, userEmail)
        return ResponseEntity.ok(stages)
    }

    @GetMapping("/finished")
    fun finishedStages(userEmail: String?, user: UserAuth): ResponseEntity<List<*>> {
        val stages = stageServices.finishedStages(user, userEmail)
        return ResponseEntity.ok(stages)
    }

    @GetMapping("/{stageId}/users")
    fun stageResponsible(@PathVariable stageId: String, user: UserAuth): ResponseEntity<List<*>> {
        val users = stageServices.stageUsers(stageId)
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{stageId}/signatures")
    fun stageSignatures(@PathVariable stageId: String, user: UserAuth): ResponseEntity<List<*>> {
        val signatures = stageServices.stageSignatures(stageId)
        return ResponseEntity.ok(signatures)
    }

    /**
     * --------------- Comments --------------------------
     */

    @GetMapping("/{stageId}/comments")
    fun stageComments(@PathVariable stageId: String, user: UserAuth): ResponseEntity<List<Comment>> {
        val comments = stageServices.stageComments(stageId)
        return ResponseEntity.ok(comments)
    }

    @PostMapping("/{stageId}/comments")
    fun addComment(
        @PathVariable stageId: String,
        @RequestBody comment: NewComment,
        user: UserAuth
    ): ResponseEntity<Void> {
        stageServices.addComment(stageId, comment.text, user)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @DeleteMapping("/comments/{commentId}")
    fun deleteComment(@PathVariable commentId: String, user: UserAuth): ResponseEntity<Void> {
        stageServices.deleteComment(commentId, user)
        return ResponseEntity(HttpStatus.CREATED)
    }

}
