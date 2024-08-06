import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import AddCustomerModal from "./AddCustomerModal";
import EditCustomerModal from "./EditCustomerModal";
import SyncModal from "./SyncModal";

// Component for managing customers
const CustomerManagement = () => {
  // State to hold customer data and pagination information
  const [customers, setCustomers] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [pageSize] = useState(10); // Number of customers per page
  const [showModal, setShowModal] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedCustomerId, setSelectedCustomerId] = useState(null);

  // Fetch customers whenever currentPage or searchQuery changes
  useEffect(() => {
    fetchCustomers();
  }, [currentPage, searchQuery]);

  const navigate = useNavigate();

  // Handle user logout
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("isLoggedIn");
    navigate("/login");
  };

  // Fetch customers from the API
  const fetchCustomers = async () => {
    try {
      // Construct URL with query parameters for pagination and search
      const url = `http://localhost:8080/api/customers?page=${currentPage}&size=${pageSize}&sortBy=${searchType}&value=${searchQuery}`;

      // Get the JWT token from local storage
      const token = localStorage.getItem("token");

      // Make the GET request to fetch customer data
      const response = await axios.get(url, {
        headers: {
          Authorization: `Bearer ${token}`, // Include JWT token in the Authorization header
          "Content-Type": "application/json",
        },
      });

      // Update state with the response data
      setCustomers(response.data.content); // Adjust according to actual response structure
      setTotalPages(response.data.totalPages); // Adjust according to actual response structure
    } catch (error) {
      console.error("Error fetching customers:", error);
    }
  };

  // Handle search input and reset page to 0
  const handleSearch = async () => {
    setCurrentPage(0);
  };

  // Handle page change for pagination
  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  // Open the Add Customer modal
  const handleAddCustomer = () => {
    setShowModal(true);
  };

  // Close the Add Customer modal
  const handleCloseModal = () => {
    setShowModal(false);
  };

  // Close the Edit Customer modal
  const handleCloseEditModal = () => {
    setShowEditModal(false);
    setSelectedCustomerId(null);
  };

  // Open the Edit Customer modal with the selected customer ID
  const handleEditCustomer = (id) => {
    setSelectedCustomerId(id);
    setShowEditModal(true);
  };

  // Add these to your existing state declarations
  const [showSyncModal, setShowSyncModal] = useState(false);

  // Function to open the sync modal
  const handleSyncModalOpen = () => setShowSyncModal(true);

  // Function to close the sync modal
  const handleSyncModalClose = () => setShowSyncModal(false);

  // Function to handle synchronization
  const handleSync = async (password) => {
    try {
      // Retrieve the token from localStorage
      const token = localStorage.getItem("token");

      // Check if token exists
      if (!token) {
        throw new Error("No token found in localStorage");
      }

      // Construct the URL
      const url = `http://localhost:8080/api/customers/sync`;

      // Make the POST request
      const response = await axios.post(
        url,
        { password },
        {
          headers: {
            Authorization: `Bearer ${token}`, // Include JWT token in the Authorization header
            "Content-Type": "application/json",
          },
        }
      );

      fetchCustomers(); // Refresh the customer list
      alert(response.data);
      handleSyncModalClose(); // Close the sync modal
    } catch (error) {
      // Handle any errors that occurred during the request
      console.error("Error syncing data:", error);
      alert("Failed to sync data.");
    }
  };

  // Delete a customer by ID
  const handleDeleteCustomer = async (id) => {
    try {
      // Get the JWT token from local storage
      const token = localStorage.getItem("token");

      // Make the DELETE request to delete the customer
      await axios.delete(`http://localhost:8080/api/customers/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`, // Include JWT token in the Authorization header
          "Content-Type": "application/json",
        },
      });

      // Refresh the customer list after deletion
      fetchCustomers();

      // Alert the user about the successful deletion
      alert(`Customer with ID ${id} deleted successfully.`);
    } catch (error) {
      // Log and alert the error if the deletion fails
      console.error("Error deleting customer:", error);
      alert(`Failed to delete customer with ID ${id}.`);
    }
  };

  return (
    <div className="container mt-5">
      {/* Header and buttons for the dashboard */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <div>
          <button
            className="btn btn-primary me-3 mb-3"
            onClick={handleAddCustomer}
          >
            Add Customer
          </button>
          <button
            className="btn btn-secondary mb-3"
            onClick={handleSyncModalOpen}
          >
            Sync
          </button>
        </div>

        <div>
          <h2 className="text-center">Dashboard</h2>
          <p className="text-center">Welcome to your dashboard!</p>
        </div>
        <button className="btn btn-danger mb-3" onClick={handleLogout}>
          Logout
        </button>
      </div>

      {/* Search and filter options */}
      <div className="mb-3">
        <select
          className="form-select mb-2"
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
        >
          <option value="firstName">First Name</option>
          <option value="city">City</option>
          <option value="email">E-mail</option>
          <option value="phone">Phone</option>
        </select>
        <div className="input-group">
          <input
            type="text"
            className="form-control"
            placeholder="Search..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button className="btn btn-secondary" onClick={handleSearch}>
            Search
          </button>
        </div>
      </div>

      {/* Table to display customer data */}
      <table className="table table-striped">
        <thead>
          <tr>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Street</th>
            <th>Address</th>
            <th>City</th>
            <th>State</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {customers.length > 0 ? (
            customers.map((customer) => (
              <tr key={customer.uuid}>
                <td>{customer.firstName}</td>
                <td>{customer.lastName}</td>
                <td>{customer.street}</td>
                <td>{customer.address}</td>
                <td>{customer.city}</td>
                <td>{customer.state}</td>
                <td>{customer.email}</td>
                <td>{customer.phone}</td>
                <td>
                  <button
                    className="btn btn-success btn-sm me-2"
                    onClick={() => handleEditCustomer(customer.uuid)}
                  >
                    Edit
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDeleteCustomer(customer.uuid)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="9">No customers found</td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Pagination controls */}
      <nav>
        <ul className="pagination">
          <li className={`page-item ${currentPage === 0 ? "disabled" : ""}`}>
            <button
              className="page-link"
              onClick={() => handlePageChange(currentPage - 1)}
            >
              Previous
            </button>
          </li>
          {[...Array(totalPages)].map((_, index) => (
            <li
              key={index}
              className={`page-item ${currentPage === index ? "active" : ""}`}
            >
              <button
                className="page-link"
                onClick={() => handlePageChange(index)}
              >
                {index + 1}
              </button>
            </li>
          ))}
          <li
            className={`page-item ${
              currentPage >= totalPages - 1 ? "disabled" : ""
            }`}
          >
            <button
              className="page-link"
              onClick={() => handlePageChange(currentPage + 1)}
            >
              Next
            </button>
          </li>
        </ul>
      </nav>

      {/* Modals for adding, editing, and syncing customers */}
      <AddCustomerModal
        show={showAddModal}
        handleClose={handleCloseModal}
        fetchCustomers={fetchCustomers}
      />
      <EditCustomerModal
        show={showEditModal}
        handleClose={handleCloseEditModal}
        customerId={selectedCustomerId}
        fetchCustomers={fetchCustomers}
      />
      <SyncModal
        show={showSyncModal}
        handleClose={handleSyncModalClose}
        handleSync={handleSync}
      />
    </div>
  );
};

export default CustomerManagement;
