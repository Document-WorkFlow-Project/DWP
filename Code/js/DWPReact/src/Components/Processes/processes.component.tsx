import { useEffect, useState, useRef } from "react"
import './processes.css'
import processServices from "../../Services/process.service"
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

    function createProcess() {
        if (processName === "" || processDescription === "") {
            setError("Nome e/ou descrição do processo em falta.")
            return
        }

        if (uploadedDocs.length === 0){
            setError("Nenhum documento carregado.")
            return
        }

        console.log(uploadedDocs)
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
                        <div className="files-scroll">
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
                    
                        <p><label><b>Nome: </b><input type="text" value={processName} onChange={e => {setProcessName(e.target.value)}}/></label></p>
                        <p><label><b>Descrição: </b><textarea value={processDescription} onChange={e => setProcessDescription(e.target.value)}/></label></p>
                        <p className="error">{error}</p>
                        <DragDropFile/>
                        <p></p>
                        <button onClick={createProcess}>Criar processo</button>

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