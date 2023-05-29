import { useEffect, useState, useRef } from "react"
import './processes.css'
import processServices from "../../Services/Processes/process.service"
import { createPortal } from 'react-dom'
import { TemplateDetailsModal } from "../Templates/templateModals"

export const NewProcess = () => {

    const [availableTemplates, setAvailableTemplates] = useState([])
    const [uploadedDocs, setUploadedDocs] = useState([])

    const [processName, setProcessName] = useState("")
    const [processDescription, setProcessDescription] = useState("")
    const [selectedTemplate, setSelectedTemplate] = useState("")

    const [error, setError] = useState("")

    const [showDetailsModal, setShowDetailsModal] = useState(false)

    useEffect(() => {
        const fetchData = async () => {
            setAvailableTemplates(await processServices.availableTemplates())
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

    function DragDropFiles() {

        const [dragActive, setDragActive] = useState(false)
        const inputRef = useRef(null)
        
        const handleDrag = function(e) {
            e.preventDefault()
            e.stopPropagation()
            
            if (e.type === "dragenter" || e.type === "dragover")
                setDragActive(true)
            else if (e.type === "dragleave")
                setDragActive(false)
        }
        
        const handleDrop = function(e) {
            e.preventDefault()
            e.stopPropagation()
            setDragActive(false)

            if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
                const files = Array.from(e.dataTransfer.files)
                setUploadedDocs((prevDocs) => [...prevDocs, ...files])
            }
        }
        
        const handleChange = function (e) {
            e.preventDefault()

            inputRef.current.click()
        
            if (e.target.files && e.target.files.length > 0) {
                const files = Array.from(e.target.files)
                setUploadedDocs((prevDocs) => [...prevDocs, ...files])
            }
        }

        const handleDelete = function (fileName) {
            setUploadedDocs((prevDocs) => prevDocs.filter((file) => file.name !== fileName))
        }

        const handleSubmit = function(e) {
            e.preventDefault()
            
            if (processName === "" || processDescription === "") {
                setError("Nome e/ou descrição do processo em falta.")
                return
            }
    
            if (uploadedDocs.length === 0){
                setError("Nenhum documento carregado.")
                return
            }

            const formData = new FormData()
            formData.append('templateName', selectedTemplate)
            formData.append('name', processName)
            formData.append('description', processDescription)

            uploadedDocs.forEach((file) => formData.append('file', file))
    
            processServices.createProcess(formData)
        }
        
        return (
            <form id="form-file-upload" onDragEnter={handleDrag} onSubmit={handleSubmit}>
                <input ref={inputRef} type="file" id="input-file-upload" multiple={true} onChange={handleChange} />
                <label id="label-file-upload" htmlFor="input-file-upload" className={dragActive ? "drag-active" : "" }>
                    <div>
                        <p>Arrasta documentos para aqui ou</p>
                        <button className="upload-button" onClick={handleChange}>Procura documentos</button>
                        {uploadedDocs.length > 0 ? (
                            <div>
                                <div className="files-scroll">
                                    {uploadedDocs.map((file) => (
                                        <div key={file.name}>
                                            <p>{file.name} <button onClick={() => handleDelete(file.name)}>x</button></p>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        ) : (
                            <p>Nenhum documento carregado</p>
                        )}
                    </div> 
                </label>
                { dragActive && <div id="drag-file-element" onDragEnter={handleDrag} onDragLeave={handleDrag} onDragOver={handleDrag} onDrop={handleDrop}></div> }
                <p></p>
                <input type="submit" value="Criar Processo"/>
            </form>
        )
    }

    return (
        <div>
            <h2>Novo processo</h2>

            { availableTemplates.length === 0 ?
                <p className="error">Não existem templates disponíveis.</p>
            : 
                <div>  
                        <label><b>Template: </b>
                            <select value={selectedTemplate} onChange={(e) => setSelectedTemplate(e.target.value)}>
                                {templateOptions()}
                            </select>
                            <button onClick={() => setShowDetailsModal(true)}>Detalhes</button>
                        </label>
                    
                        <p><b>Nome: </b></p>
                        <input className="name-input" type="text" value={processName} onChange={e => {setProcessName(e.target.value)}}/>
                        <p><b>Descrição: </b></p>
                        <textarea className="description-area" value={processDescription} onChange={e => setProcessDescription(e.target.value)}/>
                        <p className="error">{error}</p>
                        
                        <DragDropFiles/>

                        <div>
                            {showDetailsModal && createPortal(
                                <TemplateDetailsModal 
                                    onClose={() => setShowDetailsModal(false)}
                                    selectedTemplate={selectedTemplate}
                                />,
                                document.body
                            )}
                        </div>
                </div>
            }
        </div>
    )
}