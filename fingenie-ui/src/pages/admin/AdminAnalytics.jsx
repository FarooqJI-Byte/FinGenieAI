import Layout from "../../components/Layout";
import { useEffect, useState } from "react";

import {
  Chart as ChartJS,
  BarElement,
  ArcElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
} from "chart.js";

import { Bar, Pie } from "react-chartjs-2";

import { getAdminDashboard } from "../../services/apiService";

ChartJS.register(
  BarElement,
  ArcElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
);

function AdminAnalytics() {

  const [stats, setStats] = useState({
    totalUsers: 0,
    totalAccounts: 0,
    totalLoans: 0,
    pendingLoans: 0
  });

  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const res = await getAdminDashboard();
      const data = res.data.data || {};

      setStats({
        totalUsers: data.totalUsers || 0,
        totalAccounts: data.totalAccounts || 0,
        totalLoans: data.totalLoans || 0,
        pendingLoans: data.pendingLoans || 0
      });

      setIsError(false);
      setMessage("Analytics loaded");

    } catch {
      setIsError(true);
      setMessage("Failed to load analytics");
    }
  };

  const processedLoans = Math.max(stats.totalLoans - stats.pendingLoans, 0);

  const barData = {
    labels: ["Users", "Accounts", "Loans"],
    datasets: [
      {
        label: "System Data",
        data: [
          stats.totalUsers,
          stats.totalAccounts,
          stats.totalLoans
        ],
        backgroundColor: ["#3b82f6", "#22c55e", "#9333ea"]
      }
    ]
  };

  const pieData = {
    labels:
      stats.pendingLoans === 0
        ? ["Processed"]
        : ["Processed", "Pending"],
    datasets: [
      {
        data:
          stats.pendingLoans === 0
            ? [processedLoans]
            : [processedLoans, stats.pendingLoans],
        backgroundColor:
          stats.pendingLoans === 0
            ? ["#22c55e"]
            : ["#22c55e", "#f59e0b"]
      }
    ]
  };

  const txnBarData = {
    labels: ["Deposit", "Withdraw", "Transfer"],
    datasets: [
      {
        label: "Transactions",
        data: [10, 5, 8],
        backgroundColor: ["#22c55e", "#ef4444", "#3b82f6"]
      }
    ]
  };

  const fraudPieData = {
    labels: ["Normal", "Fraud"],
    datasets: [
      {
        data: [20, 2],
        backgroundColor: ["#22c55e", "#dc2626"]
      }
    ]
  };

  return (
    <Layout>
      <div className="px-3 px-md-4 py-4">

        {/* ✅ HEADER */}
        <div className="mb-4">
          <h4 className="fw-bold">Analytics Dashboard</h4>
          <p className="opacity-50">
            System insights and performance metrics
          </p>
        </div>

        {/* ✅ MESSAGE */}
        {message && (
          <div className={`alert ${isError ? "alert-danger" : "alert-success"}`}>
            {message}
          </div>
        )}

        {/* ✅ SUMMARY STRIP */}
        <div
          className="d-flex justify-content-between flex-wrap mb-4 p-4"
          style={{
            borderRadius: "18px",
            background: "linear-gradient(135deg, rgba(59,130,246,0.15), rgba(2,6,23,0.6))",
            backdropFilter: "blur(12px)"
          }}
        >
          <div><strong>{stats.totalUsers}</strong><br /><small>Users</small></div>
          <div><strong>{stats.totalAccounts}</strong><br /><small>Accounts</small></div>
          <div><strong>{stats.totalLoans}</strong><br /><small>Loans</small></div>
          <div><strong>{stats.pendingLoans}</strong><br /><small>Pending</small></div>
        </div>

        {/* ✅ CHARTS GRID */}
        <div className="row g-3">

          {/* BAR */}
          <div className="col-12 col-lg-6">
            <div style={{
              padding: "20px",
              borderRadius: "16px",
              background: "rgba(255,255,255,0.04)",
              backdropFilter: "blur(12px)"
            }}>
              <h6 className="mb-3">System Overview</h6>
              <div style={{ height: "260px" }}>
                <Bar data={barData} options={{ maintainAspectRatio: false }} />
              </div>
            </div>
          </div>

          {/* PIE */}
          <div className="col-12 col-lg-6">
            <div style={{
              padding: "20px",
              borderRadius: "16px",
              background: "rgba(255,255,255,0.04)",
              backdropFilter: "blur(12px)"
            }}>
              <h6 className="mb-3">Loan Distribution</h6>
              <div style={{ height: "260px" }}>
                <Pie data={pieData} options={{ maintainAspectRatio: false }} />
              </div>
            </div>
          </div>

          {/* TRANSACTIONS */}
          <div className="col-12 col-lg-6">
            <div style={{
              padding: "20px",
              borderRadius: "16px",
              background: "rgba(255,255,255,0.04)",
              backdropFilter: "blur(12px)"
            }}>
              <h6 className="mb-3">Transaction Types</h6>
              <div style={{ height: "260px" }}>
                <Bar data={txnBarData} options={{ maintainAspectRatio: false }} />
              </div>
            </div>
          </div>

          {/* FRAUD */}
          <div className="col-12 col-lg-6">
            <div style={{
              padding: "20px",
              borderRadius: "16px",
              background: "rgba(255,255,255,0.04)",
              backdropFilter: "blur(12px)"
            }}>
              <h6 className="mb-3">Fraud Detection</h6>
              <div style={{ height: "260px" }}>
                <Pie data={fraudPieData} options={{ maintainAspectRatio: false }} />
              </div>
            </div>
          </div>

        </div>

      </div>
    </Layout>
  );
}

export default AdminAnalytics;
