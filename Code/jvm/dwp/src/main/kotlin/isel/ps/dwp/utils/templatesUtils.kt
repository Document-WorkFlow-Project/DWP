package isel.ps.dwp.utils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.ps.dwp.model.ProcessTemplate

/**
 * Exemplo de template:
 *
 *
 */



class templatesUtils {

    //LÊ o template e converte em objeto

    private val objectMapper: ObjectMapper = ObjectMapper()
    fun readTemplateToObject(json: String): ProcessTemplate {
       return objectMapper.readValue<ProcessTemplate>(json)

    }


    // chama o ProcessesRepository.addProcessFromTemplate( passar todos os campos do objeto)
}