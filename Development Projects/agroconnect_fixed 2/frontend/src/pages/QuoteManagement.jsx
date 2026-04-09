import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import QuoteStatusBadge from '../components/quote/QuoteStatusBadge';

/**
 * Quote Management Page for Suppliers
 * 
 * Allows suppliers to view and manage incoming quote requests.
 * Suppliers can update quote status (Pending → Reviewing → Approved/Rejected).
 */
export default function QuoteManagement() {
    const { isAuthenticated, user } = useAuth();
    const { showError, showSuccess } = useToast();
    
    const [quotes, setQuotes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [statusFilter, setStatusFilter] = useState('all');
    const [products, setProducts] = useState({});
    const [updating, setUpdating] = useState({}); // Track which quote is being updated

    useEffect(() => {
        if (!isAuthenticated) {
            setError('Please log in to manage quotes');
            setLoading(false);
            return;
        }

        if (user?.role !== 'SUPPLIER') {
            setError('This page is only available for suppliers');
            setLoading(false);
            return;
        }

        fetchQuotes();
    }, [isAuthenticated, user]);

    const fetchQuotes = async () => {
        try {
            setLoading(true);
            setError(null);
            
            const quotesData = await api.quotes.getAll();
            setQuotes(quotesData);
            
            // Fetch product details
            const productIds = [...new Set(quotesData.map(q => q.productId))];
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
            showError('Failed to load quotes');
        } finally {
            setLoading(false);
        }
    };

    const handleStatusUpdate = async (quoteId, newStatus) => {
        if (!window.confirm(`Are you sure you want to ${newStatus.toLowerCase()} this quote?`)) {
            return;
        }

        setUpdating(prev => ({ ...prev, [quoteId]: true }));
        try {
            const updatedQuote = await api.quotes.updateStatus(quoteId, { status: newStatus });
            
            setQuotes(prev => prev.map(q => q.id === quoteId ? updatedQuote : q));
            showSuccess(`Quote status updated to ${newStatus}`);
        } catch (err) {
            console.error('Error updating quote status:', err);
            showError(err.message || 'Failed to update quote status');
        } finally {
            setUpdating(prev => ({ ...prev, [quoteId]: false }));
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
                    <p className="text-muted">You need to be logged in to manage quotes.</p>
                    <Link to="/login" className="btn btn-success">
                        Go to Login
                    </Link>
                </div>
            </div>
        );
    }

    if (user?.role !== 'SUPPLIER') {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Access Denied</h2>
                    <p className="text-muted">This page is only available for suppliers.</p>
                    <Link to="/" className="btn btn-success">
                        Go to Home
                    </Link>
                </div>
            </div>
        );
    }

    return (
        <div className="container py-5" style={{ paddingTop: '100px' }}>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h1 className="fw-bold">Quote Management</h1>
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
                    <p className="text-muted mt-2">Loading quotes...</p>
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
                                    ? "No quote requests have been submitted yet."
                                    : `No quotes with status "${statusFilter}".`}
                            </p>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-hover">
                                <thead className="table-light">
                                    <tr>
                                        <th>Quote ID</th>
                                        <th>Product</th>
                                        <th>Quantity</th>
                                        <th>Packaging</th>
                                        <th>Port</th>
                                        <th>Status</th>
                                        <th>Created</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredQuotes.map((quote) => {
                                        const product = products[quote.productId];
                                        const isUpdating = updating[quote.id];
                                        
                                        return (
                                            <tr key={quote.id}>
                                                <td>
                                                    <Link 
                                                        to={`/quotes/${quote.id}`}
                                                        className="text-decoration-none"
                                                    >
                                                        {quote.id.substring(0, 8)}...
                                                    </Link>
                                                </td>
                                                <td>
                                                    {product ? (
                                                        <Link 
                                                            to={`/products/${product.id}`}
                                                            className="text-decoration-none"
                                                        >
                                                            {product.name}
                                                        </Link>
                                                    ) : (
                                                        <span className="text-muted">Product {quote.productId.substring(0, 8)}...</span>
                                                    )}
                                                </td>
                                                <td>{quote.quantity}</td>
                                                <td>{quote.packagingSize || '-'}</td>
                                                <td>{quote.port || '-'}</td>
                                                <td>
                                                    <QuoteStatusBadge status={quote.status} />
                                                </td>
                                                <td>
                                                    <small className="text-muted">
                                                        {new Date(quote.createdAt).toLocaleDateString()}
                                                    </small>
                                                </td>
                                                <td>
                                                    <div className="d-flex gap-1">
                                                        {quote.status === 'PENDING' && (
                                                            <>
                                                                <button
                                                                    className="btn btn-sm btn-info"
                                                                    onClick={() => handleStatusUpdate(quote.id, 'REVIEWING')}
                                                                    disabled={isUpdating}
                                                                    title="Mark as Reviewing"
                                                                >
                                                                    {isUpdating ? '...' : 'Review'}
                                                                </button>
                                                                <button
                                                                    className="btn btn-sm btn-success"
                                                                    onClick={() => handleStatusUpdate(quote.id, 'APPROVED')}
                                                                    disabled={isUpdating}
                                                                    title="Approve"
                                                                >
                                                                    {isUpdating ? '...' : '✓'}
                                                                </button>
                                                                <button
                                                                    className="btn btn-sm btn-danger"
                                                                    onClick={() => handleStatusUpdate(quote.id, 'REJECTED')}
                                                                    disabled={isUpdating}
                                                                    title="Reject"
                                                                >
                                                                    {isUpdating ? '...' : '✗'}
                                                                </button>
                                                            </>
                                                        )}
                                                        {quote.status === 'REVIEWING' && (
                                                            <>
                                                                <button
                                                                    className="btn btn-sm btn-success"
                                                                    onClick={() => handleStatusUpdate(quote.id, 'APPROVED')}
                                                                    disabled={isUpdating}
                                                                    title="Approve"
                                                                >
                                                                    {isUpdating ? '...' : '✓'}
                                                                </button>
                                                                <button
                                                                    className="btn btn-sm btn-danger"
                                                                    onClick={() => handleStatusUpdate(quote.id, 'REJECTED')}
                                                                    disabled={isUpdating}
                                                                    title="Reject"
                                                                >
                                                                    {isUpdating ? '...' : '✗'}
                                                                </button>
                                                            </>
                                                        )}
                                                        <Link
                                                            to={`/quotes/${quote.id}`}
                                                            className="btn btn-sm btn-outline-primary"
                                                            title="View Details"
                                                        >
                                                            View
                                                        </Link>
                                                    </div>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}



