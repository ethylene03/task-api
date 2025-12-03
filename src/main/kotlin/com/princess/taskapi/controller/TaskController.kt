package com.princess.taskapi.controller

import com.princess.taskapi.dto.AssigneeIdDTO
import com.princess.taskapi.dto.TaskDTO
import com.princess.taskapi.service.TaskService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RestController
@RequestMapping("/tasks")
class TaskController(private val service: TaskService) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@RequestBody details: TaskDTO, @AuthenticationPrincipal userId: UUID): TaskDTO {
        log.info("Running POST /tasks method.")

        return service.create(details, userId)
            .also { log.info("Task created.") }
    }

    @GetMapping("/{id}")
    fun find(@PathVariable("id") taskId: UUID): TaskDTO {
        log.info("Running GET /tasks/{id} method.")

        return service.find(taskId)
            .also { log.info("Task fetched.") }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") taskId: UUID,
        @RequestBody details: TaskDTO,
        @AuthenticationPrincipal userId: UUID
    ): TaskDTO {
        log.info("Running PUT /tasks/{id} method.")

        return service.update(taskId, details, userId)
            .also { log.info("Task updated.") }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") taskId: UUID, @AuthenticationPrincipal userId: UUID) {
        log.info("Running DELETE /tasks/{id} method.")

        service.delete(taskId, userId)
            .also { log.info("Task deleted.") }
    }
}