package isel.ps.dwp

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    class DocumentNotFoundException(message: String) : RuntimeException(message)
    class TemplateNotFoundException(message: String) : RuntimeException(message)
    class UserNotFoundException(message: String) : RuntimeException(message)
    class InvalidParameterException(message : String) : RuntimeException(message)
    class FailedAuthenticationException(message: String) : RuntimeException(message)
    class ParameterIsBlank(message: String) : RuntimeException(message)
    class DataTransferError(message: String) : RuntimeException(message)


    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<String> {
        val status = when (ex) {
            is UserNotFoundException,
            is DocumentNotFoundException,
            is TemplateNotFoundException -> HttpStatus.NOT_FOUND
            is InvalidParameterException,
            is FailedAuthenticationException,
            is ParameterIsBlank -> HttpStatus.BAD_REQUEST
            is DataTransferError -> HttpStatus.FAILED_DEPENDENCY
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return ResponseEntity
            .status(status)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ex.message)
    }

}
