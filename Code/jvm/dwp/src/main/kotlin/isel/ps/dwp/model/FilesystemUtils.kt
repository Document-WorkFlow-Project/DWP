package isel.ps.dwp.model

import org.springframework.web.multipart.MultipartFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

// Save file in filesystem in the specified path
fun saveInFilesystem(file: MultipartFile, path: String) {
    val bytes = file.bytes
    val stream = BufferedOutputStream(FileOutputStream(File(path)))
    stream.write(bytes)
    stream.close()
}
