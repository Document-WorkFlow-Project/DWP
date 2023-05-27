import { useState } from "react";
import LoginForm from "../LoginForm/login.component";
import NavBar from "../NavBar/navbar.component";

export default function Home() {

    const [isShowLogin, setIsShowLogin] = useState(false)

    const handleLoginClick = () => {
        setIsShowLogin((isShowLogin) => !isShowLogin)
    }

    return (
        <div>
            <NavBar handleLoginClick={handleLoginClick}/>
            {isShowLogin && 
                <div className="sign-in-container">
                    <LoginForm isShowLogin={isShowLogin}/>
                </div>
            }
            <h1>Document Workflow Platform</h1>
            <p>
                Os processos de geração, verificação, aprovação e publicação de documentos em organizações hierárquicas são vitais para o funcionamento das mesmas. <br></br>
                No entanto, estes processos são consumidores de recursos materiais e humanos, ao longo de toda a cadeia, pelos diferentes órgãos da instituição. <br></br>
                Torna-se necessário que todas as etapas dos processos sejam bem definidas e bem calendarizadas. <br></br>
                Em simultâneo, em todos os momentos do processo os vários intervenientes devem conseguir aferir sobre o estado do mesmo e identificar os restantes intervenientes.<br></br>
            </p>
            <p>Neste projeto pretende-se conceber, desenvolver e implementar uma infraestrutura genérica de workflow de autorizações/aprovações de documentos.</p>
        </div>
    )
}