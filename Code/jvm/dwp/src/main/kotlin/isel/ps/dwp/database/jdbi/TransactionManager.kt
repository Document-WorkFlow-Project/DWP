package isel.ps.dwp.database.jdbi

import org.jdbi.v3.core.transaction.TransactionIsolationLevel

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
    fun <R> run(isolationLevel: TransactionIsolationLevel, block: (Transaction) -> R): R
}