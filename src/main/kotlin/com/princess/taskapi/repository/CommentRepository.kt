package com.princess.taskapi.repository

import com.princess.taskapi.model.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CommentRepository : JpaRepository<CommentEntity, UUID> {
}