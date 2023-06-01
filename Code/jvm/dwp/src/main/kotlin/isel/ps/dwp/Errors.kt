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
    class InvalidParameterException(message: String) : RuntimeException(message)
    class FailedAuthenticationException(message: String) : RuntimeException(message)
    class ParameterIsBlank(message: String) : RuntimeException(message)
    class DataTransferError(message: String) : RuntimeException(message)
    class ProcessNotFound(message: String) : RuntimeException(message)
    class DatabaseIsNotAvailable(message: String) : RuntimeException(message)
    class UserNotAuthorizedException(message: String) : RuntimeException(message)
    class NativeRequestDoesntExistException(message: String) : RuntimeException(message) {}
    class StageNotFound(message: String) : RuntimeException(message){}

    class UserNotFound(message: String) : RuntimeException(message){}

    class CommentNotFound(message: String) : RuntimeException(message) {}


    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<String> {
        val status = when (ex) {
            is CommentNotFound,
            is StageNotFound,
            is UserNotFound,
            is ProcessNotFound,
            is UserNotFoundException,
            is DocumentNotFoundException,
            is TemplateNotFoundException -> HttpStatus.NOT_FOUND

            is DatabaseIsNotAvailable -> HttpStatus.INTERNAL_SERVER_ERROR
            is InvalidParameterException,
            is NativeRequestDoesntExistException -> HttpStatus.BAD_REQUEST

            is FailedAuthenticationException -> HttpStatus.UNAUTHORIZED

            is UserNotAuthorizedException -> HttpStatus.FORBIDDEN

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
