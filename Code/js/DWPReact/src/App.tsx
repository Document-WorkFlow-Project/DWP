import { Routes, Route, Link } from "react-router-dom";
import Home from "./Components/Home/home.component";
import Templates from "./Components/Templates/templates.component";
import { NewProcess } from "./Components/Processes/newProcess";
import { Processes } from "./Components/Processes/processes";
import { Roles } from "./Components/Roles/roles.component";
import "./App.css";
import {useEffect, useState} from "react";
import AuthService from "./Services/Users/auth.service";
import Login from "./Components/LoginForm/login.component";
import Profile from "./Components/Profile/profile.component";

export default function App() {

    const [currentUser, setCurrentUser] = useState(undefined);
    const [showAdminBoard, setShowAdminBoard] = useState(false);
    const [showLogin, setShowLogin] = useState(false);


    useEffect(() => {
        /*
        // Function to retrieve the cookie value
        const getCookie = (name) => {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) return parts.pop().split(';').shift();
        };

        // Retrieve the cookie value
        const userCookie = getCookie('user');

        // Update the current user state
        setCurrentUser(userCookie);

         */
        const user = AuthService.getCurrentUserInfo();

        if (user) {
            setCurrentUser(user);
            setShowAdminBoard(user.roles.includes("ROLE_ADMIN"));
        }
    },[])

    const toggleLogin = () => {
        setShowLogin(!showLogin);
    };

    return (
        <div>
            <div className="navbar-container">
                <div className="navbar-login">
                    {currentUser ? (
                        <div className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <Link to={"/profile"} className="nav-link">
                                    {currentUser.username}
                                </Link>
                            </li>
                            <li className="nav-item">
                                <a href="/" className="nav-link" onClick={() => AuthService.logout()}>
                                    Logout
                                </a>
                            </li>
                        </div>
                    ) : (
                        <div className="navbar-nav ml-auto">
                            <li className="nav-item">
                                <button className="loginicon" onClick={toggleLogin}>
                                    Login
                                </button>
                            </li>
                        </div>
                    )}
                    {showLogin && (
                        <div className="popup-container">
                            <div className="popup">
                                <button className="close-button" onClick={toggleLogin}>
                                    X
                                </button>
                                <Login />
                            </div>
                        </div>
                    )}
                </div>
                <Link to={"/"} className="navbar-brand">
                    Home
                </Link>
                <Link to={"/processes"} className="navbar-brand">
                    Processos
                </Link>
                <Link to={"/templates"} className="navbar-brand">
                    Templates
                </Link>
                <Link to={"/roles"} className="navbar-brand">
                    Papéis
                </Link>
                {currentUser && currentUser.roles.includes("ROLE_ADMIN") && (
                    <Link to={"/admin"} className="navbar-brand">
                        Administrador
                    </Link>
                )}
            </div>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/processes" element={<Processes />} />
                <Route path="/newprocess" element={<NewProcess />} />
                <Route path="/templates" element={<Templates />} />
                <Route path="/roles" element={<Roles />} />
                <Route path="/admin" element={<Admin />} /> {/* Added admin route */}
                <Route path="/profile" element={<Profile/>}/>
            </Routes>

        </div>
    );
}

function Admin() {
    return <h1>Admin Page</h1>; {/* Sample content for the admin route */}
}
