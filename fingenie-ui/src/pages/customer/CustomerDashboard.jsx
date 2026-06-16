import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import { getMyAccounts, getMyId } from "../../services/apiService";

function CustomerDashboard() {
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState([]);
  const [myId, setMyId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      const [accountRes, idRes] = await Promise.all([getMyAccounts(), getMyId()]);
      setAccounts(accountRes.data.data || []);
      setMyId(idRes.data.data);
      setMessage("");
    } catch (err) {
      setMessage(err.response?.data?.message || "Unable to load dashboard");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  const totalBalance = accounts.reduce((sum, acc) => sum + (acc.balance || 0), 0);
  const money = new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" });

  const actions = [
    { title: "Deposit", text: "Add funds securely", path: "/transactions?mode=deposit" },
    { title: "Withdraw", text: "Move cash out", path: "/transactions?mode=withdraw" },
    { title: "Transfer", text: "Send between accounts", path: "/transactions?mode=transfer" },
    { title: "Loans", text: "Apply and track loans", path: "/loans" },
  ];

  return (
    <Layout>
      <section className="hero-panel mb-4">
        <div className="position-relative" style={{ zIndex: 1 }}>
          <div className="page-header mb-0">
            <div>
              <div className="eyebrow">Customer dashboard</div>
              <h1 className="page-title">Welcome back</h1>
              <p className="page-subtitle mb-0">Your accounts, money movement, and AI insights in one place.</p>
              <small className="muted">User ID: {myId || "Loading"}</small>
            </div>
            <div className="text-md-end">
              <div className="muted">Total available balance</div>
              <div className="stat-value text-success">{money.format(totalBalance)}</div>
            </div>
          </div>
        </div>
      </section>

      {message && <div className="alert alert-danger">{message}</div>}
      {loading && <div className="loading-state mb-4">Loading your banking overview...</div>}

      <div className="row g-3 mb-4">
        {[
          ["Accounts", accounts.length],
          ["Balance", money.format(totalBalance)],
          ["Security", "MFA Active"],
          ["AI Advisor", "Ready"],
        ].map(([label, value]) => (
          <div className="col-6 col-lg-3" key={label}>
            <div className="stat-card">
              <div className="muted">{label}</div>
              <div className="stat-value">{value}</div>
            </div>
          </div>
        ))}
      </div>

      <div className="row g-3 mb-4">
        {actions.map((action) => (
          <div className="col-12 col-md-6 col-xl-3" key={action.title}>
            <button type="button" className="action-card text-start w-100" onClick={() => navigate(action.path)}>
              <div className="eyebrow">{action.title}</div>
              <h5 className="mt-2 mb-1">{action.title}</h5>
              <p className="muted mb-0">{action.text}</p>
            </button>
          </div>
        ))}
      </div>

      <section className="panel">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <div>
            <h4 className="mb-1">Your Accounts</h4>
            <p className="muted mb-0">Recently created banking accounts</p>
          </div>
          <button className="btn btn-outline-primary btn-sm" onClick={() => navigate("/accounts")}>Manage</button>
        </div>

        {accounts.length === 0 ? (
          <div className="empty-state">No accounts yet. Create your first account from the Accounts page.</div>
        ) : (
          <div className="table-wrap">
            <table className="bank-table">
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Account number</th>
                  <th className="text-end">Balance</th>
                </tr>
              </thead>
              <tbody>
                {accounts.slice(0, 3).map((acc) => (
                  <tr key={acc.accountId}>
                    <td><span className="pill primary">{acc.accountType}</span></td>
                    <td>{acc.accountNumber}</td>
                    <td className="text-end fw-bold">{money.format(acc.balance || 0)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </Layout>
  );
}

export default CustomerDashboard;
