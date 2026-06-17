import { useEffect, useMemo, useState } from "react";
import { useLocation } from "react-router-dom";
import Layout from "../../components/Layout";
import {
  deposit,
  getMyAccounts,
  getTransactionHistory,
  getTransactionsByDate,
  getTransactionsByType,
  transfer,
  withdraw,
} from "../../services/apiService";

function Transactions() {
  const location = useLocation();
  const modeFromUrl = new URLSearchParams(location.search).get("mode");
  const [mode, setMode] = useState(modeFromUrl || "deposit");
  const [accounts, setAccounts] = useState([]);
  const [selectedAccountId, setSelectedAccountId] = useState("");
  const [receiverAccountId, setReceiverAccountId] = useState("");
  const [amount, setAmount] = useState("");
  const [transactions, setTransactions] = useState([]);
  const [transactionType, setTransactionType] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);
  const [loading, setLoading] = useState(false);

  const money = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
  });

  const selectedAccount = useMemo(
    () =>
      accounts.find(
        (account) => String(account.accountId) === String(selectedAccountId)
      ),
    [accounts, selectedAccountId]
  );

  const showMessage = (msg, error = false) => {
    setMessage(msg);
    setIsError(error);
  };

  const handleError = (err) => {
    const errors = err.response?.data?.errors;
    showMessage(
      (errors && Object.values(errors)[0]) ||
        err.response?.data?.message ||
        "Operation failed",
      true
    );
  };

  const clearForm = () => {
    setReceiverAccountId("");
    setAmount("");
    setTransactions([]);
    setTransactionType("");
    setStartDate("");
    setEndDate("");
    setMessage("");
    setIsError(false);
  };

  const fetchAccounts = async () => {
    try {
      const res = await getMyAccounts();
      setAccounts(res.data.data || []);
    } catch (err) {
      handleError(err);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  useEffect(() => {
    if (modeFromUrl) {
      setMode(modeFromUrl);
      clearForm();
    }
  }, [modeFromUrl]);

  const validateAmount = () => {
    const numericAmount = Number(amount);
    if (!selectedAccountId || !numericAmount || numericAmount <= 0) {
      showMessage(
        "Select an account and enter an amount greater than zero",
        true
      );
      return null;
    }
    return numericAmount;
  };

  const refreshAfterMutation = async (successMessage) => {
    showMessage(successMessage);
    setAmount("");
    setReceiverAccountId("");
    await fetchAccounts();
  };

  const handleDeposit = async () => {
    const numericAmount = validateAmount();
    if (!numericAmount) return;
    try {
      await deposit({
        accountId: Number(selectedAccountId),
        amount: numericAmount,
      });
      await refreshAfterMutation("Deposit completed successfully");
    } catch (err) {
      handleError(err);
    }
  };

  const handleWithdraw = async () => {
    const numericAmount = validateAmount();
    if (!numericAmount) return;
    try {
      await withdraw({
        accountId: Number(selectedAccountId),
        amount: numericAmount,
      });
      await refreshAfterMutation("Withdrawal completed successfully");
    } catch (err) {
      handleError(err);
    }
  };

  const handleTransfer = async () => {
    const numericAmount = validateAmount();
    if (!numericAmount) return;

    if (
      !receiverAccountId ||
      Number(receiverAccountId) === Number(selectedAccountId)
    ) {
      showMessage("Enter a different receiver account ID", true);
      return;
    }

    try {
      await transfer({
        fromAccountId: Number(selectedAccountId),
        toAccountId: Number(receiverAccountId),
        amount: numericAmount,
      });

      await refreshAfterMutation("Transfer completed successfully");
    } catch (err) {
      handleError(err);
    }
  };

  const loadTransactionHistory = async () => {
    if (!selectedAccountId) {
      showMessage("Select an account first", true);
      return;
    }

    try {
      setLoading(true);
      const res = await getTransactionHistory(selectedAccountId);
      setTransactions(res.data.data || []);
      showMessage("");
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  const filterByType = async () => {
    if (!transactionType) {
      showMessage("Select a transaction type", true);
      return;
    }

    try {
      const res = await getTransactionsByType(
        selectedAccountId,
        transactionType
      );
      setTransactions(res.data.data || []);
      showMessage("");
    } catch (err) {
      handleError(err);
    }
  };

  const filterByDate = async () => {
    if (!startDate || !endDate) {
      showMessage("Select a start and end date", true);
      return;
    }

    try {
      const res = await getTransactionsByDate(
        selectedAccountId,
        startDate,
        endDate
      );
      setTransactions(res.data.data || []);
      showMessage("");
    } catch (err) {
      handleError(err);
    }
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <div className="eyebrow">Banking operations</div>
          <h1 className="page-title">Transactions</h1>
          <p className="page-subtitle">
            Deposit, withdraw, transfer, and review fraud risk signals.
          </p>
        </div>
      </div>

      {message && (
        <div
          className={`alert ${isError ? "alert-danger" : "alert-success"}`}
        >
          {message}
        </div>
      )}

      <section className="panel mb-4">
        <div className="d-flex gap-2 flex-wrap mb-3">
          {["deposit", "withdraw", "transfer", "history"].map((item) => (
            <button
              key={item}
              type="button"
              className={`btn ${
                mode === item ? "btn-primary" : "btn-outline-primary"
              }`}
              onClick={() => {
                setMode(item);
                clearForm();
              }}
            >
              {item.toUpperCase()}
            </button>
          ))}
        </div>

        <div className="row g-3">
          {/* ✅ UPDATED DROPDOWN (NO BALANCE) */}
          <div className="col-lg-5">
            <label className="form-label">Source account</label>
            <select
              className="form-select"
              value={selectedAccountId}
              onChange={(e) => setSelectedAccountId(e.target.value)}
            >
              <option value="">Select account</option>
              {accounts.map((acc) => (
                <option key={acc.accountId} value={acc.accountId}>
                  {acc.accountType} - ID: {acc.accountId}
                </option>
              ))}
            </select>
          </div>

          {mode !== "history" && (
            <div className="col-lg-3">
              <label className="form-label">Amount</label>
              <input
                className="form-control"
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
              />
            </div>
          )}

          {mode === "transfer" && (
            <div className="col-lg-4">
              <label className="form-label">Receiver account ID</label>
              <input
                className="form-control"
                value={receiverAccountId}
                onChange={(e) => setReceiverAccountId(e.target.value)}
              />
            </div>
          )}
        </div>

        <div className="mt-3">
          {mode === "deposit" && (
            <button className="btn btn-success" onClick={handleDeposit}>
              Deposit Money
            </button>
          )}
          {mode === "withdraw" && (
            <button className="btn btn-danger" onClick={handleWithdraw}>
              Withdraw Money
            </button>
          )}
          {mode === "transfer" && (
            <button className="btn btn-primary" onClick={handleTransfer}>
              Transfer Money
            </button>
          )}
          {mode === "history" && (
            <button className="btn btn-primary" onClick={loadTransactionHistory}>
              Load History
            </button>
          )}
        </div>
      </section>

      {mode === "history" && (
        <section className="panel">
          <div className="row g-3 mb-3">
            <div className="col-md-4">
              <select
                className="form-select"
                value={transactionType}
                onChange={(e) => setTransactionType(e.target.value)}
              >
                <option value="">Filter by type</option>
                <option value="DEPOSIT">Deposit</option>
                <option value="WITHDRAW">Withdraw</option>
                <option value="TRANSFER">Transfer</option>
              </select>
            </div>

            <div className="col-md-2">
              <button
                className="btn btn-outline-primary w-100"
                onClick={filterByType}
              >
                Apply
              </button>
            </div>

            <div className="col-md-3">
              <input
                className="form-control"
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
              />
            </div>

            <div className="col-md-3">
              <input
                className="form-control"
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
              />
            </div>

            <div className="col-md-2">
              <button
                className="btn btn-outline-primary w-100"
                onClick={filterByDate}
              >
                Date Filter
              </button>
            </div>
          </div>

          {loading ? (
            <div className="loading-state">
              Loading transactions...
            </div>
          ) : transactions.length === 0 ? (
            <div className="empty-state">
              No transactions to display.
            </div>
          ) : (
            <div className="table-wrap">
              <table className="bank-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Type</th>
                    <th>Date</th>
                    <th>Risk</th>
                    <th className="text-end">Amount</th>
                  </tr>
                </thead>

                <tbody>
                  {transactions.map((tx) => (
                    <tr key={tx.transactionId}>
                      <td>{tx.transactionId}</td>
                      <td>{tx.type}</td>
                      <td>{new Date(tx.date).toLocaleString()}</td>
                      <td>
                        <span
                          className={`pill ${
                            tx.fraudFlag ? "danger" : "success"
                          }`}
                        >
                          {tx.fraudFlag ? "Risk" : "Safe"} {tx.riskScore}
                        </span>
                      </td>
                      <td className="text-end fw-bold">
                        {money.format(tx.amount || 0)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      )}
    </Layout>
  );
}

export default Transactions;
