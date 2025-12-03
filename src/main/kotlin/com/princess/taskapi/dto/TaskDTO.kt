package com.princess.taskapi.dto

import java.util.UUID

data class TaskDTO(
    val id: UUID? = null,
    val name: String,
    val description: String? = null,
    val assignee: UserDTO? = null,
    val comments: MutableList<CommentDTO> = mutableListOf(),
    val board: UUID? = null
)
