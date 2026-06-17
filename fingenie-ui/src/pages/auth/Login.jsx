
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../../services/apiService";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  const navigate = useNavigate();

  // ✅ Email regex
  const validateEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const handleError = (err) => {
    const errors = err.response?.data?.errors;
    setIsError(true);
    setMessage(
      (errors && Object.values(errors)[0]) ||
      err.response?.data?.message ||
      (err.request ? "Server not reachable" : "Login failed")
    );
  };

  const handleLogin = async () => {
    localStorage.clear();

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
      const res = await loginUser({ email, password });

      const data = res.data.data;
      const token = typeof data === "string" ? data : data?.token;
      const role = typeof data === "string" ? null : data?.role;

      if (!token || !role) {
        setIsError(true);
        setMessage("Invalid response from server. Please try again.");
        return;
      }

      localStorage.setItem("token", token);
      localStorage.setItem("role", role);

      setIsError(false);
      setMessage("Login successful ✅");

      navigate(role === "ADMIN" ? "/admin/dashboard" : "/dashboard");

    } catch (err) {
      handleError(err);
    }
  };

  return (
    <div className="auth-screen">
      <div className="auth-card">
        <div className="d-flex align-items-center gap-3 mb-4">
          <div className="brand-mark">FG</div>
          <div>
            <h3 className="mb-0 fw-bold">FinGenie</h3>
            <p className="muted mb-0">Secure digital banking</p>
          </div>
        </div>

        <label className="form-label">Email</label>
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

        <button className="btn btn-primary w-100" onClick={handleLogin}>
          Login
        </button>

        {message && (
          <div className={`alert mt-3 ${isError ? "alert-danger" : "alert-success"}`}>
            {message}
          </div>
        )}

        <p className="text-center muted mt-3 mb-0">
          Do not have an account?{" "}
          <button
            className="btn btn-link p-0 fw-bold"
            onClick={() => navigate("/register")}
          >
            Register
          </button>
        </p>
      </div>
    </div>
  );
}

export default Login;