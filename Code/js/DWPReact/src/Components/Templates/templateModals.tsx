import { useEffect, useState } from "react"
import processServices from "../../Services/process.service"

export function NewStageModal({ 
    onClose, 
    onSave, 
    stageName, 
    setStageName, 
    stageDescription, 
    setStageDescription, 
    stageResponsibles,
    setStageResponsibles,
    stageDuration,
    setStageDuration,
    stageError, 
    setStageError,
    stages,
    userGroups,
    users,
    selectedMode,
    setSelectedMode
}) {
    const [searchInput, setSearchInput] = useState("")
    const [selectedList, setSelectedList] = useState("Groups")
    const [groups, setGroups] = useState(userGroups)
    const [all, setAll] = useState(users)

    const [isHovering, setIsHovering] = useState(false)
  
    const handleSave = () => {
      if (stageName === "" || stageDescription === "" || stageDuration === "" || stageResponsibles.length === 0)
        setStageError("Parâmetros da etapa em falta.")
      else if (stages.find(stage => stage.name === stageName) != null) 
        setStageError("Nome de etapa deve ser único.")
      else {
        onSave()
        onClose()
      }
    }
  
    const filteredUsers = selectedList === "Groups" ?
      groups.filter(item => item.toLowerCase().includes(searchInput.toLowerCase())) :
      all.filter(item => item.toLowerCase().includes(searchInput.toLowerCase()))
  
    function addResponsible(responsible) {
      setStageResponsibles((prevResp) => [...prevResp, responsible])
      
      if(selectedList === "Groups")
        setGroups(groups.filter(group => group !== responsible))
      else
        setAll(all.filter(user => user !== responsible))
    }
  
    function removeResp(responsible) {
      setStageResponsibles(stageResponsibles.filter(resp => resp !== responsible))
      
      if (responsible.indexOf("@") === -1)
        setGroups((prevResp) => [...prevResp, responsible])
      else
        setAll((prevResp) => [...prevResp, responsible])
    }
    
    return (
        <div className="bg">
            <div className="stage-modal">
                <div><h2>Nova etapa</h2>
                    <p><b>Nome: </b></p>
                    <input className="name-input" type="text" value={stageName} onChange={e => setStageName(e.target.value)}/>
                    <p><b>Descrição: </b></p>
                    <textarea className="description-area" value={stageDescription} onChange={e => setStageDescription(e.target.value)}/>
                    <p><b>Prazo: </b><input type="number" min={1} value={stageDuration} onChange={e => setStageDuration(e.target.value)}/> dias</p>
                    <div>
                        <p><b>Responsáveis: </b></p>
                        <div className="responsible-container">
                            {stageResponsibles.map((resp, index) => {
                                return (
                                    <p key={index}> {resp} <button onClick={() => removeResp(resp)}>x</button></p>
                                )
                            })}
                        </div>
                        <div>
                            <input type="text" id="myInput" placeholder="Pesquisar responsáveis" 
                                onChange={(e) => {setSearchInput(e.target.value)}}>
                            </input>
                            <select value={selectedList} onChange={(e) => setSelectedList(e.target.value)}>
                                <option value="Groups">Grupos</option>
                                <option value="All">Utilizadores</option>
                            </select>
                        </div>
                        <div className="scroll-resp">
                            {filteredUsers.map((item, index) => (
                                <p key={index}><button key={index} onClick={() => addResponsible(item)}>{item}</button></p>
                            ))}
                        </div>
                    </div>
                    <div>
                        <p></p>
                        <label><b>Modo de assinatura: </b></label>
                        <select value={selectedMode} onChange={(e) => setSelectedMode(e.target.value)}>
                                <option value="Unanimous">Unânime</option>
                                <option value="Majority">Maioritário</option>
                        </select>
                        <b onMouseOver={() => setIsHovering(true)} onMouseOut={() => setIsHovering(false)}> ?</b>
                    </div>
                    {isHovering && (
                        <dialog open>
                            <p><b>Unânime: </b>Todos os responsáveis devem assinar a etapa para o processo prosseguir</p>
                            <p><b>Maioritário: </b>A maioria dos responsáveis deve assinar a etapa para o processo prosseguir</p>
                        </dialog>
                    )}
                    <p className="error">{stageError}</p>
                    <p>
                        <button onClick={onClose}>Cancelar</button>
                        <button onClick={handleSave}>Guardar etapa</button>
                    </p>
                </div>
            </div>
        </div>
    )
}
  
export function TemplateDetailsModal({onClose, selectedTemplate}) {
    
    const [templateDetails, setTemplateDetails] = useState({
        name:"",
        description:"",
        stages:[
            {
                name: "",
                description: "",
                responsibles: [""],
                duration: 1,
                mode: "Unânime"
            }
        ]
    })

    useEffect(() => {
        const fetchData = async () => {
            const template = await processServices.getTemplate(selectedTemplate);
            setTemplateDetails(template)
        }
        fetchData()
    }, [])

    return (
        <div className="bg">
            <div className="modal">
                <h3>{selectedTemplate}</h3>
                <p><b>Descrição: </b>{templateDetails.description}</p>
                <div className="scroll">
                    {templateDetails.stages.map((stage, index) => {
                        return (
                            <div key={index} className="clipping-container">
                                <p><b>{stage.name}</b></p>
                                <p><b>Descrição: </b>{stage.description}</p>
                                <p><b>Prazo: </b>{stage.duration} dias</p>
                                <p><b>Responsáveis: </b>
                                    {stage.responsibles.map((resp, index) => {
                                        return (
                                            <a key={index}> {resp}; </a>
                                        )
                                    })}
                                </p>
                                <p><b>Modo de assinatura: </b>{stage.mode}</p>
                            </div>
                        )
                    })}
                </div>
                <button onClick={onClose}>Fechar</button>
            </div>
        </div>
    )
}