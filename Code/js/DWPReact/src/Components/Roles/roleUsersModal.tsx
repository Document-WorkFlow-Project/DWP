import { useEffect, useState, useContext } from "react"
import rolesService from "../../Services/Roles/roles.service"
import usersService from "../../Services/Users/users.service"
import { AuthContext } from "../../AuthProvider"
import {toast} from "react-toastify";

export function RoleUsersModal({
    onClose,
    selectedRole,
    navigate
}) {
    const [roleUsers, setRoleUsers] = useState([])
    const [availableUsers, setAvailableUsers] = useState([])

    const [searchInput, setSearchInput] = useState("")

    const { loggedUser } = useContext(AuthContext);

    useEffect(() => {
        if (!loggedUser.email) {
            navigate('/');
            toast.error("O utilizador não tem sessão iniciada.")
        }

        const fetchData = async () => {
            try {
              const rUsers = await rolesService.roleUsers(selectedRole)
              setRoleUsers(rUsers)

              const users = await usersService.usersList()
              setAvailableUsers(users.filter(user => !rUsers.includes(user)))
            } catch (error) {
                toast.error("Erro a obter utilizadores. Tenta novamente...")
            }
        }
        
        fetchData()
    }, [])

    const filteredUsers = availableUsers.filter(item => item.toLowerCase().includes(searchInput.toLowerCase()))

    const removeUserFromRole = async (user) => {
        try {
            await rolesService.removeRoleFromUser(selectedRole, user)
            setRoleUsers(roleUsers.filter(email => email !== user))
            setAvailableUsers((prevUsers) => [...prevUsers, user])
        } catch (error) {
            toast.error("Erro a obter papéis. Tenta novamente...")
        }
    }

    const addUserToRole = async (user) => {
        try {
            await rolesService.addRoleToUSer(selectedRole, user)
            setRoleUsers((prevUsers) => [...prevUsers, user])
            setAvailableUsers(availableUsers.filter(email => email !== user))
        } catch (error) {
            toast.error("Erro a obter papéis. Tenta novamente...")
        }
    }

    return (
        <div className="bg">
            <div className="stage-modal">
                <h3>{selectedRole}</h3>

                <div className="row">
                    <div className="col">
                        <p><b>Utilizadores: </b></p>
                        <div className="responsible-container">
                            {roleUsers.map((user, index) => {
                                if (selectedRole === "admin" && user === loggedUser.email)
                                    return (<p key={index}> {user} </p>)
                                else
                                    return (<p key={index}> {user} <button className="btn-close" onClick={() => removeUserFromRole(user)}></button></p>)
                            })}
                        </div>
                    </div>
                </div>
                
                
                <div className="row">
                    <div className="col">
                        <p><b>Adicionar utilizadores: </b></p>
                        <div>
                            <input className="form-control" type="text" id="myInput" placeholder="Pesquisar utilizadores" 
                                onChange={(e) => {setSearchInput(e.target.value)}}>
                            </input>
                        </div>
                    </div>
                </div>
                

                <div className="row">
                    <div className="col">
                        <div className="scroll-resp">
                            {filteredUsers.map((user, index) => (
                                <p key={index}><button className="btn btn-primary" key={index} onClick={() => addUserToRole(user)}>{user}</button></p>
                            ))}
                        </div>
                    </div>
                </div>
                
                <div className="row">
                    <p></p>
                    <button className="btn btn-danger" onClick={onClose}>Fechar</button> 
                </div>
                
            </div>
        </div>
    )
}