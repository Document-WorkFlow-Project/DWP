import { useEffect, useState } from "react"
import { useParams } from 'react-router';


export const StageDetails = () => {
    
    const { id } = useParams();
    const [stageDetails, setStageDetails] = useState({})

    useEffect(() => {
        const fetchData = async () => {
            const stageDetails = {}//await 
            
            if (typeof stageDetails === 'object')
                setStageDetails(stageDetails)
        }
        fetchData()
    }, [])

    return (
        <div>

        </div>
    )
}