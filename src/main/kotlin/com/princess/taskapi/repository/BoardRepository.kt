package com.princess.taskapi.repository

import com.princess.taskapi.model.BoardEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BoardRepository : JpaRepository<BoardEntity, UUID> {
    fun findAllByOwnerId(ownerId: UUID): List<BoardEntity>
    fun findAllByMembersId(memberId: UUID): List<BoardEntity>
}