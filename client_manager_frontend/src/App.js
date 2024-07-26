// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import LoginForm from './components/LoginForm';
import CustomerManagement from './components/CustomerManagement';

// PrivateRoute component that handles route protection
// Redirects to the login page if the user is not logged in
const PrivateRoute = ({ children }) => {
  // Check if the user is logged in by checking local storage
  return localStorage.getItem('isLoggedIn') ? children : <Navigate to="/login" />;
};

const App = () => {
  return (
    <Router>
      <Routes>
        {/* Route for the login page */}
        <Route path="/login" element={<LoginForm />} />
        
        {/* Route for the dashboard page */}
        {/* Uses PrivateRoute to ensure the user is logged in before accessing the dashboard */}
        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <CustomerManagement />
            </PrivateRoute>
          }
        />
        
        {/* Redirect from root path to login page */}
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
};

export default App;
