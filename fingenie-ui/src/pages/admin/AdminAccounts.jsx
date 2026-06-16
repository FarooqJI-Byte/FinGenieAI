import Layout from "../../components/Layout";
import { useEffect, useState } from "react";

import { getAllAccounts } from "../../services/apiService";

function AdminAccounts() {

  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(false);

  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    fetchAccounts();
  }, []);

  const handleError = (err) => {
    console.error(err);
    setIsError(true);
    setMessage(err.response?.data?.message || "Failed to load accounts");
  };

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      setMessage("");
      setIsError(false);

      const res = await getAllAccounts();

      // ✅ DEBUG (remove later if you want)
      console.log("Accounts Data:", res.data.data);

      setAccounts(res.data.data || []);
      setMessage("Accounts loaded successfully");

    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  const getTypeColor = (type) => {
    return type === "SAVINGS" ? "#3b82f6" : "#22c55e";
  };

  return (
    <Layout>
      <div className="px-3 px-md-4 py-4">

        {/* ✅ HEADER */}
        <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
          <div>
            <h4 className="fw-bold">Accounts Overview</h4>
            <p className="opacity-50">
              All customer bank accounts
            </p>
          </div>

          <button
            className="btn btn-outline-light btn-sm"
            onClick={fetchAccounts}
          >
            Refresh
          </button>
        </div>

        {/* ✅ LOADING */}
        {loading && (
          <div className="alert alert-info">
            Loading accounts...
          </div>
        )}

        {/* ✅ MESSAGE */}
        {message && (
          <div className={`alert ${isError ? "alert-danger" : "alert-success"}`}>
            {message}
          </div>
        )}

        {/* ✅ EMPTY */}
        {!loading && accounts.length === 0 && !isError && (
          <div className="text-center opacity-50 mt-4">
            No accounts found
          </div>
        )}

        {/* ✅ ACCOUNT CARDS */}
        <div className="row g-3 mt-2">

          {accounts.map((account) => (

            <div className="col-12 col-md-6 col-lg-4" key={account.accountId}>

              <div
                style={{
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

                {/* ✅ TOP */}
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <strong>ID: {account.accountId}</strong>

                  <span
                    style={{
                      color: getTypeColor(account.accountType),
                      fontSize: "13px",
                      fontWeight: "600"
                    }}
                  >
                    {account.accountType || "N/A"}
                  </span>
                </div>

                {/* ✅ ACCOUNT NUMBER */}
                <div className="mb-2">
                  <small className="opacity-50">Account Number</small>
                  <div>{account.accountNumber || "N/A"}</div>
                </div>

                {/* ✅ BANK */}
                <div className="mb-2">
                  <small className="opacity-50">Bank</small>
                  <div>{account.bankName || "N/A"}</div>
                </div>

                {/* ✅ BALANCE 🔥 (IMPORTANT ADD) */}
                <div className="mb-2">
                  <small className="opacity-50">Balance</small>
                  <div style={{
                    color: "#22c55e",
                    fontWeight: "600"
                  }}>
                    ₹ {account.balance ?? 0}
                  </div>
                </div>

                {/* ✅ IFSC */}
                <div>
                  <small className="opacity-50">IFSC</small>
                  <div>{account.ifscCode || "N/A"}</div>
                </div>

              </div>
            </div>

          ))}

        </div>

      </div>
    </Layout>
  );
}

export default AdminAccounts;
