package isel.ps.dwp.database

import isel.ps.dwp.interfaces.DocumentsInterface
import isel.ps.dwp.model.Document
import isel.ps.dwp.uploadsFolderPath
import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile

@Repository
class DocumentsRepository(private val handle: Handle) : DocumentsInterface {

    override fun saveDocReference(file: MultipartFile, newId: String) {
        val fileName = file.originalFilename
        val fileType = file.contentType
        val fileSize = file.size
        val filePath = "$uploadsFolderPath/$newId-$fileName"

        handle.createUpdate(
            "insert into documento(id, nome, tipo, tamanho, localizacao) values (:uuid,:name,:type,:size,:path)"
        )
            .bind("uuid", newId)
            .bind("name", fileName)
            .bind("type", fileType)
            .bind("size", fileSize)
            .bind("path", filePath)
            .execute()
    }

    override fun findPathById(fileId: String): String? {
        return handle.createQuery("select localizacao from documento where id = :uuid")
            .bind("uuid", fileId)
            .mapTo(String::class.java)
            .singleOrNull()
    }

    override fun documentDetails(fileId: String): Document? {
        return handle.createQuery("select * from documento where id = :uuid")
            .bind("uuid", fileId)
            .mapTo(Document::class.java)
            .singleOrNull()
    }

}