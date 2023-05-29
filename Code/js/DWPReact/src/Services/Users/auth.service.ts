import axios from "axios";

const API_URL = "http://localhost:3000/api/users/";

class AuthService {

    login(email: string, password: string) {
    return axios
      .post(API_URL + "login", {
        email: email,
        password: password
      })
        .then((response) => {
            if (response.data) {
                console.log(response.data)
                localStorage.setItem("user", JSON.stringify(response.data));
                axios.get(API_URL+ "info/"+email).then((response) => {
                    console.log("data do info: "+response.data)
                    localStorage.setItem("email",email);
                    localStorage.setItem("nome",response.data.nome);
                    localStorage.setItem("roles",response.data.roles);
                })
            }
            return response.data;
        });
  }

  logout(){
      axios.post(API_URL + "logout").then((response) => {
          localStorage.removeItem("user")
          localStorage.removeItem("email");
          localStorage.removeItem("nome");
          localStorage.removeItem("roles");
          return response.data;
      });
  }

  register(email: string, name: string) {
    return axios
        .post(API_URL + "register", {
      email: email,
      name: name
    });
  }

  async getUserDetails(email: string) {
      try {
          const response = await axios.get(API_URL + "info/" + email);
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
      return { email,nome,roles}
  }

  getCurrentUser() {
    const userStr = localStorage.getItem("user");
    if (userStr) return {token: JSON.parse(userStr).token};

    return null;
  }
}


export default new AuthService();
