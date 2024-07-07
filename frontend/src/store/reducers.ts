import {combineReducers} from '@reduxjs/toolkit'
import meSlice from "./features/auth/meSlice"
import loginSlice from "./features/auth/loginSlice"
import registerSlice from "./features/auth/registerSlice"
import getTodosSlice from "./features/auth/getTodosSlice"
import postTodoSlice from "./features/auth/postTodoSlice"
import deleteTodoSlice from "./features/auth/deleteTodoSlice"
import updateTodoSlice from "./features/auth/updateTodoSlice"

export default combineReducers({
    me: meSlice,
    login: loginSlice,
    register: registerSlice,
    getTodos: getTodosSlice,
    postTodo: postTodoSlice,
    deleteTodo: deleteTodoSlice,
    updateTodo: updateTodoSlice,
})