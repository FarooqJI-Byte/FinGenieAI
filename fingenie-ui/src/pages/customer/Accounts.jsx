import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import {
  createAccount,
  getAccountBalance,
  getMyAccounts,
} from "../../services/apiService";

function Accounts() {
  const [accounts, setAccounts] = useState([]);
  const [accountType, setAccountType] = useState("SAVINGS");
  const [accountId, setAccountId] = useState("");
  const [balance, setBalance] = useState(null);
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);
  const [loading, setLoading] = useState(false);

  const money = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
  });

  const showMessage = (msg, error = false) => {
    setIsError(error);
    setMessage(msg);
  };

  const handleError = (err) => {
    const errors = err.response?.data?.errors;
    const validation = errors ? Object.values(errors)[0] : null;
    showMessage(
      validation ||
        err.response?.data?.message ||
        "Something went wrong",
      true
    );
  };

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      const res = await getMyAccounts();
      setAccounts(res.data.data || []);
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  // ✅ FIX: Reset balance when account changes
  useEffect(() => {
    setBalance(null);
  }, [accountId]);

  const createNewAccount = async () => {
    if (!accountType) {
      showMessage("Select an account type", true);
      return;
    }

    try {
      await createAccount({ accountType });
      showMessage("Account created successfully");
      await fetchAccounts();
    } catch (err) {
      handleError(err);
    }
  };

  const getBalance = async () => {
    if (!accountId) {
      showMessage("Select an account first", true);
      return;
    }

    try {
      const res = await getAccountBalance(accountId);
      setBalance(res.data.data);
      showMessage("Balance fetched successfully");
    } catch (err) {
      handleError(err);
    }
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <div className="eyebrow">Accounts</div>
          <h1 className="page-title">Manage Accounts</h1>
          <p className="page-subtitle">
            Create, review, and check balances for your FinGenie bank accounts.
          </p>
        </div>

        <button className="btn btn-outline-primary" onClick={fetchAccounts}>
          Refresh
        </button>
      </div>

      {message && (
        <div
          className={`alert ${
            isError ? "alert-danger" : "alert-success"
          }`}
        >
          {message}
        </div>
      )}

      <div className="row g-3 mb-4">
        <div className="col-lg-5">
          <section className="panel h-100">
            <h4>Create Account</h4>
            <p className="muted">
              Choose an account type. Limits are enforced by the banking API.
            </p>

            <label className="form-label">Account type</label>
            <select
              className="form-select mb-3"
              value={accountType}
              onChange={(e) => setAccountType(e.target.value)}
            >
              <option value="SAVINGS">Savings</option>
              <option value="CURRENT">Current</option>
              <option value="SALARY">Salary</option> {/* ✅ ADDED */}
            </select>

            <button
              className="btn btn-primary w-100"
              onClick={createNewAccount}
            >
              Create Account
            </button>
          </section>
        </div>

        <div className="col-lg-7">
          <section className="panel h-100">
            <h4>Balance Lookup</h4>
            <p className="muted">
              Select one of your accounts to view the latest balance.
            </p>

            <select
              className="form-select mb-3"
              value={accountId}
              onChange={(e) => setAccountId(e.target.value)}
            >
              <option value="">Select account</option>
              {accounts.map((acc) => (
                <option key={acc.accountId} value={acc.accountId}>
                  {acc.accountType} - {acc.accountNumber}
                </option>
              ))}
            </select>

            <button
              className="btn btn-outline-primary"
              onClick={getBalance}
            >
              Check Balance
            </button>

            {balance !== null && (
              <div className="stat-value mt-3 text-success">
                {money.format(balance)}
              </div>
            )}
          </section>
        </div>
      </div>

      <section className="panel">
        <h4>Your Accounts</h4>

        {loading ? (
          <div className="loading-state">
            Loading accounts...
          </div>
        ) : accounts.length === 0 ? (
          <div className="empty-state">
            No accounts found. Create one to begin banking.
          </div>
        ) : (
          <div className="table-wrap">
            <table className="bank-table">
              <thead>
                <tr>
                  <th>Bank</th>
                  <th>Type</th>
                  <th>Account Number</th>
                  <th>IFSC</th>
                </tr>
              </thead>

              <tbody>
                {accounts.map((acc) => (
                  <tr key={acc.accountId}>
                    <td>FinGenie Bank</td>
                    <td>
                      <span className="pill primary">
                        {acc.accountType}
                      </span>
                    </td>
                    <td>{acc.accountNumber}</td>
                    <td>{acc.ifscCode}</td>
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

export default Accounts;