import axios from 'axios'

export const api = axios.create({
    baseURL: import.meta.env.VITE_BASE_API,
    timeout: 5000
})

export interface PageResponse {
    code: number
    message: string
    data: {
        list: any
        total: number
    }
}

export interface Response {
    code: number
    message: string
    data: any
}