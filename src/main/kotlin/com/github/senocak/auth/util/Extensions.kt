package com.github.senocak.auth.util

import com.github.senocak.auth.domain.TodoItem
import com.github.senocak.auth.domain.User
import com.github.senocak.auth.domain.dto.TodoDto
import com.github.senocak.auth.domain.dto.UserResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils

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

/**
 * Split a string into two parts, separated by a delimiter.
 * @param delimiter The delimiter string
 * @return The array of two strings.
 */
fun String.split(delimiter: String): Array<String>? = StringUtils.split(this, delimiter)