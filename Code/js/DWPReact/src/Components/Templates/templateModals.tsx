import { useEffect, useState } from "react"
import templatesService from "../../Services/Templates/templates.service"
import { modo } from "../../utils"

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
    roleGroups,
    users,
    selectedMode,
    setSelectedMode
}) {
    const [searchInput, setSearchInput] = useState("")
    const [selectedList, setSelectedList] = useState("Groups")
    const [groups, setGroups] = useState(roleGroups)
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
                <div className="container">
                    <h2>Nova etapa</h2>
                    <div className="row">
                        <div className="col">
                            <p></p>
                            <p><b>Nome: </b></p>
                            <input className="form-control" type="text" value={stageName} onChange={e => setStageName(e.target.value)}/>
                        </div>
                    </div>

                    
                    <div className="row">
                        <div className="col">
                            <p></p>
                            <p><b>Descrição: </b></p>
                            <textarea className="form-control" style={{ resize: "none" }} value={stageDescription} onChange={e => setStageDescription(e.target.value)}/>
                        </div>
                    </div>

                    <div className="row align-items-center">
                        <p></p>
                        <div className="col-2">
                            <b>Prazo: </b>
                        </div>
                        <div className="col">
                            <input className="form-control" type="number" min={1} value={stageDuration} onChange={e => setStageDuration(e.target.value)}/>
                        </div>
                        <div className="col">
                            dias
                        </div>
                    </div>

                    <div className="row align-items-center">
                        <p></p>
                        <div className="col-5">
                            <label><b>Modo de assinatura: </b></label>
                        </div>
                        <div className="col-5">
                            <select className="form-select" value={selectedMode} onChange={(e) => setSelectedMode(e.target.value)}>
                                    <option value="Unanimous">Unânime</option>
                                    <option value="Majority">Maioritário</option>
                            </select>
                        </div>
                        <div className="col">
                            <b onMouseOver={() => setIsHovering(true)} onMouseOut={() => setIsHovering(false)}> ?</b>

                            {isHovering && (
                                <dialog open>
                                    <p><b>Unânime: </b>Todos os responsáveis devem assinar a etapa para o processo prosseguir</p>
                                    <p><b>Maioritário: </b>A maioria dos responsáveis deve assinar a etapa para o processo prosseguir</p>
                                </dialog>
                            )}
                        </div>
                    </div>

                    <div className="row">
                        <p></p>
                        <div className="col">
                            <p><b>Responsáveis: </b></p>
                            <div className="responsible-container">
                                {stageResponsibles.map((resp, index) => {
                                    return (
                                        <p key={index}> {resp} <button className="btn-close" onClick={() => removeResp(resp)}></button></p>
                                    )
                                })}
                            </div>
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-7">
                            <input className="form-control" type="text" id="myInput" placeholder="Pesquisar responsáveis" 
                                onChange={(e) => {setSearchInput(e.target.value)}}>
                            </input>
                        </div>
                        <div className="col">
                            <select className="form-select" value={selectedList} onChange={(e) => setSelectedList(e.target.value)}>
                                <option value="Groups">Grupos</option>
                                <option value="All">Utilizadores</option>
                            </select>
                        </div>
                    </div>

                    <div className="row">
                        <div className="col">
                            <div className="scroll-resp">
                                {filteredUsers.map((item, index) => (
                                    <p key={index}><button className="btn btn-primary" key={index} onClick={() => addResponsible(item)}>{item}</button></p>
                                ))}
                            </div>
                        </div>
                    </div>

                    <div className="row">
                        <p></p>
                        <div className="col">
                            <p className="error">{stageError}</p>
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-3">
                            <button className="btn btn-danger" onClick={onClose}>Cancelar</button>
                        </div>
                        <div className="col">
                            <button className="btn btn-success" onClick={handleSave}>Guardar etapa</button>
                        </div>
                    </div>
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
            const template = await templatesService.getTemplate(selectedTemplate);
            setTemplateDetails(template)
        }
        fetchData()

        console.log(templateDetails)
    }, [])

    return (
        <div className="bg">
            <div className="stage-modal">
                <div className="container-fluid">
                    <div className="row row-cols-auto">
                        <div className="col">
                            <h3>{selectedTemplate}</h3>
                            <p></p>
                            <b>Descrição: </b>
                            <p></p>
                            {templateDetails.description}
                            <p></p>

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
                                        <p><b>Modo de assinatura: </b>{modo(stage.mode)}</p>
                                    </div>
                                )
                            })}
                            <button className="btn btn-danger" onClick={onClose}>Fechar</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}