package com.example.usermgmt

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Success")])
    @GetMapping
    fun users(): Iterable<UserDto> = userService.loadUsers()

    @Operation(summary = "Get a user by UUID", description = "Returns a user by UUID")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "User not found")]
    )
    @GetMapping("/{uuid}")
    fun getUser(
        @PathVariable @Parameter(
            name = "uuid",
            description = "The UUID of the user",
            example = "471046aa-f0df-4947-8261-02061743fa1e"
        ) uuid: UUID
    ) = userService.loadUserById(uuid)

    @Operation(summary = "Update a user by UUID", description = "Updates a user by UUID")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "422", description = "User with the same name already exists")]
    )
    @PutMapping("/{uuid}")
    fun updateUser(
        @PathVariable @Parameter(
            name = "uuid",
            description = "The UUID of the user",
            example = "471046aa-f0df-4947-8261-02061743fa1e"
        ) uuid: UUID, @RequestBody user: UserDto
    ) = userService.updateUser(uuid, user)

    @Operation(summary = "Creates a new user", description = "Creates a new user based on the body of the request.")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "422", description = "User with the same name already exists")]
    )
    @PostMapping
    fun createUser(@RequestBody user: UserDto): UserDto = userService.saveUser(user)
}