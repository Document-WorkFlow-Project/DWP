import { useEffect, useState, useRef, useContext } from "react"
import './processes.css'
import processServices from "../../Services/Processes/process.service"
import { createPortal } from 'react-dom'
import { TemplateDetailsModal } from "../Templates/templateModals"
import templatesService from "../../Services/Templates/templates.service"
import { AuthContext } from '../../AuthProvider';
import {toast} from 'react-toastify';

export const NewProcess = ({ navigate }) => {

    const [availableTemplates, setAvailableTemplates] = useState([])
    const [uploadedDocs, setUploadedDocs] = useState([])

    const [processName, setProcessName] = useState("")
    const [processDescription, setProcessDescription] = useState("")
    const [selectedTemplate, setSelectedTemplate] = useState("")

    const [error, setError] = useState("")

    const [showDetailsModal, setShowDetailsModal] = useState(false)

    const [loading, setLoading] = useState(false);

    const { loggedUser } = useContext(AuthContext);

    useEffect(() => {
        if (!loggedUser.email) {
            navigate('/');
            toast.error("O utilizador não tem sessão iniciada.")
            return
        }

        const fetchData = async () => {
            try {
                const templates = await templatesService.availableTemplates()
                setAvailableTemplates(templates)
                    
                if(templates.length > 0)
                    setSelectedTemplate(templates[0])
            } catch (err) {
                const resMessage = err.response.data || err.toString();
                toast.error(resMessage);
            }
        }

        fetchData()
    }, [])

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
                const files = Array.from(e.dataTransfer.files) as unknown as FileList;
                const maxSize = 500 * 1024 * 1024 // 500MB

                // calculate the size of the files added before
                let totalSize = uploadedDocs.reduce((size, file) => size + file.size, 0);
                const validFiles = [];

                // check if the size of each file added does not exceed the limit
                for (let i = 0; i < files.length; i++) {
                    const file = files[i]
                    if (file) {
                        totalSize += file.size;
        
                        if (totalSize <= maxSize) {
                            validFiles.push(file);
                        } else {
                            toast.error("Limite 500MB de upload atingido.")
                            break;
                        }
                    }
                }
        
                setUploadedDocs(prevDocs => [...prevDocs, ...validFiles]);
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

        const handleSubmit = async function(e) {
            e.preventDefault()

            if (processName === "" || processDescription === "") {
                setError("Nome e/ou descrição do processo em falta.")
                return
            }

            if (uploadedDocs.length === 0){
                setError("Nenhum documento carregado.")
                return
            }
            
            setLoading(true)

            const formData = new FormData()
            formData.append('templateName', selectedTemplate)
            formData.append('name', processName)
            formData.append('description', processDescription)

            uploadedDocs.forEach((file) => formData.append('file', file))

            try {
                await processServices.createProcess(formData)
                navigate("/processes")
            }
            catch(err) {
                const resMessage = err.response.data || err.toString();
                toast.error(resMessage);
            }

            setLoading(false)
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
                                            <p>{file.name} <button className="btn btn-close" onClick={() => handleDelete(file.name)}></button></p>
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
                <input
                    className="btn btn-success"
                    type="submit"
                    value={loading ? "Loading..." : "Criar Processo"}
                    disabled={loading}
                />
            </form>
        )
    }

    return (
        <div className="container-fluid">
            <p></p>
            <h2>Novo processo</h2>
            <p></p>

            { availableTemplates.length === 0 ?
                <p className="error">Não existem templates disponíveis.</p>
            :
                <div className="container-fluid">
                    <div className="row row-cols-auto align-items-center">
                        <div className="col">
                            <b>Template: </b>
                        </div>
                        
                        <div className="col-3">
                            <select className="form-select" value={selectedTemplate} onChange={(e) => setSelectedTemplate(e.target.value)}>
                                {templateOptions()}
                            </select>
                        </div>
                        
                        <div className="col">
                            <button className="btn btn-primary" onClick={() => setShowDetailsModal(true)}>Detalhes</button>
                        </div>
                        
                    </div>
                    
                    
                    <p></p>
                    <div className="col-6">
                        <p><b>Nome: </b></p>
                        <input className="form-control" type="text" value={processName} onChange={e => {setProcessName(e.target.value)}}/>
                    </div>

                    <div className="col-6">
                        <p></p>
                        <p><b>Descrição: </b></p>
                        <textarea className="form-control" style={{ resize: "none" }} value={processDescription} onChange={e => setProcessDescription(e.target.value)}/>
                    </div>
                    <p></p>
                    <p className="error">{error}</p>
                    
                    
                    <div className="row">
                        <p></p>
                        <div className="col-1"></div>
                        <DragDropFiles/>
                    </div>

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