package isel.ps.dwp.http.pipeline


import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.model.User
import isel.ps.dwp.model.UserAuth
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) = parameter.parameterType == UserAuth::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw ExceptionControllerAdvice.NativeRequestDoesntExistException("Something Wrong with the request or the Bearer Token used...")
        return getUserFrom(request) ?: throw ExceptionControllerAdvice.UserNotAuthorizedException("User is not Authorized to enter!")
    }

    companion object {
        private const val KEY = "UserArgumentResolver"

        fun addUserTo(user: UserAuth, request: HttpServletRequest) {
            return request.setAttribute(KEY, user)
        }

        fun getUserFrom(request: HttpServletRequest): User? {
            return request.getAttribute(KEY)?.let {
                it as? User
            }
        }
    }
}
