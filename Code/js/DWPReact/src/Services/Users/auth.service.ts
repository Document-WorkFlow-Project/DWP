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
                    sessionStorage.setItem("user", JSON.stringify(response.data));
                    await axios.get(`${API_URL}/users/info/${email}`)
                        .then((response) => {
                            console.log("data do info: " + response.data)
                            sessionStorage.setItem("email", email);
                            sessionStorage.setItem("nome", response.data.nome);
                            sessionStorage.setItem("roles", response.data.roles);
                        })
                }
                return response.data;
            });
    }

    async logout() {
        sessionStorage.removeItem("user")
        sessionStorage.removeItem("email");
        sessionStorage.removeItem("nome");
        sessionStorage.removeItem("roles");
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
        const email = sessionStorage.getItem("email");
        const nome = sessionStorage.getItem("nome");
        const roles = sessionStorage.getItem("roles");
        return {email, nome, roles}
    }

    getCurrentUser() {
        const userStr = sessionStorage.getItem("user");
        if (userStr) return {token: JSON.parse(userStr).token};

        return null;
    }
}


export default new AuthService();
