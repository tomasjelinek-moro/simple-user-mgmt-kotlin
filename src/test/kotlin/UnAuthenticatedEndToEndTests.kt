package com.example.usermgmt


import com.example.usermgmt.core.UserDto
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.inspectors.forAll
import io.kotest.matchers.be
import io.kotest.matchers.should
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import java.util.*

@SpringBootTest(classes = [InsecureTestProjectConfig::class])
class UnAuthenticatedEndToEndTests @Autowired constructor(val testingClient: TestingClient) : ShouldSpec({

    lateinit var context: ConfigurableApplicationContext

    context("Create User") {
        should("create a user when the arguments are correct") {
            with(testingUser()) {
                testingClient.createUser(this).nullSafeBody().userName should be(userName)
            }
        }

        should("fail when user name is not unique") {
            with(testingUser()) {
                statusCodeFromApiCall { testingClient.createUser(it) } should be(
                    HttpStatus.OK
                )
                copy(id = UUID.randomUUID())
                    .statusCodeFromApiCall { testingClient.createUser(it) } should be(
                    HttpStatus.UNPROCESSABLE_ENTITY
                )
            }
        }
    }

    context("Get User") {
        should("be possible to get the just created user") {
            with(testingUser()) {
                val createdUser = testingClient.createUser(this).nullSafeBody()
                testingClient.getUser(createdUser.id).nullSafeBody().userName should be(userName)
            }
        }

        should("fail on 404 when the user does not exist") {
            UUID(0L, 0L).statusCodeFromApiCall { testingClient.getUser(it) } should be(HttpStatus.NOT_FOUND)
        }

        should("never return the password") {
            with(
                testingClient.createUser(testingUser()).nullSafeBody()
            ) {
                password should be("")
                testingClient.updateUser(id, copy(userName = "other", password = "not empty again"))
                    .nullSafeBody().password should be("")
                testingClient.getUser(id).nullSafeBody().password should be("")
                // delete has to be tested in the "authorized" tests
            }
        }
    }

    context("Get all users") {
        should("Never return password for loaded users") {
            testingClient.createUser(testingUser())
            testingClient.createUser(testingUser())

            testingClient.users().forAll {
                it.password should be("")
            }
        }
    }

    context("Update User") {
        should("update the user when arguments are correct") {
            with(testingClient.createUser(testingUser()).nullSafeBody()) {
                val updatedName = "updated name"
                testingClient.updateUser(id, copy(userName = updatedName, password = "not empty"))
                    .nullSafeBody().userName should be(
                    updatedName
                )
            }
        }

        should("fail on 422 when the user name is not unique") {
            val created: UserDto = testingClient.createUser(testingUser()).nullSafeBody()

            with(UUID.randomUUID()) {
                created.copy(this, password = "not empty").statusCodeFromApiCall {
                    testingClient.updateUser(
                        this,
                        it
                    )
                } should be(HttpStatus.UNPROCESSABLE_ENTITY)
            }
        }
    }

    beforeSpec() {
        context = runApplication<TrainingProjApplication>()
    }

    afterSpec() {
        context.stop()
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}