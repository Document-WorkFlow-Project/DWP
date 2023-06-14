import axios from 'axios';
import { API_URL } from '../../utils';

class ProcessesService {

    async createProcess(formData) {   
        try {
            const response = await axios.post(`${API_URL}/processes`, formData, {
                withCredentials: true,
                headers: {'Content-Type': 'multipart/form-data'}
            })
            console.log(response)
            window.location.href = "/processes"
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    }

    async pendingStages() {
        try {
            const response = await axios.get(`${API_URL}/stages/pending`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    } 

    async   finishedStages() {
        try {
            const response = await axios.get(`${API_URL}/stages/finished`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    } 

    async pendingProcesses() {
        try {
            const response = await axios.get(`${API_URL}/processes/pending`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    }

    async finishedProcesses() {
        try {
            const response = await axios.get(`${API_URL}/processes/finished`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw error
        }
    }

    async processDetails(processId) {
        try {
            const response = await axios.get(`${API_URL}/processes/${processId}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    }

    async processDocDetails(processId) {
        try {
            const response = await axios.get(`${API_URL}/processes/${processId}/docsInfo`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    }

    async downloadDocs(processId) {
        try {
            const response = await axios.get(`${API_URL}/processes/${processId}/docs`, {withCredentials: true, responseType: 'blob'})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    }

    async processStages(processId) {
        try {
            const response = await axios.get(`${API_URL}/processes/${processId}/stages`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    }

}

export default new ProcessesService();