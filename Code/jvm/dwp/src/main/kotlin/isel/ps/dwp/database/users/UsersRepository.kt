package isel.ps.dwp.database.users

interface UsersRepository {

    fun checkBearerToken(bearerToken: String): String?

    fun login(username: String, password: String): String?

    fun register(username: String, password: String): String

    fun deleteUser(userId: String)

    fun userDetails(userId: String)

    fun updateProfile(userId: String, newPass: String)
}
