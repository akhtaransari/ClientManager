// src/components/LoginForm.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "bootstrap/dist/css/bootstrap.min.css"; // Import Bootstrap CSS for styling
import "./LoginForm.css"; // Import the custom CSS file for specific styling

const LoginForm = () => {
  // State variables to hold email and password inputs
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();


  // Handler function for form submission
  const handleLogin = async (e) => {
    e.preventDefault(); // Prevent the default form submission behavior

    try {
      // Create basic authentication header
      const base64Credentials = btoa(email + ":" + password);
      const headers = {
        Authorization: `Basic ${base64Credentials}`,
        "Content-Type": "application/json",
      };

      // Make the GET request with axios
      const response = await axios.get("http://localhost:8080/api/auth/login", {
        headers,
      });

      // Extract the token from response headers
      let token = response.headers["authorization"];

      // Log the response data and store the access token in local storage
      localStorage.setItem("token", token);
      if (token){
        localStorage.setItem("isLoggedIn", "true"); // Set the login status in local storage
      }
  
      // Navigate to the dashboard page upon successful login
      navigate("/dashboard");
    } catch (error) {
      console.error("Error:", error);
    }
  };

  return (
    <div className="login-background d-flex justify-content-center align-items-center vh-200">
      {/* Container for background styling and centering */}
      <div className="login-container d-flex justify-content-center align-items-center">
        {/* Centered container for the login card */}
        <div className="card p-4 shadow-lg login-card">
          {" "}
          {/* Card component for login form */}
          <h2 className="card-title text-center mb-4">Login</h2>
          <form onSubmit={handleLogin}>
            {" "}
            {/* Form submission handler */}
            <div className="form-group mb-3">
              <label>Email:</label>
              <input
                type="email"
                className="form-control"
                value={email}
                onChange={(e) => setEmail(e.target.value)} // Update email state on input change
                required
              />
            </div>
            <div className="form-group mb-3">
              <label>Password:</label>
              <input
                type="password"
                className="form-control"
                value={password}
                onChange={(e) => setPassword(e.target.value)} // Update password state on input change
                required
              />
            </div>
            <button
              type="submit"
              className="btn btn-primary w-100" // Bootstrap primary button, full width
            >
              Login
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginForm;
