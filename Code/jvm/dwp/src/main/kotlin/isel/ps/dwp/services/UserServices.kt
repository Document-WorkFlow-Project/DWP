package isel.ps.dwp.services

import isel.daw.battleships.database.jdbi.TransactionManager
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.controllers.UserDetails
import isel.ps.dwp.interfaces.UsersInterface
import java.math.BigInteger
import java.security.MessageDigest

class UserServices(private val transactionManager: TransactionManager): UsersInterface {

    override fun checkBearerToken(bearerToken: String): String? = transactionManager.run {
        it.usersRepository.checkBearerToken(bearerToken)
    }

    override fun login(email: String, password: String): String {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Email is required.")
        if (password.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Password is required.")

        return transactionManager.run {
            it.usersRepository.login(email, password.md5())
        } ?: throw ExceptionControllerAdvice.FailedAuthenticationException("Invalid username or password.")
    }

    override fun register(email: String, name: String, password: String): String {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Email is required.")
        if (name.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Name is required.")
        if (password.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Password is required.")
        if (name.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Name is too long.")
        if (password.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Password is too long.")

        return transactionManager.run {
            it.usersRepository.register(email, password.md5())
        }
    }

    override fun deleteUser(email: String) {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Email is required.")

        return transactionManager.run {
            it.usersRepository.deleteUser(email)
        }
    }

    override fun userDetails(email: String): UserDetails {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Email is required.")

        return transactionManager.run {
            it.usersRepository.userDetails(email)
        }
    }

    override fun updateProfile(email: String, hashPassword: String, newPass: String) {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Email is required.")
        if (hashPassword.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Current password is required.")
        if (newPass.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("New password is required.")
        if (newPass.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Password is too long.")

        return transactionManager.run {
            it.usersRepository.updateProfile(email, hashPassword.md5(), newPass.md5())
        }
    }
}

/**
 * Generates an MD5 Hash for a certain password
 */
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}