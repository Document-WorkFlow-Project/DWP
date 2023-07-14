import { useEffect, useState, useContext } from "react"
import { createPortal } from 'react-dom'
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd'
import './templates.css'
import templatesService from "../../Services/Templates/templates.service";
import { NewStageModal, TemplateDetailsModal } from "./templateModals"
import rolesService from "../../Services/Roles/roles.service";
import usersService from "../../Services/Users/users.service";
import { TemplateUsersModal } from "./templateUsersModal"
import { AuthContext } from "../../AuthProvider";
import {toast} from "react-toastify";
import { modo } from "../../utils";

export default function Templates() {

  const tempObj = {
    nome: "",
    ativo: true
  }
  const [availableTemplates, setAvailableTemplates] = useState([tempObj])
  const [selectedTemplate, setSelectedTemplate] = useState(tempObj)
  
  // template name, description, and stages
  const [templateName, setTemplateName] = useState("")
  const [templateDescription, setTemplateDescription] = useState("")
  const [stages, setStages] = useState([])

  // Availables roles and users, fetched from the API
  const [roleGroups, setRoleGroups] = useState([])
  const [users, setUsers] = useState([])

  // new stage name, description, and responsibles
  const [stageName, setStageName] = useState("")
  const [stageDescription, setStageDescription] = useState("")
  const [stageResponsibles, setStageResponsibles] = useState([])
  const [stageDuration, setStageDuration] = useState(1)
  const [selectedMode, setSelectedMode] = useState("Unanimous")

  // modal for new stage parameter fill
  const [showNewStageModal, setShowModal] = useState(false)
  const [error, setError] = useState("")
  const [stageError, setStageError] = useState(null)

  const [showDetailsModal, setShowDetailsModal] = useState(false)
  const [showUsersModal, setShowUsersModal] = useState(false)

  const { loggedUser } = useContext(AuthContext);

  async function fetchData() {
    try {
      const templates = await templatesService.allTemplates()
      
      setAvailableTemplates(templates)

      if (templates.length > 0)
        setSelectedTemplate(templates[0])

    } catch (error) {
      toast.error("Erro ao obter templates. Tenta novamente...")
    }

    try {
      setRoleGroups(await rolesService.availableRoles())
    } catch (error) {
      toast.error("Erro ao obter papéis. Tenta novamente...")
    }

    try {
      setUsers(await usersService.usersList())
    } catch (error) {
      toast.error("Erro ao obter utilizadores. Tenta novamente...")
    }
  }

  useEffect(() => {
    if (!loggedUser.email)
      window.location.href = '/';
    
    fetchData()
  }, [])

  function templateOptions() {
    let options = []

    availableTemplates.forEach ((template, index) =>  {
        options.push(<option key={index} value={template.nome}>{template.nome}</option>)
    })

    return options;
  } 

  async function changeTemplateAvailability(name, value) {
    try {
      await templatesService.setTemplateAvailability(name, value)
    } catch (err) {
      toast.error("Erro ao ativar/desativar template. Tenta novamente...")
    }
    
    fetchData()
  }

  function resetStageParams() {
    // Reset the stage name, description, and responsibles to empty strings and arrays
    setStageName("")
    setStageDescription("")
    setStageResponsibles([])
    setStageError("")
  }

  const addStage = () => {
    setError("")

    const newStage = {
      name: stageName,
      description: stageDescription,
      responsibles: stageResponsibles,
      duration: stageDuration,
      mode: selectedMode
    }
    
    setStages((prevStages) => [...prevStages, newStage])
    resetStageParams()
  }

  const deleteStage = (index) => {
    // Create a copy of the current stages array
    const newStages = [...stages]
    // Remove the stage at the specified index
    newStages.splice(index, 1)
    // Update the stages state variable with the new stages array
    setStages(newStages)
  }

  const saveTemplate = () => {
    if (availableTemplates.find(temp => temp.nome === templateName)) {
      setError("Nome do template já existe.")
      return
    }

    if (templateName === "" || templateDescription === "") {
      setError("Nome e/ou descrição do template em falta.")
      return
    }

    if (stages.length === 0) {
      setError("Nenhuma etapa criada.")
      return
    }
      
    const template = {
      name: templateName,
      description: templateDescription,
      stages: stages
    }

    templatesService.saveTemplate(template)
  }

  function handleOnDragEnd(result) {
    if (!result.destination) return;

    const items = Array.from(stages);
    const [reorderedItem] = items.splice(result.source.index, 1);
    items.splice(result.destination.index, 0, reorderedItem);

    setStages(items);
  }

  return (
    <div>
      <div className="container-fluid">

        <div className="row">
          <p></p>
          <h2>Templates disponíveis</h2>
          <p></p>
          
          { availableTemplates.length === 0 ?
            <div className="row">
              <div className="col">
                <p className="error">Não existem templates disponíveis.</p>
              </div>
            </div>
          : 
            <div className="row row-cols-auto"> 
              <div className="col-4">
                <select 
                  className="form-select"
                  value={selectedTemplate.nome} 
                  onChange={(e) => {setSelectedTemplate(availableTemplates.find(temp => temp.nome === e.target.value))}}>
                  {templateOptions()}
                </select>
              </div> 
              <div className="col">
                <button className="btn btn-primary" onClick={() => setShowDetailsModal(true)}>Detalhes</button>
              </div>
              <div className="col">
                <button className="btn btn-primary" onClick={() => {setShowUsersModal(true)}}>Utilizadores</button>
              </div>
              <div className="col">
                {selectedTemplate.ativo ?
                  <button className="btn btn-danger" onClick={() => changeTemplateAvailability(selectedTemplate.nome, false)}>Desativar template</button>
                :
                  <button className="btn btn-success" onClick={() => changeTemplateAvailability(selectedTemplate.nome, true)}>Ativar template</button>
                }
              </div>
            </div>
          }
        </div>  
        
        <div className="row row-cols-auto">

          <div className="col-6">
              
            <div className="col">
              <p></p>
              <h2>Novo template de processo</h2>
              <p><b>Nome: </b></p>
              <input className="form-control" type="text" value={templateName} onChange={e => {setTemplateName(e.target.value)}}/>
              <p></p>
              <p><b>Descrição: </b></p>
              <textarea className="form-control" style={{ resize: "none" }} value={templateDescription} onChange={e => setTemplateDescription(e.target.value)}/>
              <p className="error">{error}</p>
            </div>

            <div className="row row-cols-auto">
              <div className="col">
                <button className="btn btn-primary" onClick={() => setShowModal(true)}>Adicionar etapa</button>
              </div>
              
              <div className="col">
                <button className="btn btn-success" onClick={saveTemplate}>Guardar template</button>
              </div>
            </div>
            <p></p>

          </div>
          
          <div className="col align-self-center">
            <DragDropContext onDragEnd={handleOnDragEnd}>
              <Droppable droppableId="characters">
                {(provided) => (
                  <ul className="characters" {...provided.droppableProps} ref={provided.innerRef}>
                    {stages.map((stage, index) => {
                      return (
                        <div>
                          <p></p>
                          <Draggable key={stage.name} draggableId={stage.name} index={index}>
                            {(provided) => (
                              <div ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps} className="clipping-container" key={index}>
                                <p><b>Nome: </b>{stage.name}</p>
                                <p><b>Descrição: </b>{stage.description}</p>
                                <p><b>Prazo: </b>{stage.duration} dias</p>
                                <p><b>Modo de assinatura: </b>{modo(stage.mode)}</p>
                                <p><b>Responsáveis: </b>
                                  {stage.responsibles.map((resp, index) => {
                                    return (
                                      <a key={index}> {resp}; </a>
                                    )
                                  })}
                                </p>
                                <button className="btn btn-danger" onClick={() => deleteStage(index)}>Apagar etapa</button>
                              </div>
                            )}
                          </Draggable>
                        </div>
                        
                      )
                    })}
                    {provided.placeholder}
                  </ul>
                )}
              </Droppable>
            </DragDropContext>
          </div> 
        </div>
      
      </div>
        
      <div>
        {showUsersModal && createPortal(
          <TemplateUsersModal 
            onClose={() => setShowUsersModal(false)}
            loggedUser={loggedUser}
            selectedTemplate={selectedTemplate.nome}
          />,
          document.body
        )}
      </div>
      <div>
        {showDetailsModal && createPortal(
          <TemplateDetailsModal 
            onClose={() => setShowDetailsModal(false)}
            selectedTemplate={selectedTemplate.nome}
          />,
          document.body
        )}
      </div>
      <div>
        {showNewStageModal && createPortal(
          <NewStageModal 
            onClose={() => {
              setShowModal(false)
              resetStageParams()
            }}
            onSave={addStage}
            stageName={stageName} 
            setStageName={setStageName} 
            stageDescription={stageDescription} 
            setStageDescription={setStageDescription}
            stageResponsibles={stageResponsibles}
            setStageResponsibles={setStageResponsibles}
            stageDuration={stageDuration}
            setStageDuration={setStageDuration}
            stageError={stageError} 
            setStageError={setStageError}
            stages={stages}
            roleGroups={roleGroups}
            users={users}
            selectedMode={selectedMode}
            setSelectedMode={setSelectedMode}
          />,
          document.body
        )}
      </div>
    
    </div>
  )
}
