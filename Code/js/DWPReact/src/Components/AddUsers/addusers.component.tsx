// MyFormComponent.tsx

import React, { useState, useRef } from 'react';
import addusersService from "../../Services/Users/addusers.service";
import { isEmail } from "validator";
import Input from "react-validation/build/input";
import Form from "react-validation/build/form";
import CheckButton from "react-validation/build/button";
import AuthService from "../../Services/Users/auth.service";

const required = (value) => {
    if (!value) {
        return (
            <div className="invalid-feedback d-block">
                This field is required!
            </div>
        );
    }
};

const validEmail = (value) => {
    if (!isEmail(value)) {
        return (
            <div className="invalid-feedback d-block">
                This is not a valid email.
            </div>
        );
    }
};

const vusername = (value) => {
    if (value.length < 3 || value.length > 20) {
        return (
            <div className="invalid-feedback d-block">
                The username must be between 3 and 20 characters.
            </div>
        );
    }
};


const adduserscomponent: React.FC = () => {
    const form = useRef();
    const checkBtn = useRef();

    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [successful, setSuccessful] = useState(false);
    const [message, setMessage] = useState("");

    const onChangeUsername = (e) => {
        const username = e.target.value;
        setUsername(username);
    };

    const onChangeEmail = (e) => {
        const email = e.target.value;
        setEmail(email);
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        setMessage("");
        setSuccessful(false);

        // @ts-ignore
        form.current.validateAll();

        // @ts-ignore
        if (checkBtn.current.context._errors.length === 0) {
            addusersService.registeruser(username, email).then(
                (response) => {
                    setMessage(response.data.message);
                    setSuccessful(true);
                },
                (error) => {
                    const resMessage =
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString();

                    setMessage(resMessage);
                    setSuccessful(false);
                }
            );
        }
    };


    return (
        <div>
            <h2>Adicionar Novo Utilizador</h2>
            <Form onSubmit={handleSubmit} ref={form}>
                {!successful && (
                    <div>
                        <div>
                            <label htmlFor="username">Username</label>
                            <Input
                                type="text"
                                className="form-control"
                                name="username"
                                value={username}
                                onChange={onChangeUsername}
                                validations={[required, vusername]}
                            />
                        </div>
                        <div>
                            <label htmlFor="email">Email</label>
                            <Input
                                type="text"
                                className="form-control"
                                name="email"
                                value={email}
                                onChange={onChangeEmail}
                                validations={[required, validEmail]}
                            />
                        </div>
                        <div className="form-group">
                            <button className="btn btn-primary btn-block">Submit</button>
                        </div>
                    </div>
                )}

                {message && (
                    <div className="form-group">
                        <div
                            className={
                                successful ? "alert alert-success" : "alert alert-danger"
                            }
                            role="alert"
                        >
                            {message}
                        </div>
                    </div>
                )}
                <CheckButton style={{ display: "none" }} ref={checkBtn} />
            </Form>
        </div>
    );
};

export default adduserscomponent;
