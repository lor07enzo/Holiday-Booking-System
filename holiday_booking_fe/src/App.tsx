import { Route, Routes } from "react-router"
import Homepage from "./pages/homepage"
import Layout from "./components/layout"
import { CreateUser } from "./pages/create-user"
import { Habitation } from "./pages/habitation"
import { UserProvider } from "./context/user-context"
import { Reservation } from "./pages/reservation"


function App() {

  return (
    <UserProvider>
      <Routes>
        <Route path="/" element={<Layout/>}>
          <Route index element={<Homepage/>}/>
          <Route path="new-user" element={<CreateUser/>}/> 
          <Route path="new-habitation" element={<Habitation/>}/>
          <Route path="new-reservation" element={<Reservation/>}/>
        </Route>
      </Routes>
    </UserProvider>
  )
}

export default App
