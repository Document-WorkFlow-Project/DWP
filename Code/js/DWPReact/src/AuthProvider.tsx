import { createContext, useState, useEffect } from 'react';
import axios from 'axios';
import { API_URL } from './utils';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    
    const loggedOut = {
        email: null,
        nome: null,
        roles: []
    }

    const [loggedUser, setLoggedUser] = useState(loggedOut);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const response = await axios.get(`${API_URL}/users/auth`);
                
                if (response.status === 200) 
                    setLoggedUser(response.data);
                else 
                    setLoggedUser(loggedOut);
            } catch (error) {
                console.log(error)
                setLoggedUser(loggedOut);
            }
        };

        checkAuth();
    }, []);

    return (
        <AuthContext.Provider value={{ loggedUser }}>
            {children}
        </AuthContext.Provider>
    );
}