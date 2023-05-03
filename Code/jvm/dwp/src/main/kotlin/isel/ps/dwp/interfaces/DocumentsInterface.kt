package isel.ps.dwp.interfaces

import isel.ps.dwp.model.Document
import org.springframework.web.multipart.MultipartFile

interface DocumentsInterface {

    /**
     * Guardar a descrição de documento na base de dados
     */
    fun saveDocReference(file: MultipartFile, newId: String)

    /**
     * Obter localização de um documento através do seu id
     */
    fun findPathById(fileId: String): String?

    /**
     * Detalhes de um documento
     */
    fun documentDetails(fileId: String): Document?

    /**
     * Histórico de documentos (função de administrador ou utilizador autorizado)
     */
    //TODO
}