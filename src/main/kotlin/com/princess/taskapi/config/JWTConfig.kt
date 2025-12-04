package com.princess.taskapi.config

import com.princess.taskapi.helpers.JWTUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JWTConfig(private val utils: JWTUtil) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.getHeader("Authorization")
            ?.let { auth ->
                if (!auth.startsWith("Bearer ")) return@let

                val token = auth.substring(7)

                utils.extractToken(token)?.subject
                    ?.let { id ->
                        UsernamePasswordAuthenticationToken(UUID.fromString(id), null, emptyList())
                            .apply {
                                details = WebAuthenticationDetailsSource().buildDetails(request)
                                SecurityContextHolder.getContext().authentication = this
                            }
                    }
            }

        filterChain.doFilter(request, response)
    }
}

class JwtChannelInterceptor(val jwtUtil: JWTUtil) : ChannelInterceptor {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = StompHeaderAccessor.wrap(message)
        if (StompCommand.CONNECT == accessor.command) {
            val token = accessor.getFirstNativeHeader("Authorization")
                ?.removePrefix("Bearer ")

            val auth = UsernamePasswordAuthenticationToken(
                UUID.fromString(jwtUtil.extractToken(token!!)?.subject),
                null,
                emptyList()
            )
            accessor.user = auth
        }
        return message
    }
}