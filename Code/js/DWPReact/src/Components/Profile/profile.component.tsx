import React from "react";
import AuthService from "../../Services/Users/auth.service";


const Profile =  () => {
    const currentUser =  AuthService.getCurrentUserInfo();

    return (
        <div className="container">
            <header className="jumbotron">
                <h3>
                    <strong>{currentUser.nome}</strong> Profile
                </h3>
            </header>
            <p>
                <strong>Email:</strong> {currentUser.email}
            </p>
            <strong>Authorities:</strong>
            <ul>
                {currentUser.roles &&
                    currentUser.roles.map((role, index) => <li key={index}>{role}</li>)}
            </ul>
        </div>
    );
};

export default Profile;