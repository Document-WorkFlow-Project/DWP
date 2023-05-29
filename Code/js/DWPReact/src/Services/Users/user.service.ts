import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:3000/api';

class UserService {
  let
  getPublicContent() {
    return axios.get(API_URL + 'all');
  }


      getProfileActions() {
        return axios.get(API_URL + '/me/', { withCredentials: true })
            .then(response => {
              return response.data.actions
            })
        }

        getInfoWithRef(href: string){
            return axios.get(API_URL + href, {withCredentials: true})
                .then(response => {
                    return response.data
                })
        }

    async getSecondPlayer(uuid){
        try {
            // Make HTTP request to exit the current match
            const response = await axios.get(API_URL + '/me/running', {withCredentials: true });
            const match = response.data.filter(obj => obj.uuid == uuid)[0]
            console.log(match)
            if(match.player1===localStorage.getItem("username")) return match.player2
            else return match.player1
        } catch (e) {
            localStorage.setItem("error", e.message);
            console.log("Error Detected: "+e)
        }
    };


}

export default new UserService();
