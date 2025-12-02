package com.princess.taskapi.dto

import java.util.*

data class CommentDTO(
    val id: UUID? = null,
    val user: UserDTO? = null,
    val comment: String,
    val task: TaskDTO? = null
)