import {createAsyncThunk, createSlice, PayloadAction} from '@reduxjs/toolkit'
import {IState} from '../../types/global'
import UserApiClient from "../../../utils/http-client/UserApiClient"
import {TodoDto} from "../../types/todos"

const userApiClient: UserApiClient = UserApiClient.getInstance()

export const fetchPatchTodo = createAsyncThunk('user/fetchPatchTodo',
    async (params: { id: string, description: string, finished: boolean }, {rejectWithValue}) => {
        try {
            const {data} = await userApiClient.updatetodo(params.id, params.description, params.finished)
            return data
        } catch (error: any) {
            if (!error.response) {
                console.error("Error while updating todo", error.response)
                throw error
            }
            return rejectWithValue(error)
        }
    })

const initialState: IState<TodoDto> = {
    isLoading: false,
    response: null,
    error: null
}

const patchTodoSlice = createSlice({
    name: 'user/patchtodo',
    initialState,
    reducers: {
        reset: () => initialState
    },
    extraReducers: builder => {
        builder.addCase(fetchPatchTodo.pending, state => {
            state.isLoading = true
            state.response = null
            state.error = null
        })

        builder.addCase(fetchPatchTodo.fulfilled, (state, action: PayloadAction<TodoDto>): void => {
            state.isLoading = false
            state.response = action.payload
            state.error = null
        })

        builder.addCase(fetchPatchTodo.rejected, (state, action): void => {
            state.isLoading = false
            state.response = null
            state.error = action.payload
        })
    }
})

export default patchTodoSlice.reducer
export const {
    reset,
} = patchTodoSlice.actions