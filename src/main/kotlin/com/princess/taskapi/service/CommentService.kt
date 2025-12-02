package com.princess.taskapi.service

import com.princess.taskapi.dto.CommentDTO
import com.princess.taskapi.helpers.ResourceNotFoundException
import com.princess.taskapi.helpers.createCommentEntity
import com.princess.taskapi.helpers.toCommentResponse
import com.princess.taskapi.repository.CommentRepository
import com.princess.taskapi.repository.TaskRepository
import com.princess.taskapi.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun create(details: CommentDTO, userId: UUID): CommentDTO {
        log.debug("Fetching user details..")
        val user = userRepository.findById(userId).orElseThrow {
            log.error("User does not exist.")
            ResourceNotFoundException("User does not exist.")
        }

        log.debug("Fetching task..")
        val task = details.task?.id?.let {
            taskRepository.findById(it).orElseThrow {
                log.error("Task does not exist.")
                ResourceNotFoundException("Task does not exist.")
            }
        } ?: let {
            log.error("Task details is required.")
            throw IllegalArgumentException("Task details is required.")
        }

        log.debug("Saving comment..")
        return details.createCommentEntity(user, task)
            .let { commentRepository.save(it) }
            .toCommentResponse()
    }
}