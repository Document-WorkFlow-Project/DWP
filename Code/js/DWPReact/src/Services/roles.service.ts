import axios from 'axios';

const API_URL = 'http://localhost:3000/api/roles'

class RolesService {

    async availableRoles() {
        try {
            const response = await axios.get(API_URL, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async newRole(role) {
        try {
            const response = await axios.post(API_URL, role, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async deleteRole(roleName) {
        try {
            const response = await axios.delete(API_URL + "/" + roleName, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async roleDetails(roleName) {
        try {
            const response = await axios.get(API_URL + "/" + roleName, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async addRoleToUSer(roleName, email) {
        try {
            const response = await axios.put(API_URL + `/${roleName}/${email}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }

    async removeRoleFromUser(roleName, email) {
        try {
            const response = await axios.delete(API_URL + `/${roleName}/${email}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
        }
    }
}

export default new RolesService();