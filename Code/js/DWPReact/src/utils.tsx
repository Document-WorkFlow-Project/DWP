import { isEmail } from "validator";

export const API_URL = 'http://localhost:3000/api'

export function convertTimestamp(timestamp) {
    const date = new Date(timestamp);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = String(date.getFullYear());
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}

export const estado = (value) => {
    if (value == "PENDING")
        return "Pendente"
    else if (value == "APPROVED")
        return "Aprovado"
    else if (value == "DISAPPROVED")
        return "Não aprovado"
}

export const modo = (value) => {
    if (value == "Unanimous")
        return "Unânime"
    else
        return "Maioritário"
}

export function formatBytes(bytes) {
    const units = ['bytes', 'KB', 'MB', 'GB', 'TB'];
    let i = 0;
  
    while (bytes >= 1024 && i < units.length - 1) {
      bytes /= 1024;
      i++;
    }
  
    return `${bytes.toFixed(2)} ${units[i]}`;
}

export function required(value) {
    if (!value) {
        return (
            <div className="invalid-feedback d-block">
                Campo em falta.
            </div>
        )
    }
}

export function validEmail(value) {
    if (!isEmail(value)) {
        return (
            <div className="invalid-feedback d-block">
                Este email não é válido.
            </div>
        )
    }
}

export function vusername (value) {
    if (value.length < 3 || value.length > 20) {
        return (
            <div className="invalid-feedback d-block">
                Este campo deve ter entre 3 and 20 caracteres.
            </div>
        )
    }
}

export function vpassword(value) {
    if (value.length < 6 || value.length > 40) {
        return (
            <div className="invalid-feedback d-block">
                A password deve ter entre 6 and 40 caracteres.
            </div>
        );
    }
};
  