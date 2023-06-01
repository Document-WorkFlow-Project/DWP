import { useEffect, useState } from "react"
import processServices from "../../Services/process.service"
import { useParams } from 'react-router';
import { convertTimestamp, estado } from "../../utils";
import {Link} from "react-router-dom";
import { formatBytes } from "../../utils";


export const ProcessDetails = () => {
    
    const { id } = useParams();
    const [processDetails, setProcessDetails] = useState({
        id: "",
        nome: "",
        descricao: "",
        template_processo: "",
        estado: "PENDING",
        autor: "",
        data_inicio: "",
        data_fim: null
    })
    const [processDocs, setProcessDocs] = useState({
        names: [],
        size: 0
    })
    const [processStages, setProcessStages] = useState([])
    const [currentStage, setCurrentStage] = useState(0)

    useEffect(() => {
        const fetchData = async () => {
            const details = await processServices.processDetails(id)
            
            if (typeof details === 'object')
                setProcessDetails(details)

            const docDetails = await processServices.processDocDetails(id)
            if (typeof docDetails === 'object')
                setProcessDocs(docDetails)

            const stages = await processServices.processStages(id)
            
            if (Array.isArray(stages)){
                setProcessStages(stages)
                
                const currentStageIndex = stages.findIndex((stage) => stage.estado === 'PENDING');
                
                if (currentStageIndex === -1) 
                    setCurrentStage(stages.length - 1)
                else
                    setCurrentStage(currentStageIndex)
            }
        }
        fetchData()
    }, [])

    async function downloadDocs() {
        const data = await processServices.downloadDocs(id);
        const downloadUrl = window.URL.createObjectURL(new Blob([data]));
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.setAttribute('download', `documents-${id}.zip`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    return (
        <div>
            <h2>{processDetails.nome}</h2>
            <p><b>Descrição: </b>{processDetails.descricao}</p>
            <p><b>Template: </b>{processDetails.template_processo}</p>
            <p><b>Autor: </b>{processDetails.autor}</p>
            <p><b>Estado: </b>{estado(processDetails.estado)}</p>
            <p><b>Data de início: </b>{convertTimestamp(processDetails.data_inicio)}</p>
            {processDetails.data_fim && <p><b>Data de fim: </b>{convertTimestamp(processDetails.data_fim)}</p>}
            <p><b>Documentos: </b></p>
            <div className="scroll">
                {processDocs.names.map((name, index) => {
                    return (<p key={index}>{name} </p>)})
                }
            </div>
            <p><b>Tamanho: </b>{formatBytes(processDocs.size)}</p>
            <p><button onClick={() => downloadDocs()}>Transferir documentos</button></p>
            <div>
                {processStages.map((stage, index) => {
                    const className = index === currentStage ? 'pending-stage' : '';

                    return (
                        <div key={index} className={`clipping-container ${className}`}>
                            <p><Link to={`/stage/${stage.id}`}><b>{stage.nome}</b></Link></p>
                            <p><b>{estado(stage.estado)}</b></p>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}