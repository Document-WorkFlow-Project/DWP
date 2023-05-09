package isel.ps.dwp.database

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.controllers.UserDetails
import isel.ps.dwp.interfaces.UsersInterface
import isel.ps.dwp.model.User
import isel.ps.dwp.services.md5
import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UsersRepository(private val handle: Handle) : UsersInterface {

    override fun checkBearerToken(bearerToken: String): String? =
        handle.createQuery("select email from utilizador where authtoken = :token")
            .bind("token", bearerToken)
            .mapTo(String::class.java)
            .singleOrNull()

    override fun login(email: String, password: String): String? {
        val user =
            handle.createQuery("select token from utilizador where email = :email and hashpassword = :hashpassword")
                .bind("email", email)
                .bind("password", password)
                .mapTo(User::class.java)
                .singleOrNull() ?: return null
        return user.token
    }

    override fun register(email: String, name: String, password: String): String {
        val newUUID = UUID.randomUUID().toString()
        handle.createUpdate(
            "insert into utilizador(email, nome, pass, authtoken) values (:email,:nome,:password,:uuid)"
        )
            .bind("email", email)
            .bind("nome", name)
            .bind("password", password)
            .bind("uuid", newUUID)
            .execute()
        return newUUID
    }

    override fun deleteUser(email: String) {
        handle.createUpdate(
            "delete from utilizador where email = :email"
        )
            .bind("email", email)
            .execute()
    }

    override fun userDetails(email: String): UserDetails {
        return handle.createQuery("select email, name from utilizador where email = :email")
            .bind("email", email)
            .mapTo(UserDetails::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.UserNotFoundException("User not found")
    }

    override fun updateProfile(email: String, hashPassword: String, newPass: String) {
        handle.createQuery("select token from utilizador where email = :email and hashpassword = :hashpassword")
            .bind("email", email)
            .bind("password", hashPassword)
            .mapTo(User::class.java)
            .singleOrNull() ?: throw ExceptionControllerAdvice.FailedAuthenticationException("Invalid password.")

        handle.createUpdate(
            "update utilizador set pass = :newPass where email = :email"
        )
            .bind("email", email)
            .bind("password", newPass.md5())
            .execute()
    }

}