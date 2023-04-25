package isel.daw.battleships.database.jdbi

import isel.daw.battleships.database.GamesRepository
import isel.daw.battleships.database.InfoRepository
import isel.daw.battleships.database.UsersRepository
import isel.ps.dwp.database.jdbi.JdbiUsersRepository
import org.jdbi.v3.core.Handle

class JdbiTransaction(
    private val handle: Handle
) : Transaction{

    override val usersRepository: UsersRepository by lazy { JdbiUsersRepository(handle) }

    override val gamesRepository: GamesRepository by lazy { JdbiGamesRepository(handle) }

    override val infoRepository: InfoRepository by lazy { JdbiInfoRepository(handle) }


    override fun rollback() {
        handle.rollback()
    }
}
