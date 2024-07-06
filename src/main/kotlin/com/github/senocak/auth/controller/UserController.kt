package com.github.senocak.auth.controller

import com.github.senocak.auth.domain.User
import com.github.senocak.auth.domain.dto.CreateTodoDto
import com.github.senocak.auth.domain.dto.ExceptionDto
import com.github.senocak.auth.domain.dto.TodoDto
import com.github.senocak.auth.domain.dto.TodoItemPaginationDTO
import com.github.senocak.auth.domain.dto.UpdateTodoDto
import com.github.senocak.auth.domain.dto.UpdateUserDto
import com.github.senocak.auth.domain.dto.UserResponse
import com.github.senocak.auth.domain.dto.UserWrapperResponse
import com.github.senocak.auth.exception.ServerException
import com.github.senocak.auth.security.Authorize
import com.github.senocak.auth.service.UserService
import com.github.senocak.auth.util.AppConstants.ADMIN
import com.github.senocak.auth.util.AppConstants.DEFAULT_PAGE_NUMBER
import com.github.senocak.auth.util.AppConstants.DEFAULT_PAGE_SIZE
import com.github.senocak.auth.util.AppConstants.SECURITY_SCHEME_NAME
import com.github.senocak.auth.util.AppConstants.USER
import com.github.senocak.auth.util.OmaErrorMessageType
import com.github.senocak.auth.util.convertEntityToDto
import com.github.senocak.auth.util.logger
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import java.util.UUID
import org.slf4j.Logger
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@Authorize(roles = [ADMIN, USER])
@RequestMapping(BaseController.V1_USER_URL)
@Tag(name = "User", description = "User Controller")
class UserController(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
): BaseController() {
    private val log: Logger by logger()

    @Throws(ServerException::class)
    @Operation(
        summary = "Get me",
        tags = ["User"],
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = UserWrapperResponse::class)))),
            ApiResponse(responseCode = "500", description = "internal server error occurred",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ExceptionDto::class))))
        ],
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME, scopes = [ADMIN, USER])]
    )
    @GetMapping("/me")
    fun me(): UserResponse =
        userService.loggedInUser().convertEntityToDto()

    @PatchMapping("/me")
    @Operation(
            summary = "Update user by username",
            tags = ["User"],
            responses = [
                ApiResponse(responseCode = "200", description = "successful operation",
                        content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = HashMap::class)))),
                ApiResponse(responseCode = "500", description = "internal server error occurred",
                        content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ExceptionDto::class))))
            ],
            security = [SecurityRequirement(name = SECURITY_SCHEME_NAME, scopes = [ADMIN, USER])]
    )
    @Throws(ServerException::class)
    fun patchMe(request: HttpServletRequest,
        @Parameter(description = "Request body to update", required = true) @Validated @RequestBody userDto: UpdateUserDto,
        resultOfValidation: BindingResult
    ): UserResponse {
        validate(resultOfValidation = resultOfValidation)
        val user: User = userService.loggedInUser()
        val name: String? = userDto.name
        if (!name.isNullOrEmpty())
            user.name = name
        val password: String? = userDto.password
        val passwordConfirmation: String? = userDto.passwordConfirmation
        if (!password.isNullOrEmpty()) {
            if (passwordConfirmation.isNullOrEmpty()) {
                "password_confirmation_not_provided"
                    .apply { log.error(this) }
                    .run { throw ServerException(omaErrorMessageType = OmaErrorMessageType.BASIC_INVALID_INPUT,
                        variables = arrayOf(this), statusCode = HttpStatus.BAD_REQUEST) }
            }
            if (passwordConfirmation != password) {
                "password_and_confirmation_not_matched"
                    .apply { log.error(this) }
                    .run { throw ServerException(omaErrorMessageType = OmaErrorMessageType.BASIC_INVALID_INPUT,
                        variables = arrayOf(this), statusCode = HttpStatus.BAD_REQUEST) }
            }
            user.password = passwordEncoder.encode(password)
        }
        return userService.save(user = user)
            .run user@ {
                this@user.convertEntityToDto()
            }
    }

    @Throws(ServerException::class)
    @Operation(
        summary = "Get List of todos",
        tags = ["User"],
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = UserWrapperResponse::class)))),
            ApiResponse(responseCode = "500", description = "internal server error occurred",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ExceptionDto::class))))
        ],
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME, scopes = [ADMIN, USER])]
    )
    @GetMapping("/todos")
    fun todos(
        @Parameter(name = "page", description = "Page number", example = DEFAULT_PAGE_NUMBER) @RequestParam(defaultValue = "0", required = false) page: Int,
        @Parameter(name = "size", description = "Page size", example = DEFAULT_PAGE_SIZE) @RequestParam(defaultValue = "\${spring.data.web.pageable.default-page-size:10}", required = false) size: Int,
    ): TodoItemPaginationDTO = run {
        val owner: User = userService.loggedInUser()
        userService.findByTodoItems(id = owner.id!!, pageable = PageRequest.of(page, size))
            .run {
                TodoItemPaginationDTO(
                    pageModel = this,
                    items = this.content.map { it.convertEntityToDto(owner = owner) }
                )
            }
    }

    @Throws(ServerException::class)
    @Operation(
        summary = "Create todo",
        tags = ["User"],
        responses = [
            ApiResponse(responseCode = "201", description = "successful operation",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = TodoDto::class)))),
            ApiResponse(responseCode = "500", description = "internal server error occurred",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ExceptionDto::class))))
        ],
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME, scopes = [ADMIN, USER])]
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/todos")
    fun createTodo(
        @Parameter(description = "Request body", required = true) @Validated @RequestBody createTodo: CreateTodoDto,
        resultOfValidation: BindingResult
    ): TodoDto = run {
        validate(resultOfValidation = resultOfValidation)
        val owner: User = userService.loggedInUser()
        userService.createTodoItem(createTodo = createTodo, owner = owner)
            .run {
                this.convertEntityToDto(owner = owner)
            }
    }

    @Throws(ServerException::class)
    @Operation(
        summary = "Get todo",
        tags = ["User"],
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = TodoDto::class)))),
            ApiResponse(responseCode = "500", description = "internal server error occurred",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ExceptionDto::class))))
        ],
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME, scopes = [ADMIN, USER])]
    )
    @GetMapping("/todos/{id}")
    fun getTodo(@Parameter(description = "Id", required = true) @PathVariable id: String): TodoDto = run {
        val owner: User = userService.loggedInUser()
        userService.findTodoItem(id = UUID.fromString(id))
            .run {
                this.convertEntityToDto(owner = owner)
            }
    }

    @Throws(ServerException::class)
    @Operation(
        summary = "Update todo",
        tags = ["User"],
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = TodoDto::class)))),
            ApiResponse(responseCode = "500", description = "internal server error occurred",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ExceptionDto::class))))
        ],
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME, scopes = [ADMIN, USER])]
    )
    @PatchMapping("/todos/{id}")
    fun updateTodo(
        @Parameter(description = "Id", required = true) @PathVariable id: String,
        @Parameter(description = "Request body", required = true) @Validated @RequestBody updateTodoDto: UpdateTodoDto,
        resultOfValidation: BindingResult
    ): TodoDto = run {
        validate(resultOfValidation = resultOfValidation)
        val owner: User = userService.loggedInUser()
        userService.updateTodoItem(id = UUID.fromString(id), updateTodoDto = updateTodoDto)
            .run {
                this.convertEntityToDto(owner = owner)
            }
    }

    @Throws(ServerException::class)
    @Operation(
        summary = "Delete todo",
        tags = ["User"],
        responses = [
            ApiResponse(responseCode = "204", description = "successful operation",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Unit::class)))),
            ApiResponse(responseCode = "500", description = "internal server error occurred",
                content = arrayOf(Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = ExceptionDto::class))))
        ],
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME, scopes = [ADMIN, USER])]
    )
    @DeleteMapping("/todos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTodo(
        @Parameter(description = "Id", required = true) @PathVariable id: String,
    ): Unit = userService.deleteTodoItem(id = UUID.fromString(id))
}
