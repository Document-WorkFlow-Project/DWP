package isel.ps.dwp.model

data class SignInModel(val email: String, val password: String)

data class RegisterModel(val email: String, val name: String)

data class Token(val token: String)

data class UserDetails(val nome: String, val email: String)

data class UserDetailsWithRoles(val nome: String, val email: String, val roles : List<String>)

data class RoleModel(val name: String, val description: String)
