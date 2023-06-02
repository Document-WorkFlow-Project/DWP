import axios from 'axios';
import { API_URL } from '../../utils';

class addusersservice {

    async registeruser(email: string, username: string) {
        try {
            const response = await axios.post(`${API_URL}/users/register`, {
                email: email,
                name: username
            }, { withCredentials: true })
            return response.data
        } catch (error) {
            console.log(error)
        }
    }
}

export default new addusersservice();