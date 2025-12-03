package com.princess.taskapi.controller

import com.princess.taskapi.dto.UserDTO
import com.princess.taskapi.service.UserService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RestController
@RequestMapping("/users")
class UserController(private val service: UserService) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@Valid @RequestBody details: UserDTO): UserDTO {
        log.info("Running POST /users method.")
        return service.create(details).also { log.info("User created.") }
    }

    @GetMapping
    fun findAll(): List<UserDTO> {
        log.info("Running GET /users method.")

        return service.findAll().also { log.info("Users fetched.") }
    }

    @GetMapping("/{id}")
    fun find(@PathVariable("id") id: UUID): UserDTO {
        log.info("Running GET /users/{id} method.")
        return service.find(id).also { log.info("User fetched.") }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: UUID, @Valid @RequestBody details: UserDTO): UserDTO {
        log.info("Running PUT /users/{id} method.")
        return service.update(id, details).also { log.info("User updated.") }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: UUID) {
        log.info("Running DELETE /users/{id} method.")
        return service.delete(id).also { log.info("User deleted.") }
    }
}