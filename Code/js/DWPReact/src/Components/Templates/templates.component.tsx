import { useState } from "react"
import { createPortal } from 'react-dom'
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd'
import './templates.css'

function ModalContent({ 
  onClose, 
  onSave, 
  stageName, 
  setStageName, 
  stageDescription, 
  setStageDescription, 
  stageError, 
  setStageError,
  stages
 }) {
  const handleSave = () => {
    if (stageName === "" || stageDescription === ""){
      setStageError("Nome e/ou descrição da etapa em falta.")
    } 
    else if (stages.find(stage => stage.name === stageName) != null) {
      setStageError("Nome de etapa deve ser único.")
    }
    else {
      onSave()
      onClose()
    }
  }

  return (
    <div className="bg">
      <div className="modal">
        <div>Nova etapa
          <p><label>Nome: <input type="text" value={stageName} onChange={e => setStageName(e.target.value)}/></label></p>
          <p><label>Descrição: <textarea value={stageDescription} onChange={e => setStageDescription(e.target.value)}/></label></p>
          <p className="error">{stageError}</p>
        </div>
        <button onClick={handleSave}>Guardar etapa</button>
        <button onClick={onClose}>Cancelar</button>
      </div>
    </div>
  )
}

export default function Templates() {

  const sample = {
    name: "stageName",
    description: "stageDescription",
    responsibles: [],
  }

  const sample2 = {
    name: "stageName2",
    description: "stageDescription2",
    responsibles: [],
  }
  
  // template name, description, and stages
  const [templateName, setTemplateName] = useState("")
  const [templateDescription, setTemplateDescription] = useState("")
  const [stages, setStages] = useState([sample, sample2])

  // new stage name, description, and responsibles
  const [stageName, setStageName] = useState("")
  const [stageDescription, setStageDescription] = useState("")
  const [stageResponsibles, setStageResponsibles] = useState([])

  // modal for new stage parameter fill
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState("")
  const [stageError, setStageError] = useState(null)

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
      <h2>Novo template de processo</h2>
      <div className="templateParams">
        <p><label>Nome: <input type="text" value={templateName} onChange={e => {setTemplateName(e.target.value)}}/></label></p>
        <p><label>Descrição: <textarea value={templateDescription} onChange={e => setTemplateDescription(e.target.value)}/></label></p>
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
                        Nome: {stage.name}
                        <p>Descrição: {stage.description}</p>
                        <ul>Responsáveis:
                          {stage.responsibles.map((email, index) => (
                            <li key={index}>{email}</li>
                          ))}
                        </ul>
                        <button onClick={() => deleteStage(index)}>Apagar etapa</button>
                      </div>
                    )}
                  </Draggable>
                );
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
            stageError={stageError} 
            setStageError={setStageError}
            stages={stages}
          />,
          document.body
        )}
      </div>
    
    </div>
  )
}
