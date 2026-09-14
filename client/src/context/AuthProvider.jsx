import { useState } from "react";
import AuthContext from "./AuthContext";

function AuthProvider({ children }) {
  const [auth, setAuth] = useState(null);

  function login(token, user) {
    setAuth({ token, user });
  }

  function logout() {
    setAuth(null);
  }

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export default AuthProvider;
