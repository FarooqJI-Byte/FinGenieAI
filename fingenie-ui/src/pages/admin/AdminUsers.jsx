import Layout from "../../components/Layout";
import { useEffect, useState } from "react";
import { getAllUsers } from "../../services/apiService";

function AdminUsers() {

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleError = (err) => {
    setIsError(true);
    setMessage(err.response?.data?.message || "Failed to load users");
  };

  const fetchUsers = async () => {
    try {
      setLoading(true);
      setMessage("");
      setIsError(false);

      const res = await getAllUsers();
      setUsers(res.data.data || []);

      setMessage("Users loaded successfully");

    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  const getRoleColor = (role) => {
    return role === "ADMIN" ? "#3b82f6" : "#22c55e";
  };

  const getVerifyColor = (verified) => {
    return verified ? "#22c55e" : "#ef4444";
  };

  return (
    <Layout>
      <div className="px-3 px-md-4 py-4">

        {/* ✅ HEADER */}
        <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
          <div>
            <h4 className="fw-bold">Users Management</h4>
            <p className="opacity-50">Monitor registered users</p>
          </div>

          <button
            className="btn btn-outline-light btn-sm"
            onClick={fetchUsers}
          >
            Refresh
          </button>
        </div>

        {/* ✅ STATUS */}
        {loading && <div className="alert alert-info">Loading users...</div>}

        {message && (
          <div className={`alert ${isError ? "alert-danger" : "alert-success"}`}>
            {message}
          </div>
        )}

        {/* ✅ EMPTY */}
        {!loading && users.length === 0 && !isError && (
          <div className="text-center opacity-50 mt-4">
            No users found
          </div>
        )}

        {/* ✅ LIST CONTAINER */}
        <div
          style={{
            borderRadius: "16px",
            overflow: "hidden",
            background: "rgba(255,255,255,0.03)",
            backdropFilter: "blur(12px)"
          }}
        >

          {/* ✅ HEADER ROW */}
          <div
            className="d-flex p-3"
            style={{
              fontWeight: "600",
              fontSize: "14px",
              background: "rgba(255,255,255,0.06)"
            }}
          >
            <div style={{ width: "10%" }}>ID</div>
            <div style={{ width: "20%" }}>Name</div>
            <div style={{ width: "30%" }}>Email</div>
            <div style={{ width: "20%" }}>Role</div>
            <div style={{ width: "20%" }}>Status</div>
          </div>

          {/* ✅ USER ROWS */}
          {users.map((user) => (

            <div
              key={user.userId}
              className="d-flex align-items-center p-3"
              style={{
                borderTop: "1px solid rgba(255,255,255,0.05)",
                transition: "all 0.2s ease"
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.background = "rgba(255,255,255,0.05)";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.background = "transparent";
              }}
            >

              <div style={{ width: "10%" }}>{user.userId}</div>

              <div style={{ width: "20%" }}>{user.name}</div>

              <div style={{ width: "30%", opacity: 0.8 }}>
                {user.email}
              </div>

              <div style={{ width: "20%" }}>
                <span
                  style={{
                    padding: "4px 10px",
                    borderRadius: "8px",
                    background: getRoleColor(user.role),
                    fontSize: "12px"
                  }}
                >
                  {user.role}
                </span>
              </div>

              <div style={{ width: "20%" }}>
                <span
                  style={{
                    padding: "4px 10px",
                    borderRadius: "8px",
                    background: getVerifyColor(user.verified),
                    fontSize: "12px"
                  }}
                >
                  {user.verified ? "Verified" : "Not Verified"}
                </span>
              </div>

            </div>

          ))}

        </div>

      </div>
    </Layout>
  );
}

export default AdminUsers;