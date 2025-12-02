package com.princess.taskapi.helpers

import com.princess.taskapi.dto.BoardDTO
import com.princess.taskapi.dto.CommentDTO
import com.princess.taskapi.dto.TaskDTO
import com.princess.taskapi.dto.UserDTO
import com.princess.taskapi.model.BoardEntity
import com.princess.taskapi.model.CommentEntity
import com.princess.taskapi.model.TaskEntity
import com.princess.taskapi.model.UserEntity

fun UserEntity.toUserResponse(): UserDTO = UserDTO(
    id = this.id,
    name = this.name,
    username = this.username
)

fun UserDTO.createUserEntity(): UserEntity = UserEntity(
    name = this.name,
    username = this.username,
    password = this.password ?: throw kotlin.IllegalArgumentException("Password is required.")
)



fun CommentEntity.toCommentResponse(): CommentDTO = CommentDTO(
    id = this.id,
    user = this.user?.toUserResponse() ?: throw ResourceNotFoundException("No user is passed."),
    comment = this.comment,
    task = this.task?.toTaskResponse()
)

fun CommentDTO.createCommentEntity(user: UserEntity, task: TaskEntity): CommentEntity = CommentEntity(
    user = user,
    comment = this.comment,
    task = task
)

fun TaskEntity.toTaskResponse(): TaskDTO = TaskDTO(
    id = this.id,
    name = this.name,
    description = this.description,
    assignee = this.assignee?.toUserResponse(),
    comments = this.comments.map { it.toCommentResponse() }.toMutableList(),
    board = this.board?.toBoardResponse()
)

fun TaskDTO.createTaskEntity(assignee: UserEntity? = null, board: BoardEntity): TaskEntity = TaskEntity(
    name = this.name,
    description = this.description,
    assignee = assignee,
    comments = mutableListOf(),
    board = board
)

fun BoardEntity.toBoardResponse(): BoardDTO = BoardDTO(
    id = this.id,
    owner = this.owner?.toUserResponse(),
    name = this.name,
    description = this.description,
    tasks = this.tasks.map { it.toTaskResponse() }.toMutableList(),
    members = this.members.map { it.toUserResponse() }.toMutableList()
)

fun BoardDTO.createBoardEntity(user: UserEntity): BoardEntity = BoardEntity(
    owner = user,
    name = this.name,
    description = this.description,
    tasks = mutableListOf(),
    members = mutableListOf()
)