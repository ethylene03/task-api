package com.princess.taskapi.controller

import com.princess.taskapi.dto.CommentDTO
import com.princess.taskapi.service.CommentService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@Validated
@RestController
@RequestMapping("/comments")
class CommentController(private val service: CommentService) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@RequestBody details: CommentDTO, @AuthenticationPrincipal userId: UUID): CommentDTO {
        log.info("Running POST /comments method.")

        return service.create(details, userId)
            .also { log.info("Comment created.") }
    }
}