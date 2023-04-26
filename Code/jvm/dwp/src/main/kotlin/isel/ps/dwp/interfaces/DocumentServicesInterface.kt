package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Document
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface DocumentServicesInterface {

    /**
     * Create the folder needed to store files
     */
    //fun init(): Result<Path>

    /**
     * Upload de documentos para o filesystem
     */
    fun uploadDoc(file: MultipartFile): String

    /**
     * Download de documentos do filesystem
     */
    fun downloadDoc(fileId: String): Result<Resource>

    /**
     * Detalhes de um documento
     */
    fun documentDetails(fileId: String): Document?
}