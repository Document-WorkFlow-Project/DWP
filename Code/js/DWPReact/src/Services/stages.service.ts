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
            await axios.put(`${API_URL}/stages/sign/${stageId}?approve=${value}`, {withCredentials: true})
            window.location.href = `/stage/${stageId}`
        }
        catch (error) {
            console.log(error)
        }
    }

    async stageSignatures(stageId) {
        try {
            const response = await axios.get(`${API_URL}/stages/${stageId}/signatures`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            return []
        }
    }


}

export default new StagesService();
