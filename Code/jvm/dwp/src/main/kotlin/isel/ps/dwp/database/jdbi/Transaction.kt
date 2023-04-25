package isel.ps.dwp.database.jdbi

import isel.ps.dwp.database.ProcessesRepository
import isel.ps.dwp.database.RolesRepository
import isel.ps.dwp.database.StagesRepository
import isel.ps.dwp.database.UsersRepository

interface Transaction {

    val usersRepository: UsersRepository
    val processesRepository: ProcessesRepository
    val rolesRepository: RolesRepository
    val stagesRepository: StagesRepository

    fun rollback()
}