import Layout from "../../components/Layout";
import { useEffect, useState } from "react";

import {
  getAllLoans,
  approveLoan,
  rejectLoan
} from "../../services/apiService";

function AdminLoans() {

  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(false);

  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    fetchLoans();
  }, []);

  const showSuccess = (msg) => {
    setIsError(false);
    setMessage(msg);
  };

  const handleError = (err) => {
    setIsError(true);
    setMessage(err.response?.data?.message || "Something went wrong");
  };

  const fetchLoans = async () => {
    try {
      setLoading(true);
      setMessage("");
      setIsError(false);

      const res = await getAllLoans();
      setLoans(res.data.data || []);

      showSuccess("Loans loaded");

    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (loanId) => {
    try {
      await approveLoan(loanId);
      showSuccess("Loan approved");
      fetchLoans();
    } catch (err) {
      handleError(err);
    }
  };

  const handleReject = async (loanId) => {
    try {
      await rejectLoan(loanId);
      showSuccess("Loan rejected");
      fetchLoans();
    } catch (err) {
      handleError(err);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "APPROVED":
        return "#22c55e";
      case "REJECTED":
        return "#ef4444";
      default:
        return "#f59e0b";
    }
  };

  return (
    <Layout>
      <div className="px-3 px-md-4 py-4">

        {/* ✅ HEADER */}
        <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
          <div>
            <h4 className="fw-bold">Loan Management</h4>
            <p className="opacity-50">
              Review and process loan applications
            </p>
          </div>

          <button
            className="btn btn-outline-light btn-sm"
            onClick={fetchLoans}
          >
            Refresh
          </button>
        </div>

        {/* ✅ LOADING */}
        {loading && (
          <div className="alert alert-info">
            Loading loans...
          </div>
        )}

        {/* ✅ MESSAGE */}
        {message && (
          <div className={`alert ${isError ? "alert-danger" : "alert-success"}`}>
            {message}
          </div>
        )}

        {/* ✅ EMPTY */}
        {!loading && loans.length === 0 && !isError && (
          <div className="text-center opacity-50 mt-4">
            No loans found
          </div>
        )}

        {/* ✅ LOAN CARDS 🔥 */}
        <div className="row g-3 mt-2">

          {loans.map((loan) => (

            <div className="col-12 col-md-6" key={loan.loanId}>

              <div
                style={{
                  padding: "20px",
                  borderRadius: "16px",
                  background:
                    loan.status === "PENDING"
                      ? "rgba(245,158,11,0.1)"
                      : "rgba(255,255,255,0.04)",

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

                {/* ✅ TOP */}
                <div className="d-flex justify-content-between mb-3">
                  <strong>Loan #{loan.loanId}</strong>

                  <span
                    style={{
                      color: getStatusColor(loan.status),
                      fontWeight: "600",
                      fontSize: "13px"
                    }}
                  >
                    {loan.status}
                  </span>
                </div>

                {/* ✅ DETAILS */}
                <div className="mb-2">
                  <small className="opacity-50">User ID</small>
                  <div>{loan.userId}</div>
                </div>

                <div className="mb-2">
                  <small className="opacity-50">Amount</small>
                  <div style={{ color: "#22c55e", fontWeight: "600" }}>
                    ₹ {loan.amount}
                  </div>
                </div>

                <div className="mb-2">
                  <small className="opacity-50">Income</small>
                  <div>₹ {loan.income}</div>
                </div>

                <div className="mb-3">
                  <small className="opacity-50">Credit Score</small>
                  <div>{loan.creditScore}</div>
                </div>

                {/* ✅ ACTIONS */}
                {loan.status === "PENDING" ? (
                  <div className="d-flex gap-2">
                    <button
                      className="btn btn-success w-100"
                      onClick={() => handleApprove(loan.loanId)}
                    >
                      Approve
                    </button>

                    <button
                      className="btn btn-danger w-100"
                      onClick={() => handleReject(loan.loanId)}
                    >
                      Reject
                    </button>
                  </div>
                ) : (
                  <div className="text-center opacity-50">
                    No action required
                  </div>
                )}

              </div>
            </div>

          ))}

        </div>

      </div>
    </Layout>
  );
}

export default AdminLoans;