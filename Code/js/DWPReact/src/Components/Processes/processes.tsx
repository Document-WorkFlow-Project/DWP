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
            <p></p>
            <div>
                {pendingTasks.length === 0 ?
                    <p>Nenhuma tarefa pendente</p>
                :
                    <div>
                        <table>
                            <thead>
                                <tr>
                                    <th>Etapa</th>
                                    <th>Estado da Etapa</th>
                                    <th>Data Início</th>
                                    <th>Data Fim</th>
                                    <th>Processo</th>
                                </tr>
                            </thead>
                            <tbody>
                                {pendingTasks.map((stage, index) => (
                                    <tr key={index}>
                                        <td><Link to={"/stage/" + stage.id}>{stage.nome}</Link></td>
                                        <td>{estado(stage.estado)}</td>
                                        <td>{convertTimestamp(stage.data_inicio)}</td>
                                        <td>{stage.data_fim && convertTimestamp(stage.data_fim)}</td>
                                        <td><Link to={"/process/" + stage.id_processo}>{stage.processo_nome}</Link></td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                }
            </div>
            <h2>Processos</h2>
            <select value={selectedProccessType} onChange={(e) => setSelectedProccessType(e.target.value)}>
                <option value="PENDING">Pendentes</option>
                <option value="FINISHED">Terminados</option>
            </select>
            <p></p>
            <div>  
                {processes.length === 0 ?
                    <p>Nenhum processo disponível</p>
                :
                    <div>
                        <table style={{ borderCollapse: 'collapse', width: '100%' }}>
                            <thead>
                                <tr>
                                    <th>Processo</th>
                                    <th>Estado do processo</th>
                                    <th>Data Início</th>
                                    <th>Data Fim</th>
                                </tr>
                            </thead>
                            <tbody>
                                {processes.map((process, index) => (
                                    <tr key={index}>
                                        <td><Link to={"/process/" + process.id}>{process.nome}</Link></td>
                                        <td>{estado(process.estado)}</td>
                                        <td>{convertTimestamp(process.data_inicio)}</td>
                                        <td>{process.data_fim && convertTimestamp(process.data_fim)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                }
            </div>
        </div>
    )
}