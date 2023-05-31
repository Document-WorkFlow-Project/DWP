import { useEffect, useState } from "react"
import usersService from "../../Services/Users/users.service"
import templatesService from "../../Services/templates.service"

export function TemplateUsersModal({
    onClose,
    selectedTemplate
}) {

    const [templateUsers, setTemplateUsers] = useState([])
    const [availableUsers, setAvailableUsers] = useState([])

    const [searchInput, setSearchInput] = useState("")

    useEffect(() => {
        const fetchData = async () => {
          const tUsers = await templatesService.templateUsers(selectedTemplate)
          if(Array.isArray(tUsers))
            setTemplateUsers(tUsers)
          
          const users = await usersService.usersList()
          if(Array.isArray(users))
            setAvailableUsers(users.filter(user => !tUsers.includes(user)))
        }
        fetchData()
    }, [])

    const filteredUsers = availableUsers.filter(item => item.toLowerCase().includes(searchInput.toLowerCase()))

    const removeUserFromTemplate = async (user) => {
        const res = await templatesService.removeUSerFromTemplate(selectedTemplate, user)
        console.log(res.status)
        if (res.status === 201) {
            setTemplateUsers(templateUsers.filter(email => email !== user))
            setAvailableUsers((prevUsers) => [...prevUsers, user])
        }
    }

    const addUserToTemplate = async (user) => {
        const res = await templatesService.addUserToTemplate(selectedTemplate, user)
        console.log(res.status)
        if (res.status === 201) {
            setTemplateUsers((prevUsers) => [...prevUsers, user])
            setAvailableUsers(availableUsers.filter(email => email !== user))
        }
    }

    return (
        <div className="bg">
            <div className="stage-modal">
                <h3>{selectedTemplate}</h3>
                <p><b>Utilizadores: </b></p>
                <div className="responsible-container">
                    {templateUsers.map((user, index) => {
                        return (
                            <p key={index}> {user} <button onClick={() => removeUserFromTemplate(user)}>x</button></p>
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
                        <p key={index}><button key={index} onClick={() => addUserToTemplate(user)}>{user}</button></p>
                    ))}
                </div>

                <p><button onClick={onClose}>Fechar</button></p>
            </div>
        </div>
    )
}