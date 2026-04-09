import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';

/**
 * Quote Request Modal Component
 * 
 * Allows buyers to request quotes for products.
 * Shows a form with quantity, packaging size, port, and notes.
 */
export default function QuoteRequestModal({ show, onHide, product }) {
    const { isAuthenticated, user } = useAuth();
    const { showSuccess, showError } = useToast();
    const navigate = useNavigate();
    
    const [formData, setFormData] = useState({
        quantity: 1,
        packagingSize: '',
        port: '',
        notes: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Reset form when modal opens/closes or product changes
    useEffect(() => {
        if (show && product) {
            setFormData({
                quantity: 1,
                packagingSize: '',
                port: '',
                notes: ''
            });
            setError(null);
        }
    }, [show, product]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name === 'quantity' ? parseInt(value) || 1 : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!isAuthenticated) {
            showError('Please log in to request a quote');
            onHide();
            navigate('/login');
            return;
        }

        if (!product || !product.id) {
            showError('Product information is missing');
            return;
        }

        if (formData.quantity < 1) {
            setError('Quantity must be at least 1');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const quoteData = {
                userId: user?.id, // Get from auth context
                productId: product.id,
                quantity: formData.quantity,
                packagingSize: formData.packagingSize || null,
                port: formData.port || null,
                notes: formData.notes || null
            };

            const response = await api.quotes.create(quoteData);
            
            showSuccess(`Quote request submitted successfully! Quote ID: ${response.id?.substring(0, 8)}...`);
            onHide();
            
            // Reset form
            setFormData({
                quantity: 1,
                packagingSize: '',
                port: '',
                notes: ''
            });
        } catch (err) {
            console.error('Error creating quote request:', err);
            const errorMessage = err.message || 'Failed to submit quote request. Please try again.';
            setError(errorMessage);
            showError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    if (!product || !show) {
        return null;
    }

    return (
        <>
            {/* Modal Backdrop */}
            <div 
                className="modal-backdrop fade show" 
                style={{ display: show ? 'block' : 'none' }}
                onClick={onHide}
            ></div>

            {/* Modal */}
            <div 
                className={`modal fade ${show ? 'show' : ''}`}
                style={{ display: show ? 'block' : 'none' }}
                tabIndex="-1"
                role="dialog"
            >
                <div className="modal-dialog modal-lg modal-dialog-centered" role="document">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title">Request Quote for {product.name}</h5>
                            <button
                                type="button"
                                className="btn-close"
                                onClick={onHide}
                                disabled={loading}
                                aria-label="Close"
                            ></button>
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className="modal-body">
                                {error && (
                                    <div className="alert alert-danger alert-dismissible fade show" role="alert">
                                        {error}
                                        <button
                                            type="button"
                                            className="btn-close"
                                            onClick={() => setError(null)}
                                            aria-label="Close"
                                        ></button>
                                    </div>
                                )}

                                {!isAuthenticated && (
                                    <div className="alert alert-warning">
                                        Please <a href="/login">log in</a> to request a quote.
                                    </div>
                                )}

                                <div className="mb-3">
                                    <p className="text-muted mb-2">
                                        <strong>Product:</strong> {product.name}
                                    </p>
                                    {product.category && (
                                        <p className="text-muted mb-0">
                                            <strong>Category:</strong> {product.category}
                                        </p>
                                    )}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="quantity" className="form-label">
                                        Quantity <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="number"
                                        className="form-control"
                                        id="quantity"
                                        name="quantity"
                                        value={formData.quantity}
                                        onChange={handleChange}
                                        min="1"
                                        required
                                        disabled={loading || !isAuthenticated}
                                    />
                                    <div className="form-text">
                                        Enter the quantity you need
                                    </div>
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="packagingSize" className="form-label">
                                        Packaging Size
                                    </label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="packagingSize"
                                        name="packagingSize"
                                        value={formData.packagingSize}
                                        onChange={handleChange}
                                        placeholder="e.g., 25kg bags, 1 ton pallets"
                                        disabled={loading || !isAuthenticated}
                                    />
                                    <div className="form-text">
                                        Specify your preferred packaging size
                                    </div>
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="port" className="form-label">
                                        Port of Destination
                                    </label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="port"
                                        name="port"
                                        value={formData.port}
                                        onChange={handleChange}
                                        placeholder="e.g., Port of Bangkok, Port of Singapore"
                                        disabled={loading || !isAuthenticated}
                                    />
                                    <div className="form-text">
                                        Specify the port where you want the goods delivered
                                    </div>
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="notes" className="form-label">
                                        Additional Notes
                                    </label>
                                    <textarea
                                        className="form-control"
                                        id="notes"
                                        name="notes"
                                        rows="4"
                                        value={formData.notes}
                                        onChange={handleChange}
                                        placeholder="Any additional requirements or specifications..."
                                        disabled={loading || !isAuthenticated}
                                    ></textarea>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={onHide}
                                    disabled={loading}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-success"
                                    disabled={loading || !isAuthenticated}
                                >
                                    {loading ? 'Submitting...' : 'Submit Quote Request'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </>
    );
}
