package isel.daw.battleships.http.pipeline

import isel.ps.dwp.model.User
import isel.ps.dwp.services.UserServices
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.collections.HashMap

@Component
class AuthorizationHeaderProcessor(
    val usersService: UserServices
) {

    fun process(authorizationValue: String?): User? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME_BEARER) {
            return null
        }
        val name = usersService.checkBearerToken(parts[1])?: return null
        return User("test",name,"test")
    }

        fun processCookie(authorizationValue: String?): User? {
            if (authorizationValue == null) {
                return null
            }
            val cookies = HashMap<String, String>()
            authorizationValue.split("; ").forEach { cookie ->
                val index = cookie.indexOf('=')
                val name = cookie.substring(0, index)
                val value = cookie.substring(index + 1)
                cookies[name] = value
            }
            val tokenCookie = cookies["token"] ?: return null
            val name = usersService.checkBearerToken(tokenCookie)?: return null
            return User("test",name,"test")
        }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizationHeaderProcessor::class.java)
        const val SCHEME_BEARER = "bearer"
    }
}
