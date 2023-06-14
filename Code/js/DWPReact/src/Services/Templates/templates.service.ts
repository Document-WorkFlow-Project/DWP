import axios from 'axios';
import { API_URL } from '../../utils';

class TemplatesService {
    async availableTemplates() {
        try {
            const response = await axios.get(`${API_URL}/templates`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

    async getTemplate(templateName) {
        try {
            const response = await axios.get(`${API_URL}/templates/${templateName}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

    async deleteTemplate(templateName) {
        try {
            const response = await axios.delete(`${API_URL}/templates/${templateName}`, {withCredentials: true})
            console.log(response.data)
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

    async templateUsers(templateName) {
        try {
            const response = await axios.get(`${API_URL}/templates/${templateName}/users`, {withCredentials: true})
            console.log(response.data)
            return response.data
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

    async addUserToTemplate(templateName, user) {
        try {
            const response = await axios.put(`${API_URL}/templates/${templateName}/${user}`, {withCredentials: true})
            console.log(response.data)
            return response
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

    async removeUSerFromTemplate(templateName, user) {
        try {
            const response = await axios.delete(`${API_URL}/templates/${templateName}/${user}`, {withCredentials: true})
            console.log(response.data)
            return response
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }
    
    async saveTemplate(formData) {
        try {
            const response = await axios.post(`${API_URL}/templates`, formData, {
                withCredentials: true, 
                headers: { 'Content-Type': `multipart/form-data; boundary=${formData._boundary}` }
            })
            console.log(response)
            window.location.href = "/newprocess"
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }
}

export default new TemplatesService();