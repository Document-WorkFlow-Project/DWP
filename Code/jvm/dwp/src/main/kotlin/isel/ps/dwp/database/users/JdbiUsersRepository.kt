package isel.ps.dwp.database.users

class JdbiUsersRepository: UsersRepository {

    override fun checkBearerToken(bearerToken: String): String? {
        TODO("Not yet implemented")
    }

    override fun login(username: String, password: String): String? {
        TODO("Not yet implemented")
    }

    override fun register(username: String, password: String): String {
        TODO("Not yet implemented")
    }

    override fun deleteUser(userId: String) {
        TODO("Not yet implemented")
    }

    override fun userDetails(userId: String) {
        TODO("Not yet implemented")
    }

    override fun updateProfile(userId: String, newPass: String) {
        TODO("Not yet implemented")
    }

}