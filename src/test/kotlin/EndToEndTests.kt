package com.example.usermgmt

import feign.FeignException.FeignClientException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import java.util.*

@SpringBootTest(classes = [TestProjectConfig::class])
class EndToEndTests @Autowired constructor(val testingClient: TestingClient) {

    @Test
    fun saveUser() {
        val testingName = "testing name - saveUser"
        val res = testingClient.createUser(UserDto().copy(name = testingName))
        assertEquals(testingName, res.nsbody().name)

        testingClient.users().filter { it.name == testingName }
    }

    @Test
    fun saveUser_ensureUniqueName() {
        val testingName = "testing name - ensure unique name"

        // first time passes
        assertEquals(HttpStatus.OK, UserDto().copy(name = testingName).safeCallApi(testingClient::createUser))

        // second time does not
        assertEquals(
            HttpStatus.UNPROCESSABLE_ENTITY,
            UserDto().copy(name = testingName).safeCallApi(testingClient::createUser)
        )
    }

    @Test
    fun getUser() {
        val testingName = "testing name - getUser"
        val res = testingClient.createUser(UserDto().copy(name = testingName)).nsbody()
        assertEquals(testingName, testingClient.getUser(res.id).nsbody().name)
    }

    @Test
    fun getUser_nonExistingIdReturns404() {
        assertEquals(HttpStatus.NOT_FOUND, UUID.randomUUID().safeCallApi(testingClient::getUser))
    }

    @Test
    fun updateUser() {
        val testingName = "testing name - update user"
        val updatedName = "updated name"
        val created: UserDto = testingClient.createUser(UserDto().copy(name = testingName)).nsbody()

        testingClient.updateUser(created.id, created.copy(name = updatedName)).run {
            assertEquals(updatedName, nsbody().name)
        }
    }

    @Test
    fun updateUser_sameUsername() {
        val testingName = "testing name - update user"
        val created: UserDto = testingClient.createUser(UserDto().copy(name = testingName)).nsbody()

        fun outer(uuid: UUID): (UserDto) -> ResponseEntity<UserDto> {
            return fun(userDto: UserDto): ResponseEntity<UserDto> {
                return testingClient.updateUser(uuid, userDto)
            }
        }

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, created.safeCallApi(outer(created.id)))
    }

    fun ResponseEntity<UserDto>.nsbody(): UserDto = body ?: throw RuntimeException("body should not be null")

    // ok, I know, this does not make too much sense here. I just wanted to play with extension functions
    fun <T> T.safeCallApi(call: (T) -> ResponseEntity<UserDto>): HttpStatusCode =
        try {
            call(this).statusCode
        } catch (e: FeignClientException) {
            HttpStatusCode.valueOf(e.status())
        }

    companion object {
        @BeforeAll
        @JvmStatic
        fun runServer() {
            runApplication<TrainingProjApplication>()
        }
    }
}