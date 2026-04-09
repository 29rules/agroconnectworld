import React, { useState } from 'react';
import { Link } from 'react-router-dom';

export default function NewsUpdates() {
    const [activeCategory, setActiveCategory] = useState('latest-news');

    const categories = [
        { id: 'latest-news', label: 'Latest News' },
        { id: 'sourcing-tips', label: 'Sourcing & Purchasing Tips' },
        { id: 'international-trade', label: 'International Trade' },
        { id: 'b2b-marketing', label: 'B2B E-commerce & Marketing' }
    ];

    const industryNews = [
        {
            title: 'AgroConnectWorld Partners with Local Farmers to Expand Global Reach',
            category: 'Channel Development',
            description: 'Discover how AgroConnectWorld is connecting local farmers with international markets through strategic partnerships and innovative distribution channels.',
            link: '#'
        },
        {
            title: 'Exclusive Interview with Leading Agricultural Export Specialist',
            description: 'Welcome to an exclusive interview with industry experts who specialize in producing and distributing premium agricultural products to global markets.',
            link: '#'
        },
        {
            title: 'AgroConnectWorld Partners with Regional Distributors to Expand Reach in Asian Markets',
            description: 'AgroConnectWorld is excited to announce its partnership with regional distributors of Asian fruits, dried fruits, organic products and eco-friendly goods.',
            link: '#'
        },
        {
            title: 'Elevate Your B2B e-Store with Our Premium On-Boarding Service at AgroConnectWorld',
            description: 'In the rapidly evolving marketplace of agricultural produce and food products, e-store making an impactful presence is key to successful business growth.',
            link: '#'
        },
        {
            title: 'AgroConnectWorld Partners with Premium Suppliers to Launch Authentic Products Globally',
            description: 'AgroConnectWorld, the premier online business-to-business (B2B) marketplace for food and agricultural products, is thrilled to announce new partnerships.',
            link: '#'
        },
        {
            title: 'Premium Farms Join AgroConnectWorld to Bring Finest Products to the World',
            description: 'Premium farms and producers from around the world have partnered with AgroConnectWorld to expand international distribution of high-quality products.',
            link: '#'
        },
        {
            title: 'Global Trade Expansion: A Premier Exporter of Premium Products to Global Markets',
            description: 'Leading producers and wholesalers are pleased to announce their expansion into global markets through AgroConnectWorld platform.',
            link: '#'
        },
        {
            title: 'Premium Products Launches on AgroConnectWorld: Quality Products Go Global',
            description: 'Premium suppliers are now offering their premier selection of products at AgroConnectWorld, expanding global reach for quality agricultural products.',
            link: '#'
        }
    ];

    const sourcingTips = [
        {
            title: 'Food Powders for Bakers: Boost Taste and Product Value',
            description: 'Baking has always been as much a science as it is an art. From nailing the chemistry of rising dough to balancing the flavors and textures of sweet treats, attention to quality ingredients makes all the difference.',
            link: '#'
        },
        {
            title: 'How to Find a Reliable Food Supplier for Wholesale Online',
            description: 'Are you a distributor or wholesaler seeking authentic products to enrich your product lineup? Finding reliable suppliers online offers unique opportunities for expanding your business.',
            link: '#'
        },
        {
            title: 'Exploring the Rich Flavors of Global Cuisine',
            description: 'Global cuisines stand out as hidden gems in the food industry. Rich in history, bursting with flavors, and crafted using diverse influences, these dishes offer truly unique gastronomic experiences.',
            link: '#'
        },
        {
            title: 'Starting a Restaurant Business Is Easier Than You Think',
            description: 'Opening a restaurant is no small feat, but for those dreaming of bringing unique flavors to life, exciting opportunities await. With proper sourcing and planning, your restaurant business can thrive.',
            link: '#'
        },
        {
            title: '5 Easy Ways to Use Premium Ingredients to Impress Customers',
            description: 'Premium ingredients are treasures of global cuisine, combining quality, flavor, and versatility into incredible products. Known for their bold flavors and versatility, they\'re must-haves for any successful food business.',
            link: '#'
        },
        {
            title: 'What\'s the Difference Between Organic and Conventional Products?',
            description: 'Understanding the differences between organic and conventional products is crucial for making informed purchasing decisions that align with your business values and customer preferences.',
            link: '#'
        },
        {
            title: 'Premium Ingredients: The Bold New Flavor Profile Your Business Needs',
            description: 'Global cuisine continues to captivate food lovers worldwide, and premium ingredients remain largely untapped in the commercial market. With complex blends of aromatic spices and authentic flavors, these ingredients can transform your menu.',
            link: '#'
        },
        {
            title: 'Seasonal Products That Will Transform Your Menu',
            description: 'Seasonal products drive revenue, and premium seasonal ingredients are your secret weapon for standing out in a crowded market. These products offer the perfect balance of quality, visual appeal, and customer satisfaction.',
            link: '#'
        }
    ];

    const internationalTrade = [
        {
            title: 'How to Penetrate a New Market in Another Country',
            description: 'Have you ever considered expanding your business to another country? If so, what\'s holding you back? Many businesses don\'t expand internationally because they\'re afraid of the unknown. But with the right strategy, international expansion can be highly rewarding.',
            link: '#'
        },
        {
            title: 'How to Ship Your Products Overseas',
            description: 'If you\'re looking to expand your business into new markets, you\'ll need to know how to ship your products overseas. Shipping products can be a complex process, but with the right knowledge and partners, it becomes manageable.',
            link: '#'
        },
        {
            title: 'Elevate Your Menu: Importing Food Products for Your Restaurant',
            description: 'In the world of gastronomy, sourcing high-quality food products is crucial for creating exceptional dining experiences. For restaurateurs, restaurant owners, and food service managers, importing food can open a world of possibilities.',
            link: '#'
        },
        {
            title: 'Where to Find International Buyers for Your Food Products',
            description: 'Are you a food manufacturer looking to take your business to the next level by finding international buyers for your products? Expanding into the global market can be a game-changer for your business growth.',
            link: '#'
        },
        {
            title: '3 Easy Steps to Get Started with Food Export Online',
            description: 'Venturing into the world of food export can be an exciting opportunity for entrepreneurs and businesses alike. The global demand for quality food and beverage products has grown significantly, offering new opportunities for growth.',
            link: '#'
        },
        {
            title: 'How Agricultural Manufacturers Can Navigate Currency Risk',
            description: 'Agricultural manufacturers rely heavily on international markets for growth. Exporting to foreign markets exposes businesses to significant currency risk. Without proactive FX risk management, currency fluctuations can erode profit margins and impact business sustainability.',
            link: '#'
        }
    ];

    const b2bMarketing = [
        {
            title: 'Building Your B2B Brand Online: Essential Strategies',
            description: 'In today\'s digital marketplace, building a strong B2B brand online is essential for success. Learn the key strategies that can help your agricultural business stand out and attract the right buyers.',
            link: '#'
        },
        {
            title: 'E-commerce Best Practices for Food and Agriculture Businesses',
            description: 'E-commerce has revolutionized how food and agriculture businesses connect with buyers. Discover best practices for creating an effective online presence that drives sales and builds trust.',
            link: '#'
        },
        {
            title: 'Digital Marketing Strategies for Agricultural Suppliers',
            description: 'Effective digital marketing can transform your agricultural supply business. Learn how to leverage online platforms, content marketing, and social media to reach your target audience and grow your business.',
            link: '#'
        }
    ];

    const getCurrentArticles = () => {
        switch (activeCategory) {
            case 'latest-news':
                return industryNews;
            case 'sourcing-tips':
                return sourcingTips;
            case 'international-trade':
                return internationalTrade;
            case 'b2b-marketing':
                return b2bMarketing;
            default:
                return industryNews;
        }
    };

    const getSectionTitle = () => {
        switch (activeCategory) {
            case 'latest-news':
                return 'THE INDUSTRY NEWS & UPDATES';
            case 'sourcing-tips':
                return 'SOURCING AND PURCHASING TIPS';
            case 'international-trade':
                return 'INTERNATIONAL TRADE';
            case 'b2b-marketing':
                return 'B2B E-COMMERCE & MARKETING';
            default:
                return 'THE INDUSTRY NEWS & UPDATES';
        }
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
                    <h1 className="display-4 fw-bold mb-4">THE LATEST NEWS AND UPDATES</h1>
                    <p className="lead mb-0" style={{ maxWidth: '800px', margin: '0 auto' }}>
                        The Latest news, innovation and knowhow of food and agriculture industry. 
                        Discover the cutting-edge advancements shaping the future of food and agriculture.
                    </p>
                </div>
            </section>

            {/* Category Navigation */}
            <section className="py-4 bg-light border-bottom">
                <div className="container">
                    <div className="d-flex flex-wrap gap-2 justify-content-center">
                        <Link 
                            to="/" 
                            className="btn btn-outline-secondary btn-sm"
                        >
                            Home
                        </Link>
                        {categories.map((category) => (
                            <button
                                key={category.id}
                                className={`btn btn-sm ${
                                    activeCategory === category.id 
                                        ? 'btn-success' 
                                        : 'btn-outline-secondary'
                                }`}
                                onClick={() => setActiveCategory(category.id)}
                            >
                                {category.label}
                            </button>
                        ))}
                    </div>
                </div>
            </section>

            {/* Articles Section */}
            <section className="py-5">
                <div className="container">
                    <h2 className="fw-bold mb-5 text-center">{getSectionTitle()}</h2>
                    
                    <div className="row g-4">
                        {getCurrentArticles().map((article, index) => (
                            <div key={index} className="col-md-6 col-lg-4">
                                <div className="card h-100 border-0 shadow-sm">
                                    <div className="card-body p-4">
                                        {article.category && (
                                            <p className="text-muted small mb-2 fw-bold">
                                                {article.category}
                                            </p>
                                        )}
                                        <h4 className="card-title fw-bold mb-3" style={{ fontSize: '1.1rem', minHeight: '60px' }}>
                                            {article.title}
                                        </h4>
                                        <p className="card-text text-muted small mb-3" style={{ minHeight: '80px' }}>
                                            {article.description}
                                        </p>
                                        <Link 
                                            to={article.link} 
                                            className="btn btn-link p-0 text-success text-decoration-none fw-bold"
                                        >
                                            Read More »
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Newsletter Subscription Section */}
            <section className="py-5 bg-light">
                <div className="container">
                    <div className="row justify-content-center">
                        <div className="col-lg-8 text-center">
                            <h3 className="fw-bold mb-3">Stay tuned</h3>
                            <p className="text-muted mb-4">
                                Subscribe to Our Newsletter. Get the latest news, expert insights, and exclusive offers 
                                delivered straight to your inbox.
                            </p>
                            <form className="row g-3 justify-content-center">
                                <div className="col-md-4">
                                    <input
                                        type="text"
                                        className="form-control"
                                        placeholder="Full Name*"
                                        required
                                    />
                                </div>
                                <div className="col-md-4">
                                    <input
                                        type="email"
                                        className="form-control"
                                        placeholder="Email*"
                                        required
                                    />
                                </div>
                                <div className="col-md-auto">
                                    <button type="submit" className="btn btn-success px-4">
                                        Submit
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}



