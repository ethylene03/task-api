package com.princess.taskapi.dto

data class ErrorResponseDTO(
    val error: List<String>
)

data class BroadcastDTO(
    val type: String, // <action>:<entity> i.e. delete:task
    val data: Any
)