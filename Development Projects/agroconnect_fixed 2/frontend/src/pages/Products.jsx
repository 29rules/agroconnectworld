import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import QuoteRequestModal from '../components/quote/QuoteRequestModal';

export default function Products() {
    const [searchParams] = useSearchParams();
    const categoryFromUrl = searchParams.get('category');
    const [selectedCategory, setSelectedCategory] = useState(
        categoryFromUrl || 'all'
    );
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showQuoteModal, setShowQuoteModal] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState(null);
    const { addToCart, setIsCartOpen } = useCart();
    const { isAuthenticated } = useAuth();
    const { showError } = useToast();
    const navigate = useNavigate();

    useEffect(() => {
        if (categoryFromUrl) {
            setSelectedCategory(categoryFromUrl);
        }
    }, [categoryFromUrl]);

    // Fetch products from API
    useEffect(() => {
        const fetchProducts = async () => {
            try {
                setLoading(true);
                setError(null);
                const data = await api.products.getAll();
                
                // Handle response - could be array or object with data property
                let productList = [];
                if (Array.isArray(data)) {
                    productList = data;
                } else if (data && Array.isArray(data.data)) {
                    productList = data.data;
                } else if (data && data.products && Array.isArray(data.products)) {
                    productList = data.products;
                }
                
                setProducts(productList);
                
                // If no products, show info message instead of error
                if (productList.length === 0) {
                    setError(null); // Clear error, show empty state
                }
            } catch (err) {
                console.error('Error fetching products:', err);
                // Provide more specific error message
                let errorMessage = 'Failed to load products';
                
                // Handle different error types
                if (err.status === 0 || !err.status) {
                    // Network error or CORS issue
                    errorMessage = err.message || 'Network error. Please check if the backend services are running.';
                } else if (err.status === 404) {
                    errorMessage = 'Products endpoint not found.';
                } else if (err.status === 500) {
                    errorMessage = 'Server error. Please try again later.';
                } else if (err.message) {
                    errorMessage = err.message;
                }
                
                setError(errorMessage);
                setProducts([]);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    const categories = [
        'All Products',
        'Fresh Food',
        'Dried Food',
        'Frozen Food',
        'Beverage',
        'Seasonings & Ingredients',
        'Ready to Cook/Eat/Drink',
        'Snack & Dessert',
        'Daily Food Products'
    ];

    const filteredProducts = selectedCategory === 'all' 
        ? products 
        : products.filter(p => p.category === selectedCategory);

    const handleAddToCart = (product) => {
        if (product) {
            addToCart(product);
            setIsCartOpen(true);
        }
    };

    const handleRequestQuote = (product) => {
        if (!isAuthenticated) {
            showError('Please login to request a quote');
            navigate('/login');
            return;
        }
        setSelectedProduct(product);
        setShowQuoteModal(true);
    };

    // Get product image - use first image URL or fallback to emoji
    const getProductImage = (product) => {
        // Backend returns productImages array
        const images = product.productImages || product.images || [];
        if (images.length > 0 && images[0].url) {
            return <img src={images[0].url} alt={product.name} className="img-fluid" style={{ maxHeight: '200px', objectFit: 'cover' }} />;
        }
        // Fallback to category emoji
        const categoryEmojis = {
            'Fresh Food': '🥬',
            'Dried Food': '🌾',
            'Frozen Food': '❄️',
            'Beverage': '🥤',
            'Seasonings & Ingredients': '🧂',
            'Ready to Cook/Eat/Drink': '🍽️',
            'Snack & Dessert': '🍰',
            'Daily Food Products': '🥛'
        };
        return <div style={{ fontSize: '4rem' }}>{categoryEmojis[product.category] || '📦'}</div>;
    };

    return (
        <div className="py-5" style={{ paddingTop: '100px' }}>
            <div className="container">
                <h1 className="text-center mb-5 fw-bold">PRODUCT CATEGORIES</h1>
                
                {/* Category Filter */}
                <div className="mb-5">
                    <div className="d-flex flex-wrap gap-2 justify-content-center">
                        {categories.map((cat) => (
                            <button
                                key={cat}
                                className={`btn ${selectedCategory === (cat === 'All Products' ? 'all' : cat) 
                                    ? 'btn-success' 
                                    : 'btn-outline-success'}`}
                                onClick={() => setSelectedCategory(cat === 'All Products' ? 'all' : cat)}
                            >
                                {cat}
                            </button>
                        ))}
                    </div>
                </div>

                {/* Loading State */}
                {loading && (
                    <div className="text-center py-5">
                        <div className="spinner-border text-success" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                        <p className="mt-3 text-muted">Loading products...</p>
                    </div>
                )}

                {/* Error State */}
                {error && !loading && (
                    <div className="alert alert-danger text-center" role="alert">
                        <strong>Error:</strong> {error}
                        <button 
                            className="btn btn-sm btn-outline-danger ms-3"
                            onClick={() => window.location.reload()}
                        >
                            Retry
                        </button>
                    </div>
                )}

                {/* Products Grid */}
                {!loading && !error && (
                    <div className="row g-4">
                        {filteredProducts.map((product) => (
                            <div key={product.id} className="col-md-4 col-lg-3">
                                <div className="card h-100 border-0 shadow-sm product-card">
                                    <div className="card-body p-4">
                                        <div className="text-center mb-3">
                                            {getProductImage(product)}
                                        </div>
                                        <p className="text-muted small mb-2">{product.category || 'Uncategorized'}</p>
                                    <Link 
                                        to={`/products/${product.id}`}
                                        className="text-decoration-none text-dark"
                                    >
                                        <h5 className="card-title fw-bold mb-3" style={{ fontSize: '1rem', minHeight: '48px' }}>
                                            {product.name}
                                        </h5>
                                    </Link>
                                        <p className="card-text text-muted small mb-3" style={{ minHeight: '40px' }}>
                                            {product.description || 'No description available'}
                                        </p>
                                        {product.price && (
                                            <p className="fw-bold text-success mb-2">
                                                ${parseFloat(product.price).toFixed(2)}
                                            </p>
                                        )}
                                        <div className="d-flex gap-2">
                                            <button
                                                className="btn btn-outline-success btn-sm flex-fill"
                                                onClick={() => handleAddToCart(product)}
                                            >
                                                Add to cart
                                            </button>
                                            <button
                                                className="btn btn-success btn-sm flex-fill"
                                                onClick={() => handleRequestQuote(product)}
                                            >
                                                Request a Quote
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {/* Empty State */}
                {!loading && !error && filteredProducts.length === 0 && products.length === 0 && (
                    <div className="text-center py-5">
                        <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>📦</div>
                        <h4 className="text-muted mb-3">No Products Available</h4>
                        <p className="text-muted">
                            There are no products in the database yet.
                            {selectedCategory !== 'all' && (
                                <>
                                    <br />
                                    Try selecting "All Products" or a different category.
                                </>
                            )}
                        </p>
                    </div>
                )}

                {/* Empty Category State */}
                {!loading && !error && filteredProducts.length === 0 && products.length > 0 && (
                    <div className="text-center py-5">
                        <p className="text-muted">No products found in the "{selectedCategory === 'all' ? 'All Products' : selectedCategory}" category.</p>
                        <button 
                            className="btn btn-outline-success mt-3"
                            onClick={() => setSelectedCategory('all')}
                        >
                            View All Products
                        </button>
                    </div>
                )}
            </div>

            {/* Quote Request Modal */}
            <QuoteRequestModal
                show={showQuoteModal}
                onHide={() => {
                    setShowQuoteModal(false);
                    setSelectedProduct(null);
                }}
                product={selectedProduct}
            />
        </div>
    );
}
