import ReactDOM from 'react-dom/client';
import { BrowserRouter } from "react-router-dom";
import './index.css';
import App from './App';
import { AuthProvider } from './AuthProvider';
import {ToastContainer} from "react-toastify";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

root.render(
    <BrowserRouter>
        <ToastContainer autoClose={3000} />
        <AuthProvider>
            <App />
        </AuthProvider>
    </BrowserRouter>
);

