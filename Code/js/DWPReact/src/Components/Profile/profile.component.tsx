import {useContext, useEffect, useState} from "react";
import { AuthContext } from "../../AuthProvider";

const Profile =  () => {

    const { loggedUser } = useContext(AuthContext);

    const [currentPass, setCurrentPass] = useState("")
    const [newPass, setNewPass] = useState("")
    const [repeatNewPass, setRepeatNewPass] = useState("")
    
    const [error, setError] = useState("")

    useEffect( () => {
        console.log(loggedUser.roles)
    }, [])

    async function changePassword() {
        if (newPass !== repeatNewPass) {
            setError("Palavras passe não coincidem.")
        }
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
                    <h3>Alterar palavra-passe</h3>
                    <form onSubmit={(e) => {
                            e.preventDefault()
                            changePassword()
                        }}>
                        <p>Palavra-passe atual:</p>
                        <input className="form-control" required={true} type="password" value={currentPass} onChange={e => {setCurrentPass(e.target.value)}}/>
                        <p>Nova palavra-passe:</p>
                        <input className="form-control" required={true} type="password" value={newPass} onChange={e => {setNewPass(e.target.value)}}/>
                        <p>Repetir nova palavra-passe:</p>
                        <input className="form-control" required={true} type="password" value={repeatNewPass} onChange={e => {setRepeatNewPass(e.target.value)}}/>
                        <p className="error">{error}</p>

                        <input className="btn btn-primary" type="submit" value="Alterar palavra-passe"></input>
                    </form>
                </div>
            </div>
            
        </div>
    );
};

export default Profile;