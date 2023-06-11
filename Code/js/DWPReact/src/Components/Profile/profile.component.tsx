import {useContext} from "react";
import { AuthContext } from "../../AuthProvider";

const Profile =  () => {

    const { loggedUser } = useContext(AuthContext);

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
            {loggedUser.roles > 0 ?
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