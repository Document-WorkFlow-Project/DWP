package isel.ps.dwp.database.jdbi

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}