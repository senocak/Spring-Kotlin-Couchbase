export interface User {
    name: string
    email: string
    roles: Role[]
    emailActivatedAt: number
}

export interface Role {
    name: string
}
