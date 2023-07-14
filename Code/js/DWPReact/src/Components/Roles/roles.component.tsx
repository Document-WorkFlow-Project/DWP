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
                toast.error("Erro a obter papéis. Tenta novamente...")
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
                    toast.error("Erro a obter papéis. Tenta novamente...")
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
        <div className="container-fluid">
            <div className="row align-items-start">
                <div className="col-10">
                    <p></p>
                    <h2>Papéis disponíveis</h2>
                    <p></p>
                    { availableRoles.length === 0 ?
                        <p className="error">Não existem papéis disponíveis.</p>
                    : 
                        <div className="row row-cols-auto">  
                            <div className="col-5">
                                <select className="form-select" value={selectedRole} onChange={(e) => setSelectedRole(e.target.value)}>
                                    {roleOptions()}
                                </select>
                            </div>
                            <div className="col">
                                <button className="btn btn-primary" onClick={() => {setShowModal(true)}}>Utilizadores</button>
                            </div>
                            <div className="col">
                                {selectedRole !== "admin" && <button className="btn btn-danger" onClick={() => {rolesService.deleteRole(selectedRole)}}>Apagar papel</button>}
                            </div>
                        </div>
                    }
                </div>
            </div>
            <div className="row align-items-start">
                <div className="col">
                    <p></p>
                    <p><b>Descrição: </b> {selectedRoleDetails.descricao}</p>
                </div>
            </div>
            <div className="row align-items-start">
                <div className="col-6">
                    <h2>Novo Papel</h2>
                    <p><b>Nome: </b></p>
                    <input className="form-control" type="text" value={roleName} onChange={e => {setRoleName(e.target.value)}}/>
                    <p></p>
                    <p><b>Descrição: </b></p>
                    <textarea className="form-control" style={{ resize: "none" }} value={roleDescription} onChange={e => setRoleDescription(e.target.value)}/>
                    <p className="error">{error}</p>

                    <button className="btn btn-success" onClick={createRole}>Criar papel</button>
                </div>
            </div>        

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