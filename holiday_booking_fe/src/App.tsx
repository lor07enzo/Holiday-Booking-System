import { Route, Routes } from "react-router"
import Homepage from "./pages/homepage"
import Layout from "./components/layout"


function App() {

  return (
    <Routes>
      <Route path="/" element={<Layout/>}>
        <Route index element={<Homepage/>}/>
      </Route>
    </Routes>
  )
}

export default App
