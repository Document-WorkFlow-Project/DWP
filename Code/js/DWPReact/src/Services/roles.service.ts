import axios from 'axios';
import { API_URL } from '../utils';

class RolesService {

    async availableRoles() {
        try {
            const response = await axios.get(`${API_URL}/roles`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async saveRole(role) {
        try {
            const response = await axios.post(`${API_URL}/roles`, role, {withCredentials: true})
            console.log(response.data)
            window.location.href = "/roles"
        }
        catch (error) {
            console.log(error)
        }
    }

    async deleteRole(roleName) {
        try {
            const response = await axios.delete(`${API_URL}/roles/${roleName}`, {withCredentials: true})
            console.log(response.data)
            window.location.href = "/roles"
        }
        catch (error) {
            console.log(error)
        }
    }

    async roleDetails(roleName) {
        try {
            const response = await axios.get(`${API_URL}/roles/${roleName}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async roleUsers(roleName) {
        try {
            const response = await axios.get(`${API_URL}/roles/${roleName}/users`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async addRoleToUSer(roleName, email) {
        try {
            const response = await axios.put(`${API_URL}/roles/${roleName}/${email}`, {withCredentials: true})
            console.log(response.data)
            return response
        }
        catch (error) {
            console.log(error)
        }
    }

    async removeRoleFromUser(roleName, email) {
        try {
            const response = await axios.delete(`${API_URL}/roles/${roleName}/${email}`, {withCredentials: true})
            return response
        }
        catch (error) {
            console.log(error)
        }
    }
}

export default new RolesService();