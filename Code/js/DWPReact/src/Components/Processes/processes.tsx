import { useEffect, useState } from "react"
import processServices from "../../Services/process.service"
import { Link } from "react-router-dom"


export const Processes = () => {

    const [pendingTasks, setPendingTasks] = useState([])
    const [processes, setProcesses] = useState([])
    const [selectedProccessType, setSelectedProccessType] = useState("PENDING")

    useEffect(() => {
        const fetchData = async () => {
            const tasks = await processServices.pendingStages()
            if(Array.isArray(tasks))
                setPendingTasks(tasks)
        }
        fetchData()
    }, [])

    useEffect(() => {
        const fetchData = async () => {
            let processes
            if (selectedProccessType === "PENDING") 
                processes = await processServices.pendingProcesses()
            else if (selectedProccessType === "FINISHED")
                processes = await processServices.finishedProcesses()
        
            if(Array.isArray(processes))
                setProcesses(processes)
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
                    <div>
                        {pendingTasks.map((stage, index) => {
                            return (
                                <p key={index}> 
                                    <Link to={"/stage/" + stage.id} >{stage.nome}</Link>
                                </p>
                            )
                        })}
                    </div>
                }
            </div>
            <h2>Processos</h2>
            <select value={selectedProccessType} onChange={(e) => setSelectedProccessType(e.target.value)}>
                <option value="PENDING">Pendentes</option>
                <option value="FINISHED">Terminados</option>
            </select>
            <div>  
                {processes.length === 0 ?
                    <p>Nenhum processo disponível</p>
                :
                    <div>
                        {processes.map((process, index) => {
                            return (
                                <p key={index}> 
                                    <Link to={"/process/" + process.id} >{process.nome}</Link>
                                </p>
                            )
                        })}
                    </div>
                }
            </div>
        </div>
    )
}