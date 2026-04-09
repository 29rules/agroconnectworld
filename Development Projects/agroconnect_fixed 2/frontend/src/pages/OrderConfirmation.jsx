import React from 'react';
import { useParams, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import OrderStatusBadge from '../components/order/OrderStatusBadge';

/**
 * Order Confirmation Page
 * 
 * Displays order confirmation after successful checkout.
 */
export default function OrderConfirmation() {
    const { id } = useParams();
    const location = useLocation();
    const { isAuthenticated } = useAuth();
    
    const order = location.state?.order;

    if (!isAuthenticated) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Please Log In</h2>
                    <p className="text-muted">You need to be logged in to view order confirmation.</p>
                    <Link to="/login" className="btn btn-success">
                        Go to Login
                    </Link>
                </div>
            </div>
        );
    }

    if (!order) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Order Not Found</h2>
                    <p className="text-muted">The order confirmation could not be found.</p>
                    <Link to="/orders" className="btn btn-success">
                        View My Orders
                    </Link>
                </div>
            </div>
        );
    }

    return (
        <div className="container py-5" style={{ paddingTop: '100px' }}>
            <div className="row justify-content-center">
                <div className="col-lg-8">
                    <div className="text-center mb-5">
                        <div style={{ fontSize: '5rem', marginBottom: '1rem' }}>✅</div>
                        <h1 className="fw-bold text-success">Order Confirmed!</h1>
                        <p className="text-muted">Thank you for your order. We've received your order and will process it shortly.</p>
                    </div>

                    <div className="card shadow-sm mb-4">
                        <div className="card-header bg-light d-flex justify-content-between align-items-center">
                            <h5 className="mb-0">Order Details</h5>
                            <OrderStatusBadge status={order.status} />
                        </div>
                        <div className="card-body">
                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <p className="mb-1">
                                        <strong>Order ID:</strong>
                                    </p>
                                    <p className="text-muted">{order.id}</p>
                                </div>
                                <div className="col-md-6">
                                    <p className="mb-1">
                                        <strong>Order Date:</strong>
                                    </p>
                                    <p className="text-muted">
                                        {new Date(order.createdAt).toLocaleString()}
                                    </p>
                                </div>
                            </div>

                            <div className="mb-3">
                                <h6 className="fw-bold mb-3">Order Items</h6>
                                {order.items && order.items.length > 0 ? (
                                    <div className="table-responsive">
                                        <table className="table table-sm">
                                            <thead>
                                                <tr>
                                                    <th>Product</th>
                                                    <th>Quantity</th>
                                                    <th className="text-end">Price</th>
                                                    <th className="text-end">Total</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {order.items.map((item, index) => (
                                                    <tr key={index}>
                                                        <td>
                                                            {item.productName || `Product ${item.productId?.substring(0, 8)}...`}
                                                        </td>
                                                        <td>{item.quantity}</td>
                                                        <td className="text-end">${parseFloat(item.price || 0).toFixed(2)}</td>
                                                        <td className="text-end">
                                                            ${(parseFloat(item.price || 0) * item.quantity).toFixed(2)}
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                            <tfoot>
                                                <tr>
                                                    <td colSpan="3" className="text-end fw-bold">Total:</td>
                                                    <td className="text-end fw-bold text-success">
                                                        ${parseFloat(order.totalAmount || 0).toFixed(2)}
                                                    </td>
                                                </tr>
                                            </tfoot>
                                        </table>
                                    </div>
                                ) : (
                                    <p className="text-muted">No items found</p>
                                )}
                            </div>
                        </div>
                    </div>

                    <div className="card shadow-sm mb-4">
                        <div className="card-header bg-light">
                            <h5 className="mb-0">What's Next?</h5>
                        </div>
                        <div className="card-body">
                            <ul className="list-unstyled mb-0">
                                <li className="mb-2">
                                    <strong>1.</strong> You will receive an email confirmation shortly
                                </li>
                                <li className="mb-2">
                                    <strong>2.</strong> We'll process your order and notify you when it ships
                                </li>
                                <li className="mb-2">
                                    <strong>3.</strong> Track your order status in <Link to="/orders">My Orders</Link>
                                </li>
                            </ul>
                        </div>
                    </div>

                    <div className="text-center">
                        <Link to="/orders" className="btn btn-success me-2">
                            View My Orders
                        </Link>
                        <Link to="/products" className="btn btn-outline-success">
                            Continue Shopping
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}



