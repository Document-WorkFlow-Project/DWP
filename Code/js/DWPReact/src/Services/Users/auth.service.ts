import axios from "axios";
import { API_URL } from "../../utils";

class AuthService {
    
    async login(email: string, password: string) {
        const response = await axios.post(`${API_URL}/users/login`, {
            email: email,
            password: password
        })
        
        if (response.data) {
            console.log(response.data)
            localStorage.setItem("user", JSON.stringify(response.data));
            
            const res = await axios.get(`${API_URL}/users/info/${email}`)
                
            console.log("data do info: " + res.data)
            localStorage.setItem("email", email);
            localStorage.setItem("nome", res.data.nome);
            localStorage.setItem("roles", res.data.roles); 
        }

        return response.data;
    }

    async logout() {
        localStorage.removeItem("user")
        localStorage.removeItem("email");
        localStorage.removeItem("nome");
        localStorage.removeItem("roles");
        
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

    getCurrentUserInfo() {
        const email = localStorage.getItem("email");
        const nome = localStorage.getItem("nome");
        const roles = localStorage.getItem("roles");
        return {email, nome, roles}
    }

    getCurrentUser() {
        const userStr = localStorage.getItem("user");
        if (userStr) return {token: JSON.parse(userStr).token};

        return null;
    }
}

export default new AuthService();
