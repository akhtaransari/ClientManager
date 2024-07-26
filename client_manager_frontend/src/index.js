// src/index.jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css'; // Importing global CSS styles
import App from './App'; // Importing the main App component
import 'bootstrap/dist/css/bootstrap.min.css'; // Importing Bootstrap CSS for styling

// Create a root for the React application and render the App component
const root = ReactDOM.createRoot(document.getElementById('root'));

// Rendering the App component into the root element of the HTML
root.render(
    <App />
);
