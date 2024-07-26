import React, { useState } from 'react';
import axios from 'axios';
import { Modal, Button, Form } from 'react-bootstrap';

// Modal component for adding a new customer
const AddCustomerModal = ({ show, handleClose, fetchCustomers }) => {
    // State to hold new customer form data
    const [newCustomer, setNewCustomer] = useState({
        firstName: '',
        lastName: '',
        street: '',
        address: '',
        city: '',
        state: '',
        email: '',
        phone: ''
    });

    // Handle input changes for the form fields
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewCustomer({ ...newCustomer, [name]: value });
    };

    // Handle form submission to add a new customer
    const handleSubmit = async (e) => {
        e.preventDefault(); // Prevent default form submission behavior
        try {
            // Send a POST request to add the new customer
            await axios.post('http://localhost:8080/api/customers', newCustomer);
            fetchCustomers(); // Refresh the customer list to include the new customer
            handleClose(); // Close the modal after successful addition
            alert('Customer added successfully.'); // Notify the user of success
        } catch (error) {
            console.error('Error adding customer:', error);
            alert('Failed to add customer.'); // Notify the user of failure
        }
    };

    return (
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Add Customer</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    {/* Form field for First Name */}
                    <Form.Group className="mb-3">
                        <Form.Label>First Name</Form.Label>
                        <Form.Control 
                            type="text" 
                            name="firstName" 
                            value={newCustomer.firstName} 
                            onChange={handleInputChange} 
                            required 
                        />
                    </Form.Group>

                    {/* Form field for Last Name */}
                    <Form.Group className="mb-3">
                        <Form.Label>Last Name</Form.Label>
                        <Form.Control 
                            type="text" 
                            name="lastName" 
                            value={newCustomer.lastName} 
                            onChange={handleInputChange} 
                            required 
                        />
                    </Form.Group>

                    {/* Form field for Street */}
                    <Form.Group className="mb-3">
                        <Form.Label>Street</Form.Label>
                        <Form.Control 
                            type="text" 
                            name="street" 
                            value={newCustomer.street} 
                            onChange={handleInputChange} 
                        />
                    </Form.Group>

                    {/* Form field for Address */}
                    <Form.Group className="mb-3">
                        <Form.Label>Address</Form.Label>
                        <Form.Control 
                            type="text" 
                            name="address" 
                            value={newCustomer.address} 
                            onChange={handleInputChange} 
                        />
                    </Form.Group>

                    {/* Form field for City */}
                    <Form.Group className="mb-3">
                        <Form.Label>City</Form.Label>
                        <Form.Control 
                            type="text" 
                            name="city" 
                            value={newCustomer.city} 
                            onChange={handleInputChange} 
                        />
                    </Form.Group>

                    {/* Form field for State */}
                    <Form.Group className="mb-3">
                        <Form.Label>State</Form.Label>
                        <Form.Control 
                            type="text" 
                            name="state" 
                            value={newCustomer.state} 
                            onChange={handleInputChange} 
                        />
                    </Form.Group>

                    {/* Form field for Email */}
                    <Form.Group className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control 
                            type="email" 
                            name="email" 
                            value={newCustomer.email} 
                            onChange={handleInputChange} 
                            required 
                        />
                    </Form.Group>

                    {/* Form field for Phone */}
                    <Form.Group className="mb-3">
                        <Form.Label>Phone</Form.Label>
                        <Form.Control 
                            type="text" 
                            name="phone" 
                            value={newCustomer.phone} 
                            onChange={handleInputChange} 
                        />
                    </Form.Group>

                    {/* Submit button to add customer */}
                    <Button variant="primary" type="submit">Add Customer</Button>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default AddCustomerModal;
