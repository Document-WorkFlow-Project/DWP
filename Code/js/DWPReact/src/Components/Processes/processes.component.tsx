import { useEffect, useState, useRef } from "react"
import './processes.css'
import processServices from "../../Services/process.service"
import { createPortal } from 'react-dom'
import { Form } from "../Form"

export const NewProcess = () => {

    const [availableTemplates, setAvailableTemplates] = useState([])
    const [uploadedDocs, setUploadedDocs] = useState([])

    const [processName, setProcessName] = useState("")
    const [processDescription, setProcessDescription] = useState("")
    const [selectedTemplate, setSelectedTemplate] = useState("")

    const [showModal, setShowModal] = useState(false);
    const [modalError, setModalError] = useState("")
    const [error, setError] = useState("")

    useEffect(() => {
        const fetchData = async () => {
            const templates = await processServices.getAvailableTemplates()
            setAvailableTemplates(templates);
        }
        fetchData()
    }, [])

    useEffect(() => {
        setSelectedTemplate(availableTemplates[0])
    }, [availableTemplates])

    function templateOptions() {
        let options = []

        availableTemplates.forEach ((template, index) =>  {
            options.push(<option key={index} value={template}>{template}</option>)
        })

        return options;
    }  

    function fillProcessParams() {
        /*
        if (processName === "" || processDescription === "") {
            setError("Nome e/ou descrição do processo em falta.")
            return
        }

        if (uploadedDocs.length === 0){
            setError("Nenhum documento carregado.")
            return
        }*/

        setShowModal(true)
    }

    function createProcess() {
        console.log(uploadedDocs)
    }

    function ModalContent({ 
        onClose, 
        onSave,
        modalError
    }) {

        const [templateJson, setTemplateJson] = useState({
            name:"",
            description:"",
            stages:[
              {
                name:"",
                description:"",
                responsibles:[""],
                prazo: null
              }
            ]
        })

        const [stageDurations, setStageDurations] = useState(() => {
            return Array(templateJson.stages.length).fill(0)
        })

        useEffect(() => {
            const fetchData = async () => {
                const template = await processServices.getTemplate(selectedTemplate)
                setTemplateJson(template)
                setStageDurations(Array(templateJson.stages.length).fill(0))
            }
            fetchData()
        }, [selectedTemplate, templateJson])
      
        const handleSave = () => {
            onSave()
            onClose()
        }

        return (
            <div className="bg">
                <div className="modal">Preencher campos do processo
                    <div className="scroll">
                        {templateJson.stages.map((stage, index) => {                           
                            return (
                                <div key={index} className="clipping-container">
                                    <p><b>{stage.name}</b></p>
                                    <p><b>Descrição: </b>{stage.description}</p>
                                    <p><b>Responsáveis: </b>
                                    {stage.responsibles.map((resp, index) => {
                                        return (
                                            <a key={index}> {resp}; </a>
                                        )
                                    })}</p>
                                    <p><b>Prazo: </b><input type="number" value={stageDurations[index]} onChange={e => {
                                        const updatedDurations = [...stageDurations]
                                        updatedDurations[index] = parseInt(e.target.value) || 0
                                        setStageDurations(updatedDurations)
                                    }} /></p>
                                </div>
                            )
                        })}
                    </div>
                    <p className="error">{modalError}</p>
                    <button onClick={handleSave}>Criar processo</button>
                    <button onClick={onClose}>Cancelar</button>
                </div>
            </div>
        )
    }

    function DragDropFile() {

        const [dragActive, setDragActive] = useState(false);
        const inputRef = useRef(null);
        
        const handleDrag = function(e) {
            e.preventDefault()
            e.stopPropagation()
            
            if (e.type === "dragenter" || e.type === "dragover")
                setDragActive(true)
            else if (e.type === "dragleave")
                setDragActive(false)
        }

        const handleFile = function(files) {
            setUploadedDocs(Array.from(files))
        }
        
        const handleDrop = function(e) {
            e.preventDefault()
            e.stopPropagation()
            setDragActive(false)
            
            if (e.dataTransfer.files && e.dataTransfer.files[0])
                handleFile(e.dataTransfer.files);
        }
        
        const handleChange = function(e) {
            e.preventDefault()
            if (e.target.files && e.target.files[0])
                handleFile(e.target.files)
        }
        
        return (
            <form id="form-file-upload" onDragEnter={handleDrag} onSubmit={(e) => e.preventDefault()}>
                <input ref={inputRef} type="file" id="input-file-upload" multiple={true} onChange={handleChange} />
                <label id="label-file-upload" htmlFor="input-file-upload" className={dragActive ? "drag-active" : "" }>
                <div>
                    <p>Arrasta documentos para aqui ou</p>
                    <button className="upload-button" onClick={() => inputRef.current.click()}>Procura documentos</button>
                    {uploadedDocs.length > 0 ? (
                        <div>
                        <pre className="output">Selected files:</pre>
                            {uploadedDocs.map((file) => (
                                <p key={file.name}>{file.name}</p>
                            ))}
                        </div>
                    ) : (
                        <p>Nenhum documento carregado</p>
                    )}
                </div> 
                </label>
                { dragActive && <div id="drag-file-element" onDragEnter={handleDrag} onDragLeave={handleDrag} onDragOver={handleDrag} onDrop={handleDrop}></div> }
            </form>
        )
    }

    return (
        <div>
            <p><button onClick={() => window.location.href = "/templates"}>Criar template</button></p>
            <h2>Novo processo</h2>

            { availableTemplates.length === 0 ?
                <p className="error">Não existem templates disponíveis.</p>
            : 
                <div>  
                        <label><b>Template: </b>
                            <select value={selectedTemplate} onChange={(e) => setSelectedTemplate(e.target.value)}>
                            {templateOptions()}
                            </select>
                        </label>
                    
                        <p><label><b>Nome: </b><input type="text" value={processName} onChange={e => {setProcessName(e.target.value)}}/></label></p>
                        <p><label><b>Descrição: </b><textarea value={processDescription} onChange={e => setProcessDescription(e.target.value)}/></label></p>
                        <p className="error">{error}</p>
                        <DragDropFile/>
                        <button onClick={fillProcessParams}>Preencher campos</button>

                        {showModal && createPortal(
                            <ModalContent 
                                onClose={() => setShowModal(false)}
                                onSave={createProcess}
                                modalError={modalError}
                            />,
                            document.body
                        )}
                </div>
            }
        </div>
    )
}