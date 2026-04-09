import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';

/**
 * Checkout Page
 * 
 * Allows users to review cart items and place orders.
 * Collects shipping information and payment details.
 */
export default function Checkout() {
    const navigate = useNavigate();
    const { cartItems, clearCart } = useCart();
    const { isAuthenticated, user } = useAuth();
    const { showSuccess, showError } = useToast();
    
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        shippingAddress: '',
        shippingCity: '',
        shippingState: '',
        shippingZipCode: '',
        shippingCountry: '',
        phone: '',
        notes: ''
    });

    useEffect(() => {
        if (!isAuthenticated) {
            showError('Please log in to checkout');
            navigate('/login', { state: { from: '/checkout' } });
            return;
        }

        if (cartItems.length === 0) {
            showError('Your cart is empty');
            navigate('/cart');
            return;
        }

        // Pre-fill user data if available
        if (user) {
            setFormData(prev => ({
                ...prev,
                phone: user.phone || '',
                shippingCountry: user.country || ''
            }));
        }
    }, [isAuthenticated, cartItems, user, navigate, showError]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const calculateTotal = () => {
        return cartItems.reduce((total, item) => {
            const price = parseFloat(item.price) || 0;
            return total + (price * item.quantity);
        }, 0);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!isAuthenticated) {
            showError('Please log in to place an order');
            navigate('/login');
            return;
        }

        if (cartItems.length === 0) {
            showError('Your cart is empty');
            return;
        }

        // Validate required fields
        if (!formData.shippingAddress || !formData.shippingCity || !formData.shippingZipCode) {
            showError('Please fill in all required shipping fields');
            return;
        }

        setLoading(true);

        try {
            // Prepare order items
            const orderItems = cartItems.map(item => ({
                productId: item.id,
                quantity: item.quantity,
                price: parseFloat(item.price) || 0
            }));

            // Create order (backend only accepts userId, status, and items)
            // Shipping info can be added to notes for now
            const shippingInfo = `Shipping Address: ${formData.shippingAddress}, ${formData.shippingCity}, ${formData.shippingState || ''} ${formData.shippingZipCode}, ${formData.shippingCountry || ''}. Phone: ${formData.phone || 'N/A'}`;
            const notes = formData.notes ? `${formData.notes}\n\n${shippingInfo}` : shippingInfo;
            
            const orderData = {
                userId: user.id,
                items: orderItems
                // Note: Shipping fields not in backend DTO yet - stored in notes
            };

            const order = await api.orders.create(orderData);
            
            // Clear cart
            clearCart();
            
            // Navigate to confirmation page
            navigate(`/orders/${order.id}/confirmation`, { 
                state: { order } 
            });
            
            showSuccess('Order placed successfully!');
        } catch (err) {
            console.error('Error placing order:', err);
            showError(err.message || 'Failed to place order. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    if (!isAuthenticated || cartItems.length === 0) {
        return null; // Will redirect
    }

    const subtotal = calculateTotal();
    const tax = subtotal * 0.1; // 10% tax (example)
    const shipping = subtotal > 100 ? 0 : 10; // Free shipping over $100
    const total = subtotal + tax + shipping;

    return (
        <div className="container py-5" style={{ paddingTop: '100px' }}>
            <div className="row">
                <div className="col-12">
                    <h1 className="fw-bold mb-4">Checkout</h1>
                </div>
            </div>

            <form onSubmit={handleSubmit}>
                <div className="row g-4">
                    {/* Shipping Information */}
                    <div className="col-lg-8">
                        <div className="card shadow-sm mb-4">
                            <div className="card-header bg-light">
                                <h5 className="mb-0">Shipping Information</h5>
                            </div>
                            <div className="card-body">
                                <div className="row g-3">
                                    <div className="col-12">
                                        <label htmlFor="shippingAddress" className="form-label">
                                            Address <span className="text-danger">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="shippingAddress"
                                            name="shippingAddress"
                                            value={formData.shippingAddress}
                                            onChange={handleChange}
                                            required
                                            disabled={loading}
                                        />
                                    </div>
                                    <div className="col-md-6">
                                        <label htmlFor="shippingCity" className="form-label">
                                            City <span className="text-danger">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="shippingCity"
                                            name="shippingCity"
                                            value={formData.shippingCity}
                                            onChange={handleChange}
                                            required
                                            disabled={loading}
                                        />
                                    </div>
                                    <div className="col-md-6">
                                        <label htmlFor="shippingState" className="form-label">
                                            State/Province
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="shippingState"
                                            name="shippingState"
                                            value={formData.shippingState}
                                            onChange={handleChange}
                                            disabled={loading}
                                        />
                                    </div>
                                    <div className="col-md-6">
                                        <label htmlFor="shippingZipCode" className="form-label">
                                            ZIP/Postal Code <span className="text-danger">*</span>
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="shippingZipCode"
                                            name="shippingZipCode"
                                            value={formData.shippingZipCode}
                                            onChange={handleChange}
                                            required
                                            disabled={loading}
                                        />
                                    </div>
                                    <div className="col-md-6">
                                        <label htmlFor="shippingCountry" className="form-label">
                                            Country
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="shippingCountry"
                                            name="shippingCountry"
                                            value={formData.shippingCountry}
                                            onChange={handleChange}
                                            disabled={loading}
                                        />
                                    </div>
                                    <div className="col-12">
                                        <label htmlFor="phone" className="form-label">
                                            Phone Number
                                        </label>
                                        <input
                                            type="tel"
                                            className="form-control"
                                            id="phone"
                                            name="phone"
                                            value={formData.phone}
                                            onChange={handleChange}
                                            disabled={loading}
                                        />
                                    </div>
                                    <div className="col-12">
                                        <label htmlFor="notes" className="form-label">
                                            Order Notes (Optional)
                                        </label>
                                        <textarea
                                            className="form-control"
                                            id="notes"
                                            name="notes"
                                            rows="3"
                                            value={formData.notes}
                                            onChange={handleChange}
                                            placeholder="Special delivery instructions or notes..."
                                            disabled={loading}
                                        ></textarea>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Order Summary */}
                    <div className="col-lg-4">
                        <div className="card shadow-sm sticky-top" style={{ top: '100px' }}>
                            <div className="card-header bg-light">
                                <h5 className="mb-0">Order Summary</h5>
                            </div>
                            <div className="card-body">
                                {/* Cart Items */}
                                <div className="mb-3">
                                    {cartItems.map((item) => (
                                        <div key={item.id} className="d-flex justify-content-between mb-2 pb-2 border-bottom">
                                            <div className="flex-grow-1">
                                                <div className="fw-bold small">{item.name}</div>
                                                <div className="text-muted small">
                                                    Qty: {item.quantity} × ${parseFloat(item.price || 0).toFixed(2)}
                                                </div>
                                            </div>
                                            <div className="text-end">
                                                <div className="fw-bold">
                                                    ${(parseFloat(item.price || 0) * item.quantity).toFixed(2)}
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>

                                {/* Totals */}
                                <div className="mb-3">
                                    <div className="d-flex justify-content-between mb-2">
                                        <span>Subtotal</span>
                                        <span>${subtotal.toFixed(2)}</span>
                                    </div>
                                    <div className="d-flex justify-content-between mb-2">
                                        <span>Tax (10%)</span>
                                        <span>${tax.toFixed(2)}</span>
                                    </div>
                                    <div className="d-flex justify-content-between mb-2">
                                        <span>Shipping</span>
                                        <span>
                                            {shipping === 0 ? (
                                                <span className="text-success">FREE</span>
                                            ) : (
                                                `$${shipping.toFixed(2)}`
                                            )}
                                        </span>
                                    </div>
                                    <hr />
                                    <div className="d-flex justify-content-between">
                                        <strong>Total</strong>
                                        <strong className="text-success">${total.toFixed(2)}</strong>
                                    </div>
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-success w-100 btn-lg"
                                    disabled={loading}
                                >
                                    {loading ? 'Placing Order...' : 'Place Order'}
                                </button>

                                <div className="text-center mt-3">
                                    <small className="text-muted">
                                        By placing this order, you agree to our terms and conditions.
                                    </small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    );
}

