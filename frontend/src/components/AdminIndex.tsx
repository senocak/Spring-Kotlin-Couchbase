import React from 'react'
import {useAppDispatch} from '../store'
import App from "./App"

function AdminIndex(): React.JSX.Element {
    const dispatch = useAppDispatch()

    return <>
        <App/>
        Admin Index
    </>
}

export default AdminIndex