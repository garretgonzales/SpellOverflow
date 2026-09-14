import { useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import "./App.css";
import Navbar from "./components/Navbar";
import Modal from "./components/Modal";
import RegisterPage from "./pages/RegisterPage";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";

function App() {
  const [activeModal, setActiveModal] = useState(null);

  function closeModal() {
    setActiveModal(null);
  }

  return (
    <BrowserRouter>
      <Navbar
        onLoginClick={() => setActiveModal("login")}
        onRegisterClick={() => setActiveModal("register")}
      />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>

      <Modal isOpen={activeModal === "login"} onClose={closeModal}>
        <LoginPage onSuccess={closeModal} />
      </Modal>
      <Modal isOpen={activeModal === "register"} onClose={closeModal}>
        <RegisterPage />
      </Modal>
    </BrowserRouter>
  );
}

export default App;
