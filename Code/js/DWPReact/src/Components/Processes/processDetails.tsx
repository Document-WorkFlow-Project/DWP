import { useEffect, useState } from "react"
import processServices from "../../Services/Processes/process.service"
import { useParams } from 'react-router';
import { convertTimestamp, estado } from "../../utils";
import {Link} from "react-router-dom";
import { formatBytes } from "../../utils";
import { BsClockHistory, BsCheckCircle, BsXCircle, BsDashCircle, BsFillRecord2Fill } from 'react-icons/bs';


export const ProcessDetails = () => {
    
    const { id } = useParams();
    const [processDetails, setProcessDetails] = useState({
        id: "",
        nome: "",
        descricao: "",
        template_processo: "",
        estado: "PENDING",
        autor: "",
        data_inicio: null,
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
                
                const currentStageIndex = stages.findIndex((stage) => stage.estado === 'PENDING' || stage.estado === 'DISAPPROVED');
                
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

    const getClassName = (state) => {
        if (state === "PENDING")
            return 'pending-stage'
        else if (state === "APPROVED")
            return 'success-stage'
        else if (state === "DISAPPROVED")
            return 'failure-stage'
    }

    return (
        <div className="container-fluid">
            <div className="row row-cols-auto">
                <div className="col-6">
                    <p></p>
                    <h2>{processDetails.nome}</h2>
                    <p></p>
                    <p><b>Descrição: </b>{processDetails.descricao}</p>
                    <p><b>Template: </b>{processDetails.template_processo}</p>
                    <p><b>Autor: </b>{processDetails.autor}</p>
                    <p><b>Estado: </b>{estado(processDetails.estado)}</p>
                    <p><b>Data de início: </b>{convertTimestamp(processDetails.data_inicio)}</p>
                    {processDetails.data_fim && <p><b>Data de fim: </b>{convertTimestamp(processDetails.data_fim)}</p>}
                    
                    <p><b>Documentos: </b></p>
                    {processDocs.names.length > 0 ?
                        <div>
                            <div className="scroll">
                                {processDocs.names.map((name, index) => {
                                    return (<p key={index}>{name} </p>)})
                                }
                            </div>
                            <p></p>
                            <p><b>Tamanho: </b>{formatBytes(processDocs.size)}</p>
                            <p><button className="btn btn-primary" onClick={() => downloadDocs()}>Transferir documentos</button></p>
                        </div>
                        :
                        <p>Nenhum documento carregado.</p>
                    }
                </div>
                
                <div className="col align-self-center">
                    <ul className="timeline-with-icons">
                        {processStages.map((stage, index) => {
                            let className;
                            let icon
                            let state

                            if (index === currentStage) {
                                if (stage.estado === "DISAPPROVED") {
                                  className = "progress__item--disapproved";
                                  icon = <BsXCircle/>
                                  state = "Não aprovado"
                                } else if (stage.estado === "APPROVED") {
                                  className = "progress__item--approved";
                                  icon = <BsCheckCircle/>
                                  state = "Aprovado"
                                } else {
                                  className = "progress__item--current";
                                  icon = <BsFillRecord2Fill/>
                                  state = "Em desenvolvimento"
                                }
                            } else if (index > currentStage && processDetails.estado === "DISAPPROVED") {
                                className = "progress__item--left-uncomplete";
                                icon = <BsDashCircle/>
                                state = "Não iniciado"
                            } else if (stage.estado === "PENDING") {
                                className = "progress__item--pending";
                                icon = <BsClockHistory/>
                                state = "Pendente"
                            } else if (stage.estado === "APPROVED") {
                                className = "progress__item--approved";
                                icon = <BsCheckCircle/>
                                state = "Aprovado"
                            }

                            return (
                                <li key={index} className={`timeline-item mb-5 ${className}`}>
                                    <span className="timeline-icon">
                                        {icon}
                                    </span>
                                    <p>
                                        <Link className="link-dark link-offset-2 link-underline link-underline-opacity-0" to={`/stage/${stage.id}`}>
                                        <b>{stage.nome}</b>
                                        </Link>
                                    </p>
                                    <p className="progress__info">
                                        <b className={`${className}`}>{state}</b>
                                    </p>
                                </li>
                            );
                        })}
                    </ul>
                </div>

            </div>
        </div>
    )
}