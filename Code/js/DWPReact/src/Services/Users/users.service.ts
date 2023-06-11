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
        }
    }

    async getUserDetails(email: string) {
        try {
            const response = await axios.get(`${API_URL}/users/${email}`);
            return response.data
        } catch (error) {
            // Handle any errors that occur during the request
            console.error(error);
            return null; // or throw an error depending on your use case
        }
    }
}

export default new UsersService();