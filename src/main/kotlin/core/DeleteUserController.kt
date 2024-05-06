package com.example.usermgmt.core

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/users-protected")
class DeleteUserController(val userService: UserService) {

    @Operation(
        summary = "Deletes a user",
        description = "Deletes the user by UUID",
        security = [SecurityRequirement(name = "basicAuth")]
    )
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "success"),
            ApiResponse(responseCode = "404", description = "User not found")]
    )
    @DeleteMapping("/{uuid}")
    fun deleteUser(@PathVariable uuid: UUID): UserDto = userService.deleteUser(uuid)
}
