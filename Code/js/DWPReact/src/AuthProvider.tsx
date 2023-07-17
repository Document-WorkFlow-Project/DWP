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
    const [loading, setLoading] = useState(true);

    const checkAuth = async () => {
        try {
            const response = await axios.get(`${API_URL}/users/auth`);
            
            if (response.status === 200) 
                setLoggedUser(response.data);
            else 
                setLoggedUser(loggedOut);
        } catch (error) {
            setLoggedUser(loggedOut);
        }

        setLoading(false);
    };

    useEffect(() => {
        checkAuth();
    }, []);

    // Prevent rendering of children until we're done loading
    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <AuthContext.Provider value={{ loggedUser, checkAuth }}>
            {children}
        </AuthContext.Provider>
    );
}