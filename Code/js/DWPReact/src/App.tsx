import { Routes, Route, Link } from "react-router-dom";
import Home from "./Components/Home/home.component";
import Templates from "./Components/Templates/templates.component";
import {NewProcess} from "./Components/Processes/processes.component";

export default function App() {

    return (
        <div>
            <Link to={"/"} className="navbar-brand">Home </Link>
            <Link to={"/newprocess"} className="navbar-brand">Processos </Link>
            <Link to={"/templates"} className="navbar-brand">Templates</Link>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/newprocess" element={<NewProcess/>}/>
                <Route path="/templates" element={<Templates/>}/>
            </Routes>
        </div>
    )
}