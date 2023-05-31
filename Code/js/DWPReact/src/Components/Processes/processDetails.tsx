import { useEffect, useState } from "react"
import processServices from "../../Services/process.service"
import { useParams } from 'react-router';


export const ProcessDetails = () => {
    
    const { id } = useParams();
    const [processDetails, setProcessDetails] = useState({})

    useEffect(() => {
        const fetchData = async () => {
            const processDetails = await processServices.pendingStages()
            
            if (typeof processDetails === 'object')
                setProcessDetails(processDetails)
        }
        fetchData()
    }, [])

    return (
        <div>

        </div>
    )
}