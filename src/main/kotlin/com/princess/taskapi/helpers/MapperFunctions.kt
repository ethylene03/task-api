package com.princess.taskapi.helpers

import com.princess.taskapi.dto.UserDTO
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