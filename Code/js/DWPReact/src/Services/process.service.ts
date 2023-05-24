import axios from 'axios';

const API_URL = 'http://localhost:3000/api'

class TemplatesService {

    async getAvailableTemplates() {
        try {
            const response = await axios.get(API_URL + "/templates", {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async getTemplate(templateName) {
        try {
            const response = await axios.get(API_URL + "/templates/" + templateName, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async deleteTemplate(templateName) {
        try {
            const response = await axios.delete(API_URL + "/templates/" + templateName, {withCredentials: true})
            console.log(response.data)
        }
        catch (error) {
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
            window.location.href = "/newprocess"
        }
        catch (error) {
            console.log(error)
        }
    }

    async createProcess(formData) {   
        try {
            const response = await axios.post(API_URL + "/processes", formData, {
                withCredentials: true,
                headers: {'Content-Type': 'multipart/form-data'}
            })
            console.log(response)
            window.location.href = "/processes"
        }
        catch (error) {
            console.log(error)
        }
    }
}

export default new TemplatesService();