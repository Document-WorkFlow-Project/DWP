package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Document
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface DocumentServicesInterface {

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