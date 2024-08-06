// src/components/EditCustomerModal.jsx
import React, { useState, useEffect } from "react";
import axios from "axios";
import { Modal, Button, Form } from "react-bootstrap";

// Component for editing customer details in a modal
const EditCustomerModal = ({
  show,
  handleClose,
  customerId,
  fetchCustomers,
}) => {
  // State to hold customer details
  const [customer, setCustomer] = useState({
    firstName: "",
    lastName: "",
    street: "",
    address: "",
    city: "",
    state: "",
    email: "",
    phone: "",
  });

  // Effect to fetch customer data when customerId changes
  useEffect(() => {
    if (customerId) {
      fetchCustomerData(customerId);
    }
  }, [customerId]);

  // Function to fetch customer data from the API
  const fetchCustomerData = async (id) => {
    try {
      // Get the JWT token from local storage
      const token = localStorage.getItem("token");

      // Make the GET request to fetch customer data
      const response = await axios.get(
        `http://localhost:8080/api/customers/${id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`, // Include JWT token in the Authorization header
            "Content-Type": "application/json",
          },
        }
      );

      // Set customer data to state
      setCustomer(response.data);
    } catch (error) {
      console.error("Error fetching customer data:", error);
    }
  };

  // Function to handle input field changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setCustomer({ ...customer, [name]: value }); // Update specific field in state
  };

  // Function to handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent default form submission
    try {
      // Retrieve the token from localStorage
      const token = localStorage.getItem("token");

      // Check if token exists
      if (!token) {
        throw new Error("No token found in localStorage");
      }

      // Send a PUT request to update the customer
      await axios.put(
        `http://localhost:8080/api/customers/${customerId}`,
        customer,
        {
          headers: {
            Authorization: `Bearer ${token}`, // Include JWT token in the Authorization header
            "Content-Type": "application/json",
          },
        }
      );

      fetchCustomers(); // Refresh the customer list after update
      handleClose(); // Close the modal
      alert("Customer updated successfully."); // Notify user of success
    } catch (error) {
      console.error("Error updating customer:", error);
      alert("Failed to update customer."); // Notify user of failure
    }
  };

  return (
    <Modal show={show} onHide={handleClose}>
      <Modal.Header closeButton>
        <Modal.Title>Edit Customer</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={handleSubmit}>
          {/* Form fields for editing customer details */}
          <Form.Group className="mb-3">
            <Form.Label>First Name</Form.Label>
            <Form.Control
              type="text"
              name="firstName"
              value={customer.firstName}
              onChange={handleInputChange}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Last Name</Form.Label>
            <Form.Control
              type="text"
              name="lastName"
              value={customer.lastName}
              onChange={handleInputChange}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Street</Form.Label>
            <Form.Control
              type="text"
              name="street"
              value={customer.street}
              onChange={handleInputChange}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Address</Form.Label>
            <Form.Control
              type="text"
              name="address"
              value={customer.address}
              onChange={handleInputChange}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>City</Form.Label>
            <Form.Control
              type="text"
              name="city"
              value={customer.city}
              onChange={handleInputChange}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>State</Form.Label>
            <Form.Control
              type="text"
              name="state"
              value={customer.state}
              onChange={handleInputChange}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Email</Form.Label>
            <Form.Control
              type="email"
              name="email"
              value={customer.email}
              onChange={handleInputChange}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Phone</Form.Label>
            <Form.Control
              type="text"
              name="phone"
              value={customer.phone}
              onChange={handleInputChange}
            />
          </Form.Group>
          <Button variant="primary" type="submit">
            Update Customer
          </Button>{" "}
          {/* Submit button */}
        </Form>
      </Modal.Body>
    </Modal>
  );
};

export default EditCustomerModal;
