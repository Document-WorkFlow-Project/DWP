
function NavBar({handleLoginClick}){
    const handleClick = () => {
        handleLoginClick()
    }

    return (
        <div className="navBar">
            <div>
                <button onClick={handleClick} className="loginicon">Sign In</button>
            </div>
        </div>
    )
}

export default NavBar;