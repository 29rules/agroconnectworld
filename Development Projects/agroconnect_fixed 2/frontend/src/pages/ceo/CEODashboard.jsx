import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import api from '../../services/api';
import EngineeringPanel from './EngineeringPanel';
import QAPanel from './QAPanel';
import ProductPanel from './ProductPanel';
import ScrumPanel from './ScrumPanel';
import DevOpsPanel from './DevOpsPanel';
import AgentChat from './AgentChat';
import Alerts from './Alerts';
import EnterpriseDashboard from './EnterpriseDashboard';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * CEO Dashboard
 * 
 * Executive-level dashboard for the CEO of AgroConnectWorld.
 * Features dark theme header with clean white content area.
 * Includes navigation tabs for different panels.
 */
export default function CEODashboard() {
    const { isAuthenticated, user } = useAuth();
    const { showError, showSuccess } = useToast();
    const navigate = useNavigate();
    
    const [activeTab, setActiveTab] = useState('overview');
    const [stats, setStats] = useState({
        totalUsers: 0,
        totalProducts: 0,
        totalOrders: 0,
        totalRevenue: 0,
        activeSuppliers: 0,
        activeBuyers: 0,
        pendingQuotes: 0,
        completedOrders: 0
    });
    const [loading, setLoading] = useState(true);
    const [recentActivity, setRecentActivity] = useState([]);

    useEffect(() => {
        if (!isAuthenticated) {
            setLoading(false);
            return;
        }

        if (user?.role !== 'CEO') {
            setLoading(false);
            return;
        }

        fetchDashboardData();
    }, [isAuthenticated, user]);

    const fetchDashboardData = async () => {
        try {
            setLoading(true);
            
            // Fetch CEO status from AI Company API
            const ceoStatus = await api.ai.getCEOStatus().catch(() => null);
            
            // Also fetch from backend services
            const [productsData, ordersData, quotesData] = await Promise.all([
                api.products.getAll().catch(() => []),
                api.orders.getAll().catch(() => []),
                api.quotes.getAll().catch(() => [])
            ]);

            // Use AI Company data if available, otherwise calculate from backend
            const metrics = ceoStatus?.metrics || {};
            const totalUsers = metrics.totalUsers || 0;
            const totalProducts = metrics.totalProducts || productsData.length || 0;
            const totalOrders = metrics.totalOrders || ordersData.length || 0;
            const totalRevenue = metrics.totalRevenue || ordersData.reduce((sum, order) => sum + (order.totalAmount || 0), 0);
            const activeSuppliers = metrics.activeSuppliers || 0;
            const activeBuyers = metrics.activeBuyers || 0;
            const pendingQuotes = quotesData.filter(q => q.status === 'PENDING').length || 0;
            const completedOrders = ordersData.filter(o => o.status === 'COMPLETED').length || 0;

            setStats({
                totalUsers,
                totalProducts,
                totalOrders,
                totalRevenue,
                activeSuppliers,
                activeBuyers,
                pendingQuotes,
                completedOrders
            });

            // Set recent activity (mock data - would come from backend)
            setRecentActivity([
                { type: 'order', message: 'New order placed', time: '2 hours ago' },
                { type: 'quote', message: 'Quote request submitted', time: '3 hours ago' },
                { type: 'user', message: 'New supplier registered', time: '5 hours ago' },
            ]);

        } catch (error) {
            console.error('Error fetching CEO dashboard data:', error);
            showError('Failed to load dashboard data');
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh', backgroundColor: '#f8f9fa' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    if (user?.role !== 'CEO') {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh', backgroundColor: '#f8f9fa' }}>
                <div className="card shadow-sm" style={{ maxWidth: '500px' }}>
                    <div className="card-body text-center p-5">
                        <i className="bi bi-shield-exclamation text-danger" style={{ fontSize: '3rem' }}></i>
                        <h4 className="mt-3 mb-2">Access Denied</h4>
                        <p className="text-muted">You do not have permission to access the CEO Dashboard.</p>
                        <p className="mb-0">Required role: <strong>CEO</strong></p>
                    </div>
                </div>
            </div>
        );
    }

    const tabs = [
        { id: 'enterprise', label: 'Enterprise Dashboard', icon: 'bi-speedometer2' },
        { id: 'overview', label: 'Overview', icon: 'bi-graph-up' },
        { id: 'engineering', label: 'Engineering', icon: 'bi-code-slash' },
        { id: 'qa', label: 'QA', icon: 'bi-shield-check' },
        { id: 'product', label: 'Product', icon: 'bi-box-seam' },
        { id: 'scrum', label: 'Scrum', icon: 'bi-kanban' },
        { id: 'devops', label: 'DevOps', icon: 'bi-gear' },
        { id: 'chat', label: 'AI Chat', icon: 'bi-chat-dots' },
        { id: 'alerts', label: 'Alerts', icon: 'bi-bell' },
    ];

    const renderContent = () => {
        switch (activeTab) {
            case 'enterprise':
                return <EnterpriseDashboard />;
            case 'engineering':
                return <EngineeringPanel />;
            case 'qa':
                return <QAPanel />;
            case 'product':
                return <ProductPanel />;
            case 'scrum':
                return <ScrumPanel />;
            case 'devops':
                return <DevOpsPanel />;
            case 'chat':
                return <AgentChat />;
            case 'alerts':
                return <Alerts />;
            default:
                return renderOverview();
        }
    };

    const renderOverview = () => {
        return (
            <div className="container-fluid py-4">
                {/* Key Metrics Cards */}
                <div className="row g-4 mb-4">
                    <div className="col-md-3">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body">
                                <div className="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem', fontWeight: '600' }}>Total Revenue</h6>
                                        <h3 className="mb-0 text-success fw-bold">${stats.totalRevenue.toLocaleString()}</h3>
                                    </div>
                                    <div className="text-success">
                                        <i className="bi bi-currency-dollar" style={{ fontSize: '2.5rem', opacity: 0.3 }}></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body">
                                <div className="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem', fontWeight: '600' }}>Total Users</h6>
                                        <h3 className="mb-0 fw-bold">{stats.totalUsers.toLocaleString()}</h3>
                                    </div>
                                    <div className="text-primary">
                                        <i className="bi bi-people" style={{ fontSize: '2.5rem', opacity: 0.3 }}></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body">
                                <div className="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem', fontWeight: '600' }}>Total Orders</h6>
                                        <h3 className="mb-0 fw-bold">{stats.totalOrders.toLocaleString()}</h3>
                                    </div>
                                    <div className="text-info">
                                        <i className="bi bi-cart-check" style={{ fontSize: '2.5rem', opacity: 0.3 }}></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body">
                                <div className="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem', fontWeight: '600' }}>Total Products</h6>
                                        <h3 className="mb-0 fw-bold">{stats.totalProducts.toLocaleString()}</h3>
                                    </div>
                                    <div className="text-warning">
                                        <i className="bi bi-box-seam" style={{ fontSize: '2.5rem', opacity: 0.3 }}></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Secondary Metrics */}
                <div className="row g-4 mb-4">
                    <div className="col-md-3">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body text-center">
                                <i className="bi bi-truck text-primary mb-2" style={{ fontSize: '1.5rem' }}></i>
                                <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem', fontWeight: '600' }}>Active Suppliers</h6>
                                <h4 className="mb-0 fw-bold">{stats.activeSuppliers}</h4>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body text-center">
                                <i className="bi bi-person-check text-success mb-2" style={{ fontSize: '1.5rem' }}></i>
                                <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem', fontWeight: '600' }}>Active Buyers</h6>
                                <h4 className="mb-0 fw-bold">{stats.activeBuyers}</h4>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body text-center">
                                <i className="bi bi-file-earmark-text text-warning mb-2" style={{ fontSize: '1.5rem' }}></i>
                                <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem', fontWeight: '600' }}>Pending Quotes</h6>
                                <h4 className="mb-0 fw-bold">{stats.pendingQuotes}</h4>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card border-0 shadow-sm h-100">
                            <div className="card-body text-center">
                                <i className="bi bi-check-circle text-info mb-2" style={{ fontSize: '1.5rem' }}></i>
                                <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem', fontWeight: '600' }}>Completed Orders</h6>
                                <h4 className="mb-0 fw-bold">{stats.completedOrders}</h4>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Recent Activity & Quick Actions */}
                <div className="row g-4">
                    <div className="col-md-8">
                        <div className="card border-0 shadow-sm">
                            <div className="card-header bg-white border-bottom py-3">
                                <h5 className="mb-0 fw-semibold">
                                    <i className="bi bi-clock-history me-2"></i>
                                    Recent Activity
                                </h5>
                            </div>
                            <div className="card-body">
                                {recentActivity.length > 0 ? (
                                    <ul className="list-unstyled mb-0">
                                        {recentActivity.map((activity, index) => (
                                            <li key={index} className="mb-3 pb-3 border-bottom">
                                                <div className="d-flex align-items-center">
                                                    <div className="me-3">
                                                        {activity.type === 'order' && <i className="bi bi-cart-check text-success" style={{ fontSize: '1.5rem' }}></i>}
                                                        {activity.type === 'quote' && <i className="bi bi-file-earmark-text text-warning" style={{ fontSize: '1.5rem' }}></i>}
                                                        {activity.type === 'user' && <i className="bi bi-person-plus text-primary" style={{ fontSize: '1.5rem' }}></i>}
                                                    </div>
                                                    <div className="flex-grow-1">
                                                        <p className="mb-0 fw-medium">{activity.message}</p>
                                                        <small className="text-muted">{activity.time}</small>
                                                    </div>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p className="text-muted mb-0">No recent activity</p>
                                )}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-4">
                        <div className="card border-0 shadow-sm">
                            <div className="card-header bg-white border-bottom py-3">
                                <h5 className="mb-0 fw-semibold">
                                    <i className="bi bi-lightning-charge me-2"></i>
                                    Quick Actions
                                </h5>
                            </div>
                            <div className="card-body">
                                <div className="d-grid gap-2">
                                    <Link to="/admin-dashboard" className="btn btn-outline-primary btn-sm">
                                        <i className="bi bi-speedometer2 me-2"></i>
                                        Admin Dashboard
                                    </Link>
                                    <Link to="/products" className="btn btn-outline-success btn-sm">
                                        <i className="bi bi-box-seam me-2"></i>
                                        Browse Products
                                    </Link>
                                    <Link to="/contact" className="btn btn-outline-info btn-sm">
                                        <i className="bi bi-envelope me-2"></i>
                                        Contact Support
                                    </Link>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div style={{ minHeight: '100vh', backgroundColor: '#f8f9fa' }}>
            {/* Dark Theme Header */}
            <div className="bg-dark text-white shadow-sm" style={{ paddingTop: '80px' }}>
                <div className="container-fluid px-4 py-4">
                    <div className="d-flex justify-content-between align-items-center">
                        <div>
                            <h2 className="mb-1 fw-bold">
                                <i className="bi bi-speedometer2 me-2"></i>
                                CEO Portal
                            </h2>
                            <p className="text-white-50 mb-0">Executive Dashboard & Strategic Insights</p>
                        </div>
                        <div className="text-end">
                            <p className="mb-1 fw-medium">Welcome, <strong>{user?.name || user?.email}</strong></p>
                            <small className="text-white-50">
                                <i className="bi bi-shield-check me-1"></i>
                                CEO Access
                            </small>
                        </div>
                    </div>
                </div>
            </div>

            {/* Navigation Tabs */}
            <div className="bg-white border-bottom shadow-sm sticky-top" style={{ top: '80px', zIndex: 1000 }}>
                <div className="container-fluid px-4">
                    <ul className="nav nav-tabs border-0" role="tablist">
                        {tabs.map((tab) => (
                            <li key={tab.id} className="nav-item" role="presentation">
                                <button
                                    className={`nav-link ${activeTab === tab.id ? 'active' : ''} border-0`}
                                    onClick={() => setActiveTab(tab.id)}
                                    type="button"
                                    style={{
                                        borderBottom: activeTab === tab.id ? '3px solid #0d6efd' : 'none',
                                        color: activeTab === tab.id ? '#0d6efd' : '#6c757d',
                                        fontWeight: activeTab === tab.id ? '600' : '400',
                                        padding: '1rem 1.5rem',
                                    }}
                                >
                                    <i className={`${tab.icon} me-2`}></i>
                                    {tab.label}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>

            {/* White Content Area */}
            <div className="bg-white" style={{ minHeight: 'calc(100vh - 200px)' }}>
                {renderContent()}
            </div>
        </div>
    );
}
