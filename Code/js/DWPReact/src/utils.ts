
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

export function formatBytes(bytes) {
    const units = ['bytes', 'KB', 'MB', 'GB', 'TB'];
    let i = 0;
  
    while (bytes >= 1024 && i < units.length - 1) {
      bytes /= 1024;
      i++;
    }
  
    return `${bytes.toFixed(2)} ${units[i]}`;
}
  