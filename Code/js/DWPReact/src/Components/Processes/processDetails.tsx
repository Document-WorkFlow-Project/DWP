import { useEffect, useState } from "react"
import processServices from "../../Services/process.service"
import { useParams } from 'react-router';
import { convertTimestamp, estado } from "../../utils";
import {Link} from "react-router-dom";


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
    const [processStages, setProcessStages] = useState([])

    useEffect(() => {
        const fetchData = async () => {
            const details = await processServices.processDetails(id)
            
            if (typeof details === 'object')
                setProcessDetails(details)

            const stages = await processServices.processStages(id)
            
            if (Array.isArray(stages))
                setProcessStages(stages)
        }
        fetchData()
    }, [])

    return (
        <div>
            <h2>{processDetails.nome}</h2>
            <p><b>Descrição: </b>{processDetails.descricao}</p>
            <p><b>Template: </b>{processDetails.template_processo}</p>
            <p><b>Autor: </b>{processDetails.autor}</p>
            <p><b>Estado: </b>{estado(processDetails.estado)}</p>
            <p><b>Data de início: </b>{convertTimestamp(processDetails.data_inicio)}</p>
            {processDetails.data_fim && <p><b>Data de fim: </b>{processDetails.data_fim}</p>}
            <div>
                {processStages.map((stage, index) => {
                    const isPending = stage.estado === 'PENDING';
                    const isFirstPending = isPending && index === 0;
                    const className = isFirstPending ? 'pending-stage' : '';

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