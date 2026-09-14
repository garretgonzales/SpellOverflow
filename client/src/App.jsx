import { BrowserRouter, Route, Routes } from "react-router-dom";
import "./App.css";
import RegisterPage from "./pages/RegisterPage";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
