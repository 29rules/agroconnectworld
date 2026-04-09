import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

export default function Industry() {
    const [searchParams] = useSearchParams();
    const industryType = searchParams.get('type');

    const industryContent = {
        'food-manufacturer': {
            title: 'Products For Food Manufacturer',
            description: 'Streamline your food and beverage manufacturing today by accessing fresh products from the farm and specialized ingredients from food factory suppliers. We connect you with direct manufacturers for both small and large-scale orders, ensuring genuine wholesale pricing.',
            products: [
                { name: 'TS-TIDE (Ribonucleotide I+G Substitute) 10 kg/carton', category: 'Seasonings & Ingredients' },
                { name: 'Thaifood-B (Whitener/Bleaching Agent) 25 kg/bag', category: 'Seasonings & Ingredients' },
                { name: 'Food Grade Sweet Potato Product, Sweet Potato Powdered', category: 'Seasonings & Ingredients' },
                { name: 'Dried Abalone Mushroom 1 kg. Dried Mushroom from Farm', category: 'Dried Food' },
                { name: 'Fresh Pink Oyster Mushroom 1 kg./pack', category: 'Fresh Food' }
            ]
        },
        'distributor': {
            title: 'Products For Distributor & Wholesaler',
            description: 'Discover a wide range of reliable, high-quality products available for pre-order and immediate shipment. Our catalog includes products from fresh and dried fruit to ready-to-eat/cook products, beverages, and food ingredients. OEM services are available, ensuring all products meet your brand\'s exact specifications.',
            products: [
                { name: 'Jasmine Green Tea 300g. (20pack/1ctn)', category: 'Ready to Cook/Eat/Drink' },
                { name: 'Coconut Chips, Crispy For Wholesale Bulk Order 1 kg./pack', category: 'Dried Food' },
                { name: 'Frozen Baked Sweet Potato Paste', category: 'Frozen Food' },
                { name: 'Milk Green Tea Premium 400g. (12pack/1ctn)', category: 'Ready to Cook/Eat/Drink' },
                { name: 'Dried Mango with Low Sugar, Dried Mango Snacks 1 kg./pack', category: 'Dried Food' }
            ]
        },
        'restaurant': {
            title: 'For Restaurant, Hotel & Store',
            description: 'We connect you directly with a vast, curated network of suppliers, eliminating layers of intermediaries and reducing procurement risk. Access high-quality ingredients and finished goods that meet the highest global standards of quality and traceability.',
            products: [
                { name: 'Salak in Snowy Syrup 100 bags/box', category: 'Frozen Food' },
                { name: 'Wagyu Beef Burger 150g./piece', category: 'Daily Food Products' },
                { name: 'Wholesale Frankfurter Sausage Chicken, Pork, Beef 1 Kg.', category: 'Daily Food Products' },
                { name: 'Sea Bass for White meat Sea Bass Fillet, Shipping Worldwide', category: 'Daily Food Products' },
                { name: 'Burmese Coconut Curry Paste for Bulk Order', category: 'Ready to Cook/Eat/Drink' }
            ]
        },
        'retail': {
            title: 'Small Order & Retail',
            description: 'Perfect for small businesses and retail stores looking for quality products in smaller quantities. Access the same premium suppliers with flexible ordering options tailored to your needs.',
            products: [
                { name: 'Premium Tea Selection - Various Flavors', category: 'Beverage' },
                { name: 'Organic Dried Fruits - Mixed Pack', category: 'Dried Food' },
                { name: 'Gourmet Snacks Collection', category: 'Snack & Dessert' },
                { name: 'Fresh Organic Vegetables - Seasonal', category: 'Fresh Food' },
                { name: 'Artisan Food Products', category: 'Daily Food Products' }
            ]
        }
    };

    const currentContent = industryType && industryContent[industryType] 
        ? industryContent[industryType] 
        : {
            title: 'Industry Solutions',
            description: 'Select an industry type from the dropdown menu to view specialized products and services.',
            products: []
        };

    return (
        <div className="py-5" style={{ paddingTop: '100px' }}>
            <div className="container">
                <h1 className="text-center mb-4 fw-bold">{currentContent.title}</h1>
                <p className="text-center text-muted mb-5" style={{ maxWidth: '800px', margin: '0 auto 3rem' }}>
                    {currentContent.description}
                </p>

                {currentContent.products.length > 0 ? (
                    <>
                        <div className="row g-4 mb-5">
                            {currentContent.products.map((product, index) => (
                                <div key={index} className="col-md-6 col-lg-4">
                                    <div className="card h-100 border-0 shadow-sm">
                                        <div className="card-body p-4">
                                            <p className="text-muted small mb-2">{product.category}</p>
                                            <h5 className="card-title fw-bold">{product.name}</h5>
                                            <div className="d-flex gap-2 mt-3">
                                                <button className="btn btn-outline-success btn-sm flex-fill">
                                                    Add to cart
                                                </button>
                                                <button className="btn btn-success btn-sm flex-fill">
                                                    Request a Quote
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                        <div className="text-center">
                            <a href="/products" className="btn btn-success btn-lg">
                                Explore More Products
                            </a>
                        </div>
                    </>
                ) : (
                    <div className="text-center py-5">
                        <p className="text-muted">Please select an industry type from the navigation menu.</p>
                    </div>
                )}
            </div>
        </div>
    );
}
