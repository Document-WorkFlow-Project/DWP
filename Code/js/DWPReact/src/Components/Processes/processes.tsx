import { useEffect, useState, useContext } from "react"
import processServices from "../../Services/Processes/process.service"
import { Link } from "react-router-dom"
import { convertTimestamp, estado } from "../../utils"
import { AuthContext } from "../../AuthProvider"
import {toast} from 'react-toastify';
import stagesService from "../../Services/Stages/stages.service"


export const Processes = () => {

    const pageObj = {
        "hasPrevious": false,
        "hasNext": false,
        "list": []
    }

    const [currentTaskPage, setCurrentTaskPage] = useState(0)
    const [pendingTasks, setPendingTasks] = useState(pageObj)
    
    const [currentProcessPage, setCurrentProcessPage] = useState(0)
    const [processes, setProcesses] = useState(pageObj)
    
    const [selectedTaskType, setSelectedTaskType] = useState("PENDING")
    const [selectedProccessType, setSelectedProccessType] = useState("PENDING")

    const { loggedUser } = useContext(AuthContext);

    useEffect(() => {
        if (!loggedUser.email)
            window.location.href = '/';
    }, [])

    useEffect(() => {
        const fetchData = async () => {
            try {
                let tasks
                if (selectedTaskType === "PENDING")
                    tasks = await stagesService.pendingStages(currentTaskPage)
                else if (selectedTaskType === "FINISHED")
                    tasks = await stagesService.finishedStages(currentTaskPage)

                setPendingTasks(tasks)

            } catch (error) {
                let code = error.response.status
                if (code != 404) toast.error("Error getting Stages, please Refresh ...")
            }
        }

        fetchData()
    }, [selectedTaskType, currentTaskPage])

    useEffect(() => {
        const fetchData = async () => {
            try {
                let processes
                if (selectedProccessType === "PENDING")
                    processes = await processServices.pendingProcesses(currentProcessPage)
                else if (selectedProccessType === "FINISHED")
                    processes = await processServices.finishedProcesses(currentProcessPage)

                setProcesses(processes)

            } catch (error) {
                let code = error.response.status
                if (code != 404) toast.error("Error getting Processes, please Refresh ...")
            }
        }
        fetchData()

    }, [selectedProccessType, currentProcessPage])

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
                {pendingTasks.list.length === 0 ?
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
                                {pendingTasks.list.map((stage, index) => (
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
                        <p>
                            <button onClick={() => setCurrentTaskPage(curr => curr - 1)} disabled={!pendingTasks.hasPrevious}>Página anterior</button>
                            <button onClick={() => setCurrentTaskPage(curr => curr + 1)} disabled={!pendingTasks.hasPrevious}>Página seguinte</button>
                        </p>
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
                {processes.list.length === 0 ?
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
                                {processes.list.map((process, index) => (
                                    <tr key={index}>
                                        <td><Link to={"/process/" + process.id}>{process.nome}</Link></td>
                                        <td>{estado(process.estado)}</td>
                                        <td>{convertTimestamp(process.data_inicio)}</td>
                                        <td>{process.data_fim && convertTimestamp(process.data_fim)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        <p>
                            <button onClick={() => setCurrentProcessPage(curr => curr - 1)} disabled={!processes.hasPrevious}>Página anterior</button>
                            <button onClick={() => setCurrentProcessPage(curr => curr + 1)} disabled={!processes.hasNext}>Página seguinte</button>
                        </p>
                    </div>
                }
            </div>
        </div>
    )
}