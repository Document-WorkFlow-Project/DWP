package isel.ps.dwp.database.jdbi

import isel.ps.dwp.interfaces.UsersInterface
import org.jdbi.v3.core.Handle

class JdbiUsersRepository(private val handle: Handle) : UsersInterface {

    override fun checkBearerToken(bearerToken: String): String? {
        TODO("Not yet implemented")
    }

    override fun authUser(username: String, hashPassword: String): String {
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
