import {Routes, Route, Link} from "react-router-dom";
import Home from "./Components/Home/home.component";
import Templates from "./Components/Templates/templates.component";
import {NewProcess} from "./Components/Processes/newProcess";
import {Processes} from "./Components/Processes/processes";
import {Roles} from "./Components/Roles/roles.component";
import "./App.css";
import {useEffect, useState} from "react";
import AuthService from "./Services/Users/auth.service";
import Login from "./Components/LoginForm/login.component";
import Profile from "./Components/Profile/profile.component";
import {ProcessDetails} from "./Components/Processes/processDetails";
import {StageDetails} from "./Components/Stages/stageDetails";
import Admin from "./Components/Admin/admin.component";
import AddUsers from "./Components/AddUsers/addusers.component";

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

        console.log(user)
        if (user.email != null) {
            setCurrentUser(user);
            //setShowAdminBoard(user.roles.includes("admin"));
        }
    }, [])

    const toggleLogin = () => {
        setShowLogin(!showLogin);
    };

    return (
        <div>
            <div className="navbar-container">
                <Link to={"/"} className="navbar-brand">
                    Home
                </Link>
                <div className="navbar-login">
                    {currentUser && (
                        <Link to={"/processes"} className="navbar-brand">
                            Processos
                        </Link>
                    )}
                    {currentUser && currentUser.roles.includes("admin") && (
                        <div>
                            <Link to={"/admin"} className="navbar-brand">
                                Administrador
                            </Link>
                        </div>

                    )}
                    {currentUser ? (
                        <div className="navbar-login-right">
                            <div className="navbar-profile">
                                <Link to={"/profile"} className="nav-link">
                                    {currentUser.nome}
                                </Link>
                                <button className="loginicon" onClick={async () => await AuthService.logout()}>
                                    Logout
                                </button>
                            </div>
                        </div>
                    ) : (
                        <div className="navbar-nav">
                            <a className="nav-item">
                                <button className="loginicon" onClick={toggleLogin}>
                                    Login
                                </button>
                            </a>
                        </div>
                    )}
                    {showLogin && (
                        <div className="popup-container">
                            <div className="popup">
                                <button className="close-button" onClick={toggleLogin}>
                                    X
                                </button>
                                <Login/>
                            </div>
                        </div>
                    )}
                </div>
            </div>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/processes" element={<Processes/>}/>
                <Route path="/newprocess" element={<NewProcess/>}/>
                <Route path="/process/:id" element={<ProcessDetails/>}/>
                <Route path="/stage/:id" element={<StageDetails/>}/>
                <Route path="/templates" element={<Templates/>}/>
                <Route path="/roles" element={<Roles/>}/>
                <Route path="/admin" element={<Admin/>}/> {/* Added admin route */}
                <Route path="/profile" element={<Profile/>}/>
                <Route path="/addusers" element={<AddUsers/>}/>
            </Routes>

        </div>
    );
}


