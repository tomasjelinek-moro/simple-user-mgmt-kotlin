package com.example.usermgmt.core

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserService(val usersRepository: UsersRepository) {

    fun loadUsers(): Iterable<UserDto> = usersRepository.findAll()

    fun saveUser(user: UserDto): UserDto = user.ensureUnique { usersRepository.save(it) }

    fun updateUser(uuid: UUID, user: UserDto): UserDto = user.ensureUnique { usersRepository.save(it.copy(id = uuid)) }

    fun loadUserById(id: UUID): UserDto = id.ensureExists { it }

    fun UserDto.ensureUnique(action: (UserDto) -> UserDto): UserDto {
        usersRepository.findByUserName(userName) == null || throw UserManipulationException("User with name $userName already exists.")
        return action(this)
    }

    fun deleteUser(id: UUID): UserDto = id.ensureExists {
        usersRepository.deleteById(id)
        it
    }

    fun UUID.ensureExists(action: (UserDto) -> UserDto): UserDto =
        action(usersRepository.findById(this).orElseThrow { UserNotFoundException("User with id $this not found") })
}

@Service
class UserDetailsServiceImpl(val usersRepository: UsersRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails = UserDetailsImpl(
        usersRepository.findByUserName(username) ?: throw UserNotFoundException("User with name $username not found")
    )

}