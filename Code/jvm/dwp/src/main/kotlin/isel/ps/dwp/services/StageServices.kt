package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.StagesInterface
import isel.ps.dwp.model.Comment
import isel.ps.dwp.model.Stage
import isel.ps.dwp.model.User
import org.springframework.stereotype.Service

@Service
class StageServices(private val transactionManager: TransactionManager): StagesInterface {

    override fun stageDetails(stageId: String): Stage {
        /*TODO: Averiguar se etapa existe*/

        return transactionManager.run {
            it.stagesRepository.stageDetails(stageId)
        }
    }

    override fun signStage(stageId: String, approve: Boolean): List<String> {
        val notificationIds = transactionManager.run {
            it.stagesRepository.signStage(stageId, approve)
        }

        // TODO cancelar notificações

        if (approve) {
            // Verificar se os restantes responsáveis já assinaram, se sim marcar etapa como completa e prosseguir para a etapa seguinte
            transactionManager.run {
                it.stagesRepository.verifySignatures(stageId)
            }
        }

        return emptyList()
    }

    override fun createStage(processId: Int, nome: String, modo: String, responsavel: String, descricao: String, data_inicio: String, data_fim: String?, prazo: String, estado: String){

        /*TODO: Averiguar se etapa já existe*/

        if (nome.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Stage Name can't be blank.")
        if (responsavel.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Responsable can't be blank.")
        if (descricao.length > 100)
            throw ExceptionControllerAdvice.InvalidParameterException("Descrição length can't be bigger than 100 chars.")

        /* Verificar se o modo é válido */
        if (modo != "Unanimos" && modo != "Majority" && modo != "Unilateral") {
            throw ExceptionControllerAdvice.InvalidParameterException("Invalid value for parameter 'modo'. Must be 'Unanimos', 'Majority' or 'Unilateral'.")
        }


        /*TODO: Mais Averiguações*/

        return transactionManager.run {
            it.stagesRepository.createStage(processId, nome, modo, responsavel,descricao,data_inicio,null,prazo,estado)
        }

    }

    override fun stageUsers(stageId: String): List<User> {
        /*TODO: Averiguar se etapa existe*/

        return transactionManager.run {
            it.stagesRepository.stageUsers(stageId)
        }
    }

    override fun deleteStage(stageId: String) {
        TODO("Not yet implemented")
    }

    override fun editStage(stageId: String, nome: String, modo:String, descricao: String, data_inicio: String, data_fim: String, prazo: String, estado: String) {
        /*TODO: Averiguar se etapa existe*/
        if (nome.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Stage Name can't be blank.")
        if (descricao.length > 100)
            throw ExceptionControllerAdvice.InvalidParameterException("Descrição length can't be bigger than 100 chars.")

        /* Verificar se o modo é válido */
        if (modo != "Unanimos" && modo != "Majority" && modo != "Unilateral") {
            throw ExceptionControllerAdvice.InvalidParameterException("Invalid value for parameter 'modo'. Must be 'Unanimos', 'Majority' or 'Unilateral'.")
        }
        return transactionManager.run {
            it.stagesRepository.editStage(stageId,nome,modo,descricao,data_inicio,data_fim,prazo,estado)
        }
    }

    override fun viewStages(processId: String): List<Stage> {
        /*TODO: Averiguar se processo existe*/

        return transactionManager.run {
            it.stagesRepository.viewStages(processId)
        }
    }


    override fun pendingStages(processId: String): List<Stage> {
        /*TODO: Averiguar se processo existe*/

        return transactionManager.run {
            it.stagesRepository.pendingStages(processId)
        }
    }




    override fun addComment(id: String, stageId: String, date: String, text: String, authorEmail : String): String {
        /*TODO: Averiguar se comentário já existe*/

        /*TODO: Averiguar se etapa existe*/

        /*TODO: Averiguar se utilizador existe*/

        if (date.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("date can't be blank.")
        if (text.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("text can't be blank.")
        if (text.length > 150)
            throw ExceptionControllerAdvice.InvalidParameterException("text length can't be bigger than 150 chars.")

        return transactionManager.run {
            it.stagesRepository.addComment(id,stageId,date,text,authorEmail)
        }

    }

    override fun deleteComment(commentId: String) {
        /*TODO: Averiguar se comentário existe*/

        return transactionManager.run {
            it.stagesRepository.deleteComment(commentId)
        }
    }

    override fun stageComments(stageId: String): List<Comment> {
        /*TODO: Averiguar se etapa existe*/

        return transactionManager.run {
            it.stagesRepository.stageComments(stageId)
        }
    }


}