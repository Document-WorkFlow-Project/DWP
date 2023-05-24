package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.services.StageServices
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stages")
class StagesController (
    private val stageServices: StageServices = StageServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {
    /** --------------------------- Stages -------------------------------**/
    @GetMapping("/{stageId}")
    fun stageDetails(@PathVariable stageId: String): ResponseEntity<*> {
        val stage = stageServices.stageDetails(stageId)
        return ResponseEntity.ok(stage)
    }

    @PostMapping("sign/{stageId}")
    fun signStage(@PathVariable stageId: String, @RequestParam(required = true) approve: Boolean): ResponseEntity<Void> {
        stageServices.signStage(stageId, approve)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    /*
    @PostMapping
    fun createStage(
        @RequestParam processId: Int,
        @RequestParam nome: String,
        @RequestParam modo: String,
        @RequestParam responsavel: String,
        @RequestParam descricao: String,
        @RequestParam data_inicio: String,
        @RequestParam data_fim: String,
        @RequestParam prazo: String,
        @RequestParam estado: String
    ): ResponseEntity<Void> {
        stageServices.createStage(processId, nome, modo, responsavel, descricao, data_inicio, data_fim, prazo, estado)
        return ResponseEntity(HttpStatus.CREATED)
    }



    @DeleteMapping("/{stageId}")
    fun deleteStage(@PathVariable stageId: String): ResponseEntity<Void> {
        stageServices.deleteStage(stageId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PutMapping("/{stageId}")
    fun editStage(
        @PathVariable stageId: String,
        @RequestParam nome: String,
        @RequestParam modo: String,
        @RequestParam descricao: String,
        @RequestParam data_inicio: String,
        @RequestParam data_fim: String,
        @RequestParam prazo: String,
        @RequestParam estado: String
    ): ResponseEntity<Void> {
        stageServices.editStage(stageId, nome, modo, descricao, data_inicio, data_fim, prazo, estado)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
    */
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
    fun stageUsers(@PathVariable stageId: String): ResponseEntity<List<*>> {
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
