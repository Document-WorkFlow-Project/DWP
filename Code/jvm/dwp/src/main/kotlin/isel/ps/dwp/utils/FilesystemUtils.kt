package isel.ps.dwp.utils

import isel.ps.dwp.ExceptionControllerAdvice
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

fun deleteFromFilesystem(path: String) {
    val file = File(path)
    if (file.exists())
        file.delete()
    else
        throw ExceptionControllerAdvice.DocumentNotFoundException("$path not found.")
}