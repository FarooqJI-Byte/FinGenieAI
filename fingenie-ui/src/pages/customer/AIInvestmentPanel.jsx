import { useState } from "react";
import Layout from "../../components/Layout";
import { generateInvestmentPlan } from "../../services/apiService";

function AIInvestmentPanel() {
  const [monthlyIncome, setMonthlyIncome] = useState("");
  const [riskLevel, setRiskLevel] = useState("MEDIUM");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  const generatePlan = async () => {
    const income = Number(monthlyIncome);
    if (!income || income <= 0) {
      setError("Monthly income must be greater than zero");
      return;
    }
    setLoading(true);
    setError("");
    setResult(null);
    try {
      const response = await generateInvestmentPlan({ monthlyIncome: income, riskLevel });
      setResult(response.data.data);
    } catch (err) {
      const errors = err.response?.data?.errors;
      setError((errors && Object.values(errors)[0]) || err.response?.data?.message || "Failed to generate plan");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <div className="eyebrow">AI investment advisor</div>
          <h1 className="page-title">Financial Recommendation Engine</h1>
          <p className="page-subtitle">Rule-based risk profiling and savings recommendations for capstone AI scope.</p>
        </div>
      </div>

      <section className="panel mb-4">
        <div className="row g-3">
          <div className="col-md-6">
            <label className="form-label">Monthly Income</label>
            <input type="number" className="form-control" value={monthlyIncome} onChange={(e) => setMonthlyIncome(e.target.value)} />
          </div>
          <div className="col-md-6">
            <label className="form-label">Risk Level</label>
            <select className="form-select" value={riskLevel} onChange={(e) => setRiskLevel(e.target.value)}>
              <option value="LOW">Low Risk</option>
              <option value="MEDIUM">Medium Risk</option>
              <option value="HIGH">High Risk</option>
            </select>
          </div>
        </div>
        <button className="btn btn-primary mt-3" onClick={generatePlan} disabled={loading}>
          {loading ? "Analyzing..." : "Generate Investment Plan"}
        </button>
      </section>

      {error && <div className="alert alert-danger">{error}</div>}
      {loading && <div className="loading-state">Generating recommendation...</div>}
      {!result && !loading && !error && <div className="empty-state">Enter your income and risk level to generate an AI investment plan.</div>}
      {result && (
        <section className="hero-panel">
          <div className="position-relative" style={{ zIndex: 1 }}>
            <div className="eyebrow">Recommended strategy</div>
            <h3>{result.strategyName}</h3>
            <p className="muted">{result.recommendation}</p>
            <div className="row g-3">
              <div className="col-sm-6"><div className="stat-card"><div className="muted">Expected Return</div><div className="stat-value text-success">{result.expectedReturn}%</div></div></div>
              <div className="col-sm-6"><div className="stat-card"><div className="muted">Risk Level</div><div className="stat-value">{riskLevel}</div></div></div>
            </div>
          </div>
        </section>
      )}
    </Layout>
  );
}

export default AIInvestmentPanel;
