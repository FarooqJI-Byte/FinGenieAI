import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { verifyOtp } from "../../services/apiService";

function VerifyOtp() {
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);
  const navigate = useNavigate();

  const handleVerify = async () => {
    if (!email || !/^\d{6}$/.test(otp)) {
      setIsError(true);
      setMessage("Enter your email and a 6 digit OTP");
      return;
    }
    try {
      await verifyOtp({ email, otp });
      setIsError(false);
      setMessage("OTP verified successfully");
      setTimeout(() => navigate("/"), 1200);
    } catch (err) {
      const errors = err.response?.data?.errors;
      setIsError(true);
      setMessage((errors && Object.values(errors)[0]) || err.response?.data?.message || "Verification failed");
    }
  };

  return (
    <div className="auth-screen">
      <div className="auth-card">
        <h3 className="fw-bold">Verify OTP</h3>
        <p className="muted">Enter the 6 digit code sent to your email.</p>
        <label className="form-label">Email Address</label>
        <input type="email" className="form-control mb-3" value={email} onChange={(e) => setEmail(e.target.value)} />
        <label className="form-label">OTP</label>
        <input className="form-control mb-3" inputMode="numeric" maxLength="6" value={otp} onChange={(e) => setOtp(e.target.value.replace(/\D/g, ""))} />
        <button className="btn btn-primary w-100" onClick={handleVerify}>Verify OTP</button>
        {message && <div className={`alert mt-3 ${isError ? "alert-danger" : "alert-success"}`}>{message}</div>}
        <p className="text-center muted mt-3 mb-0">
          Back to <button className="btn btn-link p-0 fw-bold" onClick={() => navigate("/")}>Login</button>
        </p>
      </div>
    </div>
  );
}

export default VerifyOtp;
