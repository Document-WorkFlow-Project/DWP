import { Routes, Route, Link } from "react-router-dom";
import Home from "./Components/Home/home.component";
import Templates from "./Components/Templates/templates.component";
import {NewProcess} from "./Components/Processes/newProcess";
import {Processes} from "./Components/Processes/processes";
import { Roles } from "./Components/Roles/roles.component";

export default function App() {

    return (
        <div>
            <div className="navbar-container">
                <Link to={"/"} className="navbar-brand">Home </Link>
                <Link to={"/processes"} className="navbar-brand"> Processos </Link>
                <Link to={"/templates"} className="navbar-brand"> Templates </Link>
                <Link to={"/roles"} className="navbar-brand"> Papéis </Link>
            </div>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/processes" element={<Processes/>}/>
                <Route path="/newprocess" element={<NewProcess/>}/>
                <Route path="/templates" element={<Templates/>}/>
                <Route path="/roles" element={<Roles/>}/>
            </Routes>
        </div>
    )
}