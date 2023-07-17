import { convertTimestamp } from "../../utils"
import {Link} from "react-router-dom";

export function SignaturesModal({onClose, signatures}) {

    const assinatura = (value) => {
        if (value)
            return "Aprovado"
        else 
            return "Não aprovado"
    }
    
    return (
        <div className="bg">
            <div className="stage-modal">
                <div className="row">
                    <div className="col">
                        <h3>Assinaturas</h3>
                        <p></p>
                        {signatures.map((signature, index) => {
                            return (
                                <div key={index} className="clipping-container">
                                    <p><b><Link className="link-secondary link-offset-2 link-underline link-underline-opacity-0" to={`/profile/${signature.email_utilizador}`}>{signature.email_utilizador}</Link></b></p>
                                    {signature.assinatura !== null ? 
                                        <div>
                                            <p>{assinatura(signature.assinatura)}</p>
                                            <p><b>Data: </b>{convertTimestamp(signature.data_assinatura)}</p>
                                        </div>
                                    :
                                        <p>Por assinar</p>
                                    }
                                </div>
                            )
                        })}
                        <p></p>
                        <button className="btn btn-danger" onClick={onClose}>Fechar</button>
                    </div>
                </div>
            </div>
        </div>
    )
}