import {useContext, useEffect, useState} from "react";
import { useParams } from 'react-router';
import { AuthContext } from "../../AuthProvider";
import authService from "../../Services/Users/auth.service";
import {toast} from "react-toastify";
import usersService from "../../Services/Users/users.service";

export const Profile = ({ navigate }) => {

    const { userEmail } = useParams();
    const { loggedUser } = useContext(AuthContext);

    const [userDetails, setUserDetails] = useState(loggedUser)

    const [showChangePass, setShowChangePass] = useState(false)
    const [currentPass, setCurrentPass] = useState("")
    const [newPass, setNewPass] = useState("")
    const [repeatNewPass, setRepeatNewPass] = useState("")

    const [error, setError] = useState("")
    const [loading, setLoading] = useState(false);

    async function fetchUserDetails(email) {
        try {
            const user = await usersService.getUserDetails(email)
            setUserDetails(user)
        } catch (err) {
            setShowChangePass(true)
            toast.error(`Erro ao obter o perfil de ${email}. Tente novamente...`)
        }
    }
    
    useEffect( () => {
        if (!loggedUser.email) {
            navigate('/');
            toast.error("O utilizador não tem sessão iniciada.")
        }

        if (userEmail === loggedUser.email){
            setShowChangePass(true)
            setUserDetails(loggedUser)
        } else {
            setShowChangePass(false)
            fetchUserDetails(userEmail)
        }
    }, [userEmail])

    async function changePassword(e) {
        e.preventDefault()

        if (newPass !== repeatNewPass) {
            setError("Palavras passe não coincidem.")
            return;
        } else if (currentPass === newPass) {
            setError("A nova palavra passe não pode ser igual à atual.")
            return;
        }

        setLoading(true)

        try {
            const res = await authService.updatePass(currentPass, newPass)
            toast.success(res);
            setCurrentPass("")
            setNewPass("")
            setRepeatNewPass("")
            setError("")
        } catch(err) {
            const resMessage = err.response.data || err.toString();
            toast.error(resMessage);
        }

        setLoading(false)
    }

    return (
        <div className="container-fluid">
            <div className="row">
                <div className="col-4">
                    <p></p>
                    <h2>{userDetails.nome}</h2>
                    <p></p>
                    <p><strong>Email:</strong> {userDetails.email}</p>
                    <strong>Papéis atribuídos:</strong>
                    <p></p>
                    {userDetails.roles.length > 0 ?
                        <ul className="list-group">
                            {userDetails.roles.map((role, index) => (
                                <li className="list-group-item" key={index}>{role.trim()}</li>
                            ))}
                        </ul>
                    :
                        <p className="error">Nenhum papel atribuído.</p>
                    }
                </div>

                {showChangePass && 
                    <>
                        <div className="col-2">

                        </div>

                        <div className="col-4">
                            <p></p>
                            <h2>Alterar palavra-passe</h2>
                            <p></p>
                            <form onSubmit={changePassword}>
                                <p>Palavra-passe atual:</p>
                                <input className="form-control" required={true} minLength={6} type="password" value={currentPass} onChange={e => {setCurrentPass(e.target.value)}}/>
                                <p></p>
                                <p>Nova palavra-passe:</p>
                                <input className="form-control" required={true} minLength={6} type="password" value={newPass} onChange={e => {setNewPass(e.target.value)}}/>
                                <p></p>
                                <p>Repetir nova palavra-passe:</p>
                                <input className="form-control" required={true} minLength={6} type="password" value={repeatNewPass} onChange={e => {setRepeatNewPass(e.target.value)}}/>
                                <p></p>
                                <p className="error">{error}</p>

                                <input className="btn btn-primary" type="submit" value={loading ? "Loading..." : "Alterar palavra-passe"} disabled={loading}></input>
                            </form>
                        </div>
                    </>
                }
                
            </div>
            
        </div>
    );
};