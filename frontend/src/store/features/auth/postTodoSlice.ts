import {createAsyncThunk, createSlice, PayloadAction} from '@reduxjs/toolkit'
import {IState} from '../../types/global'
import UserApiClient from "../../../utils/http-client/UserApiClient"
import {TodoDto} from "../../types/todos"

const userApiClient: UserApiClient = UserApiClient.getInstance()

export const fetchAddTodo = createAsyncThunk('user/fetchAddTodo',
    async (params: { description: string }, {rejectWithValue}) => {
        try {
            const {data} = await userApiClient.addtodo(params.description)
            return data
        } catch (error: any) {
            if (!error.response) {
                console.error("Error while posting new todo", error.response)
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

const addTodoSlice = createSlice({
    name: 'user/addtodo',
    initialState,
    reducers: {
        reset: () => initialState
    },
    extraReducers: builder => {
        builder.addCase(fetchAddTodo.pending, state => {
            state.isLoading = true
            state.response = null
            state.error = null
        })

        builder.addCase(fetchAddTodo.fulfilled, (state, action: PayloadAction<TodoDto>): void => {
            state.isLoading = false
            state.response = action.payload
            state.error = null
        })

        builder.addCase(fetchAddTodo.rejected, (state, action): void => {
            state.isLoading = false
            state.response = null
            state.error = action.payload
        })
    }
})

export default addTodoSlice.reducer
export const {
    reset,
} = addTodoSlice.actions