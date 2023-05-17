import { useEffect, useState } from "react"
import './processes.css'
import processServices from "../../Services/process.service"

export default function Processes() {

    const [availableTemplates, setAvailableTemplates] = useState([])

    const [processName, setProcessName] = useState("")
    const [processDescription, setProcessDescription] = useState("")
    const [selectedTemplate, setSelectedTemplate] = useState("FUC")

    const [error, setError] = useState("")

    useEffect(() => {
        processServices.getTemplates(setAvailableTemplates)
    }, [])

    function templateOptions() {
        let options = []

        availableTemplates.forEach ((template, index) =>  {
            options.push(<option key={index} value={template}>{template}</option>)
        })

        return options;
    }  

    function fillProcessParams() {
        processServices.getTemplates(setAvailableTemplates)
    }

    return (
        <div>
            <p><button onClick={() => window.location.href = "/templates"}>Criar template</button></p>
            <h2>Novo processo</h2>

        { availableTemplates.length === 0 ?
            <p className="error">Não existem templates dísponiveis.</p>
        : 
            <div>  
                    <select value={selectedTemplate} onChange={(e) => setSelectedTemplate(e.target.value)}>
                        {templateOptions()}
                    </select>
                
                    <p><label><b>Nome: </b><input type="text" value={processName} onChange={e => {setProcessName(e.target.value)}}/></label></p>
                    <p><label><b>Descrição: </b><textarea value={processDescription} onChange={e => setProcessDescription(e.target.value)}/></label></p>
                    <p className="error">{error}</p>
                    <button onClick={fillProcessParams}>Avançar</button>
            </div>
        }
           
        </div>
    )
}