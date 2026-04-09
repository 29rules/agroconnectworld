import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import QuoteStatusBadge from '../components/quote/QuoteStatusBadge';

/**
 * My Quotes Page
 * 
 * Displays all quote requests made by the logged-in buyer.
 * Allows filtering by status and viewing quote details.
 */
export default function MyQuotes() {
    const { isAuthenticated, user } = useAuth();
    const { showError } = useToast();
    
    const [quotes, setQuotes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [statusFilter, setStatusFilter] = useState('all');
    const [products, setProducts] = useState({}); // Cache for product details

    useEffect(() => {
        if (!isAuthenticated) {
            setError('Please log in to view your quotes');
            setLoading(false);
            return;
        }

        fetchQuotes();
    }, [isAuthenticated]);

    const fetchQuotes = async () => {
        try {
            setLoading(true);
            setError(null);
            
            const quotesData = await api.quotes.getAll();
            
            // Filter quotes for current user (backend should handle this, but filter client-side as well)
            const userQuotes = quotesData.filter(quote => 
                quote.userId === user?.id || !user?.id // Show all if no user ID yet
            );
            
            setQuotes(userQuotes);
            
            // Fetch product details for each quote
            const productIds = [...new Set(userQuotes.map(q => q.productId))];
            const productPromises = productIds.map(id => 
                api.products.getById(id).catch(() => null)
            );
            const productResults = await Promise.all(productPromises);
            
            const productMap = {};
            productResults.forEach((product, index) => {
                if (product) {
                    productMap[productIds[index]] = product;
                }
            });
            setProducts(productMap);
            
        } catch (err) {
            console.error('Error fetching quotes:', err);
            setError(err.message || 'Failed to load quotes');
            showError('Failed to load your quotes');
        } finally {
            setLoading(false);
        }
    };

    const filteredQuotes = statusFilter === 'all' 
        ? quotes 
        : quotes.filter(quote => quote.status === statusFilter.toUpperCase());

    const statusCounts = {
        all: quotes.length,
        pending: quotes.filter(q => q.status === 'PENDING').length,
        reviewing: quotes.filter(q => q.status === 'REVIEWING').length,
        approved: quotes.filter(q => q.status === 'APPROVED').length,
        rejected: quotes.filter(q => q.status === 'REJECTED').length
    };

    if (!isAuthenticated) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Please Log In</h2>
                    <p className="text-muted">You need to be logged in to view your quotes.</p>
                    <Link to="/login" className="btn btn-success">
                        Go to Login
                    </Link>
                </div>
            </div>
        );
    }

    return (
        <div className="container py-5" style={{ paddingTop: '100px' }}>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h1 className="fw-bold">My Quote Requests</h1>
                <Link to="/products" className="btn btn-success">
                    Request New Quote
                </Link>
            </div>

            {/* Status Filter */}
            <div className="mb-4">
                <div className="d-flex flex-wrap gap-2">
                    {Object.entries(statusCounts).map(([status, count]) => (
                        <button
                            key={status}
                            className={`btn ${
                                statusFilter === status 
                                    ? 'btn-success' 
                                    : 'btn-outline-success'
                            }`}
                            onClick={() => setStatusFilter(status)}
                        >
                            {status.charAt(0).toUpperCase() + status.slice(1)} 
                            {count > 0 && <span className="badge bg-light text-dark ms-2">{count}</span>}
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
                    <p className="text-muted mt-2">Loading your quotes...</p>
                </div>
            )}

            {/* Error State */}
            {error && !loading && (
                <div className="alert alert-danger">
                    <strong>Error:</strong> {error}
                    <button 
                        className="btn btn-sm btn-outline-danger ms-2"
                        onClick={fetchQuotes}
                    >
                        Retry
                    </button>
                </div>
            )}

            {/* Quotes List */}
            {!loading && !error && (
                <>
                    {filteredQuotes.length === 0 ? (
                        <div className="text-center py-5">
                            <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>📋</div>
                            <h4 className="text-muted mb-3">No Quotes Found</h4>
                            <p className="text-muted">
                                {statusFilter === 'all' 
                                    ? "You haven't requested any quotes yet."
                                    : `No quotes with status "${statusFilter}".`}
                            </p>
                            <Link to="/products" className="btn btn-success mt-3">
                                Browse Products
                            </Link>
                        </div>
                    ) : (
                        <div className="row g-4">
                            {filteredQuotes.map((quote) => {
                                const product = products[quote.productId];
                                return (
                                    <div key={quote.id} className="col-md-6 col-lg-4">
                                        <div className="card h-100 shadow-sm">
                                            <div className="card-body">
                                                <div className="d-flex justify-content-between align-items-start mb-3">
                                                    <QuoteStatusBadge status={quote.status} />
                                                    <small className="text-muted">
                                                        {new Date(quote.createdAt).toLocaleDateString()}
                                                    </small>
                                                </div>
                                                
                                                {product ? (
                                                    <>
                                                        <h5 className="card-title">
                                                            <Link 
                                                                to={`/products/${product.id}`}
                                                                className="text-decoration-none text-dark"
                                                            >
                                                                {product.name}
                                                            </Link>
                                                        </h5>
                                                        <p className="text-muted small mb-2">
                                                            {product.category}
                                                        </p>
                                                    </>
                                                ) : (
                                                    <h5 className="card-title">Product ID: {quote.productId}</h5>
                                                )}
                                                
                                                <div className="mb-3">
                                                    <p className="mb-1">
                                                        <strong>Quantity:</strong> {quote.quantity}
                                                    </p>
                                                    {quote.packagingSize && (
                                                        <p className="mb-1 small text-muted">
                                                            <strong>Packaging:</strong> {quote.packagingSize}
                                                        </p>
                                                    )}
                                                    {quote.port && (
                                                        <p className="mb-1 small text-muted">
                                                            <strong>Port:</strong> {quote.port}
                                                        </p>
                                                    )}
                                                </div>
                                                
                                                {quote.notes && (
                                                    <p className="small text-muted mb-3" style={{
                                                        display: '-webkit-box',
                                                        WebkitLineClamp: 2,
                                                        WebkitBoxOrient: 'vertical',
                                                        overflow: 'hidden'
                                                    }}>
                                                        {quote.notes}
                                                    </p>
                                                )}
                                                
                                                <Link 
                                                    to={`/quotes/${quote.id}`}
                                                    className="btn btn-outline-success btn-sm w-100"
                                                >
                                                    View Details
                                                </Link>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </>
            )}
        </div>
    );
}



