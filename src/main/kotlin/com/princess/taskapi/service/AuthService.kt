package com.princess.taskapi.service

import com.princess.taskapi.dto.CredentialsDTO
import com.princess.taskapi.dto.UserTokenDTO
import com.princess.taskapi.helpers.InvalidLoginException
import com.princess.taskapi.helpers.JWTUtil
import com.princess.taskapi.helpers.PasswordManager
import com.princess.taskapi.repository.UserRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthService(
    private val repository: UserRepository, private val passwordManager: PasswordManager, private val jwtUtil: JWTUtil
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun login(request: CredentialsDTO, response: HttpServletResponse): UserTokenDTO {
        log.debug("Finding username..")
        val user =
            repository.findByUsername(request.username) ?: throw InvalidLoginException("Invalid username or password.")

        log.debug("Checking if password matches..")
        return request.password.takeIf { passwordManager.isMatch(it, user.password) }?.let {
            // generate refresh token
            val refreshToken = jwtUtil.generateRefreshToken(user.id!!)
            Cookie("refresh_token", refreshToken).apply {
                isHttpOnly = true
                path = "/"
                maxAge = 60 * 60 * 24 * 7
                response.addCookie(this)
            }

            UserTokenDTO(
                id = user.id, name = user.name, username = user.username, token = jwtUtil.generateAccessToken(user.id!!)
            )
        } ?: run {
            log.error("Password mismatch.")
            throw InvalidLoginException("Invalid username or password.")
        }
    }

    fun refreshToken(token: String): UserTokenDTO {
        log.debug("Decoding refresh token..")
        val id = jwtUtil.extractToken(token)?.subject
            ?: throw InvalidLoginException("Invalid refresh token.")

        log.debug("Fetching user details..")
        val user = repository.findById(UUID.fromString(id))
            .orElseThrow {
                log.error("User does not exist!")
                InvalidLoginException("User does not exist.")
            }

        log.debug("Generating new access token..")
        return UserTokenDTO(
            id = user.id,
            name = user.name,
            username = user.username,
            token = jwtUtil.generateAccessToken(user.id!!)
        )
    }

    fun clearToken(response: HttpServletResponse) {
        log.debug("Clearing refresh token cookie..")
        Cookie("refresh_token", null).apply {
            isHttpOnly = true
            path = "/"
            maxAge = 0
            response.addCookie(this)
        }
    }
}