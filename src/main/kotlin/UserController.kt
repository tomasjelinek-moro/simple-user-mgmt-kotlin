package com.example.usermgmt

import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @GetMapping
    fun users(): Iterable<UserDto> = userService.loadUsers()

    @GetMapping("/{uuid}")
    fun getUser(@PathVariable uuid: UUID) = userService.loadUserById(uuid)

    @PutMapping("/{uuid}")
    fun updateUser(@PathVariable uuid: UUID, @RequestBody user: UserDto) = userService.updateUser(uuid, user)

    @PostMapping
    fun createUser(@RequestBody user: UserDto): UserDto = userService.saveUser(user)
}