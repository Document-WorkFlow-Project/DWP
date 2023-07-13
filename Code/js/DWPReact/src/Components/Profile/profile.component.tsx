import {useContext, useEffect, useState} from "react";
import { AuthContext } from "../../AuthProvider";
import authService from "../../Services/Users/auth.service";
import {toast, ToastContainer} from "react-toastify";

const Profile =  () => {

    const { loggedUser } = useContext(AuthContext);

    const [currentPass, setCurrentPass] = useState("")
    const [newPass, setNewPass] = useState("")
    const [repeatNewPass, setRepeatNewPass] = useState("")
    
    const [error, setError] = useState("")

    useEffect( () => {
        console.log(loggedUser.roles)
    }, [])

    function changePassword() {
        if (newPass !== repeatNewPass) {
            setError("Palavras passe não coincidem.")
            return;
        } else if (currentPass === newPass) {
            setError("A nova palavra passe não pode ser igual à atual.")
            return;
        }

        authService.updatePass(currentPass, newPass).then(
            (response) => {
                toast.success(response);
            },
            (error) => {
                console.log(error)
                const resMessage = (error.response &&  error.response.data ) || error.toString();
                toast.error(resMessage);
            }
        );
    }

    return (
        <div className="container-fluid">
            <div className="row">
                <div className="col-4">
                    <p></p>
                    <h2>{loggedUser.nome}</h2>
                    <p></p>
                    <p><strong>Email:</strong> {loggedUser.email}</p>
                    <strong>Papéis atribuídos:</strong>
                    <p></p>
                    {loggedUser.roles.length > 0 ?
                        <ul className="list-group">
                            {loggedUser.roles.map((role, index) => (
                                <li className="list-group-item" key={index}>{role.trim()}</li>
                            ))}
                        </ul>
                    :
                        <p className="error">Nenhum papel atribuído.</p>
                    }
                </div>

                <div className="col-2">

                </div>

                <div className="col-4">
                    <p></p>
                    <h2>Alterar palavra-passe</h2>
                    <p></p>
                    <form onSubmit={(e) => {
                            e.preventDefault()
                            changePassword()
                        }}>
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

                        <input className="btn btn-primary" type="submit" value="Alterar palavra-passe"></input>
                    </form>
                </div>
            </div>
            
        </div>
    );
};

export default Profile;