import {useContext, useEffect} from "react";
import { AuthContext } from "../../AuthProvider";

const Profile =  () => {

    const { loggedUser } = useContext(AuthContext);


    useEffect( () => {
        console.log(loggedUser.roles)
    }, [])


    return (
        <div className="container">
            <header className="jumbotron">
                <h3>
                    Perfil de <strong>{loggedUser.nome}</strong>
                </h3>
            </header>
            <p>
                <strong>Email:</strong> {loggedUser.email}
            </p>
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
        </div>
    );
};

export default Profile;