import {createAsyncThunk, createSlice, PayloadAction} from '@reduxjs/toolkit'
import {IState} from '../../types/global'
import UserApiClient from "../../../utils/http-client/UserApiClient"

const userApiClient: UserApiClient = UserApiClient.getInstance()

export const fetchDeleteTodo = createAsyncThunk('user/fetchDeleteTodo',
    async (id: string, {rejectWithValue}) => {
        try {
            const {data} = await userApiClient.deletetodo(id)
            return data
        } catch (error: any) {
            if (!error.response) {
                console.error("Error while deleting todo", error.response)
                throw error
            }
            return rejectWithValue(error)
        }
    })

const initialState: IState<void> = {
    isLoading: false,
    response: null,
    error: null
}

const deleteTodoSlice = createSlice({
    name: 'user/deletetodo',
    initialState,
    reducers: {
        reset: () => initialState
    },
    extraReducers: builder => {
        builder.addCase(fetchDeleteTodo.pending, state => {
            state.isLoading = true
            state.response = null
            state.error = null
        })

        builder.addCase(fetchDeleteTodo.fulfilled, (state, action: PayloadAction<void>): void => {
            state.isLoading = false
            state.response = action.payload
            state.error = null
        })

        builder.addCase(fetchDeleteTodo.rejected, (state, action): void => {
            state.isLoading = false
            state.response = null
            state.error = action.payload
        })
    }
})

export default deleteTodoSlice.reducer
export const {
    reset,
} = deleteTodoSlice.actions