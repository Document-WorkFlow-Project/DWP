import axios from 'axios';

const API_URL = 'http://localhost:3000/api/users'

class UsersService {
    async usersList() {
        try {
            const response = await axios.get(API_URL, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }
}

export default new UsersService();