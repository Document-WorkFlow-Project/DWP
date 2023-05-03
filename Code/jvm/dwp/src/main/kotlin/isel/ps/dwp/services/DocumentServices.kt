package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.DocumentServicesInterface
import isel.ps.dwp.model.Document
import isel.ps.dwp.model.saveInFilesystem
import isel.ps.dwp.uploadsFolderPath
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

@Component
class DocumentServices(private val transactionManager: TransactionManager): DocumentServicesInterface {

    override fun uploadDoc(file: MultipartFile): String {
        // Save file in filesystem
        saveInFilesystem(file, "$uploadsFolderPath/${file.originalFilename}")

        // Save file description in database
        return transactionManager.run {
            it.documentsRepository.saveDocReference(file)
        }
    }

    override fun downloadDoc(fileId: String): Result<Resource> =
        runCatching {
            val filePath =
                transactionManager.run {
                    it.documentsRepository.findPathById(fileId)
                } ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Document $fileId not found.")

            val file: Path = uploadsFolderPath.resolve(filePath)

            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable)
                resource
            else throw ExceptionControllerAdvice.DocumentNotFoundException("The file does not exist or is not readable.")
        }.onFailure {
            throw ExceptionControllerAdvice.DataTransferError("Error downloading file, reason: ${it.javaClass}")
        }

    override fun documentDetails(fileId: String): Document? {
        return transactionManager.run {
            it.documentsRepository.documentDetails(fileId)
        }
    }

}