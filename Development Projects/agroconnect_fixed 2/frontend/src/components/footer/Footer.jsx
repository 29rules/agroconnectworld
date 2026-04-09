import React from 'react';
import { Link } from 'react-router-dom';

export default function Footer() {
    return (
        <>
            <footer className="bg-dark text-white py-5 mt-5" style={{ backgroundColor: '#2B2B2B' }}>
                <div className="container">
                    {/* Main Footer Content - Two Columns */}
                    <div className="row g-4 mb-4">
                        {/* Left Column - Company Information */}
                        <div className="col-md-6">
                            <h5 className="fw-bold mb-3">AgroConnectWorld Company Limited</h5>
                            <p className="text-white-50 small mb-2">
                                <strong>Address:</strong><br />
                                141/63 Skulthai Surawong Tower, 40th Floor, Bangrak, Bangkok 10500 Thailand
                            </p>
                            <p className="text-white-50 small mb-3">
                                <strong>Email:</strong> inquiry@agroconnectworld.com
                            </p>
                            
                            {/* Social Media Icons */}
                            <div className="d-flex gap-3 mb-3">
                                <a 
                                    href="#" 
                                    className="text-white text-decoration-none"
                                    style={{ 
                                        width: '32px', 
                                        height: '32px', 
                                        display: 'flex', 
                                        alignItems: 'center', 
                                        justifyContent: 'center',
                                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                                        borderRadius: '4px',
                                        transition: 'all 0.3s ease'
                                    }}
                                    onMouseEnter={(e) => {
                                        e.currentTarget.style.backgroundColor = 'var(--agro-green-light)';
                                        e.currentTarget.style.transform = 'translateY(-2px)';
                                    }}
                                    onMouseLeave={(e) => {
                                        e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                                        e.currentTarget.style.transform = 'translateY(0)';
                                    }}
                                    aria-label="Facebook"
                                >
                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
                                        <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                                    </svg>
                                </a>
                                <a 
                                    href="#" 
                                    className="text-white text-decoration-none"
                                    style={{ 
                                        width: '32px', 
                                        height: '32px', 
                                        display: 'flex', 
                                        alignItems: 'center', 
                                        justifyContent: 'center',
                                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                                        borderRadius: '4px',
                                        transition: 'all 0.3s ease'
                                    }}
                                    onMouseEnter={(e) => {
                                        e.currentTarget.style.backgroundColor = 'var(--agro-green-light)';
                                        e.currentTarget.style.transform = 'translateY(-2px)';
                                    }}
                                    onMouseLeave={(e) => {
                                        e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                                        e.currentTarget.style.transform = 'translateY(0)';
                                    }}
                                    aria-label="LinkedIn"
                                >
                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
                                        <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                                    </svg>
                                </a>
                                <a 
                                    href="#" 
                                    className="text-white text-decoration-none"
                                    style={{ 
                                        width: '32px', 
                                        height: '32px', 
                                        display: 'flex', 
                                        alignItems: 'center', 
                                        justifyContent: 'center',
                                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                                        borderRadius: '4px',
                                        transition: 'all 0.3s ease'
                                    }}
                                    onMouseEnter={(e) => {
                                        e.currentTarget.style.backgroundColor = 'var(--agro-green-light)';
                                        e.currentTarget.style.transform = 'translateY(-2px)';
                                    }}
                                    onMouseLeave={(e) => {
                                        e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                                        e.currentTarget.style.transform = 'translateY(0)';
                                    }}
                                    aria-label="Instagram"
                                >
                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
                                        <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
                                    </svg>
                                </a>
                                <a 
                                    href="#" 
                                    className="text-white text-decoration-none"
                                    style={{ 
                                        width: '32px', 
                                        height: '32px', 
                                        display: 'flex', 
                                        alignItems: 'center', 
                                        justifyContent: 'center',
                                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                                        borderRadius: '4px',
                                        transition: 'all 0.3s ease'
                                    }}
                                    onMouseEnter={(e) => {
                                        e.currentTarget.style.backgroundColor = 'var(--agro-green-light)';
                                        e.currentTarget.style.transform = 'translateY(-2px)';
                                    }}
                                    onMouseLeave={(e) => {
                                        e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                                        e.currentTarget.style.transform = 'translateY(0)';
                                    }}
                                    aria-label="YouTube"
                                >
                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
                                        <path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/>
                                    </svg>
                                </a>
                            </div>
                        </div>

                        {/* Right Column - Company Description */}
                        <div className="col-md-6">
                            <p className="text-white-50 small mb-0">
                                AgroConnectWorld is an innovative business-to-business (B2B) marketplace that connects 
                                buyers and suppliers of food and agricultural products all around the world. AgroConnectWorld 
                                is a business platform connecting farmers, food manufacturers, exporters, importers, distributors, 
                                wholesalers, retailers, supermarkets, restaurants, and bulk food buyers. We offer a professional 
                                platform where businesses can access a wide range of products, from ingredients for food product 
                                development to cosmetics, medicine, and items for resale, wholesale, OEM, and other purposes.
                            </p>
                        </div>
                    </div>

                    {/* Bottom Navigation Links */}
                    <div className="row mb-3">
                        <div className="col-12">
                            <div className="d-flex flex-wrap gap-3">
                                <Link to="/about" className="text-white-50 text-decoration-none small">About</Link>
                                <Link to="/contact" className="text-white-50 text-decoration-none small">Contact</Link>
                                <Link to="#" className="text-white-50 text-decoration-none small">Terms of Use (For Buyer)</Link>
                                <Link to="#" className="text-white-50 text-decoration-none small">Terms of Use (Supplier)</Link>
                                <Link to="#" className="text-white-50 text-decoration-none small">Supplier Dashboard</Link>
                                <Link to="#" className="text-white-50 text-decoration-none small">Privacy Policy</Link>
                            </div>
                        </div>
                    </div>

                    {/* Disclaimer */}
                    <div className="mb-3">
                        <p className="small mb-0" style={{ color: 'var(--agro-green-light)' }}>
                            <strong>Disclaimer:</strong> The Platform functions solely as a transaction facilitator, connecting 
                            Buyers and Suppliers for B2B trade. We are explicitly not a principal in any resulting Transaction. 
                            We do not take ownership of, sell, or provide any guarantee or warranty regarding the goods or services 
                            offered by Suppliers. We shall not be liable for any indirect, incidental, consequential, or punitive damages 
                            (including, but not limited to, lost profits or data) arising from your use of the Marketplace or the Platform.
                        </p>
                    </div>

                    {/* Copyright */}
                    <div className="text-center">
                        <p className="text-white-50 small mb-0">
                            Copyright © {new Date().getFullYear()} AgroConnectWorld Company Limited
                        </p>
                    </div>
                </div>
            </footer>
        </>
    );
}
