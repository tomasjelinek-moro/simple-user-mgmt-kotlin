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
import java.util.UUID

@SpringBootTest(classes = [SecureTestProjectConfig::class])
class AuthenticatedEndToEndTests @Autowired constructor(val testingClient: TestingClient) : ShouldSpec({

    context("Delete User") {
        should("delete the user when present in the db") {
            with(testingClient.createUser(UserDto()).nullSafeBody()) {
                statusCodeFromApiCall { testingClient.deleteUser(id) } should be(HttpStatus.OK)
                statusCodeFromApiCall { testingClient.getUser(id) } should be(HttpStatus.NOT_FOUND)
            }
        }

        should("fail on 404 when the user does not exist ") {
            UUID(0L, 0L).statusCodeFromApiCall { testingClient.deleteUser(it) } should be(HttpStatus.NOT_FOUND)
        }

        should("never return the password of the deleted user") {
            with(testingClient.createUser(UserDto()).nullSafeBody()) {
                testingClient.deleteUser(id).nullSafeBody().password should be("")
            }
        }
    }

    beforeSpec() {
        runApplication<TrainingProjApplication>()
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}