import {Routes, Route, Link, useNavigate} from "react-router-dom";
import Home from "./Components/Home/home.component";
import Templates from "./Components/Templates/templates.component";
import {NewProcess} from "./Components/Processes/newProcess";
import {Processes} from "./Components/Processes/processes";
import {Roles} from "./Components/Roles/roles.component";
import "./App.css";
import {useState, useContext} from "react";
import AuthService from "./Services/Users/auth.service";
import Login from "./Components/LoginForm/login.component";
import {Profile} from "./Components/Profile/profile.component";
import {ProcessDetails} from "./Components/Processes/processDetails";
import {StageDetails} from "./Components/Stages/stageDetails";
import AddUsers from "./Components/AddUsers/addusers.component";
import { AuthContext } from "./AuthProvider";
import {toast} from "react-toastify";

export default function App() {

    const navigate = useNavigate();

    const { loggedUser, checkAuth } = useContext(AuthContext);

    const [showLogin, setShowLogin] = useState(false);

    const toggleLogin = () => {
        setShowLogin(!showLogin);
    };

    async function handleLogout() {
        await AuthService.logout()
        await checkAuth()
        navigate('/');
        toast.success("Logout feito com sucesso.")
    }

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
                                <Link to={`/profile/${loggedUser.email}`} className="nav-link">
                                    {loggedUser.nome}
                                </Link>
                                <button 
                                    className="loginicon" 
                                    onClick={handleLogout}>
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
                                    navigate={navigate}
                                />
                            </div>
                        </div>
                    )}
                </div>
            </div>
            
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/processes" element={<Processes navigate={navigate}/>}/>
                <Route path="/newprocess" element={<NewProcess navigate={navigate}/>}/>
                <Route path="/process/:id" element={<ProcessDetails navigate={navigate}/>}/>
                <Route path="/stage/:id" element={<StageDetails navigate={navigate}/>}/>
                <Route path="/templates" element={<Templates navigate={navigate}/>}/>
                <Route path="/roles" element={<Roles navigate={navigate}/>}/>
                <Route path="/profile/:userEmail" element={<Profile navigate={navigate}/>}/>
                <Route path="/addusers" element={<AddUsers navigate={navigate}/>}/>
            </Routes>

        </div>
    );
}


