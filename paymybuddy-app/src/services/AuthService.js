import axios from "axios";

const API_URL = "http://localhost:8081"

export default {
    login(email, password) {
        return axios.post(`${API_URL}/api/auth/login`, {
            email: email,
            password: password
        })
    },
    register(username, email, password) {
        return axios.post(`${API_URL}/api/auth/register`, {
            username: username,
            email: email, 
            password: password
        })
    }
}