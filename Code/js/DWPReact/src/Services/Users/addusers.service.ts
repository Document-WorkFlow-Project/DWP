import axios from 'axios';
import { API_URL } from '../../utils';

class addusersservice {

    async registeruser(email: string, username: string) {
        return await axios.post(`${API_URL}/users/register`, {
                email: email,
                name: username
        }, { withCredentials: true })
                .then(async (response) => {
                    return response.data;})
    }
}

export default new addusersservice();