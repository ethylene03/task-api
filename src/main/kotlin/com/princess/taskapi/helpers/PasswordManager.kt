package com.princess.taskapi.helpers

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordManager {
    private val encoder = BCryptPasswordEncoder()

    fun hash(raw: String?) =
        raw?.let { encoder.encode(raw) }
            ?: throw IllegalArgumentException("Input is null.")

    fun isMatch(raw: String, hashed: String): Boolean = encoder.matches(raw, hashed)
}