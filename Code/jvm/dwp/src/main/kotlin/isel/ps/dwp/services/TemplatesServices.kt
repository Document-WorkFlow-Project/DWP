package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.TemplatesInterface
import isel.ps.dwp.model.ProcessTemplate
import isel.ps.dwp.model.deleteFromFilesystem
import isel.ps.dwp.model.saveInFilesystem
import isel.ps.dwp.templatesFolderPath
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class TemplatesServices(private val transactionManager: TransactionManager): TemplatesInterface {

    override fun availableTemplates(): List<String> {
        return transactionManager.run {
            it.templatesRepository.availableTemplates()
        }
    }

    override fun addTemplate(templateFile: MultipartFile): String {
        if (templateFile.contentType != "application/json")
            throw ExceptionControllerAdvice.DataTransferError("Invalid template file format.")

        // Save template file in filesystem
        saveInFilesystem(templateFile, "$templatesFolderPath/${templateFile.originalFilename}")

        // Save template file description in database
        return transactionManager.run {
            it.templatesRepository.addTemplate(templateFile)
        }
    }

    override fun addUsersToTemplate(templateName: String, email: String) {
        if (email.isEmpty())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user emails.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.templatesRepository.addUsersToTemplate(templateName, email)
        }
    }

    override fun removeUserFromTemplate(templateName: String, email: String) {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user email.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.templatesRepository.removeUserFromTemplate(templateName, email)
        }
    }

    override fun deleteTemplate(templateName: String) {
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        val templatePath = transactionManager.run {
            it.templatesRepository.findTemplatePathByName(templateName)
        }

        // Delete template file from filesystem
        deleteFromFilesystem(templatePath)

        transactionManager.run {
            it.templatesRepository.deleteTemplate(templateName)
        }
    }

    fun insertDataFromTemplate(template: ProcessTemplate) {
        var processId : Int?
        if (template.autor.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template process author.")

        if (template.data_inicio.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template process beginning date.")

        if (template.prazo.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template process deadline.")

        if (template.template_processo.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template process file path.")

        if (template.descricao.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template process description.")

        if (template.etapas.isEmpty())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template process stage(s).")

        if (template.estado.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template process status.")

        template.etapas.forEach {

            if (it.nome.isBlank())
                throw ExceptionControllerAdvice.ParameterIsBlank("Missing template stage name.")

            if (it.modo.isEmpty())
                throw ExceptionControllerAdvice.ParameterIsBlank("Missing template stage responsible.")

            if (it.responsavel.isEmpty())
                throw ExceptionControllerAdvice.ParameterIsBlank("Missing template stage responsible.")

            if (it.descricao.isBlank())
                throw ExceptionControllerAdvice.ParameterIsBlank("Missing template description.")

            if (it.data_inicio.isBlank())
                    throw ExceptionControllerAdvice.ParameterIsBlank("Missing template beginning date.")

            if (it.prazo.isBlank())
                throw ExceptionControllerAdvice.ParameterIsBlank("Missing template stage deadline.")

            if (it.estado.isBlank())
                throw ExceptionControllerAdvice.ParameterIsBlank("Missing template stage status.")
        }


        transactionManager.run {

            processId = it.processesRepository.createProcess(
                template.nome,
                template.autor,
                template.descricao,
                template.data_inicio,
                null,
                template.prazo,
                template.estado,
                template.template_processo
            )

            if (processId==null)
                throw ExceptionControllerAdvice.ParameterIsBlank("Failed to create Process.")

            var idx = 0

            template.etapas.forEach { stage ->
                idx++
                it.stagesRepository.createStage(
                    processId!!,
                    stage.nome,
                    stage.modo,
                    stage.responsavel[idx],
                    stage.descricao,
                    stage.data_inicio,
                    null,
                    stage.prazo,
                    stage.estado
                )
            }
        }
    }
}