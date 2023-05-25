import { useEffect, useState } from "react"
import rolesService from "../../Services/roles.service"

export const Roles = () => {

    const [roleName, setRoleName] = useState("")
    const [roleDescription, setRoleDescription] = useState("")

    const [availableRoles, setAvailableRoles] = useState([])
    const [selectedRole, setSelectedRole] = useState("")

    const [error, setError] = useState("")

    function templateOptions() {
        let options = []
    
        availableRoles.forEach ((template, index) =>  {
            options.push(<option key={index} value={template}>{template}</option>)
        })
    
        return options;
      } 

    const createRole = () => {

    }

    return (
        <div>
            <h2>Papéis disponíveis</h2>
            { availableRoles.length === 0 ?
                <p className="error">Não existem papéis disponíveis.</p>
            : 
                <div>  
                        <label><b>Template: </b>
                            <select value={selectedRole} onChange={(e) => setSelectedRole(e.target.value)}>
                                {templateOptions()}
                            </select>
                            <button onClick={() => {}}>Detalhes</button>
                        </label>
                </div>
            }

            <h2>Novo Papel</h2>
            <p><b>Nome: </b></p>
            <input className="name-input" type="text" value={roleName} onChange={e => {setRoleName(e.target.value)}}/>
            <p><b>Descrição: </b></p>
            <textarea className="description-area" value={roleDescription} onChange={e => setRoleDescription(e.target.value)}/>
            <p className="error">{error}</p>

            <button onClick={createRole}>Criar papel</button>
        </div>
    )
}