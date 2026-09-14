import { useId, useState } from "react";
import { useNavigate } from "react-router-dom";
import useAuth from "../context/useAuth";

const initialForm = {
  usernameOrEmail: "",
  password: "",
};

function LoginPage({ onSuccess }) {
  const id = useId();
  const { login } = useAuth();
  const navigate = useNavigate();
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
        credentials: "include",
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

      login({
        id: responseBody.id,
        username: responseBody.username,
      });
      setForm(initialForm);

      if (onSuccess) {
        onSuccess();
      } else {
        navigate("/");
      }
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
          <label htmlFor={`${id}-usernameOrEmail`}>Username or email</label>
          <input
            id={`${id}-usernameOrEmail`}
            name="usernameOrEmail"
            type="text"
            value={form.usernameOrEmail}
            onChange={handleChange}
            autoComplete="username"
          />
          {fieldErrors.usernameOrEmail && (
            <p role="alert">{fieldErrors.usernameOrEmail}</p>
          )}
        </div>

        <div>
          <label htmlFor={`${id}-password`}>Password</label>
          <input
            id={`${id}-password`}
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
