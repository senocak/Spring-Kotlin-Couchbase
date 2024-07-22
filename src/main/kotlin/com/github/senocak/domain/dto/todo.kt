package com.github.senocak.domain.dto

import com.github.senocak.domain.TodoItem
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Page

data class CreateTodoDto(
    @Schema(example = "Lorem", description = "Description of the todo", required = true, name = "name", type = "String")
    @field:Size(min = 5, max = 250)
    var description: String,
): BaseDto()

data class UpdateTodoDto(
    @Schema(example = "Lorem", description = "Description of the todo", required = true, name = "name", type = "String")
    var description: String? = null,

    @Schema(example = "false", description = "Is finished?", required = true, name = "name", type = "Boolean")
    var finished: Boolean? = null,
): BaseDto()

data class TodoDto(
    @Schema(example = "1234-1234-1234-1234", description = "Id of the todo", required = true, name = "name", type = "String")
    var id: String,

    @Schema(example = "Lorem", description = "Description of the todo", required = true, name = "name", type = "String")
    @field:Size(min = 5, max = 250)
    var description: String,

    @Schema(example = "false", description = "Is finished?", required = true, name = "name", type = "Boolean")
    var finished: Boolean? = false,

    var owner: UserResponse
): BaseDto()

class TodoItemPaginationDTO(
    pageModel: Page<TodoItem>,
    items: List<TodoDto>,
    sortBy: String? = null,
    sort: String? = null
): PaginationResponse<TodoItem, TodoDto>(page = pageModel, items = items, sortBy = sortBy, sort = sort)