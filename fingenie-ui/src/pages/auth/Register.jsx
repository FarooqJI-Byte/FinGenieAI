import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../../services/apiService";

function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  const navigate = useNavigate();

  // ✅ Email validation
  const validateEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const handleError = (err) => {
    const errors = err.response?.data?.errors;
    setIsError(true);
    setMessage(
      (errors && Object.values(errors)[0]) ||
      err.response?.data?.message ||
      "Registration failed. Please try again."
    );
  };

  const handleRegister = async () => {

    // ✅ Name required
    if (!name.trim()) {
      setIsError(true);
      setMessage("Please enter your full name");
      return;
    }

    // ✅ Name length
    if (name.trim().length < 2) {
      setIsError(true);
      setMessage("Name must be at least 2 characters");
      return;
    }

    // ✅ Email required
    if (!email) {
      setIsError(true);
      setMessage("Please enter your email");
      return;
    }

    // ✅ Email format
    if (!validateEmail(email)) {
      setIsError(true);
      setMessage("Please enter a valid email address");
      return;
    }

    // ✅ Password required
    if (!password) {
      setIsError(true);
      setMessage("Please enter your password");
      return;
    }

    // ✅ Password length
    if (password.length < 6) {
      setIsError(true);
      setMessage("Password must be at least 6 characters long");
      return;
    }

    try {
      await registerUser({ name, email, password });

      setIsError(false);
      setMessage("Registered successfully ✅ Check your email for OTP");

      setTimeout(() => navigate("/verify"), 1200);

    } catch (err) {
      handleError(err);
    }
  };

  return (
    <div className="auth-screen">
      <div className="auth-card">
        <h3 className="fw-bold">Create Account</h3>
        <p className="muted">Register for FinGenie secure banking.</p>

        <label className="form-label">Full Name</label>
        <input
          className="form-control mb-3"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <label className="form-label">Email Address</label>
        <input
          type="email"
          className="form-control mb-3"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <label className="form-label">Password</label>
        <input
          type="password"
          className="form-control mb-3"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button className="btn btn-primary w-100" onClick={handleRegister}>
          Create Account
        </button>

        {message && (
          <div className={`alert mt-3 ${isError ? "alert-danger" : "alert-success"}`}>
            {message}
          </div>
        )}

        <p className="text-center muted mt-3 mb-0">
          Already registered?{" "}
          <button
            className="btn btn-link p-0 fw-bold"
            onClick={() => navigate("/")}
          >
            Login
          </button>
        </p>
      </div>
    </div>
  );
}

export default Register;