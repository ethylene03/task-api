package com.princess.taskapi.dto

import java.util.*

data class BoardDTO(
    val id: UUID? = null,
    val owner: UserDTO? = null,
    val name: String,
    val description: String? = null,
    val tasks: MutableList<TaskDTO> = mutableListOf(),
    val members: MutableList<UserDTO> = mutableListOf()
)
