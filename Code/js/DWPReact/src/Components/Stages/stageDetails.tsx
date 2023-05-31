import { useEffect, useState } from "react"
import {Link} from "react-router-dom";
import { useParams } from 'react-router';
import stagesService from "../../Services/stages.service";
import { Comments } from "../Comments/commentBox";
import { convertTimestamp } from "../../utils";


export const StageDetails = () => {
    
    const { id } = useParams();
    const [stageDetails, setStageDetails] = useState({
        id: "",
        id_processo: "",
        indice: 1,
        modo: "",
        nome: "",
        descricao: "",
        data_inicio: "",
        data_fim: null,
        estado: "PENDING",
        prazo: 1
    })

    useEffect(() => {
        const fetchData = async () => {
            const stageDetails = await stagesService.stageDetails(id)
            
            if (typeof stageDetails === 'object')
                setStageDetails(stageDetails)
        }
        fetchData()
    }, [])

    const estado = () => {
        if (stageDetails.estado == "PENDING")
            return <a>Pendente</a>
        else if (stageDetails.estado == "APPROVED")
            return <a>Aprovado</a>
        else if (stageDetails.estado == "DISAPPROVED")
            return <a>Reprovado</a>
    }

    //TODO assinaturas e apagar comentários

    return (
        <div>
            <Link to={`/process/${stageDetails.id_processo}`}>Voltar ao processo</Link>
            <h2>{stageDetails.nome}</h2>
            <p><b>Descrição: </b>{stageDetails.descricao}</p>
            <p><b>Estado: </b>{estado()}</p>
            <p><b>Prazo: </b>{stageDetails.prazo} dias</p>
            <p><b>Data início: </b>{convertTimestamp(stageDetails.data_inicio)}</p>
            {stageDetails.data_fim && <p><b>Data fim: </b>{convertTimestamp(stageDetails.data_fim)}</p>}
            <p><b>Modo de assinatura: </b>{stageDetails.modo}</p>
            
            <Comments stageId={id}/>
        </div>
    )
}