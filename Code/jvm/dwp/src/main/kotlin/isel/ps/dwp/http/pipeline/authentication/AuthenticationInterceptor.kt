package isel.ps.dwp.http.pipeline.authentication

import isel.ps.dwp.DwpApplication
import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.model.UserAuth
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor,
    private val jdbi: Jdbi
) : HandlerInterceptor{

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        jdbi.open().connection.use { connection ->

            if (handler is HandlerMethod && handler.methodParameters.any { it.parameterType == UserAuth::class.java }) {
                if (!connection.isValid(5))
                    throw ExceptionControllerAdvice.DatabaseIsNotAvailable("O Servidor não tem Acesso à DB")

                val user = if (request.getHeader(NAME_AUTHORIZATION_HEADER) != null)
                    authorizationHeaderProcessor.process(request.getHeader(NAME_AUTHORIZATION_HEADER))
                else
                    authorizationHeaderProcessor.processCookie(request.getHeader(NAME_COOKIE_HEADER))

                return if (user == null) {
                    response.status = 401
                    response.contentType = MediaType.APPLICATION_PROBLEM_JSON.type
                    response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, AuthorizationHeaderProcessor.SCHEME_BEARER)
                    false
                } else {
                    UserArgumentResolver.addUserTo(user, request)
                    true
                }
            }
            return true
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
        private const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_COOKIE_HEADER = "Cookie"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}


