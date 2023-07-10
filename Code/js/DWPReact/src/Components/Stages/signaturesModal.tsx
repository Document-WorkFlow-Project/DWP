import { convertTimestamp } from "../../utils"

export function SignaturesModal({onClose, signatures}) {

    const assinatura = (value) => {
        if (value)
            return "Aprovado"
        else 
            return "Reprovado"
    }
    
    return (
        <div className="bg">
            <div className="stage-modal">
                <div className="container-fluid">
                    <div className="row">
                        <div className="col">
                            <h3>Assinaturas</h3>
                            <p></p>
                            {signatures.map((signature, index) => {
                                return (
                                    <div key={index} className="clipping-container">
                                        <p><b>{signature.email_utilizador}</b></p>
                                        {signature.assinatura !== null ? 
                                            <div>
                                                <p><b>{assinatura(signature.assinatura)}</b></p>
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
        </div>
    )
}