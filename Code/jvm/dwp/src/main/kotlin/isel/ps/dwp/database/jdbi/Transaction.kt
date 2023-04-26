package isel.ps.dwp.database.jdbi

import isel.ps.dwp.database.*

interface Transaction {

    val usersRepository: UsersRepository
    val processesRepository: ProcessesRepository
    val rolesRepository: RolesRepository
    val stagesRepository: StagesRepository
    val documentsRepository: DocumentsRepository

    fun rollback()
}