import { Link } from "react-router-dom";
import useAuth from "../context/useAuth";

function LandingPage() {
  const { auth, isLoading, logout } = useAuth();

  return (
    <main>
      <h1>SpellOverflow</h1>
      <p>Where clumsy Wizards debug their broken incantations.</p>

      {isLoading ? (
        <p>Loading...</p>
      ) : auth?.user ? (
        <>
          <p>Welcome back, {auth.user.username}.</p>
          <button type="button" onClick={logout}>
            Leave the Great Hall
          </button>
        </>
      ) : (
        <nav>
          <Link to="/register">Create an account</Link>
          <Link to="/login">Login</Link>
        </nav>
      )}
    </main>
  );
}

export default LandingPage;
