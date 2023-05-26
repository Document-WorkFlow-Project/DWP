import { useEffect, useState } from "react"
import rolesService from "../../Services/roles.service"
import usersService from "../../Services/users.service"

export function RoleUsersModal({
    onClose,
    selectedRole
}) {

    const [roleUsers, setRoleUsers] = useState([])
    const [availableUsers, setAvailableUsers] = useState([])

    const [searchInput, setSearchInput] = useState("")

    useEffect(() => {
        const fetchData = async () => {
          const rUsers = await rolesService.roleUsers(selectedRole)
          setRoleUsers(rUsers)
          
          const users = await usersService.usersList()
          setAvailableUsers(users.filter(user => !rUsers.includes(user)))
        }
        fetchData()
    }, [])

    const filteredUsers = availableUsers.filter(item => item.toLowerCase().includes(searchInput.toLowerCase()))

    const removeUserFromRole = async (user) => {
        await rolesService.removeRoleFromUser(selectedRole, user)
        setRoleUsers(roleUsers.filter(email => email !== user))
        setAvailableUsers((prevUsers) => [...prevUsers, user])
    }

    const addUserToRole = async (user) => {
        await rolesService.addRoleToUSer(selectedRole, user)
        setRoleUsers((prevUsers) => [...prevUsers, user])
        setAvailableUsers(availableUsers.filter(email => email !== user))
    }

    return (
        <div className="bg">
            <div className="stage-modal">
                <h3>{selectedRole}</h3>
                <p><b>Utilizadores: </b></p>
                <div className="responsible-container">
                    {roleUsers.map((user, index) => {
                        return (
                            <p key={index}> {user} <button onClick={() => removeUserFromRole(user)}>x</button></p>
                        )
                    })}
                </div>
                
                <p><b>Adicionar utilizadores: </b></p>
                <div>
                    <input type="text" id="myInput" placeholder="Pesquisar utilizadores" 
                        onChange={(e) => {setSearchInput(e.target.value)}}>
                    </input>
                </div>
                <div className="scroll-resp">
                    {filteredUsers.map((user, index) => (
                        <p key={index}><button key={index} onClick={() => addUserToRole(user)}>{user}</button></p>
                    ))}
                </div>

                <p><button onClick={onClose}>Cancelar</button></p>
            </div>
        </div>
    )
}