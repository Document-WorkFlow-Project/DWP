package isel.ps.dwp.controllers

data class SignInModel(val email: String, val password: String)

data class RegisterModel(val email: String, val name: String, val password: String)

data class Token(val token: String)

data class UserDetails (val email: String, val name: String)