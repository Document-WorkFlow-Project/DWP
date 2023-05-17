import axios from 'axios';

const API_URL = 'http://localhost:3000/api/processes/templates/'

class TemplatesService {

    async getTemplates(setTemplates){
        try {
            const response = await axios.get(API_URL, {withCredentials: true})
            console.log(response)
            setTemplates(response.data.properties)
        }
        catch (error) {
            setTemplates([])
            console.log(error)
        }
    }
    
    async createTemplate(jsonTemplate: string){
        try {
            await axios.post(API_URL, jsonTemplate, {withCredentials: true})
        }
        catch (error) {
            console.log(error)
        }
    }
}

export default new TemplatesService();