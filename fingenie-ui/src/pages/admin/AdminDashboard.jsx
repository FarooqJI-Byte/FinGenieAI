import Layout from "../../components/Layout";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

import { getAdminDashboard } from "../../services/apiService";

function AdminDashboard() {
  const navigate = useNavigate();

  const [stats, setStats] = useState({
    totalUsers: 0,
    totalAccounts: 0,
    totalLoans: 0,
    pendingLoans: 0
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    fetchAdminDashboard();
  }, []);

  const handleError = (err) => {
    setIsError(true);
    setMessage(err.response?.data?.message || "Something went wrong");
  };

  const fetchAdminDashboard = async () => {
    try {
      setLoading(true);
      setMessage("");
      setIsError(false);

      const res = await getAdminDashboard();
      const data = res.data.data || {};

      setStats({
        totalUsers: data.totalUsers || 0,
        totalAccounts: data.totalAccounts || 0,
        totalLoans: data.totalLoans || 0,
        pendingLoans: data.pendingLoans || 0
      });

      setMessage("Dashboard loaded");
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="px-3 px-md-4 py-4">

        {/* ✅ HEADER */}
        <div className="mb-3">
          <h4 className="fw-bold">Admin Dashboard</h4>
          <p className="opacity-50 mb-2">
            Centralized control of system operations
          </p>
        </div>

        {/* ✅ MESSAGE (TOP - POLISHED) */}
        {(loading || message) && (
          <div
            className={`mb-3 px-3 py-2 rounded-3`}
            style={{
              background: isError
                ? "rgba(239,68,68,0.15)"
                : "rgba(34,197,94,0.15)",
              border: isError
                ? "1px solid rgba(239,68,68,0.4)"
                : "1px solid rgba(34,197,94,0.4)",
              color: isError ? "#ef4444" : "#22c55e",
              fontSize: "0.9rem",
              backdropFilter: "blur(10px)"
            }}
          >
            {loading ? "Loading dashboard..." : message}
          </div>
        )}

        {/* ✅ HERO SUMMARY */}
        <div
          className="mb-4 p-4"
          style={{
            borderRadius: "18px",
            background:
              "linear-gradient(135deg, rgba(59,130,246,0.15), rgba(2,6,23,0.6))",
            backdropFilter: "blur(12px)",
            boxShadow: "0 15px 40px rgba(0,0,0,0.6)"
          }}
        >
          <div className="d-flex justify-content-between flex-wrap gap-3">

            <div>
              <h5 className="mb-1">System Overview</h5>
              <small className="opacity-50">
                Monitor users, accounts, and financial activity
              </small>
            </div>

            <button
              className="btn btn-outline-light btn-sm"
              onClick={fetchAdminDashboard}
            >
              Refresh
            </button>

          </div>
        </div>

        {/* ✅ STATS */}
        <div className="row g-3 mb-4">

          {[
            {
              label: "Users",
              value: stats.totalUsers,
              icon: "bi bi-people",
              color: "#3b82f6"
            },
            {
              label: "Accounts",
              value: stats.totalAccounts,
              icon: "bi bi-wallet2",
              color: "#22c55e"
            },
            {
              label: "Loans",
              value: stats.totalLoans,
              icon: "bi bi-bank",
              color: "#f59e0b"
            },
            {
              label: "Pending",
              value: stats.pendingLoans,
              icon: "bi bi-hourglass-split",
              color: "#ef4444"
            }
          ].map((item, i) => (

            <div className="col-6 col-md-3" key={i}>
              <div
                style={{
                  padding: "20px",
                  borderRadius: "16px",
                  background: "rgba(255,255,255,0.04)",
                  backdropFilter: "blur(12px)",
                  boxShadow: "0 10px 25px rgba(0,0,0,0.5)",
                  textAlign: "center",
                  transition: "transform 0.2s ease"
                }}
                onMouseEnter={(e) =>
                  (e.currentTarget.style.transform = "translateY(-4px)")
                }
                onMouseLeave={(e) =>
                  (e.currentTarget.style.transform = "none")
                }
              >
                <i
                  className={`${item.icon} fs-3`}
                  style={{ color: item.color }}
                ></i>

                <h5 className="mt-2 mb-0">{item.value}</h5>
                <small className="opacity-50">{item.label}</small>
              </div>
            </div>

          ))}

        </div>

        {/* ✅ ADMIN ACTIONS */}
        <div className="mb-3">
          <h5 className="mb-1">Quick Controls</h5>
          <p className="opacity-50 mb-2">
            Navigate to admin modules
          </p>
        </div>

        <div className="row g-3">

          {[
            { title: "Loans", path: "/admin/loans", icon: "bi bi-bank" },
            { title: "Users", path: "/admin/users", icon: "bi bi-people" },
            { title: "Accounts", path: "/admin/accounts", icon: "bi bi-wallet" },
            { title: "Analytics", path: "/admin/analytics", icon: "bi bi-bar-chart" }
          ].map((item, i) => (

            <div className="col-12 col-md-3" key={i}>
              <div
                onClick={() => navigate(item.path)}
                style={{
                  cursor: "pointer",
                  padding: "20px",
                  borderRadius: "16px",
                  background: "rgba(255,255,255,0.04)",
                  backdropFilter: "blur(12px)",
                  boxShadow: "0 10px 25px rgba(0,0,0,0.6)",
                  transition: "all 0.25s ease"
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = "translateY(-5px)";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = "none";
                }}
              >
                <i className={`${item.icon} fs-4 mb-2`}></i>

                <h6 className="mb-0">{item.title}</h6>

                <small className="opacity-50">
                  Manage {item.title.toLowerCase()}
                </small>
              </div>
            </div>

          ))}

        </div>

      </div>
    </Layout>
  );
}

export default AdminDashboard;