import { useEffect, useState } from "react"
import processServices from "../../Services/Processes/process.service"


export const Processes = () => {

    const [pendingTasks, setPendingTasks] = useState([])
    const [processes, setProcesses] = useState([])
    const [selectedProccessType, setSelectedProccessType] = useState("PENDING")

    useEffect(() => {
        const fetchData = async () => {
            setPendingTasks(await processServices.pendingStages())
        }
        fetchData()
    }, [])

    useEffect(() => {
        const fetchData = async () => {
            if (selectedProccessType === "PENDING")
                setProcesses(await processServices.pendingProcesses())
            else if (selectedProccessType === "FINISHED")
                setProcesses(await processServices.finishedProcesses())
        }
        fetchData()
    }, [selectedProccessType])

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
            <select value={selectedProccessType} onChange={(e) => setSelectedProccessType(e.target.value)}>
                <option value="PENDING">Pendentes</option>
                <option value="FINISHED">Terminados</option>
            </select>
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