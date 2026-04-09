import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import QuoteRequestModal from '../components/quote/QuoteRequestModal';

export default function ProductDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { addToCart, setIsCartOpen } = useCart();
    const { isAuthenticated } = useAuth();
    const { showSuccess, showError } = useToast();

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [showQuoteModal, setShowQuoteModal] = useState(false);

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                setLoading(true);
                setError(null);
                const data = await api.products.getById(id);
                setProduct(data);
            } catch (err) {
                console.error('Error fetching product:', err);
                setError(err.message || 'Failed to load product');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchProduct();
        }
    }, [id]);

    const handleAddToCart = () => {
        if (product) {
            addToCart({ ...product, quantity });
            setIsCartOpen(true);
            showSuccess(`${product.name} added to cart!`);
        }
    };

    const handleRequestQuote = () => {
        if (!isAuthenticated) {
            showError('Please login to request a quote');
            navigate('/login', { state: { from: `/products/${id}` } });
            return;
        }
        setShowQuoteModal(true);
    };

    // Get product images - use first image URL or fallback
    const getProductImages = () => {
        if (product?.productImages && product.productImages.length > 0) {
            return product.productImages.map(img => img.url).filter(url => url);
        }
        return [];
    };

    const getCategoryEmoji = (category) => {
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
        return categoryEmojis[category] || '📦';
    };

    if (loading) {
        return (
            <div className="py-5" style={{ paddingTop: '120px', minHeight: '60vh' }}>
                <div className="container">
                    <div className="text-center py-5">
                        <div className="spinner-border text-success" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                        <p className="mt-3 text-muted">Loading product details...</p>
                    </div>
                </div>
            </div>
        );
    }

    if (error || !product) {
        return (
            <div className="py-5" style={{ paddingTop: '120px', minHeight: '60vh' }}>
                <div className="container">
                    <div className="alert alert-danger text-center" role="alert">
                        <h4 className="alert-heading">Product Not Found</h4>
                        <p>{error || 'The product you are looking for does not exist.'}</p>
                        <hr />
                        <div className="d-flex gap-2 justify-content-center">
                            <Link to="/products" className="btn btn-success">
                                Browse Products
                            </Link>
                            <button className="btn btn-outline-secondary" onClick={() => navigate(-1)}>
                                Go Back
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    const images = getProductImages();
    const mainImage = images.length > 0 ? images[0] : null;

    return (
        <div className="py-5" style={{ paddingTop: '120px' }}>
            <div className="container">
                {/* Breadcrumb */}
                <nav aria-label="breadcrumb" className="mb-4">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <Link to="/">Home</Link>
                        </li>
                        <li className="breadcrumb-item">
                            <Link to="/products">Products</Link>
                        </li>
                        {product.category && (
                            <li className="breadcrumb-item">
                                <Link to={`/products?category=${encodeURIComponent(product.category)}`}>
                                    {product.category}
                                </Link>
                            </li>
                        )}
                        <li className="breadcrumb-item active" aria-current="page">
                            {product.name}
                        </li>
                    </ol>
                </nav>

                <div className="row">
                    {/* Product Images */}
                    <div className="col-md-6 mb-4">
                        <div className="card border-0 shadow-sm">
                            <div className="card-body p-4">
                                {mainImage ? (
                                    <img
                                        src={mainImage}
                                        alt={product.name}
                                        className="img-fluid rounded"
                                        style={{ width: '100%', maxHeight: '500px', objectFit: 'cover' }}
                                    />
                                ) : (
                                    <div className="text-center py-5" style={{ fontSize: '8rem' }}>
                                        {getCategoryEmoji(product.category)}
                                    </div>
                                )}
                                
                                {/* Thumbnail Gallery */}
                                {images.length > 1 && (
                                    <div className="row g-2 mt-3">
                                        {images.slice(1, 5).map((img, index) => (
                                            <div key={index} className="col-3">
                                                <img
                                                    src={img}
                                                    alt={`${product.name} ${index + 2}`}
                                                    className="img-fluid rounded border"
                                                    style={{ cursor: 'pointer', height: '80px', objectFit: 'cover', width: '100%' }}
                                                />
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Product Information */}
                    <div className="col-md-6">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body p-4">
                                {/* Category Badge */}
                                {product.category && (
                                    <span className="badge bg-success mb-3">
                                        {product.category}
                                    </span>
                                )}

                                {/* Product Name */}
                                <h1 className="card-title fw-bold mb-3">{product.name}</h1>

                                {/* SKU */}
                                {product.sku && (
                                    <p className="text-muted small mb-2">
                                        SKU: <strong>{product.sku}</strong>
                                    </p>
                                )}

                                {/* Price */}
                                {product.price && (
                                    <div className="mb-4">
                                        <h3 className="text-success fw-bold mb-0">
                                            ${parseFloat(product.price).toFixed(2)}
                                        </h3>
                                        <small className="text-muted">Per unit</small>
                                    </div>
                                )}

                                {/* Description */}
                                <div className="mb-4">
                                    <h5 className="fw-bold mb-3">Description</h5>
                                    <p className="text-muted" style={{ lineHeight: '1.8' }}>
                                        {product.description || 'No description available.'}
                                    </p>
                                </div>

                                {/* Quantity Selector */}
                                <div className="mb-4">
                                    <label htmlFor="quantity" className="form-label fw-bold">
                                        Quantity
                                    </label>
                                    <div className="input-group" style={{ maxWidth: '200px' }}>
                                        <button
                                            className="btn btn-outline-secondary"
                                            type="button"
                                            onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                        >
                                            -
                                        </button>
                                        <input
                                            type="number"
                                            className="form-control text-center"
                                            id="quantity"
                                            value={quantity}
                                            onChange={(e) => {
                                                const val = parseInt(e.target.value) || 1;
                                                setQuantity(Math.max(1, val));
                                            }}
                                            min="1"
                                        />
                                        <button
                                            className="btn btn-outline-secondary"
                                            type="button"
                                            onClick={() => setQuantity(quantity + 1)}
                                        >
                                            +
                                        </button>
                                    </div>
                                </div>

                                {/* Action Buttons */}
                                <div className="d-grid gap-2 mb-4">
                                    <button
                                        className="btn btn-success btn-lg"
                                        onClick={handleAddToCart}
                                    >
                                        <svg width="20" height="20" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                            <path d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 .491.592l-1.5 8A.5.5 0 0 1 13 12H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 1-.5-.5zM3.102 4l1.22 6.326a.5.5 0 0 0 .496.374h8.662a.5.5 0 0 0 .496-.374L14.898 4H3.102zM5 14a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm-2 1a2 2 0 1 1 4 0 2 2 0 0 1-4 0zm9-1a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm-2 1a2 2 0 1 1 4 0 2 2 0 0 1-4 0z"/>
                                        </svg>
                                        Add to Cart
                                    </button>
                                    <button
                                        className="btn btn-outline-success btn-lg"
                                        onClick={handleRequestQuote}
                                    >
                                        Request a Quote
                                    </button>
                                </div>

                                {/* Product Info */}
                                <div className="border-top pt-3">
                                    <div className="row small text-muted">
                                        <div className="col-6">
                                            <strong>Status:</strong> {product.isActive ? 'Available' : 'Unavailable'}
                                        </div>
                                        {product.createdAt && (
                                            <div className="col-6 text-end">
                                                <strong>Added:</strong> {new Date(product.createdAt).toLocaleDateString()}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Related Products Section - Placeholder */}
                <div className="row mt-5">
                    <div className="col-12">
                        <h3 className="fw-bold mb-4">Related Products</h3>
                        <p className="text-muted">
                            <Link to={`/products?category=${encodeURIComponent(product.category || '')}`}>
                                View more products in {product.category || 'this category'}
                            </Link>
                        </p>
                    </div>
                </div>
            </div>

            {/* Quote Request Modal */}
            <QuoteRequestModal
                show={showQuoteModal}
                onHide={() => setShowQuoteModal(false)}
                product={product}
            />
        </div>
    );
}

