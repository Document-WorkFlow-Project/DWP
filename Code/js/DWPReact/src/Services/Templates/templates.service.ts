import axios from 'axios';
import { API_URL } from '../../utils';

class TemplatesService {

    async allTemplates() {
        try {
            const response = await axios.get(`${API_URL}/templates/all`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

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

    async setTemplateAvailability(templateName, value) {
        try {
            const response = await axios.put(`${API_URL}/templates/${templateName}?active=${value}`, {withCredentials: true})
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
    
    async saveTemplate(templateJson) {
        try {
            const response = await axios.post(`${API_URL}/templates`, templateJson, { withCredentials: true })
            console.log(response)
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }
}

export default new TemplatesService();