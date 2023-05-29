package isel.ps.dwp.controllers

import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.services.StageServices
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stages")
class StagesController (
    private val stageServices: StageServices
) {
    /** --------------------------- Stages -------------------------------**/
    @GetMapping("/{stageId}")
    fun stageDetails(@PathVariable stageId: String): ResponseEntity<*> {
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

    @GetMapping("/{processId}")
    fun viewStages(@PathVariable processId: String): ResponseEntity<List<*>> {
        val stages = stageServices.viewStages(processId)
        return ResponseEntity.ok(stages)
    }

    @GetMapping("/pending")
    fun pendingStages(userEmail: String?): ResponseEntity<List<*>> {
        val stages = stageServices.pendingStages(userEmail)
        return ResponseEntity.ok(stages)
    }

    @GetMapping("/{stageId}/users")
    fun stageResponsible(@PathVariable stageId: String): ResponseEntity<List<*>> {
        val users = stageServices.stageUsers(stageId)
        return ResponseEntity.ok(users)
    }

    @PostMapping("/{stageId}/comments")
    fun addComment(
        @PathVariable stageId: String,
        @RequestParam id: String,
        @RequestParam date: String,
        @RequestParam text: String,
        @RequestParam authorEmail: String
    ): ResponseEntity<Void> {
        stageServices.addComment(id, stageId, date, text, authorEmail)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @DeleteMapping("/comments/{commentId}")
    fun deleteComment(@PathVariable commentId: String): ResponseEntity<Void> {
        stageServices.deleteComment(commentId)
        return ResponseEntity(HttpStatus.CREATED)
    }

}
