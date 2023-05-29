package isel.ps.dwp

import isel.ps.dwp.database.jdbi.configure
import isel.ps.dwp.interfaces.NotificationsServicesInterface
import isel.ps.dwp.services.NotificationServices
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.nio.file.Path
import java.nio.file.Paths
import isel.ps.dwp.http.pipeline.AuthenticationInterceptor
import isel.ps.dwp.http.pipeline.UserArgumentResolver
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

private val JDBC_DATABASE_URL: String = System.getenv("DWP_DATABASE_URL")

// Define the folder within the project where the files will be stored
val uploadsFolderPath: Path = Paths.get("filestorage")
val templatesFolderPath: Path = Paths.get("templates")

// Ativar/Desativar notificações por email
const val notificationsSwitch = true

@SpringBootApplication
@Configuration
@EnableScheduling
class DwpApplication {

    @Bean
    fun jdbi() = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(JDBC_DATABASE_URL)
        }
    ).configure()

    @Bean
    fun taskScheduler(): TaskScheduler {
        return ThreadPoolTaskScheduler()
    }

    @Bean
    @Qualifier("notificationsService")
    fun notificationsService(): NotificationsServicesInterface {
        return NotificationServices()
    }

}

@Configuration
class PipelineConfigurer(
    val authenticationInterceptor: AuthenticationInterceptor,
    val userArgumentResolver: UserArgumentResolver,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(authenticationInterceptor)
            .addPathPatterns("/docs/**")
            .addPathPatterns("/processes/**")
            .addPathPatterns("/roles/**")
            .addPathPatterns("/stages/**")
            .addPathPatterns("/templates/**")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userArgumentResolver)
    }
}

fun main(args: Array<String>) {
    runApplication<DwpApplication>(*args)
}
