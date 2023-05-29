import React, {useEffect, useState} from "react";
import AuthService from "../../Services/Users/auth.service";
import processServices from "../../Services/Processes/process.service";


const Profile =  () => {

    const [info, setInfo] = useState({nome:"",email:"",roles:""})

    useEffect( () => {

        const fetchInfo = async () => {
            setInfo(AuthService.getCurrentUserInfo())
        }

        fetchInfo()
    }, [])

    return (
        <div className="container">
            <header className="jumbotron">
                <h3>
                    Perfil de <strong>{info.nome}</strong>
                </h3>
            </header>
            <p>
                <strong>Email:</strong> {info.email}
            </p>
            <strong>Papeis:</strong>
            <ul>
                {info.roles.split(',').map((role, index) => (
                    <li key={index}>{role.trim()}</li>
                ))}
            </ul>
        </div>
    );
};

export default Profile;