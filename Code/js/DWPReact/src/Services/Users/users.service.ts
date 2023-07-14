import axios from 'axios';
import { API_URL } from '../../utils';

class UsersService {
    
    async usersList() {
        try {
            const response = await axios.get(`${API_URL}/users/list`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

    async getUserDetails(email: string) {
        try {
            const response = await axios.get(`${API_URL}/users/${email}`);
            return response.data
        } catch (error) {
            console.error(error);
            throw error
        }
    }
}

export default new UsersService();