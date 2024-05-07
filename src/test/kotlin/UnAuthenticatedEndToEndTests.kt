package com.example.usermgmt


import com.example.usermgmt.core.UserDto
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.be
import io.kotest.matchers.should
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

@SpringBootTest(classes = [InsecureTestProjectConfig::class])
class UnAuthenticatedEndToEndTests @Autowired constructor(val testingClient: TestingClient) : ShouldSpec({

    context("Create User") {
        should("create a user when the arguments are correct") {
            val testingName = "testing name - saveUser"
            testingClient.createUser(UserDto().copy(userName = testingName)).nullSafeBody().userName should be(
                testingName
            )
        }

        should("fail when user name is not unique") {
            val testingName = "testing name - ensure unique name"
            UserDto(id = UUID.randomUUID(), userName = testingName)
                .statusCodeFromApiCall { testingClient.createUser(it) } should be(
                HttpStatus.OK
            )
            UserDto(id = UUID.randomUUID(), userName = testingName)
                .statusCodeFromApiCall { testingClient.createUser(it) } should be(
                HttpStatus.UNPROCESSABLE_ENTITY
            )
        }
    }

    context("Get User") {
        should("be possible to get the just created user") {
            val testingName = "testing name - getUser"
            val res = testingClient.createUser(UserDto().copy(userName = testingName)).nullSafeBody()
            testingClient.getUser(res.id).nullSafeBody().userName should be(testingName)
        }

        should("fail on 404 when the user does not exist") {
            UUID(0L, 0L).statusCodeFromApiCall { testingClient.getUser(it) } should be(HttpStatus.NOT_FOUND)
        }

        should("never return the password of the created user") {
            with(
                testingClient.createUser(UserDto(password = "not an empty password", userName = "not empty"))
                    .nullSafeBody()
            ) {
                password should be("")
                testingClient.updateUser(id, copy(userName = "other")).nullSafeBody().password should be("")
                testingClient.getUser(id).nullSafeBody().password should be("")
                // delete has to be tested in the "authorized" tests
            }
        }
    }

    context("Update User") {
        should("update the user when arguments are correct") {
            val testingName = "testing name - update user"
            val updatedName = "updated name"
            val created: UserDto = testingClient.createUser(UserDto().copy(userName = testingName)).nullSafeBody()

            testingClient.updateUser(created.id, created.copy(userName = updatedName))
                .nullSafeBody().userName should be(
                updatedName
            )
        }

        should("fail on 422 when the user name is not unique") {
            val testingName = "testing name - update user"
            val created: UserDto = testingClient.createUser(UserDto().copy(userName = testingName)).nullSafeBody()
            val newUUID = UUID.randomUUID()

            with(UUID.randomUUID()) {
                created.copy(this).statusCodeFromApiCall {
                    testingClient.updateUser(
                        this,
                        it
                    )
                } should be(HttpStatus.UNPROCESSABLE_ENTITY)
            }
        }
    }

    beforeSpec() {
        runApplication<TrainingProjApplication>()
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}