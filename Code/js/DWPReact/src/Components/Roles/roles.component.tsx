import { useEffect, useState, useContext } from "react"
import rolesService from "../../Services/Roles/roles.service"
import { createPortal } from 'react-dom'
import { RoleUsersModal } from "./roleUsersModal"
import { AuthContext } from "../../AuthProvider"
import {toast} from "react-toastify";

export const Roles = () => {

    const [roleName, setRoleName] = useState("")
    const [roleDescription, setRoleDescription] = useState("")

    const [availableRoles, setAvailableRoles] = useState([])
    const [selectedRole, setSelectedRole] = useState("")
    const [selectedRoleDetails, setSelectedRoleDetails] = useState({nome: "", descricao: ""})

    const [showModal, setShowModal] = useState(false)
    const [error, setError] = useState("")

    const { loggedUser } = useContext(AuthContext);

    useEffect(() => {
        if (!loggedUser.email)
            window.location.href = '/';
        
        const fetchData = async () => {
            try {
                setAvailableRoles(await rolesService.availableRoles())
            } catch (error) {
                let code = error.response.status
                if (code != 404) toast.error("Error Getting Roles. Please Refresh ...")
                else toast.error("There are no Roles. Please contact an Admin")
            }
        }
        fetchData()
    }, [])
      
    useEffect(() => {
        if (availableRoles.length > 0)
            setSelectedRole(availableRoles[0])
    }, [availableRoles])

    useEffect(() => {
        if (availableRoles.length > 0) {
            const fetchDetails = async () => {
                try {
                    setSelectedRoleDetails(await rolesService.roleDetails(selectedRole))
                } catch (error) {
                    let code = error.response.status
                    if (code != 404) toast.error("Error Getting Role Details. Please Refresh ...")
                }
            }
            fetchDetails()
        }
    }, [selectedRole])

    function roleOptions() {
        let options = []
    
        availableRoles.forEach ((role, index) =>  {
            options.push(<option key={index} value={role}>{role}</option>)
        })
    
        return options;
    } 

    const createRole = () => {
        if (availableRoles.find(name => name === roleName)) {
            setError("Nome do template já existe.")
            return
        }

        if (roleName === "" || roleDescription === "") {
            setError("Nome e/ou descrição em falta.")
            return
        }

        const newRole = {
            name: roleName,
            description: roleDescription
        }

        rolesService.saveRole(newRole)
    }

    return (
        <div>
            <h2>Papéis disponíveis</h2>
            { availableRoles.length === 0 ?
                <p className="error">Não existem papéis disponíveis.</p>
            : 
                <div>  
                    <label><b>Papel: </b>
                        <select value={selectedRole} onChange={(e) => setSelectedRole(e.target.value)}>
                            {roleOptions()}
                        </select>
                    </label>
                    <button onClick={() => {setShowModal(true)}}>Utilizadores</button>
                    <button onClick={() => {rolesService.deleteRole(selectedRole)}}>Apagar papel</button>
                    <p><b>Descrição: </b></p>
                    <div className="">
                        {selectedRoleDetails.descricao}
                    </div>
                </div>
            }

            <h2>Novo Papel</h2>
            <p><b>Nome: </b></p>
            <input className="name-input" type="text" value={roleName} onChange={e => {setRoleName(e.target.value)}}/>
            <p><b>Descrição: </b></p>
            <textarea className="description-area" value={roleDescription} onChange={e => setRoleDescription(e.target.value)}/>
            <p className="error">{error}</p>

            <button onClick={createRole}>Criar papel</button>

            <div>
                {showModal && createPortal(
                <RoleUsersModal 
                    onClose={() => setShowModal(false)}
                    selectedRole={selectedRole}
                />,
                document.body
                )}
            </div>
        </div>
    )
}