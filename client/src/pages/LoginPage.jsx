import { useState } from "react";
import useAuth from "../context/useAuth";

const initialForm = {
  email: "",
  password: "",
};

function LoginPage() {
  const { login } = useAuth();
  const [form, setForm] = useState(initialForm);
  const [fieldErrors, setFieldErrors] = useState({});
  const [formError, setFormError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((currentForm) => ({
      ...currentForm,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setFieldErrors({});
    setFormError("");
    setIsSubmitting(true);

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(form),
      });

      const responseBody = await response.json().catch(() => ({}));

      if (!response.ok) {
        setFormError(responseBody.message || "Unable to log in.");
        setFieldErrors(responseBody.errors || {});
        return;
      }

      login(responseBody.token, {
        id: responseBody.id,
        username: responseBody.username,
      });
      setForm(initialForm);
    } catch {
      setFormError(
        "Unable to reach SpellOverflow. Make sure the backend is up and running.",
      );
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main>
      <h1>Enter the Great Hall</h1>

      <form onSubmit={handleSubmit} noValidate>
        <div>
          <label htmlFor="email">Email</label>
          <input
            id="email"
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            autoComplete="email"
          />
          {fieldErrors.email && <p role="alert">{fieldErrors.email}</p>}
        </div>

        <div>
          <label htmlFor="password">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            autoComplete="current-password"
          />
          {fieldErrors.password && <p role="alert">{fieldErrors.password}</p>}
        </div>

        {formError && <p role="alert">{formError}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Entering..." : "Enter the Great Hall"}
        </button>
      </form>
    </main>
  );
}

export default LoginPage;
