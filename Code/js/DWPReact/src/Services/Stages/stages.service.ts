import axios from 'axios';
import { API_URL } from '../../utils';

class StagesService {

    async stageDetails(stageId) {
        try {
            const response = await axios.get(`${API_URL}/stages/${stageId}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }
    
    async signStage(stageId, value) {
        try {
            await axios.put(`${API_URL}/stages/sign/${stageId}?approve=${value}`, {withCredentials: true})
            window.location.href = `/stage/${stageId}`
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

    async stageSignatures(stageId) {
        try {
            const response = await axios.get(`${API_URL}/stages/${stageId}/signatures`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }


}

export default new StagesService();
