package com.princess.taskapi.service

import com.princess.taskapi.dto.BoardDTO
import com.princess.taskapi.dto.UserDTO
import com.princess.taskapi.helpers.ResourceNotFoundException
import com.princess.taskapi.helpers.createBoardEntity
import com.princess.taskapi.helpers.toBoardResponse
import com.princess.taskapi.repository.BoardRepository
import com.princess.taskapi.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoardService(private val boardRepository: BoardRepository, private val userRepository: UserRepository) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun create(details: BoardDTO, userId: UUID): BoardDTO {
        log.debug("Fetching user details..")
        val user = userRepository.findById(userId).orElseThrow {
            log.error("User does not exist.")
            ResourceNotFoundException("User does not exist.")
        }

        log.debug("Saving board..")
        return details.createBoardEntity(user).let { boardRepository.save(it) }.toBoardResponse()
    }

    fun findAll(userId: UUID, query: String): List<BoardDTO> {
        log.debug("Fetching all owned boards..")
        val ownedBoards = boardRepository.findAllByOwnerId(userId).map { it.toBoardResponse() }

        log.debug("Fetching all member boards..")
        val memberBoards = boardRepository.findAllByMembersId(userId).map { it.toBoardResponse() }

        log.debug("Collating boards..")
        return when (query) {
            "owner" -> ownedBoards
            "member" -> memberBoards
            else -> ownedBoards + memberBoards
        }
    }

    fun find(boardId: UUID): BoardDTO {
        log.debug("Finding board..")
        return boardRepository.findById(boardId).orElseThrow {
            log.error("Board does not exist.")
            ResourceNotFoundException("Board does not exist.")
        }.toBoardResponse()
    }

    fun update(boardId: UUID, details: BoardDTO, userId: UUID): BoardDTO {
        log.debug("Fetching user details..")
        val user = userRepository.findById(userId).orElseThrow {
            log.error("User does not exist.")
            ResourceNotFoundException("User does not exist.")
        }

        log.debug("Fetching board..")
        val board = boardRepository.findById(boardId).orElseThrow {
            log.error("Board does not exist.")
            ResourceNotFoundException("Board does not exist.")
        }

        log.debug("Checking if user is owner..")
        takeIf { board.owner?.id != user.id }?.run {
            log.error("User is not authorized to update this board.")
            IllegalArgumentException("User is not authorized to update this board.")
        }

        log.debug("Saving board..")
        return board.apply {
            name = details.name
            description = details.description
        }.let { boardRepository.save(it) }.toBoardResponse()
    }

    fun invite(boardId: UUID, invitedMembers: List<UserDTO>, userId: UUID): BoardDTO {
        log.debug("Fetching owner details..")
        val owner = userRepository.findById(userId).orElseThrow {
            log.error("User does not exist.")
            ResourceNotFoundException("User does not exist.")
        }

        log.debug("Fetching board..")
        val board = boardRepository.findById(boardId).orElseThrow {
            log.error("Board does not exist.")
            ResourceNotFoundException("Board does not exist.")
        }

        log.debug("Checking if user is owner..")
        takeIf { board.owner?.id != owner.id }?.run {
            log.error("User is not authorized to update this board.")
            IllegalArgumentException("User is not authorized to update this board.")
        }

        log.debug("Fetching member details..")
        val members = invitedMembers.map { userRepository.findById(it.id!!) }.toMutableList()

        log.debug("Saving members..")
        return board.apply {
            members.addAll(members)
        }.let { boardRepository.save(it) }.toBoardResponse()
    }

    fun delete(boardId: UUID, userId: UUID) {
        log.debug("Fetching board..")
        val board = boardRepository.findById(boardId).orElseThrow {
            log.error("Board does not exist.")
            ResourceNotFoundException("Board does not exist.")
        }

        log.debug("Checking if user is owner..")
        takeIf { board.owner?.id != userId }?.run {
            log.error("User is not authorized to update this board.")
            IllegalArgumentException("User is not authorized to update this board.")
        }

        log.debug("Deleting board..")
        boardRepository.deleteById(boardId)
    }
}