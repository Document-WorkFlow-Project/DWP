package isel.ps.dwp.controllers

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.services.StageServices
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stages")
class StagesController (
    private val stageServices: StageServices = StageServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

    @PostMapping("/create")
    fun createEtapa(@RequestBody etapa: Etapa): ResponseEntity<Etapa> {
        val savedEtapa = etapaRepository.save(etapa)
        return ResponseEntity.ok(savedEtapa)
    }

    @DeleteMapping("/{id}")
    fun deleteEtapa(@PathVariable id: Long): ResponseEntity<Void> {
        etapaRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}/approve")
    fun approveEtapa(@PathVariable id: Long): ResponseEntity<Etapa> {
        val etapa = etapaRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Etapa with id $id not found") }

        etapa.estado = "Aprovada"
        val updatedEtapa = etapaRepository.save(etapa)
        return ResponseEntity.ok(updatedEtapa)
    }

    @PutMapping("/{id}/disapprove")
    fun disapproveEtapa(@PathVariable id: Long): ResponseEntity<Etapa> {
        val etapa = etapaRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Etapa with id $id not found") }

        etapa.estado = "Rejeitada"
        val updatedEtapa = etapaRepository.save(etapa)
        return ResponseEntity.ok(updatedEtapa)
    }

    @GetMapping("/{id}")
    fun viewEtapa(@PathVariable id: Long): ResponseEntity<Etapa> {
        val etapa = etapaRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Etapa with id $id not found") }

        return ResponseEntity.ok(etapa)
    }

    @GetMapping("/{id}/details")
    fun getEtapaDetails(@PathVariable id: Long): ResponseEntity<EtapaDetails> {
        val etapa = etapaRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Etapa with id $id not found") }

        val etapaDetails = EtapaDetails(
            etapa.id,
            etapa.nome,
            etapa.responsavel,
            etapa.descricao,
            etapa.data_inicio,
            etapa.data_fim,
            etapa.prazo,
            etapa.estado
        )

        return ResponseEntity.ok(etapaDetails)
    }

}