package isel.ps.dwp.controllers

import isel.ps.dwp.http.pipeline.authorization.Admin
import isel.ps.dwp.model.RegisterModel
import isel.ps.dwp.model.SignInModel
import isel.ps.dwp.model.UpdateCredentialsModel
import isel.ps.dwp.model.UserAuth
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

    @GetMapping("/auth")
    fun checkAuth(user: UserAuth): ResponseEntity<*> {
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(user)
    }

    @GetMapping("/list")
    @Admin
    fun apiUsers(user: UserAuth): ResponseEntity<*> {
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
    @Admin
    fun register(@RequestBody register: RegisterModel, user: UserAuth): ResponseEntity<*> {
        userServices.register(register.email, register.name)
        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("${register.email} registado com sucesso.")
    }

    @PostMapping("/login")
    fun login(@RequestBody signIn: SignInModel): ResponseEntity<*> {
        val uuid = userServices.login(signIn.email, signIn.password)
        // Cookie com validade de 24h
        val expirationTime = 24 * 60 * 60

        return ResponseEntity
            .status(201)
            .header("Set-Cookie", "token=${uuid};max-age=$expirationTime;Path=/;HttpOnly")
            .contentType(MediaType.APPLICATION_JSON)
            .body("Login feito com Sucesso")
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<*> {
        val cookie = request.getHeader("Cookie")
        if (cookie != null)
            response.addHeader("Set-Cookie", "$cookie;Path=/;HttpOnly;Max-Age=-1")

        return ResponseEntity
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .body("O Utilizador acabou de sair da sessão")
    }

    @PutMapping("/credentials")
    fun updatePassword(@RequestBody credentials: UpdateCredentialsModel, user: UserAuth): ResponseEntity<*> {
        userServices.updateCredentials(user.email, credentials.password, credentials.newPassword)
        return ResponseEntity
            .status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body("Palavra-passe alterada.")
    }
}