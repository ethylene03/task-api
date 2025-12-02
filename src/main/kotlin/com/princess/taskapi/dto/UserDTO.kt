package com.princess.taskapi.dto

import java.util.UUID

data class UserDTO(
    val id: UUID? = null,
    val name: String,
    val username: String,
    val password: String? = null,
)

data class CredentialsDTO(
    val username: String,
    val password: String
)

data class UserTokenDTO(
    val id: UUID? = null,
    val name: String = "",
    val username: String = "",
    val token: String = ""
)