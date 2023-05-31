import axios from 'axios';
import { API_URL } from '../utils';

class StagesService {

    async stageDetails(stageId) {
        try {
            const response = await axios.get(`${API_URL}/stages/${stageId}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }
    
    async signStage(stageId, value) {
        try {
            const response = await axios.put(`${API_URL}/stages/sign/${stageId}?approve=${value}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }


}

export default new StagesService();
