import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import OrderStatusBadge from '../components/order/OrderStatusBadge';

/**
 * My Orders Page
 * 
 * Displays all orders placed by the logged-in user.
 * Allows filtering by status and viewing order details.
 */
export default function MyOrders() {
    const { isAuthenticated, user } = useAuth();
    const { showError } = useToast();
    
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [statusFilter, setStatusFilter] = useState('all');

    useEffect(() => {
        if (!isAuthenticated) {
            setError('Please log in to view your orders');
            setLoading(false);
            return;
        }

        fetchOrders();
    }, [isAuthenticated]);

    const fetchOrders = async () => {
        try {
            setLoading(true);
            setError(null);
            
            const ordersData = await api.orders.getAll();
            
            // Filter orders for current user (backend should handle this, but filter client-side as well)
            const userOrders = ordersData.filter(order => 
                order.userId === user?.id || !user?.id // Show all if no user ID yet
            );
            
            // Sort by creation date (newest first)
            userOrders.sort((a, b) => 
                new Date(b.createdAt) - new Date(a.createdAt)
            );
            
            setOrders(userOrders);
        } catch (err) {
            console.error('Error fetching orders:', err);
            setError(err.message || 'Failed to load orders');
            showError('Failed to load your orders');
        } finally {
            setLoading(false);
        }
    };

    const filteredOrders = statusFilter === 'all' 
        ? orders 
        : orders.filter(order => order.status === statusFilter.toUpperCase());

    const statusCounts = {
        all: orders.length,
        pending: orders.filter(o => o.status === 'PENDING').length,
        confirmed: orders.filter(o => o.status === 'CONFIRMED').length,
        processing: orders.filter(o => o.status === 'PROCESSING').length,
        shipped: orders.filter(o => o.status === 'SHIPPED').length,
        delivered: orders.filter(o => o.status === 'DELIVERED').length,
        cancelled: orders.filter(o => o.status === 'CANCELLED').length
    };

    if (!isAuthenticated) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Please Log In</h2>
                    <p className="text-muted">You need to be logged in to view your orders.</p>
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
                <h1 className="fw-bold">My Orders</h1>
                <Link to="/products" className="btn btn-success">
                    Continue Shopping
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
                    <p className="text-muted mt-2">Loading your orders...</p>
                </div>
            )}

            {/* Error State */}
            {error && !loading && (
                <div className="alert alert-danger">
                    <strong>Error:</strong> {error}
                    <button 
                        className="btn btn-sm btn-outline-danger ms-2"
                        onClick={fetchOrders}
                    >
                        Retry
                    </button>
                </div>
            )}

            {/* Orders List */}
            {!loading && !error && (
                <>
                    {filteredOrders.length === 0 ? (
                        <div className="text-center py-5">
                            <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>📦</div>
                            <h4 className="text-muted mb-3">No Orders Found</h4>
                            <p className="text-muted">
                                {statusFilter === 'all' 
                                    ? "You haven't placed any orders yet."
                                    : `No orders with status "${statusFilter}".`}
                            </p>
                            <Link to="/products" className="btn btn-success mt-3">
                                Browse Products
                            </Link>
                        </div>
                    ) : (
                        <div className="row g-4">
                            {filteredOrders.map((order) => (
                                <div key={order.id} className="col-12">
                                    <div className="card shadow-sm">
                                        <div className="card-body">
                                            <div className="row align-items-center">
                                                <div className="col-md-3">
                                                    <div className="mb-2">
                                                        <strong>Order ID:</strong>
                                                    </div>
                                                    <div className="text-muted small mb-2">
                                                        {order.id}
                                                    </div>
                                                    <OrderStatusBadge status={order.status} />
                                                </div>
                                                <div className="col-md-3">
                                                    <div className="mb-2">
                                                        <strong>Date:</strong>
                                                    </div>
                                                    <div className="text-muted">
                                                        {new Date(order.createdAt).toLocaleDateString()}
                                                    </div>
                                                    <div className="text-muted small">
                                                        {new Date(order.createdAt).toLocaleTimeString()}
                                                    </div>
                                                </div>
                                                <div className="col-md-3">
                                                    <div className="mb-2">
                                                        <strong>Items:</strong>
                                                    </div>
                                                    <div className="text-muted">
                                                        {order.items?.length || 0} item(s)
                                                    </div>
                                                </div>
                                                <div className="col-md-3 text-md-end">
                                                    <div className="mb-2">
                                                        <strong>Total:</strong>
                                                    </div>
                                                    <div className="h5 text-success mb-0">
                                                        ${parseFloat(order.totalAmount || 0).toFixed(2)}
                                                    </div>
                                                    <Link 
                                                        to={`/orders/${order.id}`}
                                                        className="btn btn-outline-success btn-sm mt-2"
                                                    >
                                                        View Details
                                                    </Link>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </>
            )}
        </div>
    );
}



