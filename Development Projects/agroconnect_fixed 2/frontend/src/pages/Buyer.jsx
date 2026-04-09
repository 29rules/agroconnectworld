import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import RegistrationModal from '../components/registration/RegistrationModal';

export default function Buyer() {
    const [isRegistrationOpen, setIsRegistrationOpen] = useState(false);

    const benefits = [
        {
            icon: '🌍',
            title: 'Global Network',
            description: 'Access a vast network of trusted suppliers from around the world.'
        },
        {
            icon: '💰',
            title: 'Competitive Pricing',
            description: 'Get the best prices with direct connections to manufacturers and farmers.'
        },
        {
            icon: '✅',
            title: 'Quality Assurance',
            description: 'All products are verified for quality and meet international standards.'
        },
        {
            icon: '🚚',
            title: 'Fast Shipping',
            description: 'Efficient logistics and shipping options for timely delivery.'
        },
        {
            icon: '📦',
            title: 'Bulk Orders',
            description: 'Special pricing and support for bulk and wholesale orders.'
        },
        {
            icon: '🤝',
            title: 'Dedicated Support',
            description: 'Personalized support team to help with your sourcing needs.'
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
                    <h1 className="display-4 fw-bold mb-4">Join as Buyer</h1>
                    <p className="lead mb-4" style={{ maxWidth: '700px', margin: '0 auto' }}>
                        Connect with trusted suppliers and access a wide range of food and agricultural products 
                        at competitive prices.
                    </p>
                    <button 
                        className="btn btn-light btn-lg px-5 py-3 fw-bold"
                        onClick={() => setIsRegistrationOpen(true)}
                    >
                        Register as Buyer
                    </button>
                </div>
            </section>

            {/* Benefits Section */}
            <section className="py-5">
                <div className="container">
                    <h2 className="text-center mb-5 fw-bold">Why Join as a Buyer?</h2>
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
                            <p className="text-muted">Create your buyer account in minutes</p>
                        </div>
                        <div className="col-md-3 text-center">
                            <div className="mb-3" style={{ fontSize: '3rem' }}>2️⃣</div>
                            <h5 className="fw-bold">Browse</h5>
                            <p className="text-muted">Explore thousands of products from verified suppliers</p>
                        </div>
                        <div className="col-md-3 text-center">
                            <div className="mb-3" style={{ fontSize: '3rem' }}>3️⃣</div>
                            <h5 className="fw-bold">Request Quote</h5>
                            <p className="text-muted">Get competitive quotes for your requirements</p>
                        </div>
                        <div className="col-md-3 text-center">
                            <div className="mb-3" style={{ fontSize: '3rem' }}>4️⃣</div>
                            <h5 className="fw-bold">Order</h5>
                            <p className="text-muted">Place orders and track shipments</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="py-5 text-white text-center" style={{ 
                background: 'linear-gradient(135deg, var(--charcoal-grey) 0%, #1a1a1a 100%)'
            }}>
                <div className="container">
                    <h2 className="mb-4 fw-bold">Ready to Get Started?</h2>
                    <p className="lead mb-4">Join thousands of buyers who trust AgroConnectWorld for their sourcing needs.</p>
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
                type="buyer"
            />
        </div>
    );
}
