import { Link } from "react-router-dom";
import useAuth from "../context/useAuth";

function Navbar({ onLoginClick, onRegisterClick }) {
  const { auth, isLoading, logout } = useAuth();

  return (
    <header className="navbar">
      <nav>
        <Link to="/" className="navbar-brand">
          <img src="/wizard-hat.png" alt="" />
          SpellOverflow
        </Link>

        {isLoading ? null : auth?.user ? (
          <div className="navbar-actions">
            <span className="navbar-username">{auth.user.username}</span>
            <button type="button" className="btn-outline" onClick={logout}>
              Leave the Great Hall
            </button>
          </div>
        ) : (
          <div className="navbar-actions">
            <button type="button" className="btn-outline" onClick={onLoginClick}>
              Login
            </button>
            <button type="button" className="btn-solid" onClick={onRegisterClick}>
              Create an account
            </button>
          </div>
        )}
      </nav>
    </header>
  );
}

export default Navbar;
