# 🚀 FinGenieAI – Intelligent Digital Banking System

FinGenieAI is a full-stack, AI-driven digital banking and wealth management platform built using Spring Boot and React. It provides secure banking operations, smart financial features, and real-time fraud detection.

---

## 📌 Problem Statement

Traditional banking systems lack:
- Real-time fraud detection
- Personalized financial recommendations
- Intelligent insights for users
- Seamless digital-first experience

---

## 🎯 Objective

To build a secure, scalable, and intelligent digital banking platform that:
- Enhances user experience
- Ensures data security
- Provides AI-based financial insights
- Supports real-time banking operations

---

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security (JWT Authentication)
- Spring Data JPA
- MySQL

### Frontend
- React (Vite)
- Bootstrap

### Other Tools
- REST APIs
- Swagger (API Documentation)
- Maven

---

## 🧠 Key Features

### 🔐 Authentication & Security
- JWT-based authentication
- Email OTP verification
- Role-based access (Admin / Customer)

---

### 🏦 Account Management
- Create multiple accounts
- View account details
- Secure ownership validation

---

### 💸 Transactions
- Deposit money
- Withdraw money
- Transfer funds
- Transaction history & filters

---

### 🚨 Fraud Detection
- Rule-based fraud detection
- Risk score calculation
- Suspicious transaction alerts via email

---

### 💳 Loan Module (Microservice)
- Apply for loans
- Admin approval/rejection
- Integrated loan service

---

### 🤖 AI Investment Recommendations
- Strategy pattern-based design
- Risk-level based investment suggestions

---

### 📊 Admin Dashboard
- Total users & accounts
- Loan statistics
- System analytics

---

## 🏗️ System Architecture

- Microservices-based architecture
- Separate Loan Service
- REST API communication
- React frontend interacting with backend APIs

---

## ▶️ How to Run the Project

### ✅ Backend (Spring Boot)

```bash
cd fingenie-ai
mvn clean install
mvn spring-boot:run
``

Loan Service

cd loan-service
mvn clean install
mvn spring-boot:run


Frontend (React)

cd fingenie-ui
npm install
npm run dev

Default Configuration

Backend runs on: http://localhost:8080
Loan Service: http://localhost:8081
Frontend: http://localhost:5173


📌 Important Notes

node_modules and target folders are excluded (auto-generated)
Run npm install before starting frontend
Ensure MySQL is running and configured properly

🚀 Future Enhancements

Machine Learning-based fraud detection
Real-time notifications
Mobile application support
Advanced analytics dashboard

👨‍💻 Author
Mahammadfarooq Irakall

⭐ Conclusion
FinGenieAI demonstrates a complete full-stack banking system with:

Secure authentication
Real-time transactions
Fraud detection
AI-based recommendations

Designed with scalability, security, and real-world banking use cases in mind.