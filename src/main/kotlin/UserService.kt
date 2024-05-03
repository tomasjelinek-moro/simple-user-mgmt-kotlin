package com.example.usermgmt

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(val usersRepository: UsersRepository) {

    fun loadUsers(): Iterable<UserDto> = usersRepository.findAll()

    fun saveUser(user: UserDto): UserDto = user.ensureUnique { usersRepository.save(it) }

    fun updateUser(uuid: UUID, user: UserDto): UserDto = user.ensureUnique { usersRepository.save(it.copy(id = uuid)) }

    fun loadUserById(id: UUID): UserDto =
        usersRepository.findById(id).orElseThrow { UserNotFoundException("User with id $id not found") }

    fun UserDto.ensureUnique(action: (UserDto) -> UserDto): UserDto {
        usersRepository.findByName(name) == null || throw UserManipulationException("User with name $name already exists.")
        return action(this)
    }
}

@Service
class UserDetailsServiceImpl : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails = UserDetailsImpl(UserDto(username))

}