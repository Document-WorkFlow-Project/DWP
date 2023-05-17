import { useEffect, useState } from "react"
import './processes.css'
import processServices from "../../Services/process.service"

export default function Processes() {
    
    const templatesSampleNames = ["FUC", "ATA"]

    const sampleTemplates = {
        "FUC" : {
            "name":"FUC",
            "description":"ficha unidade corricular",
            "stages":[
                {
                    "name":"tarefa1",
                    "description":"fweaf",
                    "responsibles":["RUC","CCD"]
                },
                {
                    "name":"task2",
                    "description":"cwe",
                    "responsibles":["CCC","CTC"]
                }
            ]
        },
        "ATA" : {
            "name":"ATA",
            "description":"exemplo",
            "stages":[
                {
                    "name":"tarefa1",
                    "description":"fweaf",
                    "responsibles":["RUC","CCD"]
                },
                {
                    "name":"task2",
                    "description":"cwe",
                    "responsibles":["CCC","CTC"]
                }
            ]
        }
    }

    const [availableTemplates, setAvailableTemplates] = useState(templatesSampleNames)

    const [processName, setProcessName] = useState("")
    const [processDescription, setProcessDescription] = useState("")
    const [selectedTemplate, setSelectedTemplate] = useState("FUC")

    const [error, setError] = useState("")

    useEffect(() => {
        async () => await processServices.getTemplates(setAvailableTemplates)
    }, [])

    function templateOptions() {
        let options = []

        availableTemplates.forEach ((template, index) =>  {
            options.push(<option key={index} value={template}>{template}</option>)
        })

        return options;
    }  

    return (
        <div>
           <h2>Novo processo</h2>

           <div>  
                <select value={selectedTemplate} onChange={(e) => setSelectedTemplate(e.target.value)}>
                    {templateOptions()}
                </select>
            </div>
            <p><label><b>Nome: </b><input type="text" value={processName} onChange={e => {setProcessName(e.target.value)}}/></label></p>
            <p><label><b>Descrição: </b><textarea value={processDescription} onChange={e => setProcessDescription(e.target.value)}/></label></p>
            <p className="error">{error}</p>
        </div>
    )
}