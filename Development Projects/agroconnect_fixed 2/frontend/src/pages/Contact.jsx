import React, { useState } from 'react';

export default function Contact() {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        company: '',
        subject: '',
        message: '',
        inquiryType: 'general'
    });

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitStatus, setSubmitStatus] = useState(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        setSubmitStatus(null);

        // Simulate form submission
        setTimeout(() => {
            setIsSubmitting(false);
            setSubmitStatus('success');
            setFormData({
                name: '',
                email: '',
                phone: '',
                company: '',
                subject: '',
                message: '',
                inquiryType: 'general'
            });
            
            // Reset success message after 5 seconds
            setTimeout(() => {
                setSubmitStatus(null);
            }, 5000);
        }, 1500);
    };

    return (
        <div style={{ paddingTop: '100px' }}>
            {/* Hero Section */}
            <section className="text-center text-white py-5" style={{ 
                background: 'linear-gradient(135deg, var(--agro-green-light) 0%, var(--agro-green) 100%)',
                paddingTop: '80px',
                paddingBottom: '80px'
            }}>
                <div className="container">
                    <h1 className="display-4 fw-bold mb-4">CONTACT US</h1>
                    <p className="lead mb-0" style={{ maxWidth: '700px', margin: '0 auto' }}>
                        Have questions or want to learn more about AgroConnectWorld? 
                        Get in touch with our team. We're here to help.
                    </p>
                </div>
            </section>

            {/* Contact Form and Information Section */}
            <section className="py-5">
                <div className="container">
                    <div className="row g-5">
                        {/* Contact Form */}
                        <div className="col-lg-8">
                            <h2 className="fw-bold mb-4">Send Us a Message</h2>
                            
                            {submitStatus === 'success' && (
                                <div className="alert alert-success alert-dismissible fade show" role="alert">
                                    <strong>Thank you!</strong> Your message has been sent successfully. We'll get back to you soon.
                                    <button 
                                        type="button" 
                                        className="btn-close" 
                                        onClick={() => setSubmitStatus(null)}
                                        aria-label="Close"
                                    ></button>
                                </div>
                            )}

                            <form onSubmit={handleSubmit}>
                                <div className="row g-3 mb-3">
                                    <div className="col-md-6">
                                        <label htmlFor="name" className="form-label fw-bold">
                                            Full Name <span className="text-danger">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="name"
                                            name="name"
                                            value={formData.name}
                                            onChange={handleChange}
                                            required
                                            placeholder="Enter your full name"
                                        />
                                    </div>
                                    <div className="col-md-6">
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
                                </div>

                                <div className="row g-3 mb-3">
                                    <div className="col-md-6">
                                        <label htmlFor="phone" className="form-label fw-bold">
                                            Phone Number
                                        </label>
                                        <input
                                            type="tel"
                                            className="form-control"
                                            id="phone"
                                            name="phone"
                                            value={formData.phone}
                                            onChange={handleChange}
                                            placeholder="+1 (555) 123-4567"
                                        />
                                    </div>
                                    <div className="col-md-6">
                                        <label htmlFor="company" className="form-label fw-bold">
                                            Company Name
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="company"
                                            name="company"
                                            value={formData.company}
                                            onChange={handleChange}
                                            placeholder="Your company name"
                                        />
                                    </div>
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="inquiryType" className="form-label fw-bold">
                                        Inquiry Type <span className="text-danger">*</span>
                                    </label>
                                    <select
                                        className="form-select"
                                        id="inquiryType"
                                        name="inquiryType"
                                        value={formData.inquiryType}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="general">General Inquiry</option>
                                        <option value="buyer">I'm a Buyer</option>
                                        <option value="supplier">I'm a Supplier</option>
                                        <option value="partnership">Partnership Opportunity</option>
                                        <option value="support">Technical Support</option>
                                        <option value="other">Other</option>
                                    </select>
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="subject" className="form-label fw-bold">
                                        Subject <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="subject"
                                        name="subject"
                                        value={formData.subject}
                                        onChange={handleChange}
                                        required
                                        placeholder="What is your inquiry about?"
                                    />
                                </div>

                                <div className="mb-4">
                                    <label htmlFor="message" className="form-label fw-bold">
                                        Message <span className="text-danger">*</span>
                                    </label>
                                    <textarea
                                        className="form-control"
                                        id="message"
                                        name="message"
                                        rows="6"
                                        value={formData.message}
                                        onChange={handleChange}
                                        required
                                        placeholder="Please provide details about your inquiry..."
                                    ></textarea>
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-success btn-lg px-5 py-3 fw-bold"
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                            Sending...
                                        </>
                                    ) : (
                                        'Send Message'
                                    )}
                                </button>
                            </form>
                        </div>

                        {/* Contact Information */}
                        <div className="col-lg-4">
                            <div className="card border-0 shadow-sm h-100">
                                <div className="card-body p-4">
                                    <h3 className="fw-bold mb-4">Get in Touch</h3>
                                    
                                    <div className="mb-4">
                                        <h5 className="fw-bold mb-3" style={{ color: 'var(--agro-green-light)' }}>
                                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                <path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z"/>
                                            </svg>
                                            Office Address
                                        </h5>
                                        <p className="text-muted mb-0">
                                            AgroConnectWorld Company Limited<br />
                                            141/63 Skulthai Surawong Tower, 40th Floor<br />
                                            Bangrak, Bangkok 10500<br />
                                            Thailand
                                        </p>
                                    </div>

                                    <div className="mb-4">
                                        <h5 className="fw-bold mb-3" style={{ color: 'var(--agro-green-light)' }}>
                                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                <path d="M0 4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V4Zm2-1a1 1 0 0 0-1 1v.217l7 4.2 7-4.2V4a1 1 0 0 0-1-1H2Zm13 2.383-4.708 2.825L15 11.105V5.383Zm-.034 6.867-5.196-3.124L2 9.105v5.278a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V9.105l-3.034 1.145Z"/>
                                            </svg>
                                            Email
                                        </h5>
                                        <p className="text-muted mb-0">
                                            <a href="mailto:inquiry@agroconnectworld.com" className="text-decoration-none">
                                                inquiry@agroconnectworld.com
                                            </a>
                                        </p>
                                    </div>

                                    <div className="mb-4">
                                        <h5 className="fw-bold mb-3" style={{ color: 'var(--agro-green-light)' }}>
                                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                <path d="M3.654 1.328a.678.678 0 0 0-1.015-.063L1.605 2.3c-.483.484-.661 1.169-.45 1.77a17.568 17.568 0 0 0 4.168 6.608 17.569 17.569 0 0 0 6.608 4.168c.601.211 1.286.033 1.77-.45l1.034-1.034a.678.678 0 0 0-.063-1.015l-2.307-1.794a.678.678 0 0 0-.58-.122L9.78 10.5a.678.678 0 0 1-.564-.122l-2.307-1.794a.678.678 0 0 0-.58-.122L4.5 9.5a.678.678 0 0 1-.564-.122l-2.307-1.794a.678.678 0 0 0-.58-.122l-1.034 1.034a.678.678 0 0 0-.063 1.015l2.307 1.794a.678.678 0 0 0 .58.122l1.034-1.034a.678.678 0 0 1 .564.122l2.307 1.794a.678.678 0 0 0 .58.122l1.034-1.034a.678.678 0 0 1 .564.122l2.307 1.794a.678.678 0 0 0 .58.122l1.034-1.034a.678.678 0 0 1 .564.122l2.307 1.794a.678.678 0 0 0 .58.122l1.034-1.034a.678.678 0 0 0-.063-1.015L3.654 1.328z"/>
                                            </svg>
                                            Phone
                                        </h5>
                                        <p className="text-muted mb-0">
                                            <a href="tel:+66123456789" className="text-decoration-none">
                                                +66 (0) 1234 5678
                                            </a>
                                        </p>
                                    </div>

                                    <div className="mb-4">
                                        <h5 className="fw-bold mb-3" style={{ color: 'var(--agro-green-light)' }}>
                                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                <path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 1-.5.5H4.5a.5.5 0 0 0 0 1h3A1.5 1.5 0 0 0 8.5 9V3.5z"/>
                                                <path d="M8 2a6 6 0 1 0 0 12A6 6 0 0 0 8 2zM0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8z"/>
                                            </svg>
                                            Business Hours
                                        </h5>
                                        <p className="text-muted mb-0">
                                            Monday - Friday: 9:00 AM - 6:00 PM<br />
                                            Saturday: 10:00 AM - 4:00 PM<br />
                                            Sunday: Closed
                                        </p>
                                    </div>

                                    <div>
                                        <h5 className="fw-bold mb-3" style={{ color: 'var(--agro-green-light)' }}>
                                            Follow Us
                                        </h5>
                                        <div className="d-flex gap-3">
                                            <a 
                                                href="#" 
                                                className="text-decoration-none"
                                                style={{ 
                                                    width: '40px', 
                                                    height: '40px', 
                                                    display: 'flex', 
                                                    alignItems: 'center', 
                                                    justifyContent: 'center',
                                                    backgroundColor: 'var(--agro-green-light)',
                                                    borderRadius: '50%',
                                                    color: 'white',
                                                    transition: 'all 0.3s ease'
                                                }}
                                                onMouseEnter={(e) => {
                                                    e.currentTarget.style.backgroundColor = 'var(--agro-green)';
                                                    e.currentTarget.style.transform = 'translateY(-3px)';
                                                }}
                                                onMouseLeave={(e) => {
                                                    e.currentTarget.style.backgroundColor = 'var(--agro-green-light)';
                                                    e.currentTarget.style.transform = 'translateY(0)';
                                                }}
                                                aria-label="Facebook"
                                            >
                                                <svg width="18" height="18" fill="currentColor" viewBox="0 0 24 24">
                                                    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                                                </svg>
                                            </a>
                                            <a 
                                                href="#" 
                                                className="text-decoration-none"
                                                style={{ 
                                                    width: '40px', 
                                                    height: '40px', 
                                                    display: 'flex', 
                                                    alignItems: 'center', 
                                                    justifyContent: 'center',
                                                    backgroundColor: 'var(--agro-green-light)',
                                                    borderRadius: '50%',
                                                    color: 'white',
                                                    transition: 'all 0.3s ease'
                                                }}
                                                onMouseEnter={(e) => {
                                                    e.currentTarget.style.backgroundColor = 'var(--agro-green)';
                                                    e.currentTarget.style.transform = 'translateY(-3px)';
                                                }}
                                                onMouseLeave={(e) => {
                                                    e.currentTarget.style.backgroundColor = 'var(--agro-green-light)';
                                                    e.currentTarget.style.transform = 'translateY(0)';
                                                }}
                                                aria-label="LinkedIn"
                                            >
                                                <svg width="18" height="18" fill="currentColor" viewBox="0 0 24 24">
                                                    <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                                                </svg>
                                            </a>
                                            <a 
                                                href="#" 
                                                className="text-decoration-none"
                                                style={{ 
                                                    width: '40px', 
                                                    height: '40px', 
                                                    display: 'flex', 
                                                    alignItems: 'center', 
                                                    justifyContent: 'center',
                                                    backgroundColor: 'var(--agro-green-light)',
                                                    borderRadius: '50%',
                                                    color: 'white',
                                                    transition: 'all 0.3s ease'
                                                }}
                                                onMouseEnter={(e) => {
                                                    e.currentTarget.style.backgroundColor = 'var(--agro-green)';
                                                    e.currentTarget.style.transform = 'translateY(-3px)';
                                                }}
                                                onMouseLeave={(e) => {
                                                    e.currentTarget.style.backgroundColor = 'var(--agro-green-light)';
                                                    e.currentTarget.style.transform = 'translateY(0)';
                                                }}
                                                aria-label="Instagram"
                                            >
                                                <svg width="18" height="18" fill="currentColor" viewBox="0 0 24 24">
                                                    <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
                                                </svg>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}
