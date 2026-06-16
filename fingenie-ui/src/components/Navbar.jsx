import { useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();
  const role = localStorage.getItem("role") || "CUSTOMER";

  const logout = () => {
    localStorage.clear();
    navigate("/");
  };

  return (
    <header className="topbar">
      <div>
        <div className="eyebrow">Secure digital banking</div>
        <strong>{role === "ADMIN" ? "Operations Console" : "Customer Workspace"}</strong>
      </div>

      <div className="d-flex align-items-center gap-3">
        <span className="pill primary d-none d-sm-inline-flex">{role}</span>
        <button className="btn btn-outline-primary btn-sm" onClick={logout}>
          Logout
        </button>
      </div>
    </header>
  );
}

export default Navbar;
