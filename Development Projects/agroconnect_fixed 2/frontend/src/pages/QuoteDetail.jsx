import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import QuoteStatusBadge from '../components/quote/QuoteStatusBadge';

/**
 * Quote Detail Page
 * 
 * Shows detailed information about a specific quote request.
 * Buyers can view their quote details.
 * Suppliers can view and update quote status.
 */
export default function QuoteDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { isAuthenticated, user } = useAuth();
    const { showError, showSuccess } = useToast();
    
    const [quote, setQuote] = useState(null);
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [updating, setUpdating] = useState(false);

    useEffect(() => {
        if (!isAuthenticated) {
            setError('Please log in to view quote details');
            setLoading(false);
            return;
        }

        fetchQuote();
    }, [id, isAuthenticated]);

    const fetchQuote = async () => {
        try {
            setLoading(true);
            setError(null);
            
            const quoteData = await api.quotes.getById(id);
            setQuote(quoteData);
            
            // Fetch product details
            if (quoteData.productId) {
                try {
                    const productData = await api.products.getById(quoteData.productId);
                    setProduct(productData);
                } catch (err) {
                    console.warn('Could not fetch product details:', err);
                }
            }
        } catch (err) {
            console.error('Error fetching quote:', err);
            setError(err.message || 'Failed to load quote details');
            showError('Failed to load quote details');
        } finally {
            setLoading(false);
        }
    };

    const handleStatusUpdate = async (newStatus) => {
        if (!quote) return;
        
        if (!window.confirm(`Are you sure you want to ${newStatus.toLowerCase()} this quote?`)) {
            return;
        }

        setUpdating(true);
        try {
            const updatedQuote = await api.quotes.updateStatus(quote.id, { status: newStatus });
            setQuote(updatedQuote);
            showSuccess(`Quote status updated to ${newStatus}`);
        } catch (err) {
            console.error('Error updating quote status:', err);
            showError(err.message || 'Failed to update quote status');
        } finally {
            setUpdating(false);
        }
    };

    const isSupplier = user?.role === 'SUPPLIER';
    const isOwner = quote && user?.id === quote.userId;

    if (!isAuthenticated) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Please Log In</h2>
                    <p className="text-muted">You need to be logged in to view quote details.</p>
                    <Link to="/login" className="btn btn-success">
                        Go to Login
                    </Link>
                </div>
            </div>
        );
    }

    if (loading) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <div className="spinner-border text-success" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <p className="text-muted mt-2">Loading quote details...</p>
                </div>
            </div>
        );
    }

    if (error || !quote) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Quote Not Found</h2>
                    <p className="text-muted">{error || 'The quote you are looking for does not exist.'}</p>
                    <Link to="/quotes" className="btn btn-success">
                        Back to My Quotes
                    </Link>
                </div>
            </div>
        );
    }

    return (
        <div className="container py-5" style={{ paddingTop: '100px' }}>
            <nav aria-label="breadcrumb" className="mb-4">
                <ol className="breadcrumb">
                    <li className="breadcrumb-item">
                        <Link to="/">Home</Link>
                    </li>
                    <li className="breadcrumb-item">
                        <Link to="/quotes">My Quotes</Link>
                    </li>
                    <li className="breadcrumb-item active" aria-current="page">
                        Quote #{quote.id.substring(0, 8)}
                    </li>
                </ol>
            </nav>

            <div className="row">
                <div className="col-lg-8">
                    <div className="card shadow-sm mb-4">
                        <div className="card-header bg-light d-flex justify-content-between align-items-center">
                            <h4 className="mb-0">Quote Details</h4>
                            <QuoteStatusBadge status={quote.status} />
                        </div>
                        <div className="card-body">
                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <p className="mb-1">
                                        <strong>Quote ID:</strong>
                                    </p>
                                    <p className="text-muted">{quote.id}</p>
                                </div>
                                <div className="col-md-6">
                                    <p className="mb-1">
                                        <strong>Created:</strong>
                                    </p>
                                    <p className="text-muted">
                                        {new Date(quote.createdAt).toLocaleString()}
                                    </p>
                                </div>
                            </div>

                            {product && (
                                <div className="mb-3">
                                    <p className="mb-1">
                                        <strong>Product:</strong>
                                    </p>
                                    <Link 
                                        to={`/products/${product.id}`}
                                        className="text-decoration-none"
                                    >
                                        <h5 className="text-success">{product.name}</h5>
                                    </Link>
                                    <p className="text-muted small">{product.category}</p>
                                    {product.description && (
                                        <p className="text-muted">{product.description}</p>
                                    )}
                                </div>
                            )}

                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <p className="mb-1">
                                        <strong>Quantity:</strong>
                                    </p>
                                    <p className="text-muted">{quote.quantity}</p>
                                </div>
                                {quote.packagingSize && (
                                    <div className="col-md-6">
                                        <p className="mb-1">
                                            <strong>Packaging Size:</strong>
                                        </p>
                                        <p className="text-muted">{quote.packagingSize}</p>
                                    </div>
                                )}
                            </div>

                            {quote.port && (
                                <div className="mb-3">
                                    <p className="mb-1">
                                        <strong>Port of Destination:</strong>
                                    </p>
                                    <p className="text-muted">{quote.port}</p>
                                </div>
                            )}

                            {quote.notes && (
                                <div className="mb-3">
                                    <p className="mb-1">
                                        <strong>Additional Notes:</strong>
                                    </p>
                                    <p className="text-muted" style={{ whiteSpace: 'pre-wrap' }}>
                                        {quote.notes}
                                    </p>
                                </div>
                            )}

                            <div className="mb-3">
                                <p className="mb-1">
                                    <strong>Last Updated:</strong>
                                </p>
                                <p className="text-muted">
                                    {new Date(quote.updatedAt).toLocaleString()}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-lg-4">
                    <div className="card shadow-sm">
                        <div className="card-header bg-light">
                            <h5 className="mb-0">Actions</h5>
                        </div>
                        <div className="card-body">
                            {isSupplier && quote.status === 'PENDING' && (
                                <>
                                    <button
                                        className="btn btn-success w-100 mb-2"
                                        onClick={() => handleStatusUpdate('REVIEWING')}
                                        disabled={updating}
                                    >
                                        Mark as Reviewing
                                    </button>
                                    <button
                                        className="btn btn-primary w-100 mb-2"
                                        onClick={() => handleStatusUpdate('APPROVED')}
                                        disabled={updating}
                                    >
                                        Approve Quote
                                    </button>
                                    <button
                                        className="btn btn-danger w-100 mb-2"
                                        onClick={() => handleStatusUpdate('REJECTED')}
                                        disabled={updating}
                                    >
                                        Reject Quote
                                    </button>
                                </>
                            )}

                            {isSupplier && quote.status === 'REVIEWING' && (
                                <>
                                    <button
                                        className="btn btn-primary w-100 mb-2"
                                        onClick={() => handleStatusUpdate('APPROVED')}
                                        disabled={updating}
                                    >
                                        Approve Quote
                                    </button>
                                    <button
                                        className="btn btn-danger w-100 mb-2"
                                        onClick={() => handleStatusUpdate('REJECTED')}
                                        disabled={updating}
                                    >
                                        Reject Quote
                                    </button>
                                </>
                            )}

                            <Link 
                                to="/quotes" 
                                className="btn btn-outline-secondary w-100"
                            >
                                Back to Quotes
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}



