package isel.daw.battleships.database.jdbi

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}