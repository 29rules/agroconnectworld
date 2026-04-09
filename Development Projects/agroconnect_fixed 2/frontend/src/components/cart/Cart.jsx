import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../../context/CartContext';

export default function Cart() {
    const { 
        cartItems, 
        removeFromCart, 
        updateQuantity, 
        clearCart, 
        isCartOpen, 
        setIsCartOpen 
    } = useCart();

    const handleQuantityChange = (productId, newQuantity) => {
        const quantity = parseInt(newQuantity) || 0;
        updateQuantity(productId, quantity);
    };

    return (
        <>
            {/* Cart Overlay */}
            {isCartOpen && (
                <div
                    className="cart-overlay"
                    style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0, 0, 0, 0.5)',
                        zIndex: 1040,
                        animation: 'fadeIn 0.3s ease'
                    }}
                    onClick={() => setIsCartOpen(false)}
                />
            )}

            {/* Cart Sidebar */}
            <div
                className={`cart-sidebar ${isCartOpen ? 'open' : ''}`}
                style={{
                    position: 'fixed',
                    top: 0,
                    right: isCartOpen ? 0 : '-450px',
                    width: '450px',
                    height: '100vh',
                    backgroundColor: 'white',
                    boxShadow: '-2px 0 10px rgba(0, 0, 0, 0.1)',
                    zIndex: 1050,
                    transition: 'right 0.3s ease',
                    display: 'flex',
                    flexDirection: 'column',
                    overflow: 'hidden'
                }}
            >
                {/* Cart Header */}
                <div
                    style={{
                        padding: '1.5rem',
                        borderBottom: '1px solid #e9ecef',
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        backgroundColor: 'var(--agro-green-light)',
                        color: 'white'
                    }}
                >
                    <h3 className="mb-0 fw-bold">Shopping Cart</h3>
                    <button
                        onClick={() => setIsCartOpen(false)}
                        style={{
                            background: 'none',
                            border: 'none',
                            color: 'white',
                            fontSize: '1.5rem',
                            cursor: 'pointer',
                            padding: '0',
                            width: '30px',
                            height: '30px',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                        }}
                    >
                        ×
                    </button>
                </div>

                {/* Cart Items */}
                <div
                    style={{
                        flex: 1,
                        overflowY: 'auto',
                        padding: '1rem'
                    }}
                >
                    {cartItems.length === 0 ? (
                        <div className="text-center py-5">
                            <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🛒</div>
                            <p className="text-muted">Your cart is empty</p>
                            <Link
                                to="/products"
                                className="btn btn-success mt-3"
                                onClick={() => setIsCartOpen(false)}
                            >
                                Browse Products
                            </Link>
                        </div>
                    ) : (
                        <>
                            {cartItems.map((item) => (
                                <div
                                    key={item.id}
                                    className="card mb-3 border-0 shadow-sm"
                                >
                                    <div className="card-body p-3">
                                        <div className="d-flex gap-3">
                                            <div
                                                style={{
                                                    fontSize: '3rem',
                                                    minWidth: '60px',
                                                    textAlign: 'center'
                                                }}
                                            >
                                                {item.image || '📦'}
                                            </div>
                                            <div style={{ flex: 1 }}>
                                                <h6 className="fw-bold mb-1" style={{ fontSize: '0.9rem' }}>
                                                    {item.name}
                                                </h6>
                                                <p className="text-muted small mb-2">{item.category}</p>
                                                
                                                <div className="d-flex align-items-center gap-2 mb-2">
                                                    <label className="small mb-0">Qty:</label>
                                                    <input
                                                        type="number"
                                                        min="1"
                                                        value={item.quantity}
                                                        onChange={(e) => handleQuantityChange(item.id, e.target.value)}
                                                        style={{
                                                            width: '60px',
                                                            padding: '0.25rem',
                                                            border: '1px solid #ced4da',
                                                            borderRadius: '4px',
                                                            textAlign: 'center'
                                                        }}
                                                    />
                                                </div>
                                                
                                                <button
                                                    onClick={() => removeFromCart(item.id)}
                                                    className="btn btn-sm btn-outline-danger"
                                                    style={{ fontSize: '0.8rem' }}
                                                >
                                                    Remove
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </>
                    )}
                </div>

                {/* Cart Footer */}
                {cartItems.length > 0 && (
                    <div
                        style={{
                            padding: '1.5rem',
                            borderTop: '1px solid #e9ecef',
                            backgroundColor: '#f8f9fa'
                        }}
                    >
                        <div className="d-flex justify-content-between mb-3">
                            <span className="fw-bold">Total Items:</span>
                            <span className="fw-bold">{cartItems.reduce((sum, item) => sum + item.quantity, 0)}</span>
                        </div>
                        <div className="d-flex gap-2">
                            <button
                                onClick={clearCart}
                                className="btn btn-outline-secondary flex-fill"
                            >
                                Clear Cart
                            </button>
                            <Link
                                to="/cart"
                                className="btn btn-success flex-fill"
                                onClick={() => setIsCartOpen(false)}
                            >
                                View Cart
                            </Link>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
}



