import { useState } from "react";

const initialForm = {
  username: "",
  email: "",
  password: "",
  confirmPassword: "",
};

function RegisterPage() {
  const [form, setForm] = useState(initialForm);
  const [fieldErrors, setFieldErrors] = useState({});
  const [formError, setFormError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
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
    setSuccessMessage("");
    setIsSubmitting(true);

    try {
      const response = await fetch("/api/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(form),
      });

      const responseBody = await response.json().catch(() => ({}));

      if (!response.ok) {
        setFormError(responseBody.message || "Unable to create your account.");
        setFieldErrors(responseBody.errors || {});
        return;
      }

      setSuccessMessage(
        `Account created for ${responseBody.username}. You can now log in.`,
      );
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
      <h1>Create your SpellOverflow account</h1>

      <form onSubmit={handleSubmit} noValidate>
        <div>
          <label htmlFor="username">Username</label>
          <input
            id="username"
            name="username"
            type="text"
            value={form.username}
            onChange={handleChange}
            autoComplete="username"
          />
          {fieldErrors.username && <p role="alert">{fieldErrors.username}</p>}
        </div>

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
            autoComplete="new-password"
          />
          {fieldErrors.password && <p role="alert">{fieldErrors.password}</p>}
        </div>

        <div>
          <label htmlFor="confirmPassword">Confirm password</label>
          <input
            id="confirmPassword"
            name="confirmPassword"
            type="password"
            value={form.confirmPassword}
            onChange={handleChange}
            autoComplete="new-password"
          />
          {fieldErrors.confirmPassword && (
            <p role="alert">{fieldErrors.confirmPassword}</p>
          )}
        </div>

        {formError && <p role="alert">{formError}</p>}
        {successMessage && <p role="status">{successMessage}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Creating account..." : "Create account"}
        </button>
      </form>
    </main>
  );
}

export default RegisterPage;
