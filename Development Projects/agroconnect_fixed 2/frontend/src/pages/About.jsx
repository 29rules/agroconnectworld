import React from 'react';
import { Link } from 'react-router-dom';

export default function About() {
    const advantages = [
        {
            title: 'Global Connectivity for Diverse Stakeholders',
            description: 'It directly links a wide array of businesses, including farmers, food manufacturers, exporters, importers, distributors, wholesalers, retailers, supermarkets, restaurants, and bulk buyers, enabling seamless sourcing, trading, and collaboration across the supply chain.'
        },
        {
            title: 'Access to a Broad Product Range',
            description: 'Businesses can source diverse products, from ingredients for food product development to items for cosmetics, medicine, resale, wholesale, OEM, and other purposes, all through a single professional platform.'
        },
        {
            title: 'Overcoming Market Fragmentation',
            description: 'Addresses the challenges of a fragmented industry by helping buyers find reliable and specialized suppliers, including SMEs and local farmers, who are often overlooked in online searches dominated by large factories and agents.'
        },
        {
            title: 'Enhanced Visibility for Underserved Suppliers',
            description: 'Empowers talented farmers, producers, and smaller businesses with limited resources to gain global market access, increasing their online visibility and competitive opportunities.'
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
                    <p className="text-uppercase mb-3" style={{ letterSpacing: '2px', fontSize: '14px', opacity: 0.9 }}>
                        Empowering Agri-Food Excellence: Connect, Source, and Thrive
                    </p>
                    <h1 className="display-3 fw-bold mb-4">ABOUT AGROCONNECT WORLD</h1>
                    <Link to="/contact" className="btn btn-light btn-lg px-5 py-3 fw-bold mt-3">
                        Contact
                    </Link>
                </div>
            </section>

            {/* Company Overview Section */}
            <section className="py-5">
                <div className="container">
                    <div className="row">
                        <div className="col-lg-10 mx-auto">
                            <h2 className="fw-bold mb-4">COMPANY OVERVIEW</h2>
                            <p className="lead mb-4">
                                AgroConnectWorld is a global B2B marketplace connecting the food and agriculture industry. 
                                We empower businesses to source, trade, and grow through our innovative platform.
                            </p>
                            <p className="mb-4">
                                AgroConnectWorld is a business platform connecting farmers, food manufacturers, exporters, 
                                importers, distributors, wholesalers, retailers, supermarkets, restaurants, and bulk food buyers.
                            </p>
                            <p className="mb-0">
                                We offer a professional platform where businesses can access a wide range of products, from 
                                ingredients for food product development to cosmetics, medicine, and items for resale, wholesale, 
                                OEM, and other purposes.
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Our Story Section */}
            <section className="py-5 bg-light">
                <div className="container">
                    <div className="row">
                        <div className="col-lg-10 mx-auto">
                            <h2 className="fw-bold mb-4">OUR STORY</h2>
                            <h3 className="h4 mb-4" style={{ color: 'var(--agro-green-light)' }}>
                                Connecting Global Food & Agriculture
                            </h3>
                            
                            <p className="mb-4">
                                We recognized the fragmented nature of the market, where buyers struggled to find reliable suppliers, 
                                and talented farmers and producers lacked access to global markets. In a world increasingly interconnected, 
                                the need for efficient and sustainable food systems has never been greater.
                            </p>
                            
                            <p className="mb-4">
                                Recognizing this critical need, AgroConnectWorld was born with a vision to revolutionize the way the 
                                global food and agriculture industry operates.
                            </p>
                            
                            <p className="mb-4">
                                We observed significant challenges within the food and agriculture industry. Overseas buyers often faced 
                                limitations in sourcing, struggling to find and connect directly with suitable suppliers. Online searches 
                                frequently led them towards large factories and agents, making it difficult to discover more specialized 
                                suppliers like SMEs and local farmers. These businesses often lacked the online visibility and resources 
                                to effectively reach international markets, hindering their growth and impacting their ability to compete.
                            </p>
                            
                            <p className="mb-0">
                                Driven by a passion for connecting people and fostering sustainable practices, we envisioned a platform that 
                                would break down these barriers. This vision led to the creation of AgroConnectWorld, a platform designed 
                                to bridge these gaps and create a more equitable and efficient food supply chain.
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Key Advantages Section */}
            <section className="py-5">
                <div className="container">
                    <div className="row">
                        <div className="col-lg-10 mx-auto">
                            <h2 className="fw-bold mb-2">THE KEY ADVANTAGES</h2>
                            <h3 className="h4 mb-5" style={{ color: 'var(--agro-green-light)' }}>
                                AGROCONNECT WORLD: The B2B Food & Agriculture Business Platform
                            </h3>
                            
                            <div className="row g-4">
                                {advantages.map((advantage, index) => (
                                    <div key={index} className="col-md-6">
                                        <div className="card h-100 border-0 shadow-sm">
                                            <div className="card-body p-4">
                                                <h4 className="card-title fw-bold mb-3" style={{ color: 'var(--agro-green-light)' }}>
                                                    {advantage.title}
                                                </h4>
                                                <p className="card-text text-muted">
                                                    {advantage.description}
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Investor Section */}
            <section className="py-5 bg-light">
                <div className="container">
                    <div className="row">
                        <div className="col-lg-10 mx-auto text-center">
                            <h2 className="fw-bold mb-4">INVESTOR</h2>
                            <div className="mb-4">
                                <h4 className="mb-3" style={{ color: 'var(--agro-green-light)' }}>
                                    Accelerators & Incubators
                                </h4>
                                <p className="text-muted">
                                    AgroConnectWorld is supported by leading accelerators and incubators in the agri-food technology space, 
                                    enabling us to continuously innovate and expand our global reach.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Contact Us Section */}
            <section className="py-5 text-white text-center" style={{ 
                background: 'linear-gradient(135deg, var(--charcoal-grey) 0%, #1a1a1a 100%)'
            }}>
                <div className="container">
                    <h2 className="fw-bold mb-4">CONTACT US</h2>
                    <p className="lead mb-4" style={{ maxWidth: '700px', margin: '0 auto' }}>
                        Have questions or want to learn more about AgroConnectWorld? Get in touch with our team.
                    </p>
                    <Link to="/contact" className="btn btn-success btn-lg px-5 py-3 fw-bold">
                        Contact Us
                    </Link>
                </div>
            </section>
        </div>
    );
}
