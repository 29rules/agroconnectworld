import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';

/**
 * Supplier Dashboard
 * 
 * Main dashboard for suppliers to manage their business on AgroConnectWorld.
 * Provides quick access to products, orders, quotes, and analytics.
 */
export default function SupplierDashboard() {
    const { isAuthenticated, user } = useAuth();
    const { showError } = useToast();
    
    const [stats, setStats] = useState({
        products: 0,
        orders: 0,
        quotes: 0,
        revenue: 0
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!isAuthenticated) {
            setLoading(false);
            return;
        }

        if (user?.role !== 'SUPPLIER') {
            setLoading(false);
            return;
        }

        fetchDashboardStats();
    }, [isAuthenticated, user]);

    const fetchDashboardStats = async () => {
        try {
            setLoading(true);
            
            // Fetch all data to calculate stats
            const [productsData, ordersData, quotesData] = await Promise.all([
                api.products.getAll().catch(() => []),
                api.orders.getAll().catch(() => []),
                api.quotes.getAll().catch(() => [])
            ]);

            // Calculate stats (in a real app, backend would provide these)
            setStats({
                products: productsData.length || 0,
                orders: ordersData.length || 0,
                quotes: quotesData.length || 0,
                revenue: ordersData.reduce((sum, order) => 
                    sum + parseFloat(order.totalAmount || 0), 0
                )
            });
        } catch (err) {
            console.error('Error fetching dashboard stats:', err);
            showError('Failed to load dashboard statistics');
        } finally {
            setLoading(false);
        }
    };

    if (!isAuthenticated) {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Please Log In</h2>
                    <p className="text-muted">You need to be logged in to access the supplier dashboard.</p>
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
                    <p className="text-muted">This dashboard is only available for suppliers.</p>
                    <Link to="/" className="btn btn-success">
                        Go to Home
                    </Link>
                </div>
            </div>
        );
    }

    const dashboardCards = [
        {
            title: 'My Products',
            value: stats.products,
            icon: '📦',
            link: '/supplier/products',
            color: 'primary'
        },
        {
            title: 'Pending Quotes',
            value: stats.quotes,
            icon: '📋',
            link: '/quotes/manage',
            color: 'warning'
        },
        {
            title: 'Orders',
            value: stats.orders,
            icon: '🛒',
            link: '/supplier/orders',
            color: 'success'
        },
        {
            title: 'Total Revenue',
            value: `$${stats.revenue.toFixed(2)}`,
            icon: '💰',
            link: '/supplier/analytics',
            color: 'info'
        }
    ];

    const quickActions = [
        {
            title: 'Add New Product',
            description: 'List a new product on the marketplace',
            link: '/supplier/products/new',
            icon: '➕',
            color: 'success'
        },
        {
            title: 'Manage Quotes',
            description: 'Review and respond to quote requests',
            link: '/quotes/manage',
            icon: '📋',
            color: 'warning'
        },
        {
            title: 'View Orders',
            description: 'Track and manage customer orders',
            link: '/supplier/orders',
            icon: '🛒',
            color: 'primary'
        },
        {
            title: 'Analytics',
            description: 'View sales and performance metrics',
            link: '/supplier/analytics',
            icon: '📊',
            color: 'info'
        }
    ];

    return (
        <div className="container py-5" style={{ paddingTop: '100px' }}>
            <div className="mb-4">
                <h1 className="fw-bold">Supplier Dashboard</h1>
                <p className="text-muted">Welcome back, {user?.name || 'Supplier'}!</p>
            </div>

            {/* Stats Cards */}
            <div className="row g-4 mb-5">
                {dashboardCards.map((card, index) => (
                    <div key={index} className="col-md-6 col-lg-3">
                        <Link 
                            to={card.link} 
                            className="text-decoration-none"
                        >
                            <div className={`card shadow-sm border-0 h-100 hover-card`} style={{ cursor: 'pointer' }}>
                                <div className="card-body text-center">
                                    <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>
                                        {card.icon}
                                    </div>
                                    <h3 className={`text-${card.color} mb-2`}>
                                        {loading ? '...' : card.value}
                                    </h3>
                                    <p className="text-muted mb-0">{card.title}</p>
                                </div>
                            </div>
                        </Link>
                    </div>
                ))}
            </div>

            {/* Quick Actions */}
            <div className="row mb-5">
                <div className="col-12">
                    <h3 className="fw-bold mb-4">Quick Actions</h3>
                </div>
                {quickActions.map((action, index) => (
                    <div key={index} className="col-md-6 col-lg-3 mb-3">
                        <Link 
                            to={action.link} 
                            className="text-decoration-none"
                        >
                            <div className={`card shadow-sm border-0 h-100 hover-card`} style={{ cursor: 'pointer' }}>
                                <div className="card-body">
                                    <div className="d-flex align-items-start mb-3">
                                        <div style={{ fontSize: '2rem', marginRight: '1rem' }}>
                                            {action.icon}
                                        </div>
                                        <div className="flex-grow-1">
                                            <h5 className={`text-${action.color} mb-2`}>
                                                {action.title}
                                            </h5>
                                            <p className="text-muted small mb-0">
                                                {action.description}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </Link>
                    </div>
                ))}
            </div>

            {/* Recent Activity Placeholder */}
            <div className="row">
                <div className="col-12">
                    <div className="card shadow-sm">
                        <div className="card-header bg-light">
                            <h5 className="mb-0">Recent Activity</h5>
                        </div>
                        <div className="card-body">
                            <p className="text-muted text-center py-4">
                                Recent activity will be displayed here.
                                <br />
                                <small>This feature is coming soon.</small>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}



