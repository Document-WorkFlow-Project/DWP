package isel.ps.dwp.database.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Component

@Component
class JdbiTransactionManager(
    private val jdbi: Jdbi
) : TransactionManager {
    override fun <R> run(isolationLevel: TransactionIsolationLevel, block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception>(isolationLevel) { handle ->
            val transaction = JdbiTransaction(handle)
            block(transaction)
        }

    override fun <R> run(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = JdbiTransaction(handle)
            block(transaction)

        }
}