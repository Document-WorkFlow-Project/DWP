import { Routes, Route, Link } from "react-router-dom";
import Home from "./Components/Home/home.component";
import Templates from "./Components/Templates/templates.component";
import Processes from "./Components/Processes/processes.component";

export default function App() {

    return (
        <div>
            <Link to={"/"} className="navbar-brand">Home</Link>
            <Link to={"/process"} className="navbar-brand">Processes</Link>
            <Link to={"/templates"} className="navbar-brand">Templates</Link>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/process" element={<Processes/>}/>
                <Route path="/templates" element={<Templates/>}/>
            </Routes>
        </div>
    )
}