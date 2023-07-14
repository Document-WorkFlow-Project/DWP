import { useState, useRef } from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import AuthService from "../../Services/Users/auth.service";
import {toast, ToastContainer} from "react-toastify";
import { vpassword, validEmail, required } from "../../utils";

export function Login ({
    onClose
}) {
    const form = useRef();
    const checkBtn = useRef();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);

    const onChangeEmail = (e) => {
        const email = e.target.value;
        setEmail(email);
    };

    const onChangePassword = (e) => {
        const password = e.target.value;
        setPassword(password);
    };

    const handleLogin = async (e) => {
        e.preventDefault();

        setLoading(true);

        // @ts-ignore
        form.current.validateAll();

        // @ts-ignore
        if (checkBtn.current.context._errors.length === 0) {
            try {
                const res = await AuthService.login(email, password)
                window.location.href = "processes"
                toast.success(res);
            } catch (error) {
                console.log(error)
                const resMessage = error.response.data || error.toString();
                setLoading(false);
                toast.error(resMessage);
            }
        } else {
            setLoading(false);
        }
    };

    return (
        <div className="container">
            <div className="row justify-content-end">
                <button className="btn-close" onClick={onClose}></button>
            </div>
            <Form onSubmit={handleLogin} ref={form}>
                <div className="form-group">
                    <p><b>Email</b></p>
                    <Input
                        type="text"
                        className="form-control"
                        name="email"
                        value={email}
                        onChange={onChangeEmail}
                        validations={[required, validEmail]}
                    />
                </div>
                <p></p>
                <div className="form-group">
                    <p><b>Password</b></p>
                    <Input
                        type="password"
                        className="form-control"
                        name="password"
                        value={password}
                        onChange={onChangePassword}
                        validations={[required, vpassword]}
                    />
                </div>
                <p></p>
                <div className="form-group">
                    <button className="btn btn-primary" disabled={loading}>
                        {loading && (
                            <span className="spinner-border spinner-border-sm"></span>
                        )}
                        <span>Login</span>
                    </button>
                </div>

                <CheckButton style={{ display: "none" }} ref={checkBtn} />
            </Form>
            <ToastContainer /> {/* Add the toast container */}
        </div>
    );
};

export default Login;