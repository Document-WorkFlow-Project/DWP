import { useEffect, useState } from "react"
import processServices from "../../Services/process.service"


export const Processes = () => {

    const [pendingTasks, setPendingTasks] = useState([])
    const [processes, setProcesses] = useState([])

    useEffect(() => {
        const fetchData = async () => {
            setPendingTasks(await processServices.pendingStages())
            setProcesses(await processServices.getProcesses())
        }
        fetchData()
    }, [])


    return (
        <div>
            <p><button onClick={() => window.location.href = "/newprocess"}>Novo processo</button></p>
            <h2>Tarefas pendentes</h2>
            <div>
                {pendingTasks.length === 0 ?
                    <p>Nenhuma tarefa pendente</p>
                :
                    <p>{pendingTasks}</p>
                }
            </div>
            <h2>Processos</h2>
            <div>  
                {pendingTasks.length === 0 ?
                    <p>Nenhuma processo disponível</p>
                :
                    <p>{processes}</p>
                }
            </div>
        </div>
    )
}