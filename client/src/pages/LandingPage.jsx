import { Link } from "react-router-dom";

function LandingPage() {
  return (
    <main>
      <h1>SpellOverflow</h1>
      <p>Where clumsy Wizards debug their broken incantations.</p>
      <nav>
        <Link to="/register">Create an account</Link>
        <Link to="/login">Login</Link>
      </nav>
    </main>
  );
}

export default LandingPage;
