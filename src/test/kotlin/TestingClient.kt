package com.example.usermgmt

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@FeignClient(name = "users", url = "localhost:8080")
interface TestingClient {

    @GetMapping("/users")
    fun users(): List<UserDto>

    @PostMapping("/users")
    fun createUser(@RequestBody user: UserDto): ResponseEntity<UserDto>

    @GetMapping("/users/{uuid}")
    fun getUser(@PathVariable uuid: UUID): ResponseEntity<UserDto>

    @PutMapping("/users/{uuid}")
    fun updateUser(@PathVariable uuid: UUID, @RequestBody user: UserDto): ResponseEntity<UserDto>
}