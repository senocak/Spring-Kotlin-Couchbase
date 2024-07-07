import React, {useEffect, useState} from 'react'
import {useAppDispatch, useAppSelector} from '../store'
import App from "./App"
import {IState} from "../store/types/global"
import {TodoDto, TodoItemPaginationDTO} from "../store/types/todos"
import {fetchAllTodos} from "../store/features/auth/getTodosSlice"
import {fetchAddTodo} from "../store/features/auth/postTodoSlice"
import {fetchDeleteTodo} from "../store/features/auth/deleteTodoSlice"
import {fetchPatchTodo} from "../store/features/auth/updateTodoSlice"

function AdminIndex(): React.JSX.Element {
    const dispatch = useAppDispatch()
    const getTodosSlice: IState<TodoItemPaginationDTO> = useAppSelector(state => state.getTodos)
    const postTodoSlice: IState<TodoDto> = useAppSelector(state => state.postTodo)
    const deleteTodoSlice: IState<void> = useAppSelector(state => state.deleteTodo)
    const updateTodoSlice: IState<TodoDto> = useAppSelector(state => state.updateTodo)
    const [error, setError] = useState<string>("")
    const [description, setDescription] = useState<string>("")

    useEffect((): void => {
        if (!getTodosSlice.isLoading && getTodosSlice.response === null) {
            dispatch(fetchAllTodos({page: 0, size: 100}))
        }
        if (getTodosSlice.error !== null) {
            setError(getTodosSlice.error.response?.data?.exception)
        }
    }, [getTodosSlice, postTodoSlice, dispatch])

    useEffect((): void => {
        if (!postTodoSlice.isLoading && postTodoSlice.response !== null) {
            dispatch(fetchAllTodos({page: 0, size: 100}))
            setDescription("")
        }
        if (postTodoSlice.error !== null) {
            setError(postTodoSlice.error.response?.data?.exception)
        }
    }, [postTodoSlice, dispatch])

    useEffect((): void => {
        if (!deleteTodoSlice.isLoading && deleteTodoSlice.response !== null) {
            dispatch(fetchAllTodos({page: 0, size: 100}))
        }
        if (deleteTodoSlice.error !== null) {
            setError(postTodoSlice.error.response?.data?.exception)
        }
    }, [deleteTodoSlice, dispatch])

    useEffect((): void => {
        if (!updateTodoSlice.isLoading && updateTodoSlice.response !== null) {
            dispatch(fetchAllTodos({page: 0, size: 100}))
        }
        if (updateTodoSlice.error !== null) {
            setError(postTodoSlice.error.response?.data?.exception)
        }
    }, [updateTodoSlice, dispatch])

    return <>
        <App/>
        <div id="myDIV" className="header">
            <h2 style={{margin: "5px"}}>To Do List</h2>
            <input className="input" type="text" placeholder="Açıklama..." value={description}
                   onChange={(event: React.ChangeEvent<HTMLInputElement>): void => setDescription(event.target.value)}
            />
            <span className="addBtn" onClick={() => dispatch(fetchAddTodo({description: description}))}>Ekle</span>
        </div>
        {
            (getTodosSlice.isLoading || postTodoSlice.isLoading || deleteTodoSlice.isLoading || updateTodoSlice.isLoading)
                ? <>Yükleniyor</>
                : <ul id="myUL">
                    {
                        getTodosSlice.response !== null && getTodosSlice.response.items.map((item: TodoDto) =>
                            <li className={item.finished ? "checked" : "not-checked"}>
                                <span onClick={() => dispatch(fetchPatchTodo({id: item.id, description: item.description, finished: !item.finished}))}>{item.description}</span>
                                <span className="close" onClick={() => dispatch(fetchDeleteTodo(item.id))}>×</span>
                            </li>
                        )
                    }
                </ul>
        }
        {(error !== null && error !== "") && <div>{JSON.stringify(error)}</div>}
    </>
}

export default AdminIndex