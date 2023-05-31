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
            <div className="modal">
                <h3>Assinaturas</h3>
                <div className="scroll">
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
                </div>
                <button onClick={onClose}>Fechar</button>
            </div>
        </div>
    )
}