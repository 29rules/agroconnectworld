import React, { useState } from 'react';

export default function RegistrationModal({ isOpen, onClose, type }) {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        company: '',
        country: '',
        businessType: '',
        password: '',
        confirmPassword: '',
        agreeToTerms: false
    });

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitStatus, setSubmitStatus] = useState(null);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (formData.password !== formData.confirmPassword) {
            setSubmitStatus('error');
            alert('Passwords do not match!');
            return;
        }

        if (!formData.agreeToTerms) {
            setSubmitStatus('error');
            alert('Please agree to the terms and conditions');
            return;
        }

        setIsSubmitting(true);
        setSubmitStatus(null);

        // Simulate form submission
        setTimeout(() => {
            setIsSubmitting(false);
            setSubmitStatus('success');
            
            // Reset form after 2 seconds and close modal
            setTimeout(() => {
                setFormData({
                    firstName: '',
                    lastName: '',
                    email: '',
                    phone: '',
                    company: '',
                    country: '',
                    businessType: '',
                    password: '',
                    confirmPassword: '',
                    agreeToTerms: false
                });
                setSubmitStatus(null);
                onClose();
                alert(`Thank you! Your ${type} registration has been submitted. We'll contact you soon!`);
            }, 2000);
        }, 1500);
    };

    if (!isOpen) return null;

    return (
        <>
            {/* Overlay */}
            <div
                className="modal-overlay"
                style={{
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    backgroundColor: 'rgba(0, 0, 0, 0.5)',
                    zIndex: 1040,
                    animation: 'fadeIn 0.3s ease'
                }}
                onClick={onClose}
            />

            {/* Modal */}
            <div
                className="modal-dialog modal-dialog-centered modal-dialog-scrollable"
                style={{
                    position: 'fixed',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    zIndex: 1050,
                    width: '90%',
                    maxWidth: '600px',
                    maxHeight: '90vh',
                    overflow: 'auto'
                }}
                onClick={(e) => e.stopPropagation()}
            >
                <div className="modal-content" style={{ borderRadius: '8px' }}>
                    {/* Modal Header */}
                    <div
                        className="modal-header text-white"
                        style={{
                            backgroundColor: 'var(--agro-green-light)',
                            borderTopLeftRadius: '8px',
                            borderTopRightRadius: '8px'
                        }}
                    >
                        <h3 className="modal-title fw-bold">
                            Register as {type === 'buyer' ? 'Buyer' : 'Supplier'}
                        </h3>
                        <button
                            type="button"
                            className="btn-close btn-close-white"
                            onClick={onClose}
                            aria-label="Close"
                        ></button>
                    </div>

                    {/* Modal Body */}
                    <div className="modal-body p-4">
                        {submitStatus === 'success' && (
                            <div className="alert alert-success" role="alert">
                                <strong>Success!</strong> Your registration has been submitted successfully.
                            </div>
                        )}

                        <form onSubmit={handleSubmit}>
                            <div className="row g-3 mb-3">
                                <div className="col-md-6">
                                    <label htmlFor="firstName" className="form-label fw-bold">
                                        First Name <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="firstName"
                                        name="firstName"
                                        value={formData.firstName}
                                        onChange={handleChange}
                                        required
                                        placeholder="Enter your first name"
                                    />
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="lastName" className="form-label fw-bold">
                                        Last Name <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="lastName"
                                        name="lastName"
                                        value={formData.lastName}
                                        onChange={handleChange}
                                        required
                                        placeholder="Enter your last name"
                                    />
                                </div>
                            </div>

                            <div className="mb-3">
                                <label htmlFor="email" className="form-label fw-bold">
                                    Email Address <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="email"
                                    className="form-control"
                                    id="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    required
                                    placeholder="your.email@example.com"
                                />
                            </div>

                            <div className="mb-3">
                                <label htmlFor="phone" className="form-label fw-bold">
                                    Phone Number <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="tel"
                                    className="form-control"
                                    id="phone"
                                    name="phone"
                                    value={formData.phone}
                                    onChange={handleChange}
                                    required
                                    placeholder="+1 (555) 123-4567"
                                />
                            </div>

                            <div className="mb-3">
                                <label htmlFor="company" className="form-label fw-bold">
                                    Company Name <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="company"
                                    name="company"
                                    value={formData.company}
                                    onChange={handleChange}
                                    required
                                    placeholder="Your company name"
                                />
                            </div>

                            <div className="row g-3 mb-3">
                                <div className="col-md-6">
                                    <label htmlFor="country" className="form-label fw-bold">
                                        Country <span className="text-danger">*</span>
                                    </label>
                                    <select
                                        className="form-select"
                                        id="country"
                                        name="country"
                                        value={formData.country}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="">Select Country</option>
                                        <option value="US">United States</option>
                                        <option value="CA">Canada</option>
                                        <option value="UK">United Kingdom</option>
                                        <option value="IN">India</option>
                                        <option value="TH">Thailand</option>
                                        <option value="CN">China</option>
                                        <option value="AU">Australia</option>
                                        <option value="DE">Germany</option>
                                        <option value="FR">France</option>
                                        <option value="other">Other</option>
                                    </select>
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="businessType" className="form-label fw-bold">
                                        Business Type <span className="text-danger">*</span>
                                    </label>
                                    <select
                                        className="form-select"
                                        id="businessType"
                                        name="businessType"
                                        value={formData.businessType}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="">Select Type</option>
                                        {type === 'buyer' ? (
                                            <>
                                                <option value="manufacturer">Food Manufacturer</option>
                                                <option value="distributor">Distributor</option>
                                                <option value="wholesaler">Wholesaler</option>
                                                <option value="retailer">Retailer</option>
                                                <option value="restaurant">Restaurant/Hotel</option>
                                                <option value="supermarket">Supermarket</option>
                                                <option value="other">Other</option>
                                            </>
                                        ) : (
                                            <>
                                                <option value="farmer">Farmer</option>
                                                <option value="manufacturer">Food Manufacturer</option>
                                                <option value="exporter">Exporter</option>
                                                <option value="processor">Food Processor</option>
                                                <option value="distributor">Distributor</option>
                                                <option value="other">Other</option>
                                            </>
                                        )}
                                    </select>
                                </div>
                            </div>

                            <div className="row g-3 mb-3">
                                <div className="col-md-6">
                                    <label htmlFor="password" className="form-label fw-bold">
                                        Password <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="password"
                                        className="form-control"
                                        id="password"
                                        name="password"
                                        value={formData.password}
                                        onChange={handleChange}
                                        required
                                        minLength="8"
                                        placeholder="Minimum 8 characters"
                                    />
                                </div>
                                <div className="col-md-6">
                                    <label htmlFor="confirmPassword" className="form-label fw-bold">
                                        Confirm Password <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="password"
                                        className="form-control"
                                        id="confirmPassword"
                                        name="confirmPassword"
                                        value={formData.confirmPassword}
                                        onChange={handleChange}
                                        required
                                        placeholder="Confirm your password"
                                    />
                                </div>
                            </div>

                            <div className="mb-4">
                                <div className="form-check">
                                    <input
                                        className="form-check-input"
                                        type="checkbox"
                                        id="agreeToTerms"
                                        name="agreeToTerms"
                                        checked={formData.agreeToTerms}
                                        onChange={handleChange}
                                        required
                                    />
                                    <label className="form-check-label" htmlFor="agreeToTerms">
                                        I agree to the <a href="#" className="text-success">Terms and Conditions</a> and <a href="#" className="text-success">Privacy Policy</a> <span className="text-danger">*</span>
                                    </label>
                                </div>
                            </div>

                            <div className="d-grid gap-2">
                                <button
                                    type="submit"
                                    className="btn btn-success btn-lg fw-bold"
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                            Submitting...
                                        </>
                                    ) : (
                                        `Register as ${type === 'buyer' ? 'Buyer' : 'Supplier'}`
                                    )}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={onClose}
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </>
    );
}



