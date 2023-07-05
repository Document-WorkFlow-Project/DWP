import {useState, useRef, useEffect, useContext} from 'react';
import Input from "react-validation/build/input";
import Form from "react-validation/build/form";
import CheckButton from "react-validation/build/button";
import {toast, ToastContainer} from "react-toastify";
import './addusers.component.css'
import "react-toastify/dist/ReactToastify.css";
import authService from '../../Services/Users/auth.service';
import { AuthContext } from '../../AuthProvider';
import { required, validEmail, vusername } from '../../utils';

const adduserscomponent: React.FC = () => {

    const { loggedUser } = useContext(AuthContext);

    useEffect(() => {
        if (!loggedUser.email)
            window.location.href = '/';
    }, [])

    const form = useRef();
    const checkBtn = useRef();

    const [loading, setLoading] = useState(false);
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        // @ts-ignore
        form.current.validateAll();

        // @ts-ignore
        if (checkBtn.current.context._errors.length === 0) {
            await authService.register(email, username).then(
                (response) => {
                    console.log(response)
                    toast.success(response); // Show success toast
                    setUsername("")
                    setEmail("")
                },
                (error) => {
                    console.log(error)
                    const resMessage =
                        (error.response.data &&
                            error.response.data )||
                        error.toString();

                    toast.error(resMessage);
                }
            );
        } else {
            setLoading(false);
        }
    };


    return (
        <div>
            <h2>Adicionar Novo Utilizador</h2>
            <Form onSubmit={handleSubmit} ref={form}>
                <div>
                    <label htmlFor="username">Nome</label>
                    <Input
                        type="text"
                        className="form-control"
                        name="username"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        validations={[required, vusername]}
                    />
                    <p></p>
                    <label htmlFor="email">Email</label>
                    <Input
                        type="text"
                        className="form-control"
                        name="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        validations={[required, validEmail]}
                    />
                    <p></p>
                    <button className="btn btn-primary btn-block" disabled={loading}>
                        {loading && (
                            <span className="spinner-border spinner-border-sm"></span>
                        )}
                        <span>Adicionar utilizador</span>
                    </button>
                </div>

                <CheckButton style={{ display: "none" }} ref={checkBtn} />
            </Form>
            <ToastContainer /> {/* Add the toast container */}
        </div>
    );
};

export default adduserscomponent;
