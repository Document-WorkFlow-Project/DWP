package isel.ps.dwp.http.pipeline.authorization

import isel.ps.dwp.model.UserAuth
import isel.ps.dwp.services.RoleServices
import isel.ps.dwp.services.UserServices
import org.springframework.stereotype.Component

@Component
class RBACProcessor(
    val usersService: UserServices,
    val rolesServices: RoleServices,
) {

    fun isUserAdmin(user: UserAuth): Boolean {
        return user.roles.contains("admin")
    }

    fun checkUserRoleIsAboveNeeded(user: UserAuth, role: String): Boolean {
        // TODO("Check if user role is above the one needed to perform this action")
        return true
    }


    // Será que é preciso?
//    fun userInProcess(user: UserAuth, process: ProcessAuth): Boolean{
//
//        return true
//    }
//
//    fun userInStage(user: UserAuth, process: StageAuth): Boolean{
//
//        return true
//    }


}