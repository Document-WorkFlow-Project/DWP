package isel.ps.dwp.controllers

import isel.ps.dwp.model.RegisterModel
import isel.ps.dwp.model.SignInModel
import isel.ps.dwp.services.UserServices
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UsersController (
    private val userServices: UserServices
) {

    @GetMapping
    fun apiUsers(): ResponseEntity<*> {
        val users = userServices.usersList()
        return ResponseEntity
                .status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(users)
    }

    @GetMapping("/{email}")
    fun getUserInfo(@PathVariable email: String): ResponseEntity<*> {
        val users = userServices.userDetails(email)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(users)
    }

    @PostMapping("/register")
    fun register(@RequestBody register: RegisterModel): ResponseEntity<*> {
        userServices.register(register.email, register.name)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("${register.email} registado com sucesso.")
    }

    @PostMapping("/login")
    fun login(@RequestBody signIn: SignInModel): ResponseEntity<*> {
        val uuid = userServices.login(signIn.email, signIn.password)
        return ResponseEntity
            .status(201)
            .header("Set-Cookie", "token=${uuid};Path=/;HttpOnly")
            .contentType(MediaType.APPLICATION_JSON)
            .body("Login feito com Sucesso")
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
            .body("O Utilizador acabou de sair da sessão")
    }
}