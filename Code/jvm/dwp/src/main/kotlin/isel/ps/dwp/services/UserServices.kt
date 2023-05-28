package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.NotificationsServicesInterface
import isel.ps.dwp.interfaces.UsersInterface
import isel.ps.dwp.model.EmailDetails
import isel.ps.dwp.model.User
import isel.ps.dwp.model.UserDetails
import isel.ps.dwp.model.UserDetailsWithRoles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class UserServices(
        private val transactionManager: TransactionManager
): UsersInterface {

    @Autowired
    @Qualifier("notificationsService")
    lateinit var notificationServices: NotificationsServicesInterface

    override fun checkBearerToken(bearerToken: String): String? = transactionManager.run {
        it.usersRepository.checkBearerToken(bearerToken)
    }

    override fun usersList(): List<String> {
        return transactionManager.run {
            it.usersRepository.usersList()
        }
    }

    override fun login(email: String, password: String): String {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Email is required.")
        if (password.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Password is required.")

        return transactionManager.run {
            it.usersRepository.login(email, password)
        }
    }

    fun register(email: String, name: String) {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Email is required.")
        if (name.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Name is required.")
        if (name.length > 32)
            throw ExceptionControllerAdvice.InvalidParameterException("Name is too long.")

        val password = transactionManager.run {
            it.usersRepository.register(email, name)
        }

        val emailDetails = EmailDetails(
                email,
                "Olá $name,\n\nAs suas credenciais de acesso:\n\nEmail: $email\nPassword: $password\n\nObrigado,\nDWP",
                "Bem-vindo ao Document workflow platform"
            )

        // Send credentials to new user
        notificationServices.sendSimpleMail(emailDetails)
    }

    override fun deleteUser(email: String) {
        if (email.isBlank())
            throw ExceptionControllerAdvice.ParameterIsBlank("Email is required.")

        return transactionManager.run {
            it.usersRepository.deleteUser(email)
        }
    }

    override fun userDetails(email: String): UserDetailsWithRoles {
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
            it.usersRepository.updateProfile(email, hashPassword, newPass)
        }
    }

    override fun checkUser(email: String): User? {
        return transactionManager.run {
            it.usersRepository.checkUser(email)
        } ?: throw ExceptionControllerAdvice.UserNotFound("User not found. Incorrect email.")
    }
}