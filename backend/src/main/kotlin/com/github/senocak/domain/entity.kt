package com.github.senocak.domain

import java.io.Serializable
import java.util.Date
import java.util.UUID
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.couchbase.repository.Collection
import org.springframework.data.couchbase.core.mapping.Document
import org.springframework.data.couchbase.core.mapping.Field

open class BaseDomain(
    @Id var id: UUID? = null,
    @Field @CreatedDate var createdAt: Date = Date(),
    @Field @LastModifiedDate var updatedAt: Date = Date()
): Serializable

@Document
@Collection("user-collection")
data class User(
    @Field var name: String? = null,
    @Field var email: String? = null,
    @Field var password: String? = null
): BaseDomain() {
    @Field var roles: List<String> = arrayListOf()
    @Field var emailActivationToken: String? = null
    @Field var emailActivatedAt: Date? = null
}

@Document
@Collection("todo-collection")
data class TodoItem(
    @Field var description: String,
    @Field var owner: UUID,
    @Field var finished: Boolean
): BaseDomain()