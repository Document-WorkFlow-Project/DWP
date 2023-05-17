package isel.ps.dwp.http.pipeline

/*
@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any { it.parameterType == User::class.java }
        ) {
            val bean = handler.bean as DataSource
            val connection = DataSourceUtils.getConnection(bean)
            connection.isClosed
            if (connection.isValid(5)) {

            } else {
                throw ExceptionControllerAdvice.DatabaseIsNotAvailable("JDBI controller connection is not valid")
            }

            val user = if(request.getHeader(NAME_AUTHORIZATION_HEADER) != null) authorizationHeaderProcessor.process(request.getHeader(NAME_AUTHORIZATION_HEADER)) else authorizationHeaderProcessor.processCookie(request.getHeader(NAME_COOKIE_HEADER))
            //println(request.getHeader(NAME_COOKIE_HEADER))
            return if (user == null) {
                response.status = 401
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, AuthorizationHeaderProcessor.SCHEME_BEARER)
                false
            } else {
                UserArgumentResolver.addUserTo(user, request)
                true
            }
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
        private const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_COOKIE_HEADER = "Cookie"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}

 */
