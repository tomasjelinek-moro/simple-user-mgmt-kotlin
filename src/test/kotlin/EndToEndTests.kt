package com.example.usermgmt

import feign.FeignException.FeignClientException
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.be
import io.kotest.matchers.should
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import java.util.*


@SpringBootTest(classes = [TestProjectConfig::class])
class EndToEndTests @Autowired constructor(val testingClient: TestingClient) : ShouldSpec({
    fun ResponseEntity<UserDto>.nullSafeBody(): UserDto = body ?: throw RuntimeException("body should not be null")

    fun <T> T.statusCodeFromApiCall(call: (T) -> ResponseEntity<UserDto>): HttpStatusCode =
        try {
            call(this).statusCode
        } catch (e: FeignClientException) {
            HttpStatusCode.valueOf(e.status())
        }

    context("User Creation") {
        should("create a user when the arguments are correct") {
            val testingName = "testing name - saveUser"
            testingClient.createUser(UserDto().copy(userName = testingName)).nullSafeBody().userName should be(
                testingName
            )
        }

        should("fail when user name is not unique") {
            val testingName = "testing name - ensure unique name"
            UserDto().copy(userName = testingName).statusCodeFromApiCall { testingClient.createUser(it) } should be(
                HttpStatus.OK
            )
            UserDto().copy(userName = testingName).statusCodeFromApiCall { testingClient.createUser(it) } should be(
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

            created.statusCodeFromApiCall {
                testingClient.updateUser(
                    created.id,
                    it
                )
            } should be(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    beforeSpec() {
        runApplication<TrainingProjApplication>()
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}