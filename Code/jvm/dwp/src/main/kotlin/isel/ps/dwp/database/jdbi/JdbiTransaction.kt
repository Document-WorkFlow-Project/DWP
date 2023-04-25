package isel.ps.dwp.database.jdbi

import isel.ps.dwp.database.ProcessesRepository
import isel.ps.dwp.database.RolesRepository
import isel.ps.dwp.database.StagesRepository
import isel.ps.dwp.database.UsersRepository
import org.jdbi.v3.core.Handle

class JdbiTransaction (private val handle: Handle) : Transaction {

    override val usersRepository: UsersRepository by lazy { UsersRepository(handle) }

    override val processesRepository: ProcessesRepository by lazy { ProcessesRepository(handle) }

    override val rolesRepository: RolesRepository by lazy { RolesRepository(handle) }

    override val stagesRepository: StagesRepository by lazy { StagesRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }
}
