import { Route, Routes } from "react-router"
import Homepage from "./pages/homepage"
import Layout from "./components/layout"
import { CreateUser } from "./pages/create-user"
import { Habitation } from "./pages/habitation"


function App() {

  return (
    <Routes>
      <Route path="/" element={<Layout/>}>
        <Route index element={<Homepage/>}/>
        <Route path="new-user" element={<CreateUser/>}/> 
        <Route path="new-habitation" element={<Habitation/>}/>
      </Route>
    </Routes>
  )
}

export default App
