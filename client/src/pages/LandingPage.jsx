import useAuth from "../context/useAuth";

function LandingPage() {
  const { auth, isLoading } = useAuth();

  return (
    <main>
      <h1>SpellOverflow</h1>
      <p>Where clumsy Wizards debug their broken incantations.</p>

      {!isLoading && auth?.user && (
        <p>Welcome back, {auth.user.username}.</p>
      )}
    </main>
  );
}

export default LandingPage;
