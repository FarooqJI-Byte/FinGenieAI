import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import { applyLoan, getMyLoans } from "../../services/apiService";

function Loans() {
  const [amount, setAmount] = useState("");
  const [income, setIncome] = useState("");
  const [creditScore, setCreditScore] = useState("");
  const [loans, setLoans] = useState([]);
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);
  const [loading, setLoading] = useState(false);

  // ✅ NEW: userId input for loan submission
  const [userId, setUserId] = useState("");

  const money = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR"
  });

  const showMessage = (msg, error = false) => {
    setMessage(msg);
    setIsError(error);
  };

  const handleError = (err) => {
    const errors = err.response?.data?.errors;
    showMessage(
      (errors && Object.values(errors)[0]) ||
        err.response?.data?.message ||
        "Something went wrong",
      true
    );
  };

  const fetchLoans = async () => {
    try {
      setLoading(true);
      const res = await getMyLoans();
      setLoans(res.data.data || []);
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLoans();
  }, []);

  const handleApplyLoan = async () => {
    const numericAmount = Number(amount);
    const numericIncome = Number(income);
    const numericScore = Number(creditScore);
    const numericUserId = Number(userId);

    // ✅ VALIDATION
    if (!numericUserId || numericUserId <= 0) {
      showMessage("Please enter a valid User ID", true);
      return;
    }

    if (!numericAmount || numericAmount <= 0 || !numericIncome || numericIncome <= 0) {
      showMessage("Amount and income must be greater than zero", true);
      return;
    }

    if (numericScore < 300 || numericScore > 900) {
      showMessage("Credit score must be between 300 and 900", true);
      return;
    }

    try {
      await applyLoan({
        userId: numericUserId, // ✅ USING INPUT VALUE
        amount: numericAmount,
        income: numericIncome,
        creditScore: numericScore
      });

      showMessage("Loan application submitted");

      setAmount("");
      setIncome("");
      setCreditScore("");
      setUserId("");

      await fetchLoans();
    } catch (err) {
      handleError(err);
    }
  };

  const statusClass = (status) => {
    switch (status) {
      case "APPROVED":
        return "bg-success";
      case "REJECTED":
        return "bg-danger";
      case "PENDING":
        return "bg-warning text-dark";
      default:
        return "bg-secondary";
    }
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <div className="eyebrow">Smart loan management</div>
          <h1 className="page-title">Loan Prediction Dashboard</h1>
          <p className="page-subtitle">
            Apply with income and credit score details. FinGenie keeps the rule-based prediction flow intact.
          </p>
        </div>
        <button className="btn btn-outline-primary" onClick={fetchLoans}>
          Refresh
        </button>
      </div>

      {message && (
        <div className={`alert ${isError ? "alert-danger" : "alert-success"}`}>
          {message}
        </div>
      )}

      {/* ✅ UPDATED APPLY SECTION */}
      <section className="panel mb-4">
        <h4>Apply for Loan</h4>

        <div className="row g-3">
          {/* ✅ NEW FIELD */}
          <div className="col-md-3">
            <label className="form-label">User ID</label>
            <input
              type="number"
              className="form-control"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
            />
          </div>

          <div className="col-md-3">
            <label className="form-label">Amount</label>
            <input
              type="number"
              className="form-control"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
            />
          </div>

          <div className="col-md-3">
            <label className="form-label">Monthly Income</label>
            <input
              type="number"
              className="form-control"
              value={income}
              onChange={(e) => setIncome(e.target.value)}
            />
          </div>

          <div className="col-md-3">
            <label className="form-label">Credit Score</label>
            <input
              type="number"
              className="form-control"
              value={creditScore}
              onChange={(e) => setCreditScore(e.target.value)}
            />
          </div>
        </div>

        <button className="btn btn-primary mt-3" onClick={handleApplyLoan}>
          Submit Application
        </button>
      </section>

      {/* ✅ LOAN LIST (unchanged) */}
      <section className="panel">
        <h4>Your Loans</h4>

        {loading ? (
          <div className="loading-state">Loading loan applications...</div>
        ) : loans.length === 0 ? (
          <div className="empty-state">No loan applications yet.</div>
        ) : (
          <div className="table-wrap">
            <table className="bank-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Amount</th>
                  <th>Income</th>
                  <th>Score</th>
                  <th>Status</th>
                </tr>
              </thead>

              <tbody>
                {loans.map((loan) => (
                  <tr key={loan.loanId}>
                    <td>{loan.loanId}</td>
                    <td>{money.format(loan.amount || 0)}</td>
                    <td>{money.format(loan.income || 0)}</td>
                    <td>{loan.creditScore}</td>
                    <td>
                      <span className={`pill ${statusClass(loan.status)}`}>
                        {loan.status}
                      </span>
                    </td>
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

export default Loans;