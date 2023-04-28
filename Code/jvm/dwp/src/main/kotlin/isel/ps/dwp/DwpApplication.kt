package isel.ps.dwp

import isel.ps.dwp.database.jdbi.configure
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.nio.file.Path
import java.nio.file.Paths

private val JDBC_DATABASE_URL: String = System.getenv("DWP_DATABASE_URL")

// Define the folder within the project where the files will be stored
val uploadsFolderPath: Path = Paths.get("filestorage")
val templatesFolderPath: Path = Paths.get("templates")

@SpringBootApplication
class DwpApplication {

	@Bean
	fun jdbi() = Jdbi.create(
		PGSimpleDataSource().apply {
			setURL(JDBC_DATABASE_URL)
		}
	).configure()

}

fun main(args: Array<String>) {
	runApplication<DwpApplication>(*args)
}
