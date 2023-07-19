import axios from "axios";
import { API_URL } from "../../utils";

class AuthService {
    
    async login(email: string, password: string) {
        const response = await axios.post(`${API_URL}/users/login`, {
            email: email,
            password: password
        })

        return response.data;
    }

    async logout() {
        await axios.post(`${API_URL}/users/logout`)
    }

    async register(email: string, username: string) {
        const response =  await axios.post(`${API_URL}/users/register`, {
            email: email,
            name: username
        }, { withCredentials: true })
        return response.data;
    }

    async updatePass(oldPass: string, newPass: string) {
        const response = await axios.put(`${API_URL}/users/credentials`, {
            password: oldPass,
            newPassword: newPass
        })

        return response.data;
    }
}

export default new AuthService();
