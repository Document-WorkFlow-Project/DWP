package isel.ps.dwp.controllers

import isel.ps.dwp.database.jdbi.JdbiTransactionManager
import isel.ps.dwp.DwpApplication
import isel.ps.dwp.services.UserServices
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.MediaType

@RestController
@RequestMapping("/users")
class UsersController (
    private val userServices: UserServices = UserServices(
        JdbiTransactionManager(jdbi = DwpApplication().jdbi())
    )
) {

    @PostMapping("/register")
    fun register(@RequestBody register: RegisterModel): ResponseEntity<*> {
        val newPlayer: String = userServices.register(register.email, register.name, register.password)
        return ResponseEntity
            .status(201)
            .header("Set-Cookie", "token=${newPlayer};Path=/;HttpOnly")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Token(newPlayer))
    }

    @PostMapping("/login")
    fun login(@RequestBody signIn: SignInModel): ResponseEntity<*> {
        val uuid = userServices.login(signIn.email, signIn.password)
        return ResponseEntity
            .status(201)
            .header("Set-Cookie", "token=${uuid};Path=/;HttpOnly")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Token(uuid))
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<*> {
        val cookie = request.getHeader("Cookie")
        if (cookie != null) {
            response.addHeader("Set-Cookie", "$cookie;Path=/;HttpOnly;Max-Age=0")
        }
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Token(cookie))
    }
}