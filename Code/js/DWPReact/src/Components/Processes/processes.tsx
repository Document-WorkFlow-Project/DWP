import { useEffect, useState } from "react"
import processServices from "../../Services/Processes/process.service"
import { Link } from "react-router-dom"
import { convertTimestamp, estado } from "../../utils"


export const Processes = () => {

    const [pendingTasks, setPendingTasks] = useState([])
    const [processes, setProcesses] = useState([])
    const [selectedTaskType, setSelectedTaskType] = useState("PENDING")
    const [selectedProccessType, setSelectedProccessType] = useState("PENDING")

    useEffect(() => {
        const email = localStorage.getItem('email');

        if (!email) {
            window.location.href = '/';
        }
        const fetchData = async () => {
            let tasks
            if (selectedTaskType === "PENDING") 
                tasks = await processServices.pendingStages()
            else if (selectedTaskType === "FINISHED")
                tasks = await processServices.finishedStages()

            if (Array.isArray(tasks))
                setPendingTasks(tasks)
        }
        fetchData()
    }, [selectedTaskType])

    useEffect(() => {
        const fetchData = async () => {
            let processes
            if (selectedProccessType === "PENDING") 
                processes = await processServices.pendingProcesses()
            else if (selectedProccessType === "FINISHED")
                processes = await processServices.finishedProcesses()
        
            if (Array.isArray(processes))
                setProcesses(processes)
        }
        fetchData()
    }, [selectedProccessType])

    //TODO move values to tables

    return (
        <div>
            <p><button onClick={() => window.location.href = "/newprocess"}>Novo processo</button></p>
            <h2>Tarefas</h2>
            <select value={selectedTaskType} onChange={(e) => setSelectedTaskType(e.target.value)}>
                <option value="PENDING">Pendentes</option>
                <option value="FINISHED">Terminadas</option>
            </select>
            <div>
                {pendingTasks.length === 0 ?
                    <p>Nenhuma tarefa pendente</p>
                :
                    <div>
                        {pendingTasks.map((stage, index) => {
                            return (
                                <p key={index}> 
                                    <Link to={"/stage/" + stage.id} >{stage.nome}</Link> <b> {estado(stage.estado)} - </b>{convertTimestamp(stage.data_inicio)}<b> - </b>{stage.data_fim && convertTimestamp(stage.data_fim)} <b>Processo: </b><Link to={"/process/" + stage.id_processo}>{stage.processo_nome}</Link>
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
                                    <Link to={"/process/" + process.id} >{process.nome}</Link> <b> {estado(process.estado)} - </b>{convertTimestamp(process.data_inicio)}<b> - </b>{process.data_fim && convertTimestamp(process.data_fim)}
                                </p>
                            )
                        })}
                    </div>
                }
            </div>
        </div>
    )
}