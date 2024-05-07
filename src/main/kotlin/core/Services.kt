package com.example.usermgmt.core

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserService(val usersRepository: UsersRepository, val passwordEncoder: PasswordEncoder) {

    fun loadUsers(): Iterable<UserDto> = usersRepository.findAll()

    fun saveUser(user: UserDto): UserDto = user.ensureUniqueUserName {
        usersRepository.save(it.encodePassword()).clearPassword()
    }

    fun updateUser(uuid: UUID, user: UserDto): UserDto =
        user.ensureUniqueUserName { usersRepository.save(it.copy(id = uuid).encodePassword()).clearPassword() }

    fun loadUserById(id: UUID): UserDto = id.ensureExists { it.clearPassword() }

    fun deleteUser(id: UUID): UserDto = id.ensureExists {
        usersRepository.deleteById(id)
        it.clearPassword()
    }

    fun UserDto.encodePassword(): UserDto = copy(password = passwordEncoder.encode(password))

    fun UserDto.clearPassword(): UserDto = copy(password = "")

    fun UUID.ensureExists(action: (UserDto) -> UserDto): UserDto =
        action(usersRepository.findById(this).orElseThrow { UserNotFoundException("User with id $this not found") })

    fun UserDto.ensureUniqueUserName(action: (UserDto) -> UserDto): UserDto {
        val foundExistingWithSameName = usersRepository.findByUserName(userName)?.let {
            it.id != id
        } ?: false

        if (foundExistingWithSameName) throw UserManipulationException("User with name $userName already exists.")
        return action(this)
    }
}

@Service
class UserDetailsServiceImpl(val usersRepository: UsersRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails = UserDetailsImpl(
        usersRepository.findByUserName(username) ?: throw UserNotFoundException("User with name $username not found")
    )

}