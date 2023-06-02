import React from 'react';
import './admin.component.css';
import { Link } from 'react-router-dom';

const Admin = () => {
    return (
        <div>
            <head>
                <link href="https://fonts.googleapis.com/css?family=Open+Sans:300i,400" rel="stylesheet" />
            </head>
            <body>
            <div className="custom-container">
                <Link to="/roles" className="custom-card">
                    <h3 className="custom-title">Papéis</h3>
                    <div className="custom-bar">
                        <div className="custom-emptybar"></div>
                        <div className="custom-filledbar"></div>
                    </div>
                    <div className="custom-circle">
                        <svg version="1.1" xmlns="http://www.w3.org/2000/svg">
                            <circle className="custom-stroke" cx="60" cy="60" r="50" />
                        </svg>
                    </div>
                </Link>
                <Link to="/templates" className="custom-card">
                    <h3 className="custom-title">Templates</h3>
                    <div className="custom-bar">
                        <div className="custom-emptybar"></div>
                        <div className="custom-filledbar"></div>
                    </div>
                    <div className="custom-circle">
                        <svg version="1.1" xmlns="http://www.w3.org/2000/svg">
                            <circle className="custom-stroke" cx="60" cy="60" r="50" />
                        </svg>
                    </div>
                </Link>
                <Link to="/addusers" className="custom-card">
                    <h3 className="custom-title">Adicionar Usuário</h3>
                    <div className="custom-bar">
                        <div className="custom-emptybar"></div>
                        <div className="custom-filledbar"></div>
                    </div>
                    <div className="custom-circle">
                        <svg version="1.1" xmlns="http://www.w3.org/2000/svg">
                            <circle className="custom-stroke" cx="60" cy="60" r="50" />
                        </svg>
                    </div>
                </Link>
            </div>
            </body>
        </div>
    );
};

export default Admin;
