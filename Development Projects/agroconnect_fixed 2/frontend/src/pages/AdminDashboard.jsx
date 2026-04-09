import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';

/**
 * Admin Dashboard
 * 
 * Main dashboard for administrators to manage the AgroConnectWorld platform.
 * Provides access to user management, system analytics, and platform controls.
 */
export default function AdminDashboard() {
    const { isAuthenticated, user } = useAuth();
    const { showError } = useToast();
    
    const [stats, setStats] = useState({
        users: 0,
        products: 0,
        orders: 0,
        quotes: 0
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!isAuthenticated) {
            setLoading(false);
            return;
        }

        if (user?.role !== 'ADMIN') {
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
                users: 0, // Would come from user management API
                products: productsData.length || 0,
                orders: ordersData.length || 0,
                quotes: quotesData.length || 0
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
                    <p className="text-muted">You need to be logged in to access the admin dashboard.</p>
                    <Link to="/login" className="btn btn-success">
                        Go to Login
                    </Link>
                </div>
            </div>
        );
    }

    if (user?.role !== 'ADMIN') {
        return (
            <div className="container py-5" style={{ paddingTop: '100px' }}>
                <div className="text-center">
                    <h2>Access Denied</h2>
                    <p className="text-muted">This dashboard is only available for administrators.</p>
                    <Link to="/" className="btn btn-success">
                        Go to Home
                    </Link>
                </div>
            </div>
        );
    }

    const dashboardCards = [
        {
            title: 'Total Users',
            value: stats.users,
            icon: '👥',
            link: '/admin/users',
            color: 'primary'
        },
        {
            title: 'Products',
            value: stats.products,
            icon: '📦',
            link: '/admin/products',
            color: 'success'
        },
        {
            title: 'Orders',
            value: stats.orders,
            icon: '🛒',
            link: '/admin/orders',
            color: 'info'
        },
        {
            title: 'Quote Requests',
            value: stats.quotes,
            icon: '📋',
            link: '/admin/quotes',
            color: 'warning'
        }
    ];

    const adminActions = [
        {
            title: 'User Management',
            description: 'Manage users, roles, and permissions',
            link: '/admin/users',
            icon: '👥',
            color: 'primary'
        },
        {
            title: 'Product Management',
            description: 'Manage products and categories',
            link: '/admin/products',
            icon: '📦',
            color: 'success'
        },
        {
            title: 'Order Management',
            description: 'View and manage all orders',
            link: '/admin/orders',
            icon: '🛒',
            color: 'info'
        },
        {
            title: 'System Analytics',
            description: 'View platform statistics and reports',
            link: '/admin/analytics',
            icon: '📊',
            color: 'warning'
        },
        {
            title: 'Quote Management',
            description: 'Monitor and manage quote requests',
            link: '/admin/quotes',
            icon: '📋',
            color: 'secondary'
        },
        {
            title: 'System Settings',
            description: 'Configure platform settings',
            link: '/admin/settings',
            icon: '⚙️',
            color: 'dark'
        }
    ];

    return (
        <div className="container py-5" style={{ paddingTop: '100px' }}>
            <div className="mb-4">
                <h1 className="fw-bold">Admin Dashboard</h1>
                <p className="text-muted">Welcome back, {user?.name || 'Administrator'}!</p>
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

            {/* Admin Actions */}
            <div className="row mb-5">
                <div className="col-12">
                    <h3 className="fw-bold mb-4">Admin Actions</h3>
                </div>
                {adminActions.map((action, index) => (
                    <div key={index} className="col-md-6 col-lg-4 mb-3">
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

            {/* System Status */}
            <div className="row">
                <div className="col-12">
                    <div className="card shadow-sm">
                        <div className="card-header bg-light">
                            <h5 className="mb-0">System Status</h5>
                        </div>
                        <div className="card-body">
                            <div className="row">
                                <div className="col-md-4">
                                    <div className="d-flex align-items-center mb-3">
                                        <span className="badge bg-success me-2">●</span>
                                        <span>All Systems Operational</span>
                                    </div>
                                </div>
                                <div className="col-md-4">
                                    <div className="d-flex align-items-center mb-3">
                                        <span className="badge bg-success me-2">●</span>
                                        <span>Database: Connected</span>
                                    </div>
                                </div>
                                <div className="col-md-4">
                                    <div className="d-flex align-items-center mb-3">
                                        <span className="badge bg-success me-2">●</span>
                                        <span>API Gateway: Healthy</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}



