import { useEffect, useState } from "react"
import commentsService from "../../Services/comments.service"
import { convertTimestamp } from "../../utils"

export function Comments ({stageId}) {

    const [comments, setCommments] = useState([])
    const [newComment, setNewComment] = useState("")

    useEffect(() => {
        const fetchData = async () => {
            const comts = await commentsService.stageComments(stageId)
            setCommments(comts)
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
        <div>
            <h2>Comentários</h2>
            <p>
                <input type="text" id="myInput" onChange={(e) => {setNewComment(e.target.value)}}></input>
                <button onClick={publishComment}>Adicionar comentário</button>
            </p>
            {comments.length > 0 ?
                <div className="scroll">
                    {comments.map((comment, index) => {
                        return (
                            <div key={index} className="clipping-container">
                                <p><b>{comment.remetente} </b>{convertTimestamp(comment.data)}</p>
                                <p>{comment.texto}</p>
                            </div>
                        )
                    })}
                </div>
            :
                <p>Nenhum comentário publicado.</p>
            }
        </div>
    )
}