package isel.ps.dwp.http.pipeline.authorization

import isel.ps.dwp.http.pipeline.authentication.AuthenticationInterceptor
import isel.ps.dwp.http.pipeline.authentication.AuthorizationHeaderProcessor
import isel.ps.dwp.http.pipeline.authentication.UserArgumentResolver
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor


@Component
class AuthorizationInterceptor(
    private val rbacProcessor: RBACProcessor
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val user = UserArgumentResolver.getUserFrom(request)
        if (user == null) {
            response.status = 401
            response.contentType = MediaType.APPLICATION_PROBLEM_JSON.type
            response.addHeader(
                NAME_WWW_AUTHENTICATE_HEADER,
                NAME_COOKIE_HEADER
            )
            return false
        }
        if (handler is HandlerMethod) {
            if (handler.hasMethodAnnotation(Admin::class.java)) {
                if (!rbacProcessor.isUserAdmin(user)) {
                    response.status = 403
                    response.contentType = MediaType.APPLICATION_PROBLEM_JSON.type
                    return false
                }
            }

            if (handler.hasMethodAnnotation(RoleNeeded::class.java)) {
                val role = handler.getMethodAnnotation(RoleNeeded::class.java)
                if (role != null && rbacProcessor.checkUserRoleIsAboveNeeded(user, role.value)) {
                    response.status = 403
                    response.contentType = MediaType.APPLICATION_PROBLEM_JSON.type
                    return false
                }
            }
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
        private const val NAME_COOKIE_HEADER = "Cookie"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }

}
