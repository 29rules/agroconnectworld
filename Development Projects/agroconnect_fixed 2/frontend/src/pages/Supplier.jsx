import React, { useState } from 'react';
import RegistrationModal from '../components/registration/RegistrationModal';

export default function Supplier() {
    const [isRegistrationOpen, setIsRegistrationOpen] = useState(false);

    const benefits = [
        {
            icon: '🌐',
            title: 'Global Reach',
            description: 'Reach buyers from around the world and expand your market.'
        },
        {
            icon: '📈',
            title: 'Increase Sales',
            description: 'Access a large network of verified buyers looking for your products.'
        },
        {
            icon: '💼',
            title: 'Business Tools',
            description: 'Manage your inventory, orders, and customer relationships efficiently.'
        },
        {
            icon: '🔒',
            title: 'Secure Platform',
            description: 'Safe and secure transactions with verified buyers.'
        },
        {
            icon: '📊',
            title: 'Analytics Dashboard',
            description: 'Track your performance with detailed analytics and insights.'
        },
        {
            icon: '🎯',
            title: 'Targeted Marketing',
            description: 'Get your products in front of the right buyers at the right time.'
        }
    ];

    return (
        <div style={{ paddingTop: '100px' }}>
            {/* Hero Section */}
            <section className="text-center text-white py-5" style={{ 
                background: 'linear-gradient(135deg, var(--agro-green-light) 0%, var(--agro-green) 100%)',
                paddingTop: '80px',
                paddingBottom: '80px'
            }}>
                <div className="container">
                    <h1 className="display-4 fw-bold mb-4">Join as Supplier</h1>
                    <p className="lead mb-4" style={{ maxWidth: '700px', margin: '0 auto' }}>
                        Showcase your products to a global network of buyers and grow your business with 
                        AgroConnectWorld's powerful B2B platform.
                    </p>
                    <button 
                        className="btn btn-light btn-lg px-5 py-3 fw-bold"
                        onClick={() => setIsRegistrationOpen(true)}
                    >
                        Register as Supplier
                    </button>
                </div>
            </section>

            {/* Benefits Section */}
            <section className="py-5">
                <div className="container">
                    <h2 className="text-center mb-5 fw-bold">Why Join as a Supplier?</h2>
                    <div className="row g-4">
                        {benefits.map((benefit, index) => (
                            <div key={index} className="col-md-4">
                                <div className="card h-100 border-0 shadow-sm text-center">
                                    <div className="card-body p-4">
                                        <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>{benefit.icon}</div>
                                        <h4 className="card-title fw-bold">{benefit.title}</h4>
                                        <p className="card-text text-muted">{benefit.description}</p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* How It Works */}
            <section className="py-5 bg-light">
                <div className="container">
                    <h2 className="text-center mb-5 fw-bold">How It Works</h2>
                    <div className="row g-4">
                        <div className="col-md-3 text-center">
                            <div className="mb-3" style={{ fontSize: '3rem' }}>1️⃣</div>
                            <h5 className="fw-bold">Register</h5>
                            <p className="text-muted">Create your supplier account and verify your business</p>
                        </div>
                        <div className="col-md-3 text-center">
                            <div className="mb-3" style={{ fontSize: '3rem' }}>2️⃣</div>
                            <h5 className="fw-bold">List Products</h5>
                            <p className="text-muted">Add your products with detailed descriptions and images</p>
                        </div>
                        <div className="col-md-3 text-center">
                            <div className="mb-3" style={{ fontSize: '3rem' }}>3️⃣</div>
                            <h5 className="fw-bold">Get Orders</h5>
                            <p className="text-muted">Receive orders and quote requests from verified buyers</p>
                        </div>
                        <div className="col-md-3 text-center">
                            <div className="mb-3" style={{ fontSize: '3rem' }}>4️⃣</div>
                            <h5 className="fw-bold">Grow Business</h5>
                            <p className="text-muted">Expand your reach and grow your customer base</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="py-5 text-white text-center" style={{ 
                background: 'linear-gradient(135deg, var(--charcoal-grey) 0%, #1a1a1a 100%)'
            }}>
                <div className="container">
                    <h2 className="mb-4 fw-bold">Ready to Grow Your Business?</h2>
                    <p className="lead mb-4">Join thousands of suppliers who trust AgroConnectWorld to connect with buyers worldwide.</p>
                    <button 
                        className="btn btn-success btn-lg px-5 py-3 fw-bold"
                        onClick={() => setIsRegistrationOpen(true)}
                    >
                        Register Now
                    </button>
                </div>
            </section>

            {/* Registration Modal */}
            <RegistrationModal
                isOpen={isRegistrationOpen}
                onClose={() => setIsRegistrationOpen(false)}
                type="supplier"
            />
        </div>
    );
}
