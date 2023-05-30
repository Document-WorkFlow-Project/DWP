package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.TemplatesInterface
import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.templatesFolderPath
import isel.ps.dwp.utils.deleteFromFilesystem
import isel.ps.dwp.utils.saveInFilesystem
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class TemplatesServices(private val transactionManager: TransactionManager): TemplatesInterface {

    override fun availableTemplates(user: UserAuth): List<String> {
        return transactionManager.run {
            it.templatesRepository.availableTemplates(user)
        }
    }

    override fun templateUsers(templateName: String): List<String> {
        return transactionManager.run {
            it.templatesRepository.templateUsers(templateName)
        }
    }

    override fun addTemplate(templateName: String, templateDescription: String, templateFile: MultipartFile): String {
        if (templateFile.contentType != "application/json")
            throw ExceptionControllerAdvice.DataTransferError("Invalid template file format.")

        // Save template file in filesystem
        saveInFilesystem(templateFile, "$templatesFolderPath/${templateFile.originalFilename}")

        // Save template file description in database
        return transactionManager.run {
            it.templatesRepository.addTemplate(templateName, templateDescription, templateFile)
        }
    }

    override fun addUsersToTemplate(templateName: String, email: String) {
        if (email.isEmpty())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user emails.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.usersRepository.checkUser(email)
            it.templatesRepository.addUsersToTemplate(templateName, email)
        }
    }

    override fun removeUserFromTemplate(templateName: String, email: String) {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing user email.")
        if (templateName.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Missing template name.")

        transactionManager.run {
            it.usersRepository.checkUser(email)
            it.templatesRepository.removeUserFromTemplate(templateName, email)
        }
    }

    fun getTemplate(templateName: String): ByteArray {
        val templateDetails = transactionManager.run {
            it.templatesRepository.templateDetails(templateName)
        }

        val file: Path = Paths.get(templateDetails.path)

        return Files.readAllBytes(file)
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
}