import axios from 'axios';

const API_URL = 'http://localhost:3000/api/templates/'

class TemplatesService {
    
    async createTemplate(jsonTemplate: string){
        try {
            await axios.post(API_URL + 'create', jsonTemplate, {withCredentials: true})
        }
        catch (error) {
            console.log(error)
        }
    }
}

export default new TemplatesService();