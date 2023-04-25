package isel.daw.battleships.database.jdbi

import isel.daw.battleships.database.GamesRepository
import isel.daw.battleships.database.InfoRepository
import isel.daw.battleships.database.UsersRepository

interface Transaction {

    val usersRepository: UsersRepository

    val gamesRepository: GamesRepository

    val infoRepository: InfoRepository

    fun rollback()
}