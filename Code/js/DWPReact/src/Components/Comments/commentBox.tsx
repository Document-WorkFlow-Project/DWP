import { useEffect, useState } from "react"
import commentsService from "../../Services/comments.service"
import { convertTimestamp } from "../../utils"
import {toast} from "react-toastify";

export function Comments ({stageId}) {

    const [comments, setCommments] = useState([])
    const [newComment, setNewComment] = useState("")

    useEffect(() => {
        const fetchData = async () => {
            let comts
            try {
                comts = await commentsService.stageComments(stageId)
                setCommments(comts)
            } catch (error) {
               toast.error("Error Posting Comment. Please Refresh ...")
            }

        }
        fetchData()
    }, [])

    const publishComment = () => {
        if (newComment === "")
           return
        else {
            commentsService.postComment(stageId, newComment)
        } 	
    }

    return (
        <div className="container-fluid">
            <h2>Comentários</h2>
            <p></p>
            
            <div className="row row-cols-auto">
                <div className="col-6">
                    <textarea className="form-control" style={{ resize: "none" }} value={newComment} onChange={(e) => {setNewComment(e.target.value)}}/>
                </div>
                <p></p>
                <div className="col">
                    <button className="btn btn-primary" onClick={publishComment}>Adicionar comentário</button>
                </div>
            </div>

            <p></p>
            
            
            {comments.length > 0 ?
                <div className="row row-cols-auto">
                    <div className="col">
                        {comments.map((comment, index) => {
                            return (
                                <div key={index} className="clipping-container">
                                    <p><b>{comment.remetente} </b>{convertTimestamp(comment.data)}</p>
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