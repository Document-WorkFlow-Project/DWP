import { useEffect, useState } from "react"
import {Link} from "react-router-dom";
import { useParams } from 'react-router';
import stagesService from "../../Services/stages.service";
import { Comments } from "../Comments/commentBox";
import { convertTimestamp, estado } from "../../utils";
import { createPortal } from 'react-dom'
import { SignaturesModal } from "./signaturesModal";


export const StageDetails = () => {
    
    const { id } = useParams();
    const email = localStorage.getItem("email");
    
    const [hasToSign, setHasToSign] = useState(false)
    const [stageSignatures, setStageSignatures] = useState([])
    const [showSignatureModal, setShowSignatureModal] = useState(false)
    const [stageDetails, setStageDetails] = useState({
        id: "",
        id_processo: "",
        indice: 1,
        modo: "",
        nome: "",
        descricao: "",
        data_inicio: null,
        data_fim: null,
        estado: "PENDING",
        prazo: 1
    })

    useEffect(() => {
        const fetchData = async () => {
            const stageDetails = await stagesService.stageDetails(id)
            
            if (typeof stageDetails === 'object')
                setStageDetails(stageDetails)
            
            const signatures = await stagesService.stageSignatures(id)
            setStageSignatures(signatures)
            
            if (stageDetails.data_inicio != null && stageDetails.estado === "PENDING" && signatures.find(obj => obj.email_utilizador === email && obj.assinatura === null) !== undefined)
                setHasToSign(true)
        }
        fetchData()
    }, [])

    const signStage = async (value) => {
        await stagesService.signStage(id, value)
    }

    return (
        <div>
            <Link to={`/process/${stageDetails.id_processo}`}>Voltar ao processo</Link>
            <h2>{stageDetails.nome}</h2>
            <p><b>Descrição: </b>{stageDetails.descricao}</p>
            <p><b>Estado: </b><a>{estado(stageDetails.estado)}</a></p>
            <p><b>Prazo: </b>{stageDetails.prazo} dias</p>
            {stageDetails.data_inicio ? 
                <p><b>Data de início: </b>{convertTimestamp(stageDetails.data_inicio)}</p>
            : 
                <p><b>Etapa ainda não começou.</b></p>
            }
                
            {stageDetails.data_fim && <p><b>Data de fim: </b>{convertTimestamp(stageDetails.data_fim)}</p>}
            <p><b>Modo de assinatura: </b>{stageDetails.modo}</p>
            <p><button onClick={() => setShowSignatureModal(true)}>Assinaturas</button></p>

            {hasToSign &&
                <div>
                    <button onClick={() => signStage(true)}>Aprovar etapa</button>
                    <button onClick={() => signStage(false)}>Reprovar etapa</button>
                </div>
            }
            
            <Comments stageId={id}/>

            <div>
                {showSignatureModal && createPortal(
                    <SignaturesModal 
                        onClose={() => setShowSignatureModal(false)}
                        signatures={stageSignatures}
                    />,
                    document.body
                )}
            </div>
        </div>
    )
}