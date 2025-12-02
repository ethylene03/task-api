package com.princess.taskapi.controller

import com.princess.taskapi.dto.BoardDTO
import com.princess.taskapi.dto.UserDTO
import com.princess.taskapi.service.BoardService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RestController
@RequestMapping("/boards")
class BoardController(private val service: BoardService) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@RequestBody details: BoardDTO): BoardDTO {
        log.info("Running POST /boards method.")

        return service.create(details, UUID.randomUUID())
            .also { log.info("Board created.") }
    }

    @GetMapping
    fun findAll(@RequestParam query: String, @AuthenticationPrincipal userId: UUID): List<BoardDTO> {
        log.info("Running GET /boards method.")

        return service.findAll(userId, query)
            .also { log.info("All boards fetched.") }
    }

    @GetMapping("/{id}")
    fun find(@PathVariable("id") boardId: UUID): BoardDTO {
        log.info("Running GET /boards/{id} method.")

        return service.find(boardId)
            .also { log.info("Board fetched.") }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") boardId: UUID,
        @RequestBody details: BoardDTO,
        @AuthenticationPrincipal userId: UUID
    ): BoardDTO {
        log.info("Running PUT /boards/{id} method.")

        return service.update(boardId, details, userId)
            .also { log.info("Board updated.") }
    }

    @PutMapping("/{id}/members")
    fun invite(
        @PathVariable("id") boardId: UUID,
        @RequestBody invitedMembers: List<UserDTO>,
        @AuthenticationPrincipal userId: UUID
    ): BoardDTO {
        log.info("Running PUT /boards/{id}/members method.")

        return service.invite(boardId, invitedMembers, userId)
            .also { log.info("Members invited.") }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") boardId: UUID, @AuthenticationPrincipal userId: UUID) {
        log.info("Running DELETE /boards/{id} method.")

        service.delete(boardId, userId)
            .also { log.info("Board deleted.") }
    }
}