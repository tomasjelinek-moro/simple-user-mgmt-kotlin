package com.example.usermgmt.core

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.springframework.data.repository.CrudRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity(name = "users")
data class UserDto(
    @Id @GeneratedValue
    @Schema(title = "A unique ID of the user", example = "471046aa-f0df-4947-8261-02061743fa1e", required = true)
    val id: UUID = UUID.randomUUID(),

    @Schema(title = "A unique name of the user", example = "myUserName", required = true)
    @field:NotEmpty(message = "User name must not be empty")
    @field:Size(max = 255, message = "The user name cant be longer than 255 characters")
    val userName: String = "",

    @Schema(title = "The password of the user", example = "some-p4ssw0rd", required = true)
    @field:NotEmpty(message = "Password must not be empty")
    @field:Size(max = 255, message = "The password cant be longer than 255 characters")
    val password: String = "",
)

data class UserDetailsImpl(val user: UserDto) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.userName

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}

interface UsersRepository : CrudRepository<UserDto, UUID> {

    fun findByUserName(username: String): UserDto?

}