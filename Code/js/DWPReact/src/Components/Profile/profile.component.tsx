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
        <div className="container">
            <h2>{loggedUser.nome}</h2>
            <p><strong>Email:</strong> {loggedUser.email}</p>
            <strong>Papeis:</strong>
            {loggedUser.roles.length > 0 ?
                <ul>
                    {loggedUser.roles.map((role, index) => (
                        <li key={index}>{role.trim()}</li>
                    ))}
                </ul>
            :
                <p className="error">Nenhum papel atribuído.</p>
            }
            
            <h3>Alterar palavra-passe</h3>
            <form onSubmit={(e) => {
                    e.preventDefault()
                    changePassword()
                }}>
                <p>Palavra-passe atual:</p>
                <input required={true} type="password" value={currentPass} onChange={e => {setCurrentPass(e.target.value)}}/>
                <p>Nova palavra-passe:</p>
                <input required={true} type="password" value={newPass} onChange={e => {setNewPass(e.target.value)}}/>
                <p>Repetir nova palavra-passe:</p>
                <input required={true} type="password" value={repeatNewPass} onChange={e => {setRepeatNewPass(e.target.value)}}/>
                <p className="error">{error}</p>

                <input type="submit" value="Alterar palavra-passe"></input>
            </form>
        </div>
    );
};

export default Profile;