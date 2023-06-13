import { useEffect, useState, useContext } from "react"
import processServices from "../../Services/Processes/process.service"
import { Link } from "react-router-dom"
import { convertTimestamp, estado } from "../../utils"
import { AuthContext } from "../../AuthProvider"
import {toast, ToastContainer} from 'react-toastify';


export const Processes = () => {

    const [pendingTasks, setPendingTasks] = useState([])
    const [processes, setProcesses] = useState([])
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
                tasks = await processServices.pendingStages()
            else if (selectedTaskType === "FINISHED")
                tasks = await processServices.finishedStages()

            setPendingTasks(tasks)

        } catch (error) {
            let code = error.response.status
            if (code != 404) toast.error(error.message)
        }
    }

        fetchData()
    }, [selectedTaskType])

    useEffect(() => {
        const fetchData = async () => {
            try {
                let processes
                if (selectedProccessType === "PENDING")
                    processes = await processServices.pendingProcesses()
                else if (selectedProccessType === "FINISHED")
                    processes = await processServices.finishedProcesses()

                setProcesses(processes)

            } catch (error) {
                let code = error.response.status
                if (code != 404) toast.error(error.message)
            }
        }
        fetchData()

    }, [selectedProccessType])

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