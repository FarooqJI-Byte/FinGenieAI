import API from "./api";


//Auth 
export const loginUser = (data) =>
  API.post("/auth/login", data);

export const registerUser = (data) =>
  API.post("/auth/register", data);

export const verifyOtp = (data) =>
  API.post("/auth/verify", data);


//admin apis

export const getAllAccounts = () =>
  API.get("/accounts/all");

export const getAdminDashboard = () =>
  API.get("/admin/dashboard");


//admin loan api

export const getAllLoans = () =>
  API.get("/loans/all");

export const approveLoan = (loanId) =>
  API.put(`/loans/${loanId}/approve`);

export const rejectLoan = (loanId) =>
  API.put(`/loans/${loanId}/reject`);

//admin users

export const getAllUsers = () =>
  API.get("/admin/users");







// ✅ Accounts

export const createAccount = (data) =>
  API.post("/accounts/create", data);

export const getMyAccounts = () =>
  API.get("/accounts/my");

export const getAccountBalance = (accountId) =>
  API.get(`/accounts/${accountId}/balance`);


// ✅ Transactions
export const deposit = (data) =>
  API.post("/transactions/deposit", data);

export const withdraw = (data) =>
  API.post("/transactions/withdraw", data);

export const transfer = (data) =>
  API.post("/transactions/transfer", data);

export const getTransactionHistory = (id) =>
  API.get(`/transactions/${id}/transactions`);

export const getTransactionsByType = (id, type) =>
  API.get(`/transactions/${id}/transactions/type`, {
    params: { type },
  });

export const getTransactionsByDate = (id, startDate, endDate) =>
  API.get(`/transactions/${id}/transactions/date`, {
    params: { startDate, endDate },
  });

// ✅ Loans
export const applyLoan = (data) =>
  API.post("/loans/apply", data);

export const getMyLoans = () =>
  API.get("/loans/my");

// ✅ User
export const getMyId = () =>
  API.get("/user/id");

// ✅ AI Investment
export const generateInvestmentPlan = (data) =>
  API.post("/ai/invest", data);