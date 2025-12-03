package com.princess.taskapi.service

import com.princess.taskapi.dto.UserDTO
import com.princess.taskapi.helpers.*
import com.princess.taskapi.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val repository: UserRepository, private val passwordManager: PasswordManager) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun create(user: UserDTO): UserDTO {
        log.debug("Checking if username is unique..")
        repository.findByUsername(user.username)
            ?.let {
                log.error("Username already exists.")
                throw DuplicateKeyException("Username already exists.")
            }

        log.debug("Saving user..")
        return user.copy(password = passwordManager.hash(user.password))
            .createUserEntity()
            .let { repository.save(it) }
            .toUserResponse()
    }

    fun findAll(): List<UserDTO> {
        return repository.findAll().map { it.toUserResponse() }
    }

    fun find(id: UUID): UserDTO {
        return repository.findById(id)
            .orElseThrow {
                log.error("Data with id $id not found.")
                throw ResourceNotFoundException("ID does not exist.")
            }.toUserResponse()
    }

    fun update(id: UUID, details: UserDTO): UserDTO {
        log.debug("Checking if username is unique..")
        repository.findByUsername(details.username)
            ?.takeIf { it.id == id }
            ?.let {
                log.error("Username already exists.")
                throw DuplicateKeyException("Username already exists.")
            }

        log.debug("Finding user by given ID..")
        val currentUser = repository.findById(id)
            .orElseThrow {
                log.error("User not found.")
                ResourceNotFoundException("User not found.")
            }

        details.password?.takeUnless { passwordManager.isMatch(it, currentUser.password) }
            ?.run {
                log.error("Credentials is incorrect.")
                throw InvalidCredentialsException("Given credentials is incorrect.")
            }

        return currentUser.apply {
            name = details.name
            username = details.username
        }.run { repository.save(this) }.toUserResponse()
    }

    fun delete(id: UUID) {
        log.debug("Checking if ID exists..")
        find(id)

        log.debug("Deleting data..")
        repository.deleteById(id)
    }
}