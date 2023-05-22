package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.DocumentServicesInterface
import isel.ps.dwp.model.Document
import isel.ps.dwp.utils.saveInFilesystem
import isel.ps.dwp.uploadsFolderPath
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.*

@Service
class DocumentServices(private val transactionManager: TransactionManager): DocumentServicesInterface {

    override fun uploadDoc(file: MultipartFile): String {
        val uuid = UUID.randomUUID().toString()

        // Save file in filesystem
        saveInFilesystem(file, "$uploadsFolderPath/$uuid-${file.originalFilename}")

        // Save file description in database
        transactionManager.run {
            it.documentsRepository.saveDocReference(file, uuid)
        }
        return uuid
    }

    override fun downloadDoc(fileId: String): Result<Resource> =
        runCatching {
            val filePath =
                transactionManager.run {
                    it.documentsRepository.findPathById(fileId)
                } ?: throw ExceptionControllerAdvice.DocumentNotFoundException("Documento $fileId não encontrado.")

            val file: Path = uploadsFolderPath.resolve(filePath)

            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable)
                resource
            else throw ExceptionControllerAdvice.DocumentNotFoundException("O documento não existe ou não é permitida leitura.")
        }.onFailure {
            throw ExceptionControllerAdvice.DataTransferError("Erro a transferir documento, ${it.javaClass}")
        }

    override fun documentDetails(fileId: String): Document? {
        return transactionManager.run {
            it.documentsRepository.documentDetails(fileId)
        }
    }

}