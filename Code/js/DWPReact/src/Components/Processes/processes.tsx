import { useEffect, useState, useContext } from "react"
import processServices from "../../Services/Processes/process.service"
import { Link } from "react-router-dom"
import { convertTimestamp } from "../../utils"
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
        <div className="container-fluid">
            <p></p>
            <button className="btn btn-success" onClick={() => window.location.href = "/newprocess"}>Novo processo</button>
            <p></p>
            <h2>Tarefas</h2>
            <div className="col-3">
                <select className="form-select" value={selectedTaskType} onChange={(e) => setSelectedTaskType(e.target.value)}>
                    <option value="PENDING">Pendentes</option>
                    <option value="FINISHED">Terminadas</option>
                </select>
            </div>
            <p></p>
            <div>
                {pendingTasks.list.length === 0 ?
                    <p>Nenhuma tarefa disponível</p>
                :
                    <div>
                        <table>
                            <thead>
                                <tr>
                                    <th>Etapa</th>
                                    <th>Descrição</th>
                                    <th>Data Início</th>
                                    {selectedTaskType === "FINISHED" ? <th>Data Fim</th> : null}
                                    <th>Processo</th>
                                </tr>
                            </thead>
                            <tbody>
                                {pendingTasks.list.map((stage, index) => (
                                    <tr key={index}>
                                        <td><Link className="link-offset-2 link-underline link-underline-opacity-0" to={"/stage/" + stage.id}>{stage.nome}</Link></td>
                                        <td>{stage.descricao}</td>
                                        <td>{convertTimestamp(stage.data_inicio)}</td>
                                        {selectedTaskType === "FINISHED" ? <td>{convertTimestamp(stage.data_fim)}</td> : null}
                                        <td><Link className="link-offset-2 link-underline link-underline-opacity-0" to={"/process/" + stage.id_processo}>{stage.processo_nome}</Link></td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        <p>
                            {pendingTasks.hasPrevious && <button className="btn btn-primary btn-sm" onClick={() => setCurrentTaskPage(curr => curr - 1)}>Página anterior</button>}
                            {pendingTasks.hasNext && <button className="btn btn-primary btn-sm" onClick={() => setCurrentTaskPage(curr => curr + 1)}>Página seguinte</button>}
                        </p>
                    </div>
                }
            </div>
            <h2>Processos</h2>
            <div className="col-3">
                <select className="form-select" value={selectedProccessType} onChange={(e) => setSelectedProccessType(e.target.value)}>
                    <option value="PENDING">Pendentes</option>
                    <option value="FINISHED">Terminados</option>
                </select>
            </div>
            
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
                                    <th>Descrição</th>
                                    <th>Data Início</th>
                                    {selectedProccessType === "FINISHED" ? <th>Data Fim</th> : null}
                                </tr>
                            </thead>
                            <tbody>
                                {processes.list.map((process, index) => (
                                    <tr key={index}>
                                        <td><Link className="link-offset-2 link-underline link-underline-opacity-0" to={"/process/" + process.id}>{process.nome}</Link></td>
                                        <td>{process.descricao}</td>
                                        <td>{convertTimestamp(process.data_inicio)}</td>
                                        {selectedProccessType === "FINISHED" ? <td>{convertTimestamp(process.data_fim)}</td> : null}
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        <p>
                            {processes.hasPrevious && <button className="btn btn-primary btn-sm" onClick={() => setCurrentProcessPage(curr => curr - 1)}>Página anterior</button>}
                            {processes.hasNext && <button className="btn btn-primary btn-sm" onClick={() => setCurrentProcessPage(curr => curr + 1)}>Página seguinte</button>}
                        </p>
                    </div>
                }
            </div>
        </div>
    )
}