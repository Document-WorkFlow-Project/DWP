import axios from 'axios';

const API_URL = 'http://localhost:3000/api'

class TemplatesService {

    async getTemplates(setTemplates) {
        try {
            const response = await axios.get(API_URL + "/templates", {withCredentials: true})
            setTemplates(response.data)
        }
        catch (error) {
            setTemplates([])
            console.log(error)
        }
    }
    
    async saveTemplate(formData) {
        try {
            const response = await axios.post(API_URL + "/templates", formData, {
                withCredentials: true, 
                headers: { 'Content-Type': `multipart/form-data; boundary=${formData._boundary}` }
            })
            console.log(response)
            window.location.href = "/process"
        }
        catch (error) {
            console.log(error)
        }
    }
}

export default new TemplatesService();