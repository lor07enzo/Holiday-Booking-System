import { Outlet } from "react-router"
import Navbar from "./navbar"


function Layout () {
    return(
        <div className="max-w-5xl mx-auto">
        <Navbar/>

        <Outlet/>
        </div>
    )
}
export default Layout