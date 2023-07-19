import {useState, useEffect, useContext} from 'react';
import { isEmail } from "validator";
import {toast} from "react-toastify";
import './addusers.component.css'
import "react-toastify/dist/ReactToastify.css";
import authService from '../../Services/Users/auth.service';
import { AuthContext } from '../../AuthProvider';

const adduserscomponent = ({ navigate }) => {

    const { loggedUser } = useContext(AuthContext);

    useEffect(() => {
        if (!loggedUser.email) {
            navigate('/');
            toast.error("O utilizador não tem sessão iniciada.")
        }
    }, [])

    const [loading, setLoading] = useState(false);
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState("")

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!isEmail(email)) {
            setError("Email não é válido.")
            return;
        }

        setLoading(true)

        try {
            const response = await authService.register(email, username)
            toast.success(response);
            setUsername("")
            setEmail("")
        } catch (error) {
            const resMessage = error.response.data || error.toString();
            toast.error(resMessage);
        }
        
        setLoading(false);
    };


    return (
        <div className='container-fluid'>
            <p></p>
            <h2>Adicionar Novo Utilizador</h2>
            <p></p>
            <form onSubmit={handleSubmit}>
                <div className="row align-items-start">
                    <div className="col-4">
                        <p>Nome:</p>
                        <input
                            className="form-control"
                            name="username"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            minLength={3}
                            maxLength={20}
                            required={true}
                        />
                        <p></p>
                        <p>Email:</p>
                        <input
                            className="form-control"
                            name="email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            required={true}
                        />
                        <p></p>
                        <p className="error">{error}</p>               
                        <input className="btn btn-primary" type="submit" value={loading ? "Loading..." : "Adicionar utilizador"} disabled={loading}></input>
                    </div>
                </div>
            </form>
        </div>
    );
};

export default adduserscomponent;
