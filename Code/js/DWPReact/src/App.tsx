import {Routes, Route, Link} from "react-router-dom";
import Home from "./Components/Home/home.component";
import Templates from "./Components/Templates/templates.component";
import {NewProcess} from "./Components/Processes/newProcess";
import {Processes} from "./Components/Processes/processes";
import {Roles} from "./Components/Roles/roles.component";
import "./App.css";
import {useState, useContext} from "react";
import AuthService from "./Services/Users/auth.service";
import Login from "./Components/LoginForm/login.component";
import Profile from "./Components/Profile/profile.component";
import {ProcessDetails} from "./Components/Processes/processDetails";
import {StageDetails} from "./Components/Stages/stageDetails";
import AddUsers from "./Components/AddUsers/addusers.component";
import { AuthContext } from "./AuthProvider";

export default function App() {

    const { loggedUser } = useContext(AuthContext);

    const [showLogin, setShowLogin] = useState(false);

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
                    {loggedUser.email && (
                        <Link to={"/processes"} className="navbar-brand">
                            Processos
                        </Link>
                    )}
                    {loggedUser.email && loggedUser.roles.includes("admin") && (
                        <div>
                            <Link to="/templates" className="navbar-brand">
                                Templates
                            </Link>
                            <Link to="/roles" className="navbar-brand">
                                Papéis
                            </Link>
                            <Link to="/addusers" className="navbar-brand">
                                Adicionar Utilizadores
                            </Link>
                        </div>

                    )}
                    {loggedUser.email ? (
                        <div className="navbar-login-right">
                            <div className="navbar-profile">
                                <Link to={"/profile"} className="nav-link">
                                    {loggedUser.nome}
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
                                <Login
                                    onClose={toggleLogin}
                                />
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
                <Route path="/profile" element={<Profile/>}/>
                <Route path="/addusers" element={<AddUsers/>}/>
            </Routes>

        </div>
    );
}


