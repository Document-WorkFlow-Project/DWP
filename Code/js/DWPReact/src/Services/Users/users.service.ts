import axios from 'axios';
import { API_URL } from '../../utils';

class UsersService {
    async usersList() {
        try {
            const response = await axios.get(`${API_URL}/users`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }
}

export default new UsersService();