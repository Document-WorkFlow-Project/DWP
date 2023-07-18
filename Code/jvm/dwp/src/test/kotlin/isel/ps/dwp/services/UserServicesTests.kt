package isel.ps.dwp.services

import isel.ps.dwp.ExceptionControllerAdvice
import isel.ps.dwp.database.UsersRepository
import isel.ps.dwp.model.*
import isel.ps.dwp.services.UserServicesTest
import org.junit.jupiter.api.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.TransactionManager
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
class UserServicesTest {


    @MockBean
    private lateinit var userRepository: UsersRepository

    @Autowired
    private lateinit var userServices: UserServices

    /*
    @BeforeEach
    fun setUp() {
        userRepository = Mockito.mock(UsersRepository.class) ;
        userServices =  userServices(employeeRepository);
        MockitoAnnotations.openMocks(this)
    }

     */


    @Test
    fun `test checkBearerToken with valid token`() {
        val validToken = "6049ddab-f1ae-44fb-a083-468f70868875"
        val expectedUserAuth = UserAuth(
            "jose.nascimentomock@isel.pt",
            "José Nascimento",
            listOf("")
        )

        //Mockito.`when`(user)
        val result = userServices.checkBearerToken(validToken)

        println(result)

        Assertions.assertEquals(expectedUserAuth, result)
    }

    @Test
    fun `test get usersList`() {
        val expectedUserList = listOf(
            "jose.nascimentomock@isel.pt",
            "joao.alfredo.santosmock@isel.pt",
            "antonio.silvestremock@isel.pt",
            "pedro.silvamock@isel.pt",
            "pedro.patriciomock@isel.pt",
            "alessandro.fantonimock@isel.pt",
            "sandra.aleixomock@isel.pt",
            "artur.ferreiramock@isel.pt",
            "jose.simaomock@isel.pt",
            "nuno.datiamock@isel.pt",
            "pedro.miguensmock@isel.pt",
            "nuno.leitemock@isel.pt"
        )

        Mockito.`when`(userRepository.usersList()).thenReturn(listOf(
            "jose.nascimentomock@isel.pt",
            "joao.alfredo.santosmock@isel.pt",
            "antonio.silvestremock@isel.pt",
            "pedro.silvamock@isel.pt",
            "pedro.patriciomock@isel.pt",
            "alessandro.fantonimock@isel.pt",
            "sandra.aleixomock@isel.pt",
            "artur.ferreiramock@isel.pt",
            "jose.simaomock@isel.pt",
            "nuno.datiamock@isel.pt",
            "pedro.miguensmock@isel.pt",
            "nuno.leitemock@isel.pt"
        ))


        Assertions.assertEquals(expectedUserList, userServices.usersList())
    }

    @Test
    fun `test login with valid credentials`() {
        val email = "jose.nascimentomock@isel.pt"
        val password = "41d823ab960ef49973ce628377d7bf5d"
        val expectedToken = "6049ddab-f1ae-44fb-a083-468f70868875"

        val result = userServices.login(email, password)

        Assertions.assertEquals(expectedToken, result)
    }

    @Test
    fun `test login with invalid credentials`() {
        val email = "jose.nascimentomock@isel.pt"
        val password = "invalid_password"

        Assertions.assertThrows(ExceptionControllerAdvice.FailedAuthenticationException::class.java) {
            userServices.login(email, password)
        }
    }

    @Test
    fun `test register with valid parameters`() {
        val email = "test@example.com"
        val name = "John Doe"

        userServices.register(email, name)

    }

    @Test
    fun `test register with blank email`() {
        val email = ""
        val name = "John Doe"

        Assertions.assertThrows(ExceptionControllerAdvice.ParameterIsBlank::class.java) {
            userServices.register(email, name)
        }
    }

}