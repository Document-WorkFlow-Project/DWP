import { useEffect, useState } from "react"
import commentsService from "../../Services/comments.service"
import { convertTimestamp } from "../../utils"
import {toast} from "react-toastify";
import {Link} from "react-router-dom";

export function Comments ({stageId}) {

    const [comments, setCommments] = useState([])
    const [newComment, setNewComment] = useState("")
    const [loading, setLoading] = useState(false);

    const fetchData = async () => {
        try {
            const comts = await commentsService.stageComments(stageId)
            setCommments(comts)
        } catch (error) {
           toast.error("Erro ao obter comentários. Tenta novamente...")
        }
    }

    useEffect(() => {
        fetchData()
    }, [])

    const publishComment = async () => {
        setLoading(true)

        if (newComment !== "") {
            try {
                await commentsService.postComment(stageId, newComment)
                setNewComment("")
                fetchData()
            } catch (error) {
                toast.error("Erro ao adicionar comentário. Tenta novamente...")
            }
        } 	

        setLoading(false)
    }

    return (
        <div>
            <h2>Comentários</h2>
            <p></p>
            
            <div className="row row-cols-auto">
                <div className="col-6">
                    <textarea className="form-control" style={{ resize: "none" }} value={newComment} onChange={(e) => {setNewComment(e.target.value)}}/>
                </div>
                <p></p>
                <div className="col">
                    <button className="btn btn-primary" disabled={loading} onClick={publishComment}>
                        {loading && (
                            <span className="spinner-border spinner-border-sm"></span>
                        )}
                        <span> Adicionar comentário</span>
                    </button>
                </div>
            </div>

            <p></p>
            
            
            {comments.length > 0 ?
                <div className="row row-cols-auto">
                    <div className="col">
                        {comments.map((comment, index) => {
                            return (
                                <div key={index} className="clipping-container">
                                    <p><b><Link className="link-secondary link-offset-2 link-underline link-underline-opacity-0" to={`/profile/${comment.remetente}`}>{comment.remetente}</Link> </b>{convertTimestamp(comment.data)}</p>
                                    <p>{comment.texto}</p>
                                </div>
                            )
                        })}
                    </div>
                </div>
            :
                <p>Nenhum comentário publicado.</p>
            }
            
        </div>
    )
}