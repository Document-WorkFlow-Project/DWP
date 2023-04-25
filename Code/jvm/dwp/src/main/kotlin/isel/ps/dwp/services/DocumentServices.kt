package isel.ps.dwp.services

import isel.ps.dwp.database.jdbi.TransactionManager
import isel.ps.dwp.interfaces.DocumentsInterface

class DocumentServices(private val transactionManager: TransactionManager): DocumentsInterface {

}