import axios from "axios";
import { API_URL } from "../../utils";

class AuthService {
    async login(email: string, password: string) {
        return await axios.post(`${API_URL}/users/login`, {
            email: email,
            password: password
        })
            .then(async (response) => {
                if (response.data) {
                    console.log(response.data)
                    localStorage.setItem("user", JSON.stringify(response.data));
                    await axios.get(`${API_URL}/users/info/${email}`)
                        .then((response) => {
                            console.log("data do info: " + response.data)
                            localStorage.setItem("email", email);
                            localStorage.setItem("nome", response.data.nome);
                            localStorage.setItem("roles", response.data.roles);
                        })
                }
                return response.data;
            });
    }

    async logout() {
        localStorage.removeItem("user")
        localStorage.removeItem("email");
        localStorage.removeItem("nome");
        localStorage.removeItem("roles");
        await axios.post(`${API_URL}/users/logout`).then((response) => {
            window.location.href = "/"
        });
    }

    register(email: string, name: string) {
        return axios
            .post(`${API_URL}/users/register`, {
                email: email,
                name: name
            });
    }

    async getUserDetails(email: string) {
        try {
            const response = await axios.get(`${API_URL}/users/info/${email}`);
            return {
                email: response.data.email,
                nome: response.data.nome,
                roles: response.data.roles
            };
        } catch (error) {
            // Handle any errors that occur during the request
            console.error(error);
            return null; // or throw an error depending on your use case
        }
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
