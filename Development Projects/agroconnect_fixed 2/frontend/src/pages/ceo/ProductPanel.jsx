import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * Product Panel
 * 
 * Product management and metrics overview for the CEO.
 */
export default function ProductPanel() {
    const [productStats, setProductStats] = useState({
        totalProducts: 1247,
        activeProducts: 1156,
        pendingApproval: 45,
        lowStock: 23,
        categories: 12,
        totalRevenue: 2456789,
        avgRating: 4.6,
        totalReviews: 3456
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchProductStatus();
    }, []);

    const fetchProductStatus = async () => {
        try {
            setLoading(true);
            const status = await api.ai.getProductStatus();
            if (status?.metrics) {
                setProductStats(status.metrics);
            }
        } catch (error) {
            console.error('Error fetching product status:', error);
            // Keep default values on error
        } finally {
            setLoading(false);
        }
    };

    const topProducts = [
        { id: 1, name: 'Organic Wheat Flour', category: 'Grains', sales: 1245, revenue: 12450, rating: 4.8, stock: 1250 },
        { id: 2, name: 'Premium Rice', category: 'Grains', sales: 987, revenue: 19740, rating: 4.7, stock: 890 },
        { id: 3, name: 'Fresh Vegetables Pack', category: 'Vegetables', sales: 756, revenue: 15120, rating: 4.9, stock: 450 },
        { id: 4, name: 'Organic Spices Set', category: 'Spices', sales: 623, revenue: 18690, rating: 4.6, stock: 320 },
    ];

    const categoryBreakdown = [
        { category: 'Grains', products: 456, revenue: 1256789, growth: 12.5 },
        { category: 'Vegetables', products: 234, revenue: 567890, growth: 8.3 },
        { category: 'Fruits', products: 189, revenue: 345678, growth: 15.2 },
        { category: 'Spices', products: 156, revenue: 234567, growth: 5.7 },
        { category: 'Dairy', products: 98, revenue: 123456, growth: 3.2 },
    ];

    if (loading) {
        return (
            <div className="container py-4">
                <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '50vh' }}>
                    <div className="spinner-border text-success" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="container-fluid py-4">
            <div className="row mb-4">
                <div className="col-12">
                    <h2 className="h3 mb-1">
                        <i className="bi bi-box-seam text-warning me-2"></i>
                        Product Panel
                    </h2>
                    <p className="text-muted mb-0">Product management and performance metrics</p>
                </div>
            </div>

            {/* Key Metrics */}
            <div className="row g-4 mb-4">
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Total Products</h6>
                            <h3 className="mb-0">{productStats.totalProducts.toLocaleString()}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Total Revenue</h6>
                            <h3 className="mb-0 text-success">${productStats.totalRevenue.toLocaleString()}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Avg Rating</h6>
                            <h3 className="mb-0">{productStats.avgRating} ⭐</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Categories</h6>
                            <h3 className="mb-0">{productStats.categories}</h3>
                        </div>
                    </div>
                </div>
            </div>

            {/* Product Status */}
            <div className="row g-4 mb-4">
                <div className="col-md-4">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Product Status</h5>
                        </div>
                        <div className="card-body">
                            <div className="mb-3">
                                <div className="d-flex justify-content-between mb-2">
                                    <span>Active Products</span>
                                    <span className="fw-bold text-success">{productStats.activeProducts}</span>
                                </div>
                                <div className="progress" style={{ height: '25px' }}>
                                    <div 
                                        className="progress-bar bg-success" 
                                        role="progressbar" 
                                        style={{ width: `${(productStats.activeProducts / productStats.totalProducts) * 100}%` }}
                                    >
                                        {Math.round((productStats.activeProducts / productStats.totalProducts) * 100)}%
                                    </div>
                                </div>
                            </div>
                            <div className="mb-3">
                                <div className="d-flex justify-content-between mb-2">
                                    <span>Pending Approval</span>
                                    <span className="fw-bold text-warning">{productStats.pendingApproval}</span>
                                </div>
                                <div className="progress" style={{ height: '25px' }}>
                                    <div 
                                        className="progress-bar bg-warning" 
                                        role="progressbar" 
                                        style={{ width: `${(productStats.pendingApproval / productStats.totalProducts) * 100}%` }}
                                    >
                                        {Math.round((productStats.pendingApproval / productStats.totalProducts) * 100)}%
                                    </div>
                                </div>
                            </div>
                            <div>
                                <div className="d-flex justify-content-between mb-2">
                                    <span>Low Stock</span>
                                    <span className="fw-bold text-danger">{productStats.lowStock}</span>
                                </div>
                                <div className="progress" style={{ height: '25px' }}>
                                    <div 
                                        className="progress-bar bg-danger" 
                                        role="progressbar" 
                                        style={{ width: `${(productStats.lowStock / productStats.totalProducts) * 100}%` }}
                                    >
                                        {Math.round((productStats.lowStock / productStats.totalProducts) * 100)}%
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-8">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Top Products</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Product Name</th>
                                            <th>Category</th>
                                            <th>Sales</th>
                                            <th>Revenue</th>
                                            <th>Rating</th>
                                            <th>Stock</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {topProducts.map((product) => (
                                            <tr key={product.id}>
                                                <td className="fw-bold">{product.name}</td>
                                                <td>{product.category}</td>
                                                <td>{product.sales}</td>
                                                <td className="text-success">${product.revenue.toLocaleString()}</td>
                                                <td>
                                                    <span className="text-warning">{product.rating} ⭐</span>
                                                </td>
                                                <td>
                                                    <span className={product.stock < 500 ? 'text-danger' : 'text-success'}>
                                                        {product.stock}
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Category Breakdown */}
            <div className="row">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Category Performance</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Category</th>
                                            <th>Products</th>
                                            <th>Revenue</th>
                                            <th>Growth</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {categoryBreakdown.map((cat, index) => (
                                            <tr key={index}>
                                                <td className="fw-bold">{cat.category}</td>
                                                <td>{cat.products}</td>
                                                <td className="text-success">${cat.revenue.toLocaleString()}</td>
                                                <td>
                                                    <span className={`badge ${cat.growth > 10 ? 'bg-success' : cat.growth > 5 ? 'bg-info' : 'bg-secondary'}`}>
                                                        +{cat.growth}%
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

