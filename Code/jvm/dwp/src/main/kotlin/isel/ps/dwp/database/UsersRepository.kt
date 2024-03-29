package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.interfaces.UsersInterface
import isel.ps.dwp.model.User
import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.model.UserDetails
import org.jdbi.v3.core.Handle
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class UsersRepository(private val handle: Handle) : UsersInterface {

    override fun checkUser(email: String): User? {
        return handle.createQuery("SELECT * FROM utilizador WHERE email = :email")
            .bind("email", email)
            .mapTo(User::class.java)
            .singleOrNull()
    }

    override fun usersList(): List<String> {
        return handle.createQuery("select email from utilizador")
            .mapTo(String::class.java)
            .list()
    }

    override fun checkBearerToken(bearerToken: String): UserAuth {
        val userDetails = handle.createQuery("select nome, email from utilizador where authtoken = :token")
            .bind("token", bearerToken)
            .mapTo(UserDetails::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.UserNotFound("Utilizador não encontrado.")

        val roles = handle.createQuery("select papel from Utilizador_Papel where email_utilizador = :email")
            .bind("email", userDetails.email)
            .mapTo(String::class.java)
            .list()

        return UserAuth(userDetails.email, userDetails.nome, roles)
    }

    override fun login(email: String, password: String): String {
        return handle.createQuery("select authtoken from utilizador where email = :email and pass = :hashpassword")
            .bind("email", email)
            .bind("hashpassword", password.md5())
            .mapTo(String::class.java)
            .singleOrNull()
            ?: throw ExceptionControllerAdvice.FailedAuthenticationException("Email e/ou password incorretos.")
    }

    /**
     * Generates an MD5 Hash for a certain password
     */
    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    private fun generateRandomPassword(length: Int): String {
        val allowedChars =
            ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun register(email: String, name: String): String {
        if (checkUser(email) != null)
            throw ExceptionControllerAdvice.InvalidParameterException("Utilizador já registado.")

        val newUUID = UUID.randomUUID().toString()

        val password = generateRandomPassword(10)

        handle.createUpdate(
            "insert into utilizador (email, nome, pass, authtoken) values (:email, :nome, :password, :uuid)"
        )
            .bind("email", email)
            .bind("nome", name)
            .bind("password", password.md5())
            .bind("uuid", newUUID)
            .execute()

        return password
    }

    override fun deleteUser(email: String) {
        handle.createUpdate(
            "delete from utilizador where email = :email"
        )
            .bind("email", email)
            .execute()
    }

    override fun userDetails(email: String): UserAuth {
        val name = handle.createQuery("select nome from utilizador where email = :email")
            .bind("email", email)
            .mapTo(String::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.UserNotFound("Utilizador não encontrado.")

        val roles = handle.createQuery("select papel from Utilizador_Papel where email_utilizador = :email")
            .bind("email", email)
            .mapTo(String::class.java)
            .list()

        return UserAuth(email, name, roles)
    }

    override fun updateCredentials(email: String, oldPass: String, newPass: String) {
        handle.createQuery("select authtoken from utilizador where email = :email and pass = :password")
            .bind("email", email)
            .bind("password", oldPass.md5())
            .mapTo(String::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.FailedAuthenticationException("Password incorreta.")

        handle.createUpdate(
            "update utilizador set pass = :newPass where email = :email"
        )
            .bind("email", email)
            .bind("newPass", newPass.md5())
            .execute()
    }

}