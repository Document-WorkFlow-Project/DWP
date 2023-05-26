import { useEffect, useState } from "react"
import { createPortal } from 'react-dom'
import FormData from 'form-data';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd'
import './templates.css'
import processServices from "../../Services/process.service"
import {NewStageModal, TemplateDetailsModal} from "./templateModals"
import rolesService from "../../Services/roles.service";
import usersService from "../../Services/users.service";

export default function Templates() {

  const [availableTemplates, setAvailableTemplates] = useState([])
  const [selectedTemplate, setSelectedTemplate] = useState("")
  
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

  useEffect(() => {
    const fetchData = async () => {
      setAvailableTemplates(await processServices.availableTemplates())
      setRoleGroups(await rolesService.availableRoles())
      setUsers(await usersService.usersList())
    }
    fetchData()
  }, [])
  
  useEffect(() => {
    if (availableTemplates.length > 0)
      setSelectedTemplate(availableTemplates[0])
  }, [availableTemplates])

  function templateOptions() {
    let options = []

    availableTemplates.forEach ((template, index) =>  {
        options.push(<option key={index} value={template}>{template}</option>)
    })

    return options;
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
    if (availableTemplates.find(name => name === templateName)) {
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

    const templateJson = JSON.stringify(template)
    console.log(templateJson)
    
    const formData = new FormData()
    const jsonBlob = new Blob([templateJson], { type: 'application/json' })

    formData.append('name', templateName)
    formData.append('description', templateDescription)
    formData.append('file', jsonBlob, template.name + ".json")

    processServices.saveTemplate(formData)
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
      <div className="templateParams">
        <h2>Templates disponíveis</h2>
        { availableTemplates.length === 0 ?
              <p className="error">Não existem templates disponíveis.</p>
          : 
            <div>  
              <select value={selectedTemplate} onChange={(e) => setSelectedTemplate(e.target.value)}>
                  {templateOptions()}
              </select>
              <button onClick={() => setShowDetailsModal(true)}>Detalhes</button>
              <button onClick={() => {
                  processServices.deleteTemplate(selectedTemplate)
                  setAvailableTemplates(available => available.filter(name => name !== selectedTemplate))
                }}>Apagar template</button>
            </div>
        }
        <h2>Novo template de processo</h2>
        <p><b>Nome: </b></p>
        <input className="name-input" type="text" value={templateName} onChange={e => {setTemplateName(e.target.value)}}/>
        <p><b>Descrição: </b></p>
        <textarea className="description-area" value={templateDescription} onChange={e => setTemplateDescription(e.target.value)}/>
        <p className="error">{error}</p>

        <button onClick={() => setShowModal(true)}>Adicionar etapa</button>
        <button onClick={saveTemplate}>Guardar template</button>
      </div>

      <DragDropContext onDragEnd={handleOnDragEnd}>
        <Droppable droppableId="characters">
          {(provided) => (
            <ul className="characters" {...provided.droppableProps} ref={provided.innerRef}>
              {stages.map((stage, index) => {
                return (
                  <Draggable key={stage.name} draggableId={stage.name} index={index}>
                    {(provided) => (
                      <div ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps} className="clipping-container" key={index}>
                        <b>Nome: </b>{stage.name}
                        <p><b>Descrição: </b>{stage.description}</p>
                        <p><b>Prazo: </b>{stage.duration} dias</p>
                        <p><b>Modo de assinatura: </b>{stage.mode}</p>
                        <p><b>Responsáveis: </b>
                          {stage.responsibles.map((resp, index) => {
                            return (
                              <a key={index}> {resp}; </a>
                            )
                          })}
                        </p>
                        <button onClick={() => deleteStage(index)}>Apagar etapa</button>
                      </div>
                    )}
                  </Draggable>
                )
              })}
              {provided.placeholder}
            </ul>
          )}
        </Droppable>
      </DragDropContext>
      
      <div>
        {showDetailsModal && createPortal(
          <TemplateDetailsModal 
            onClose={() => setShowDetailsModal(false)}
            selectedTemplate={selectedTemplate}
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
