import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';

export default function CartPage() {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const { 
        cartItems, 
        removeFromCart, 
        updateQuantity, 
        clearCart 
    } = useCart();

    const handleQuantityChange = (productId, newQuantity) => {
        const quantity = parseInt(newQuantity) || 0;
        updateQuantity(productId, quantity);
    };

    const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
    const subtotal = cartItems.reduce((sum, item) => {
        const price = parseFloat(item.price) || 0;
        return sum + (price * item.quantity);
    }, 0);

    const handleCheckout = () => {
        if (!isAuthenticated) {
            navigate('/login', { state: { from: '/cart' } });
            return;
        }
        navigate('/checkout');
    };

    return (
        <div className="py-5" style={{ paddingTop: '100px' }}>
            <div className="container">
                <div className="row">
                    <div className="col-12">
                        <h1 className="fw-bold mb-4">Shopping Cart</h1>
                    </div>
                </div>

                {cartItems.length === 0 ? (
                    <div className="text-center py-5">
                        <div style={{ fontSize: '5rem', marginBottom: '2rem' }}>🛒</div>
                        <h3 className="mb-3">Your cart is empty</h3>
                        <p className="text-muted mb-4">
                            Start adding products to your cart to see them here.
                        </p>
                        <Link to="/products" className="btn btn-success btn-lg px-5">
                            Browse Products
                        </Link>
                    </div>
                ) : (
                    <div className="row g-4">
                        {/* Cart Items */}
                        <div className="col-lg-8">
                            <div className="d-flex justify-content-between align-items-center mb-4">
                                <h3 className="mb-0">Items ({totalItems})</h3>
                                <button
                                    onClick={clearCart}
                                    className="btn btn-outline-danger btn-sm"
                                >
                                    Clear Cart
                                </button>
                            </div>

                            {cartItems.map((item) => (
                                <div key={item.id} className="card mb-3 border-0 shadow-sm">
                                    <div className="card-body p-4">
                                        <div className="row align-items-center">
                                            <div className="col-md-2 text-center mb-3 mb-md-0">
                                                <div style={{ fontSize: '4rem' }}>
                                                    {item.image || '📦'}
                                                </div>
                                            </div>
                                            <div className="col-md-6">
                                                <h5 className="fw-bold mb-2">{item.name}</h5>
                                                <p className="text-muted small mb-2">{item.category}</p>
                                                <p className="text-muted small mb-0">{item.description}</p>
                                            </div>
                                            <div className="col-md-2">
                                                <label className="form-label small fw-bold">Quantity</label>
                                                <input
                                                    type="number"
                                                    min="1"
                                                    value={item.quantity}
                                                    onChange={(e) => handleQuantityChange(item.id, e.target.value)}
                                                    className="form-control"
                                                />
                                            </div>
                                            <div className="col-md-2 text-md-end">
                                                <button
                                                    onClick={() => removeFromCart(item.id)}
                                                    className="btn btn-outline-danger btn-sm mt-md-4"
                                                >
                                                    Remove
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>

                        {/* Cart Summary */}
                        <div className="col-lg-4">
                            <div className="card border-0 shadow-sm sticky-top" style={{ top: '120px' }}>
                                <div className="card-body p-4">
                                    <h4 className="fw-bold mb-4">Order Summary</h4>
                                    
                                    <div className="d-flex justify-content-between mb-3">
                                        <span>Total Items:</span>
                                        <span className="fw-bold">{totalItems}</span>
                                    </div>
                                    
                                    <div className="d-flex justify-content-between mb-3">
                                        <span>Subtotal:</span>
                                        <span className="fw-bold">${subtotal.toFixed(2)}</span>
                                    </div>
                                    
                                    <hr />
                                    
                                    <div className="d-flex justify-content-between mb-4">
                                        <span className="fw-bold">Total:</span>
                                        <span className="fw-bold text-success">${subtotal.toFixed(2)}</span>
                                    </div>

                                    <div className="d-grid gap-2">
                                        <button 
                                            className="btn btn-success btn-lg"
                                            onClick={handleCheckout}
                                        >
                                            Proceed to Checkout
                                        </button>
                                        <Link to="/products" className="btn btn-outline-success">
                                            Continue Shopping
                                        </Link>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}



