import { useEffect, useState } from "react"
import { createPortal } from 'react-dom'
import FormData from 'form-data';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd'
import './templates.css'
import processServices from "../../Services/process.service"

function ModalContent({ 
  onClose, 
  onSave, 
  stageName, 
  setStageName, 
  stageDescription, 
  setStageDescription, 
  stageResponsibles,
  setStageResponsibles,
  stageError, 
  setStageError,
  stages,
  userGroups,
  users
 }) {
  const [searchInput, setSearchInput] = useState("")
  const [selectedList, setSelectedList] = useState("Groups")
  const [groups, setGroups] = useState(userGroups)
  const [all, setAll] = useState(users)

  const handleSave = () => {
    if (stageName === "" || stageDescription === "")
      setStageError("Nome e/ou descrição da etapa em falta.")
    else if(stageResponsibles.length === 0)
      setStageError("Responsáveis em falta.")
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
      <div className="modal">
        <div>Nova etapa
          <p><label>Nome: <input type="text" value={stageName} onChange={e => setStageName(e.target.value)}/></label></p>
          <p><label>Descrição: <textarea value={stageDescription} onChange={e => setStageDescription(e.target.value)}/></label></p>
          <div>
            <label>Responsáveis: </label>
            <div>
              {stageResponsibles.map((resp, index) => {
                return (
                  <b>{resp}<button key={index} onClick={() => removeResp(resp)}>x</button></b>
                )
              })}
            </div>
            <div>
              <input type="text" id="myInput" placeholder="Pesquisar responsáveis" 
                onChange={(e) => {setSearchInput(e.target.value)}}></input>
                <select value={selectedList} onChange={(e) => setSelectedList(e.target.value)}>
                  <option value="Groups">Groups</option>
                  <option value="All">All</option>
                </select>
            </div>
            {filteredUsers.map((item, index) => (
              <button key={index} onClick={() => addResponsible(item)}>{item}</button>
            ))}
          </div>
          <p className="error">{stageError}</p>
        </div>
        <button onClick={handleSave}>Guardar etapa</button>
        <button onClick={onClose}>Cancelar</button>
      </div>
    </div>
  )
}

export default function Templates() {

  const [availableTemplates, setAvailableTemplates] = useState([])
  const [selectedTemplate, setSelectedTemplate] = useState("")

  const responsiblesSample = ["CP", "CTC", "user1@gmail.com", "user2@gmail.com", "user3@gmail.com"]
  
  // template name, description, and stages
  const [templateName, setTemplateName] = useState("")
  const [templateDescription, setTemplateDescription] = useState("")
  const [stages, setStages] = useState([])

  // TODO get users and user groups from API
  const userGroups = ["RUC", "CCC", "CCD", "CP", "CTC", "Serviços Académicos"]
  const users = ["Miguel Almeida <miguelalmeida@isel.pt>", "Ricado Bernardino <ricky@isel.pt>", "David Costa <david@isel.pt>"]
  // This contains user emails associated to a role, fetched from the API
  // TODO when a user group is added to stage responsibles, adding individual users, filters the previously added
  const [groupUsers, setGroupUsers] = useState()

  // new stage name, description, and responsibles
  const [stageName, setStageName] = useState("")
  const [stageDescription, setStageDescription] = useState("")
  const [stageResponsibles, setStageResponsibles] = useState([])

  // modal for new stage parameter fill
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState("")
  const [stageError, setStageError] = useState(null)

  useEffect(() => {
      processServices.getAvailableTemplates(setAvailableTemplates)
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
    // TODO check if template name was already used
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
      stages: stages,
    }

    const templateJson = JSON.stringify(template)
    console.log(templateJson)
    
    const formData = new FormData()
    const jsonBlob = new Blob([templateJson], { type: 'application/json' })

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
              <button onClick={() => processServices.deleteTemplate(selectedTemplate)}>Apagar template</button>
            </div>
        }
        <h2>Novo template de processo</h2>
        <p><label><b>Nome: </b><input type="text" value={templateName} onChange={e => {setTemplateName(e.target.value)}}/></label></p>
        <p><label><b>Descrição: </b><textarea value={templateDescription} onChange={e => setTemplateDescription(e.target.value)}/></label></p>
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
                        <p><b>Responsáveis: </b></p>
                        <p>{stage.responsibles}</p>
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
        {showModal && createPortal(
          <ModalContent 
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
            stageError={stageError} 
            setStageError={setStageError}
            stages={stages}
            userGroups={userGroups}
            users={users}
          />,
          document.body
        )}
      </div>
    
    </div>
  )
}
