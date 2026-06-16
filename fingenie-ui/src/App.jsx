import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Register from "./pages/auth/Register";
import Login from "./pages/auth/Login";
import VerifyOtp from "./pages/auth/VerifyOtp";
import CustomerDashboard from "./pages/customer/CustomerDashboard";
import Accounts from "./pages/customer/Accounts";
import Transactions from "./pages/customer/Transactions";
import Loans from "./pages/customer/Loans";
import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminUsers from "./pages/admin/AdminUsers";
import AdminAccounts from "./pages/admin/AdminAccounts";
import AdminLoans from "./pages/admin/AdminLoans";
import AdminAnalytics from "./pages/admin/AdminAnalytics";
import ProtectedRoute from "./components/ProtectedRoute";
import AIInvestmentPanel from "./pages/customer/AIInvestmentPanel";

function App() {
  const isLoggedIn = !!localStorage.getItem("token");

  return (
    <BrowserRouter>
      <Routes>

        {/* Public Routes */}
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/verify" element={<VerifyOtp />} />

        {/* CUSTOMER DASHBOARD */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute allowedRole="CUSTOMER">
              <CustomerDashboard />
            </ProtectedRoute>
          }
        />

        {/* ADMIN DASHBOARD */}
        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute allowedRole="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        {/* CUSTOMER ROUTES */}
        <Route
          path="/accounts"
          element={
            <ProtectedRoute allowedRole="CUSTOMER">
              <Accounts />
            </ProtectedRoute>
          }
        />

        <Route
          path="/transactions"
          element={
            <ProtectedRoute allowedRole="CUSTOMER">
              <Transactions />
            </ProtectedRoute>
          }
        />

        <Route
          path="/loans"
          element={
            <ProtectedRoute allowedRole="CUSTOMER">
              <Loans />
            </ProtectedRoute>
          }
        />            
                
        <Route
          path="/ai-investment"
          element={
            <ProtectedRoute allowedRole="CUSTOMER">

                <AIInvestmentPanel />
            </ProtectedRoute>
          }
        />


        {/* ADMIN ROUTES */}
        <Route
          path="/admin/users"
          element={
            <ProtectedRoute allowedRole="ADMIN">
              <AdminUsers />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/accounts"
          element={
            <ProtectedRoute allowedRole="ADMIN">
              <AdminAccounts />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/loans"
          element={
            <ProtectedRoute allowedRole="ADMIN">
              <AdminLoans />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/analytics"
          element={
            <ProtectedRoute allowedRole="ADMIN">
              <AdminAnalytics />
            </ProtectedRoute>
          }
        />

        {/* FALLBACK */}
        <Route
          path="*"
          element={
            isLoggedIn
              ? <Navigate to="/dashboard" replace />
              : <Navigate to="/" replace />
          }
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;
