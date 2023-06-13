import axios from 'axios';
import { API_URL } from '../utils';

class CommentsService {

    async stageComments(stageId) {
        try {
            const response = await axios.get(`${API_URL}/stages/${stageId}/comments`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    }
    
    async postComment(stageId, comment) {
        const newComment = {text: comment}
        try {
            await axios.post(`${API_URL}/stages/${stageId}/comments`, newComment, {withCredentials: true})
            window.location.href = `/stage/${stageId}`
        }
        catch (error) {
            console.log(error)
        }
    }

    async deleteComment(commentId) {
        try {
            const response = await axios.delete(`${API_URL}/stages/comments/${commentId}`, {withCredentials: true})
            return response.data
        }
        catch (error) {
            console.log(error)
            throw(error)
        }
    }


}

export default new CommentsService();