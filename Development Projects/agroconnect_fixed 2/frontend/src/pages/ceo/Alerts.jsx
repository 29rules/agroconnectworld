import React from 'react';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * Alerts Panel
 * 
 * System alerts, notifications, and critical issues for the CEO.
 */
export default function Alerts() {
    const alertStats = {
        critical: 3,
        warning: 8,
        info: 15,
        resolved: 42
    };

    const criticalAlerts = [
        { id: 1, type: 'critical', title: 'Payment Gateway Down', description: 'Payment service is experiencing downtime', service: 'payment-service', time: '5 minutes ago', status: 'Active' },
        { id: 2, type: 'critical', title: 'Database Connection Pool Exhausted', description: 'High connection usage detected', service: 'product-service', time: '15 minutes ago', status: 'Active' },
        { id: 3, type: 'critical', title: 'High Error Rate', description: 'Error rate exceeded 5% threshold', service: 'gateway', time: '30 minutes ago', status: 'Investigating' },
    ];

    const warningAlerts = [
        { id: 4, type: 'warning', title: 'High Memory Usage', description: 'Memory usage at 85% on server-02', service: 'infrastructure', time: '1 hour ago', status: 'Monitoring' },
        { id: 5, type: 'warning', title: 'Slow Response Time', description: 'API response time increased by 200ms', service: 'auth-service', time: '2 hours ago', status: 'Monitoring' },
        { id: 6, type: 'warning', title: 'Low Disk Space', description: 'Disk usage at 80% on server-03', service: 'infrastructure', time: '3 hours ago', status: 'Monitoring' },
    ];

    const infoAlerts = [
        { id: 7, type: 'info', title: 'Scheduled Maintenance', description: 'Database maintenance scheduled for tonight', service: 'infrastructure', time: '1 day ago', status: 'Scheduled' },
        { id: 8, type: 'info', title: 'New Deployment', description: 'Frontend v3.0.2 deployed successfully', service: 'frontend', time: '2 days ago', status: 'Completed' },
        { id: 9, type: 'info', title: 'Feature Release', description: 'New product search feature released', service: 'product-service', time: '3 days ago', status: 'Completed' },
    ];

    const recentResolved = [
        { id: 10, title: 'API Rate Limit Warning', resolved: '2 hours ago', service: 'gateway' },
        { id: 11, title: 'SSL Certificate Renewal', resolved: '5 hours ago', service: 'infrastructure' },
        { id: 12, title: 'Cache Miss Rate High', resolved: '1 day ago', service: 'product-service' },
    ];

    const getAlertBadge = (type) => {
        switch (type) {
            case 'critical':
                return 'bg-danger';
            case 'warning':
                return 'bg-warning';
            case 'info':
                return 'bg-info';
            default:
                return 'bg-secondary';
        }
    };

    return (
        <div className="container-fluid py-4">
            <div className="row mb-4">
                <div className="col-12">
                    <h2 className="h3 mb-1">
                        <i className="bi bi-bell text-warning me-2"></i>
                        Alerts & Notifications
                    </h2>
                    <p className="text-muted mb-0">System alerts, warnings, and critical issues</p>
                </div>
            </div>

            {/* Alert Statistics */}
            <div className="row g-4 mb-4">
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm border-danger">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Critical</h6>
                            <h3 className="mb-0 text-danger">{alertStats.critical}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm border-warning">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Warnings</h6>
                            <h3 className="mb-0 text-warning">{alertStats.warning}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm border-info">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Info</h6>
                            <h3 className="mb-0 text-info">{alertStats.info}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm border-success">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Resolved</h6>
                            <h3 className="mb-0 text-success">{alertStats.resolved}</h3>
                        </div>
                    </div>
                </div>
            </div>

            {/* Critical Alerts */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="card border-0 shadow-sm border-danger">
                        <div className="card-header bg-danger text-white">
                            <h5 className="mb-0">Critical Alerts</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Title</th>
                                            <th>Description</th>
                                            <th>Service</th>
                                            <th>Status</th>
                                            <th>Time</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {criticalAlerts.map((alert) => (
                                            <tr key={alert.id} className="table-danger">
                                                <td className="fw-bold">{alert.title}</td>
                                                <td>{alert.description}</td>
                                                <td><code>{alert.service}</code></td>
                                                <td>
                                                    <span className="badge bg-danger">{alert.status}</span>
                                                </td>
                                                <td className="text-muted">{alert.time}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Warning Alerts */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="card border-0 shadow-sm border-warning">
                        <div className="card-header bg-warning">
                            <h5 className="mb-0">Warning Alerts</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Title</th>
                                            <th>Description</th>
                                            <th>Service</th>
                                            <th>Status</th>
                                            <th>Time</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {warningAlerts.map((alert) => (
                                            <tr key={alert.id} className="table-warning">
                                                <td className="fw-bold">{alert.title}</td>
                                                <td>{alert.description}</td>
                                                <td><code>{alert.service}</code></td>
                                                <td>
                                                    <span className="badge bg-warning text-dark">{alert.status}</span>
                                                </td>
                                                <td className="text-muted">{alert.time}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Info Alerts */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Information Alerts</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Title</th>
                                            <th>Description</th>
                                            <th>Service</th>
                                            <th>Status</th>
                                            <th>Time</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {infoAlerts.map((alert) => (
                                            <tr key={alert.id}>
                                                <td className="fw-bold">{alert.title}</td>
                                                <td>{alert.description}</td>
                                                <td><code>{alert.service}</code></td>
                                                <td>
                                                    <span className="badge bg-info">{alert.status}</span>
                                                </td>
                                                <td className="text-muted">{alert.time}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Recently Resolved */}
            <div className="row">
                <div className="col-12">
                    <div className="card border-0 shadow-sm border-success">
                        <div className="card-header bg-success text-white">
                            <h5 className="mb-0">Recently Resolved</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Title</th>
                                            <th>Service</th>
                                            <th>Resolved</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {recentResolved.map((alert) => (
                                            <tr key={alert.id} className="table-success">
                                                <td className="fw-bold">{alert.title}</td>
                                                <td><code>{alert.service}</code></td>
                                                <td className="text-muted">{alert.resolved}</td>
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

