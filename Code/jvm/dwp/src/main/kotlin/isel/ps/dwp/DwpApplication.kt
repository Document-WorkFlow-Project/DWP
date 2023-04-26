package isel.ps.dwp

import isel.ps.dwp.database.jdbi.configure
import isel.ps.dwp.services.DocumentServices
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.nio.file.Path
import java.nio.file.Paths

private val JDBC_DATABASE_URL: String = System.getenv("DWP_DATABASE_URL")

// Define the folder within the project where the files will be stored
val uploadsFolderPath: Path = Paths.get("filestorage")

@SpringBootApplication
class DwpApplication {

	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setURL(JDBC_DATABASE_URL)
		}
	).configure()

	/*
	@Autowired
	lateinit var documentServices: DocumentServices

	override fun run(vararg args: String?) {
		documentServices.init()
			.onFailure { throw RuntimeException("System cannot start up because no uploads folder is set up") }
	}

	 */

}

fun main(args: Array<String>) {
	runApplication<DwpApplication>(*args)
}
