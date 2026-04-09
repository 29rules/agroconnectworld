import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import RegistrationModal from '../components/registration/RegistrationModal';

export default function Home() {
    const [isBuyerModalOpen, setIsBuyerModalOpen] = useState(false);
    const [isSupplierModalOpen, setIsSupplierModalOpen] = useState(false);

    const productCategories = [
        { 
            name: 'FROZEN FOOD', 
            icon: '❄️',
            category: 'Frozen Food'
        },
        { 
            name: 'DRIED FOOD', 
            icon: '🌾',
            category: 'Dried Food'
        },
        { 
            name: 'BEVERAGE', 
            icon: '🥤',
            category: 'Beverage'
        },
        { 
            name: 'SEASONINGS & INGREDIENTS', 
            icon: '🧂',
            category: 'Seasonings & Ingredients'
        },
        { 
            name: 'READY TO COOK:EAT:DRINK', 
            icon: '🍽️',
            category: 'Ready to Cook/Eat/Drink'
        },
        { 
            name: 'SNACK & DESSERT', 
            icon: '🍰',
            category: 'Snack & Dessert'
        },
        { 
            name: 'FRESH FOOD', 
            icon: '🥬',
            category: 'Fresh Food'
        },
        { 
            name: 'DAILY FOOD PRODUCTS', 
            icon: '🥛',
            category: 'Daily Food Products'
        }
    ];

    const newsItems = [
        {
            title: 'AgroConnectWorld Partners with Local Farmers to Launch Premium Products Globally',
            excerpt: 'We are excited to announce new partnerships that bring authentic agricultural products to international markets.',
            link: '#'
        },
        {
            title: 'New Supplier Joins AgroConnectWorld to Bring Finest Products to the World',
            excerpt: 'Expanding our network of trusted suppliers to offer more variety and quality to our buyers worldwide.',
            link: '#'
        },
        {
            title: 'Global Trade Expansion: Premium Products Now Available for Export',
            excerpt: 'Our platform now supports exports to multiple countries, making it easier for buyers to source quality products.',
            link: '#'
        },
        {
            title: 'Fresh Products Launch: Premium Quality Agricultural Products Go Global',
            excerpt: 'Discover our latest catalog of fresh, high-quality products available for immediate shipment.',
            link: '#'
        }
    ];

    return (
        <div>
            {/* Hero Section */}
            <section className="hero-section text-center text-white py-5" style={{ 
                background: 'linear-gradient(135deg, var(--agro-green-light) 0%, var(--agro-green) 100%)',
                paddingTop: '100px',
                paddingBottom: '80px',
                position: 'relative',
                zIndex: 1
            }}>
                <div className="container">
                    <p className="text-uppercase mb-3" style={{ letterSpacing: '2px', fontSize: '14px', opacity: 0.9 }}>
                        WELCOME TO AGROCONNECT WORLD
                    </p>
                    <h1 className="display-3 fw-bold mb-4">Empowering Agri-Food Excellence</h1>
                    <p className="lead mb-5" style={{ maxWidth: '700px', margin: '0 auto' }}>
                        AgroConnectWorld is a business platform connecting farmers, food manufacturers, exporters, 
                        importers, distributors, wholesalers, retailers, supermarkets, restaurants, and bulk food buyers.
                    </p>
                    <div className="d-flex gap-3 justify-content-center flex-wrap" style={{ position: 'relative', zIndex: 1 }}>
                        <button 
                            type="button"
                            className="btn btn-light btn-lg px-5 py-3 fw-bold"
                            onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                setIsBuyerModalOpen(true);
                            }}
                            style={{ cursor: 'pointer' }}
                        >
                            Join as Buyer
                        </button>
                        <button 
                            type="button"
                            className="btn btn-outline-light btn-lg px-5 py-3 fw-bold"
                            onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                setIsSupplierModalOpen(true);
                            }}
                            style={{ cursor: 'pointer' }}
                        >
                            Join as Supplier
                        </button>
                    </div>
                </div>
            </section>

            {/* Industry Section */}
            <section className="py-5 bg-light">
                <div className="container">
                    <h2 className="text-center mb-5 fw-bold">YOUR INDUSTRY</h2>
                    <h3 className="text-center mb-4 text-muted">I AM SEEKING FOOD & AGRICULTURE PRODUCTS:</h3>
                    
                    <div className="row g-4">
                        <div className="col-md-4">
                            <div className="card h-100 shadow-sm border-0">
                                <div className="card-body p-4">
                                    <h4 className="card-title fw-bold mb-3">For Food Producer & Manufacturer</h4>
                                    <p className="card-text text-muted">
                                        Place bulk orders of food ingredients directly from trusted manufacturers or farmers 
                                        to ensure the continuity of your production.
                                    </p>
                                    <Link to="/industry" className="btn btn-success mt-3">Explore More</Link>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="card h-100 shadow-sm border-0">
                                <div className="card-body p-4">
                                    <h4 className="card-title fw-bold mb-3">For Distributor & Wholesaler</h4>
                                    <p className="card-text text-muted">
                                        Food and Agricultural Product Offerings: Food, beverages, ingredients, and OEM services 
                                        tailored to meet your brand's specifications.
                                    </p>
                                    <Link to="/industry" className="btn btn-success mt-3">Explore More</Link>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="card h-100 shadow-sm border-0">
                                <div className="card-body p-4">
                                    <h4 className="card-title fw-bold mb-3">For Restaurant, Hotel & Store</h4>
                                    <p className="card-text text-muted">
                                        Access high-quality ingredients, including organic, halal, and specialty items, 
                                        that meet demanding culinary standards.
                                    </p>
                                    <Link to="/industry" className="btn btn-success mt-3">Explore More</Link>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Featured Categories */}
            <section className="py-5">
                <div className="container">
                    <h2 className="text-center mb-5 fw-bold">FEATURED CATEGORIES</h2>
                    <div className="row g-4">
                        {productCategories.map((category, index) => (
                            <div key={index} className="col-md-3 col-sm-6">
                                <Link 
                                    to={`/products?category=${encodeURIComponent(category.category)}`}
                                    className="text-decoration-none"
                                    style={{ color: 'inherit' }}
                                >
                                    <div className="card h-100 border-0 shadow-sm text-center category-card" style={{ 
                                        transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                                        cursor: 'pointer'
                                    }}
                                    onMouseEnter={(e) => {
                                        e.currentTarget.style.transform = 'translateY(-5px)';
                                        e.currentTarget.style.boxShadow = '0 8px 16px rgba(0,0,0,0.15)';
                                    }}
                                    onMouseLeave={(e) => {
                                        e.currentTarget.style.transform = 'translateY(0)';
                                        e.currentTarget.style.boxShadow = '';
                                    }}>
                                        <div className="card-body p-4">
                                            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>{category.icon}</div>
                                            <h5 className="card-title fw-bold">{category.name}</h5>
                                        </div>
                                    </div>
                                </Link>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Latest News & Updates */}
            <section className="py-5 bg-light">
                <div className="container">
                    <h2 className="text-center mb-5 fw-bold">LATEST NEWS & UPDATES</h2>
                    <div className="row g-4">
                        {newsItems.map((news, index) => (
                            <div key={index} className="col-md-6 col-lg-3">
                                <div className="card h-100 border-0 shadow-sm">
                                    <div className="card-body">
                                        <h5 className="card-title fw-bold">{news.title}</h5>
                                        <p className="card-text text-muted small">{news.excerpt}</p>
                                        <Link to={news.link} className="btn btn-link p-0 text-success text-decoration-none">
                                            Read More »
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="py-5 text-white text-center" style={{ 
                background: 'linear-gradient(135deg, var(--charcoal-grey) 0%, #1a1a1a 100%)'
            }}>
                <div className="container">
                    <h2 className="mb-4 fw-bold">Join us today and help us build a better food and agriculture system!</h2>
                    <p className="lead mb-4" style={{ maxWidth: '800px', margin: '0 auto' }}>
                        The digital world is changing at a rapid pace and businesses must not only be able to adapt, 
                        but also thrive in this constantly evolving marketplace. Our mission is to connect buyers and 
                        suppliers of food and agricultural products to create a more efficient, sustainable, and secure 
                        global food and agriculture system.
                    </p>
                    <button 
                        className="btn btn-success btn-lg px-5 py-3 fw-bold"
                        onClick={() => setIsSupplierModalOpen(true)}
                    >
                        Join as Supplier
                    </button>
                </div>
            </section>

            {/* Registration Modals */}
            <RegistrationModal
                isOpen={isBuyerModalOpen}
                onClose={() => setIsBuyerModalOpen(false)}
                type="buyer"
            />
            <RegistrationModal
                isOpen={isSupplierModalOpen}
                onClose={() => setIsSupplierModalOpen(false)}
                type="supplier"
            />
        </div>
    );
}
