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
        window.location.href = "/"
    }

    async register(email: string, username: string) {
        const response =  await axios.post(`${API_URL}/users/register`, {
            email: email,
            name: username
        }, { withCredentials: true })
        return response.data;
    }

}

export default new AuthService();
