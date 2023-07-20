import { useEffect, useState, useContext } from "react"
import {Link} from "react-router-dom";
import { useParams } from 'react-router';
import stagesService from "../../Services/Stages/stages.service";
import { Comments } from "../Comments/commentBox";
import { convertTimestamp, estado, modo } from "../../utils";
import { createPortal } from 'react-dom'
import { SignaturesModal } from "./signaturesModal";
import { AuthContext } from "../../AuthProvider";
import {toast} from "react-toastify";


export const StageDetails = ({ navigate }) => {
    
    const { id } = useParams();
    
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

    const { loggedUser } = useContext(AuthContext);

    const [loadingApprove, setLoadingApprove] = useState(false);
    const [loadingReject, setLoadingReject] = useState(false);

    const fetchData = async () => {
        try {
            const stageDetails = await stagesService.stageDetails(id)
            setStageDetails(stageDetails)

            const signatures = await stagesService.stageSignatures(id)
            setStageSignatures(signatures)

            if (stageDetails.data_inicio != null && stageDetails.estado === "PENDING" && signatures.find(obj => obj.email_utilizador === loggedUser.email && obj.assinatura === null) !== undefined)
                setHasToSign(true)
        } catch (error) {
            const resMessage = error.response.data || error.toString();
            toast.error(resMessage);
            navigate("/processes")
        }
    }

    useEffect(() => {
        if (!loggedUser.email) {
            navigate('/');
            toast.error("O utilizador não tem sessão iniciada.")
            return
        }
        
        fetchData()
    }, [])

    const signStage = async (value) => {
        if (value) 
            setLoadingApprove(true);
        else 
            setLoadingReject(true);
        
        try {
            await stagesService.signStage(id, value)
            fetchData()
            setHasToSign(false)
        } catch (error) {
            const resMessage = error.response.data || error.toString();
            toast.error(resMessage);
        }
        
        setLoadingApprove(false);
        setLoadingReject(false);
    }

    return (
        <div className="container-fluid">
            <p></p>
            <Link className="link-offset-2 link-underline link-underline-opacity-0" to={`/process/${stageDetails.id_processo}`}>Voltar ao processo</Link>
            <p></p>
            <h2>{stageDetails.nome}</h2>
            <p></p>
            <p><b>Descrição: </b>{stageDetails.descricao}</p>
            <p><b>Estado: </b><a>{estado(stageDetails.estado)}</a></p>
            <p><b>Prazo: </b>{stageDetails.prazo} dias</p>
            {stageDetails.data_inicio ? 
                <p><b>Data de início: </b>{convertTimestamp(stageDetails.data_inicio)}</p>
            : 
                <p><b>Etapa ainda não começou.</b></p>
            }
                
            {stageDetails.data_fim && <p><b>Data de fim: </b>{convertTimestamp(stageDetails.data_fim)}</p>}
            <p><b>Modo de assinatura: </b>{modo(stageDetails.modo)}</p>
            <p><button className="btn btn-primary" onClick={() => setShowSignatureModal(true)}>Assinaturas</button></p>

            {hasToSign &&
                <div className="row row-cols-auto">
                    <div className="col">
                        <button className="btn btn-success" disabled={loadingApprove || loadingReject} onClick={() => signStage(true)}>
                            {loadingApprove && (
                                <span className="spinner-border spinner-border-sm"></span>
                            )}
                            <span> Aprovar etapa</span>
                        </button>
                    </div>
                    <div className="col">
                        <button className="btn btn-danger" disabled={loadingApprove || loadingReject} onClick={() => signStage(false)}>
                            {loadingReject && (
                                <span className="spinner-border spinner-border-sm"></span>
                            )}
                            <span> Não aprovar etapa</span>
                        </button>
                    </div>
                </div>
            }

            <p></p>
            
            <Comments stageId={id}/>

            {showSignatureModal && createPortal(
                <SignaturesModal 
                    onClose={() => setShowSignatureModal(false)}
                    signatures={stageSignatures}
                />,
                document.body
            )}
        </div>
    )
}