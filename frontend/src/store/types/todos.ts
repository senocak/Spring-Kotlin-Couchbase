import {User} from "./user"

export interface TodoItemPaginationDTO {
    page: number
    pages: number
    total: number
    items: TodoDto[]
}

export interface TodoDto {
    id: string
    description: string
    finished: boolean
    owner: User
}