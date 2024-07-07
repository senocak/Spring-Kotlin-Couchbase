package com.github.senocak.util

import com.github.senocak.domain.TodoItem
import com.github.senocak.domain.User
import com.github.senocak.domain.dto.TodoDto
import com.github.senocak.domain.dto.UserResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @return -- UserResponse object
 */
fun User.convertEntityToDto(): UserResponse =
    UserResponse(
        name = this.name!!,
        email = this.email!!,
        roles = this.roles,
        emailActivatedAt = this.emailActivatedAt?.time
    )

/**
 * @return -- TodoDto object
 */
fun TodoItem.convertEntityToDto(owner: User): TodoDto =
    TodoDto(
        id = "${this.id}",
        description = this.description,
        finished = this.finished,
        owner = owner.convertEntityToDto()
    )

fun <R : Any> R.logger(): Lazy<Logger> = lazy {
    LoggerFactory.getLogger((if (javaClass.kotlin.isCompanion) javaClass.enclosingClass else javaClass).name)
}

fun Int.randomStringGenerator(): String = RandomStringGenerator(length = this).next()
