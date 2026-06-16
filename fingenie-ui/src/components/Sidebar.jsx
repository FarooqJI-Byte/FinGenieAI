import { useLocation, useNavigate } from "react-router-dom";

function Sidebar() {
  const navigate = useNavigate();
  const location = useLocation();
  const role = localStorage.getItem("role");
  const isAdmin = role?.toUpperCase().includes("ADMIN");

  const customerMenu = [
    { name: "Dashboard", path: "/dashboard", icon: "D" },
    { name: "Accounts", path: "/accounts", icon: "A" },
    { name: "Transactions", path: "/transactions", icon: "T" },
    { name: "Loans", path: "/loans", icon: "L" },
    { name: "AI Investment", path: "/ai-investment", icon: "AI" },
  ];

  const adminMenu = [
    { name: "Dashboard", path: "/admin/dashboard", icon: "D" },
    { name: "Users", path: "/admin/users", icon: "U" },
    { name: "Accounts", path: "/admin/accounts", icon: "A" },
    { name: "Loans", path: "/admin/loans", icon: "L" },
    { name: "Analytics", path: "/admin/analytics", icon: "AN" },
  ];

  const menuItems = isAdmin ? adminMenu : customerMenu;

  return (
    <aside className="sidebar">
      <div className="d-flex align-items-center gap-3">
        <div className="brand-mark">FG</div>
        <div>
          <h5 className="mb-0 fw-bold">FinGenie</h5>
          <small className="text-white-50">AI Banking Platform</small>
        </div>
      </div>

      <div className="sidebar-title">{isAdmin ? "Admin" : "Banking"}</div>
      <nav>
        {menuItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <button
              type="button"
              key={item.name}
              className={`nav-item ${isActive ? "active" : ""}`}
              onClick={() => navigate(item.path)}
            >
              <span className="nav-icon">{item.icon}</span>
              <span>{item.name}</span>
            </button>
          );
        })}
      </nav>
    </aside>
  );
}

export default Sidebar;
