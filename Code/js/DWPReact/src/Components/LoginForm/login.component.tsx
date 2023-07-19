import { useState, useContext } from "react";
import AuthService from "../../Services/Users/auth.service";
import {toast} from "react-toastify";
import { AuthContext } from "../../AuthProvider";
import { isEmail } from "validator";

export function Login ({
    onClose,
    navigate
}) {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("")

    const { checkAuth } = useContext(AuthContext);

    const handleLogin = async (e) => {
        e.preventDefault();

        if (!isEmail(email)) {
            setError("Email não é válido.")
            return;
        }

        setLoading(true);

        try {
            const res = await AuthService.login(email, password)
            await checkAuth()
            onClose()
            navigate("/processes")
            toast.success(res);
        } catch (error) {
            console.log(error)
            const resMessage = error.response.data || error.toString();
            setLoading(false);
            toast.error(resMessage);
        }
        
        setLoading(false);
    };

    return (
        <div className="container">
            <div className="row justify-content-end">
                <button className="btn-close" onClick={onClose}></button>
            </div>
            <form onSubmit={handleLogin}>
                <div className="form-group">
                    <p><b>Email</b></p>
                    <input
                        type="text"
                        className="form-control"
                        name="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required={true}
                    />
                </div>
                <p></p>
                <div className="form-group">
                    <p><b>Password</b></p>
                    <input
                        type="password"
                        className="form-control"
                        name="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        minLength={6}
                        maxLength={40}
                        required={true}
                    />
                </div>
                <p></p>
                <div className="form-group">
                    <p className="error">{error}</p>               
                    <input className="btn btn-primary" type="submit" value={loading ? "Loading..." : "Login"} disabled={loading}></input>
                </div>
            </form>
        </div>
    );
};

export default Login;