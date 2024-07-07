import {createAsyncThunk, createSlice, PayloadAction} from '@reduxjs/toolkit'
import {IState} from '../../types/global'
import UserApiClient from "../../../utils/http-client/UserApiClient"
import {TodoItemPaginationDTO} from "../../types/todos"

const userApiClient: UserApiClient = UserApiClient.getInstance()

export const fetchAllTodos = createAsyncThunk('user/fetchAllTodos',
    async (params: { page: number, size: number }, {rejectWithValue}) => {
        try {
            const {data} = await userApiClient.alltodos(params.page, params.size)
            return data
        } catch (error: any) {
            if (!error.response) {
                console.error("Error while fetching all todos", error.response)
                throw error
            }
            return rejectWithValue(error)
        }
    })

const initialState: IState<TodoItemPaginationDTO> = {
    isLoading: false,
    response: null,
    error: null
}

const allTodosSlice = createSlice({
    name: 'user/gettodos',
    initialState,
    reducers: {
        reset: () => initialState
    },
    extraReducers: builder => {
        builder.addCase(fetchAllTodos.pending, state => {
            state.isLoading = true
            state.response = null
            state.error = null
        })

        builder.addCase(fetchAllTodos.fulfilled, (state, action: PayloadAction<TodoItemPaginationDTO>): void => {
            state.isLoading = false
            state.response = action.payload
            state.error = null
        })

        builder.addCase(fetchAllTodos.rejected, (state, action): void => {
            state.isLoading = false
            state.response = null
            state.error = action.payload
        })
    }
})

export default allTodosSlice.reducer
export const {
    reset,
} = allTodosSlice.actions