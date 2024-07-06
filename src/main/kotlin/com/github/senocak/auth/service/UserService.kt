package com.github.senocak.auth.service

import com.github.senocak.auth.domain.TodoItem
import com.github.senocak.auth.domain.TodoItemRepository
import com.github.senocak.auth.domain.User
import com.github.senocak.auth.domain.UserRepository
import com.github.senocak.auth.domain.dto.CreateTodoDto
import com.github.senocak.auth.domain.dto.UpdateTodoDto
import com.github.senocak.auth.exception.ServerException
import com.github.senocak.auth.util.OmaErrorMessageType
import com.github.senocak.auth.util.RoleName
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val todoItemRepository: TodoItemRepository
): UserDetailsService {
    /**
     * @param email -- string email to find in db
     * @return -- true or false
     */
    fun existsByEmail(email: String): Boolean =
        userRepository.existsByEmail(email = email)

    /**
     * @param email -- string email to find in db
     * @return -- User object
     * @throws UsernameNotFoundException -- throws UsernameNotFoundException
     */
    @Throws(UsernameNotFoundException::class)
    fun findByEmail(email: String): User =
        userRepository.findByEmail(email = email) ?: throw UsernameNotFoundException("user_not_found")

    /**
     * @param user -- User object to persist to db
     * @return -- User object that is persisted to db
     */
    fun save(user: User): User = userRepository.save(user)

    /**
     * @param email -- id
     * @return -- Spring User object
     */
    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): org.springframework.security.core.userdetails.User {
        val user: User = findByEmail(email = email)
        val authorities: List<GrantedAuthority> = user.roles.stream()
            .map { SimpleGrantedAuthority(RoleName.fromString(r = it)!!.name) }
            .toList()
        return org.springframework.security.core.userdetails.User(user.email, user.password, authorities)
    }

    /**
     * @return -- User entity that is retrieved from db
     * @throws ServerException -- throws ServerException
     */
    @Throws(ServerException::class)
    fun loggedInUser(): User =
        (SecurityContextHolder.getContext().authentication.principal as org.springframework.security.core.userdetails.User).username
            .run { findByEmail(email = this) }

    fun findByTodoItems(id: UUID, pageable: Pageable): Page<TodoItem> =
        todoItemRepository.findAllByOwner(owner = id, pageable = pageable)

    fun findTodoItem(id: UUID): TodoItem =
        todoItemRepository.findById(id)
            .orElseThrow { ServerException(omaErrorMessageType = OmaErrorMessageType.NOT_FOUND,
                statusCode = HttpStatus.NOT_FOUND, variables = arrayOf("$id")) }

    fun createTodoItem(createTodo: CreateTodoDto, owner: User): TodoItem =
        todoItemRepository.save(TodoItem(
            description = createTodo.description,
            finished = false,
            owner = owner.id!!
        ).also { it.id = UUID.randomUUID() })

    fun updateTodoItem(id: UUID, updateTodoDto: UpdateTodoDto): TodoItem = run {
        val item: TodoItem = findTodoItem(id = id)
        if (updateTodoDto.description != null)
            item.description = updateTodoDto.description!!
        if (updateTodoDto.finished != null)
            item.finished = updateTodoDto.finished!!
        todoItemRepository.save(item)
    }

    fun deleteTodoItem(id: UUID) = run {
        val item: TodoItem = findTodoItem(id = id)
        todoItemRepository.delete(item)
    }
}
