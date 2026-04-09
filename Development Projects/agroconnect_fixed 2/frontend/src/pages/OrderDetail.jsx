import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import OrderStatusBadge from '../components/order/OrderStatusBadge';

/**
 * Order Detail Page
 * 
 * Shows detailed information about a specific order.
 */
export default function OrderDetail() {
    const { id } = useParams();
    const { isAuthenticated, user } = useAuth();
    const { showError } = useToast();
    
    const [order, setOrder] = useState(null);
    const [products, setProducts] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!isAuthenticated) {
            setError('Please log in to view order details');
            setLoading(false);
            return;
        }

        fetchOrder();
    }, [id, isAuthenticated]);

    const fetchOrder = async () => {
        try {
            setLoading(true);
            setError(null);
            
            const orderData = await api.orders.getById(id);
            setOrder(orderData);
            
            // Fetch product details for each order item
            if (orderData.items && orderData.items.length > 0) {
                const productIds = [...new Set(orderData.items.map(item => item.productId))];
                const productPromises = productIds.map(productId => 
                    api.products.getById(productId).catch(() => null)
                );
                const productResults = await Promise.all(productPromises);
                
                const productMap = {};
                productResults.forEach((product, index) => {
                    if (product) {
                        productMap[productIds[index]] = product;
                    }
                });
                setProducts(productMap);
            }
        } catch (err) {
            console.error('Error fetching order:', err);
            setError(err.message || 'Failed to load order details');
            showError('Failed to load order details');
        } finally {
            setLoading(false);
        }
    };

    if (!isAuthenticated) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Please Log In</h2>
                    <p className="text-muted">You need to be logged in to view order details.</p>
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
                    <p className="text-muted mt-2">Loading order details...</p>
                </div>
            </div>
        );
    }

    if (error || !order) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Order Not Found</h2>
                    <p className="text-muted">{error || 'The order you are looking for does not exist.'}</p>
                    <Link to="/orders" className="btn btn-success">
                        Back to My Orders
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
                        <Link to="/orders">My Orders</Link>
                    </li>
                    <li className="breadcrumb-item active" aria-current="page">
                        Order #{order.id.substring(0, 8)}
                    </li>
                </ol>
            </nav>

            <div className="row">
                <div className="col-lg-8">
                    <div className="card shadow-sm mb-4">
                        <div className="card-header bg-light d-flex justify-content-between align-items-center">
                            <h4 className="mb-0">Order Details</h4>
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
                                        <table className="table">
                                            <thead>
                                                <tr>
                                                    <th>Product</th>
                                                    <th>Quantity</th>
                                                    <th className="text-end">Unit Price</th>
                                                    <th className="text-end">Total</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {order.items.map((item, index) => {
                                                    const product = products[item.productId];
                                                    return (
                                                        <tr key={index}>
                                                            <td>
                                                                {product ? (
                                                                    <Link 
                                                                        to={`/products/${product.id}`}
                                                                        className="text-decoration-none"
                                                                    >
                                                                        {product.name}
                                                                    </Link>
                                                                ) : (
                                                                    <span className="text-muted">
                                                                        Product {item.productId?.substring(0, 8)}...
                                                                    </span>
                                                                )}
                                                            </td>
                                                            <td>{item.quantity}</td>
                                                            <td className="text-end">${parseFloat(item.price || 0).toFixed(2)}</td>
                                                            <td className="text-end">
                                                                ${(parseFloat(item.price || 0) * item.quantity).toFixed(2)}
                                                            </td>
                                                        </tr>
                                                    );
                                                })}
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
                </div>

                <div className="col-lg-4">
                    <div className="card shadow-sm">
                        <div className="card-header bg-light">
                            <h5 className="mb-0">Order Information</h5>
                        </div>
                        <div className="card-body">
                            <div className="mb-3">
                                <strong>Status:</strong>
                                <div className="mt-1">
                                    <OrderStatusBadge status={order.status} />
                                </div>
                            </div>
                            <div className="mb-3">
                                <strong>Total Amount:</strong>
                                <div className="h5 text-success mt-1">
                                    ${parseFloat(order.totalAmount || 0).toFixed(2)}
                                </div>
                            </div>
                            <div className="mb-3">
                                <strong>Items:</strong>
                                <div className="text-muted">
                                    {order.items?.length || 0} item(s)
                                </div>
                            </div>
                            <div className="mb-3">
                                <strong>Last Updated:</strong>
                                <div className="text-muted small">
                                    {new Date(order.updatedAt).toLocaleString()}
                                </div>
                            </div>
                            <Link 
                                to="/orders" 
                                className="btn btn-outline-secondary w-100"
                            >
                                Back to Orders
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}



