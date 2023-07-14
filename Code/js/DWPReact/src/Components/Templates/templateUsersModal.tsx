import { useEffect, useState } from "react"
import usersService from "../../Services/Users/users.service"
import templatesService from "../../Services/Templates/templates.service"
import {toast} from 'react-toastify';

export function TemplateUsersModal({
    onClose,
    loggedUser,
    selectedTemplate
}) {

    const [templateUsers, setTemplateUsers] = useState([])
    const [availableUsers, setAvailableUsers] = useState([])

    const [searchInput, setSearchInput] = useState("")

    useEffect(() => {
        const fetchData = async () => {
            try {
                const tUsers = await templatesService.templateUsers(selectedTemplate)
                setTemplateUsers(tUsers)
                
                const users = await usersService.usersList()
                setAvailableUsers(users.filter(user => !tUsers.includes(user) && user !== loggedUser.email))
                
            } catch(err) {
                const resMessage = err.response.data || err.toString();
                toast.error(resMessage);
            }
        }
        fetchData()
    }, [])

    const filteredUsers = availableUsers.filter(item => item.toLowerCase().includes(searchInput.toLowerCase()))

    const removeUserFromTemplate = async (user) => {
        try {
            await templatesService.removeUSerFromTemplate(selectedTemplate, user)
            setTemplateUsers(templateUsers.filter(email => email !== user))
            setAvailableUsers((prevUsers) => [...prevUsers, user])
        } catch (err) {
            toast.error("Erro ao remover utilizador. Tenta novamente...")
        }
    }

    const addUserToTemplate = async (user) => {
        try {
            await templatesService.addUserToTemplate(selectedTemplate, user)
            setTemplateUsers((prevUsers) => [...prevUsers, user])
            setAvailableUsers(availableUsers.filter(email => email !== user))
        } catch (err) {
            toast.error("Erro ao adicionar utilizador. Tenta novamente...")
        }
    }

    return (
        <div className="bg">
            <div className="stage-modal">
                <h3>{selectedTemplate}</h3>
                <p></p>
                <p><b>Utilizadores: </b></p>
                <div className="responsible-container">
                    {templateUsers.map((user, index) => {
                        return (
                            <p key={index}> {user} <button className="btn-close" onClick={() => removeUserFromTemplate(user)}></button></p>
                        )
                    })}
                </div>
                
                <p><b>Adicionar utilizadores: </b></p>
                <div>
                    <input className="form-control" type="text" id="myInput" placeholder="Pesquisar utilizadores" 
                        onChange={(e) => {setSearchInput(e.target.value)}}>
                    </input>
                </div>
                <div className="scroll-resp">
                    {filteredUsers.map((user, index) => (
                        <p key={index}><button className="btn btn-primary" key={index} onClick={() => addUserToTemplate(user)}>{user}</button></p>
                    ))}
                </div>

                <p></p>
                <p><button className="btn btn-danger" onClick={onClose}>Fechar</button></p>
            </div>
        </div>
    )
}