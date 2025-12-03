package com.princess.taskapi.service

import com.princess.taskapi.dto.TaskDTO
import com.princess.taskapi.helpers.ResourceNotFoundException
import com.princess.taskapi.helpers.createTaskEntity
import com.princess.taskapi.helpers.toTaskResponse
import com.princess.taskapi.repository.BoardRepository
import com.princess.taskapi.repository.TaskRepository
import com.princess.taskapi.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val boardRepository: BoardRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun create(details: TaskDTO, userId: UUID): TaskDTO {
        log.debug("Fetching user details..")
        val user = userRepository.findById(userId).orElseThrow {
            log.error("User does not exist.")
            ResourceNotFoundException("User does not exist.")
        }

        log.debug("Fetching board details..")
        val board = details.board?.let {
            boardRepository.findById(it).orElseThrow {
                log.error("Board does not exist.")
                ResourceNotFoundException("Board does not exist.")
            }
        } ?: run {
            log.error("Board details is required.")
            throw IllegalArgumentException("Board details is required.")
        }

        log.debug("Checking if user is a member of the board..")
        takeUnless { board.members.any { it.id == user.id } }?.let {
            log.error("User is not authorized to add tasks.")
            IllegalArgumentException("User is not authorized to add tasks.")
        }

        log.debug("Fetching assignee details..")
        val assignee = details.assignee?.id?.let {
            userRepository.findById(it).orElseThrow {
                log.error("Assignee does not exist.")
                ResourceNotFoundException("Assignee does not exist.")
            }
        } ?: run {
            log.warn("No assignee passed.")
            null
        }

        log.debug("Saving task..")
        return details.createTaskEntity(assignee, board)
            .let { taskRepository.save(it) }
            .toTaskResponse()
    }

    fun findAll(userId: UUID): List<TaskDTO> {
        log.debug("Fetching all owned tasks..")
        return taskRepository.findAllByAssigneeId(userId)
            .map { it.toTaskResponse() }
    }

    fun find(taskId: UUID): TaskDTO {
        log.debug("Finding task..")
        return taskRepository.findById(taskId).orElseThrow {
            log.error("Task does not exist.")
            ResourceNotFoundException("Task does not exist.")
        }.toTaskResponse()
    }

    fun update(taskId: UUID, details: TaskDTO, userId: UUID): TaskDTO {
        log.debug("Fetching user details..")
        val user = userRepository.findById(userId).orElseThrow {
            log.error("User does not exist.")
            ResourceNotFoundException("User does not exist.")
        }

        log.debug("Finding task..")
        val task = taskRepository.findById(taskId).orElseThrow {
            log.error("Task does not exist.")
            ResourceNotFoundException("Task does not exist.")
        }

        log.debug("Checking if user is a member of the board..")
        takeUnless { task.board?.members?.any { it.id == user.id } ?: false }?.let {
            log.error("User is not authorized to add tasks.")
            IllegalArgumentException("User is not authorized to add tasks.")
        }

        log.debug("Fetching assignee details..")
        val assignee = details.assignee?.id?.let {
            userRepository.findById(it).orElseThrow {
                log.error("Assignee does not exist.")
                ResourceNotFoundException("Assignee does not exist.")
            }
        } ?: run {
            log.warn("No assignee passed.")
            null
        }

        log.debug("Updating task..")
        return task.apply {
            status = details.status
            name = details.name
            description = details.description
            this.assignee = assignee ?: this.assignee
        }.let { taskRepository.save(it) }.toTaskResponse()
    }

    fun delete(taskId: UUID, userId: UUID) {
        log.debug("Fetching user details..")
        val user = userRepository.findById(userId).orElseThrow {
            log.error("User does not exist.")
            ResourceNotFoundException("User does not exist.")
        }

        log.debug("Finding task..")
        val task = taskRepository.findById(taskId).orElseThrow {
            log.error("Task does not exist.")
            ResourceNotFoundException("Task does not exist.")
        }

        log.debug("Checking if user is a member of the board..")
        takeUnless { task.board?.members?.any { it.id == user.id } ?: false }?.let {
            log.error("User is not authorized to add tasks.")
            IllegalArgumentException("User is not authorized to add tasks.")
        }

        log.debug("Deleting task..")
        taskRepository.deleteById(taskId)
    }
}