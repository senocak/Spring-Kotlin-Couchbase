package com.github.senocak.auth.domain

import java.util.UUID
import org.springframework.data.couchbase.repository.CouchbaseRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepository: CouchbaseRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByEmailActivationToken(token: String): User?
    fun existsByEmail(email: String): Boolean
}

interface TodoItemRepository: CouchbaseRepository<TodoItem, UUID> {
    fun findAllByOwner(owner: UUID, pageable: Pageable): Page<TodoItem>
}