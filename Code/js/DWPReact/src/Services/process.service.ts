import axios from 'axios';

const API_URL = 'http://localhost:3000/api'

class TemplatesService {

    async availableTemplates() {
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

    async pendingStages() {
        try {
            const response = await axios.get(API_URL + "/stages/pending", {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    } 

    async pendingProcesses() {
        try {
            const response = await axios.get(API_URL + "/processes/pending", {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async finishedProcesses() {
        try {
            const response = await axios.get(API_URL + "/processes/finished", {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

}

export default new TemplatesService();