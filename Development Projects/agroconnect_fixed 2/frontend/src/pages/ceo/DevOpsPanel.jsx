import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * DevOps Panel
 * 
 * Infrastructure, deployment, and operations metrics for the CEO.
 */
export default function DevOpsPanel() {
    const [devOpsStats, setDevOpsStats] = useState({
        deployments: 45,
        deploymentSuccess: 42,
        deploymentFailure: 3,
        uptime: 99.8,
        avgResponseTime: 245,
        serverCount: 8,
        activeServices: 12,
        incidents: 2
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchDevOpsStatus();
    }, []);

    const fetchDevOpsStatus = async () => {
        try {
            setLoading(true);
            const status = await api.ai.getDevOpsStatus();
            if (status?.metrics) {
                setDevOpsStats(status.metrics);
            }
        } catch (error) {
            console.error('Error fetching devops status:', error);
            // Keep default values on error
        } finally {
            setLoading(false);
        }
    };

    const deployments = [
        { id: 1, service: 'auth-service', environment: 'Production', status: 'Success', time: '2 hours ago', version: 'v1.2.3' },
        { id: 2, service: 'product-service', environment: 'Staging', status: 'Success', time: '4 hours ago', version: 'v2.1.0' },
        { id: 3, service: 'gateway', environment: 'Production', status: 'Failed', time: '6 hours ago', version: 'v1.5.1' },
        { id: 4, service: 'frontend', environment: 'Production', status: 'Success', time: '8 hours ago', version: 'v3.0.2' },
    ];

    const serviceHealth = [
        { name: 'auth-service', status: 'Healthy', uptime: '99.9%', responseTime: 120, requests: 12456 },
        { name: 'product-service', status: 'Healthy', uptime: '99.8%', responseTime: 180, requests: 23456 },
        { name: 'gateway', status: 'Degraded', uptime: '98.5%', responseTime: 350, requests: 45678 },
        { name: 'frontend', status: 'Healthy', uptime: '99.9%', responseTime: 95, requests: 67890 },
    ];

    const infrastructure = [
        { resource: 'CPU Usage', current: 65, threshold: 80, status: 'Normal' },
        { resource: 'Memory Usage', current: 72, threshold: 85, status: 'Normal' },
        { resource: 'Disk Usage', current: 58, threshold: 90, status: 'Normal' },
        { resource: 'Network I/O', current: 45, threshold: 75, status: 'Normal' },
    ];

    const successRate = Math.round((devOpsStats.deploymentSuccess / devOpsStats.deployments) * 100);

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
                        <i className="bi bi-gear text-secondary me-2"></i>
                        DevOps Panel
                    </h2>
                    <p className="text-muted mb-0">Infrastructure and deployment metrics</p>
                </div>
            </div>

            {/* Key Metrics */}
            <div className="row g-4 mb-4">
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Deployments</h6>
                            <h3 className="mb-0">{devOpsStats.deployments}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Success Rate</h6>
                            <h3 className="mb-0 text-success">{successRate}%</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Uptime</h6>
                            <h3 className="mb-0">{devOpsStats.uptime}%</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Avg Response</h6>
                            <h3 className="mb-0">{devOpsStats.avgResponseTime}ms</h3>
                        </div>
                    </div>
                </div>
            </div>

            {/* Deployment Status */}
            <div className="row g-4 mb-4">
                <div className="col-md-6">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Deployment Status</h5>
                        </div>
                        <div className="card-body">
                            <div className="mb-3">
                                <div className="d-flex justify-content-between mb-2">
                                    <span>Successful</span>
                                    <span className="fw-bold text-success">{devOpsStats.deploymentSuccess}</span>
                                </div>
                                <div className="progress" style={{ height: '25px' }}>
                                    <div 
                                        className="progress-bar bg-success" 
                                        role="progressbar" 
                                        style={{ width: `${successRate}%` }}
                                        aria-valuenow={successRate}
                                        aria-valuemin="0"
                                        aria-valuemax="100"
                                    >
                                        {successRate}%
                                    </div>
                                </div>
                            </div>
                            <div>
                                <div className="d-flex justify-content-between mb-2">
                                    <span>Failed</span>
                                    <span className="fw-bold text-danger">{devOpsStats.deploymentFailure}</span>
                                </div>
                                <div className="progress" style={{ height: '25px' }}>
                                    <div 
                                        className="progress-bar bg-danger" 
                                        role="progressbar" 
                                        style={{ width: `${(devOpsStats.deploymentFailure / devOpsStats.deployments) * 100}%` }}
                                    >
                                        {Math.round((devOpsStats.deploymentFailure / devOpsStats.deployments) * 100)}%
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-6">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Infrastructure Resources</h5>
                        </div>
                        <div className="card-body">
                            {infrastructure.map((infra, index) => (
                                <div key={index} className="mb-3">
                                    <div className="d-flex justify-content-between mb-2">
                                        <span>{infra.resource}</span>
                                        <span className={`fw-bold ${infra.current > infra.threshold ? 'text-danger' : 'text-success'}`}>
                                            {infra.current}%
                                        </span>
                                    </div>
                                    <div className="progress" style={{ height: '20px' }}>
                                        <div 
                                            className={`progress-bar ${infra.current > infra.threshold ? 'bg-danger' : 'bg-primary'}`}
                                            role="progressbar" 
                                            style={{ width: `${infra.current}%` }}
                                            aria-valuenow={infra.current}
                                            aria-valuemin="0"
                                            aria-valuemax="100"
                                        >
                                            {infra.current}%
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>

            {/* Recent Deployments */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Recent Deployments</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Service</th>
                                            <th>Environment</th>
                                            <th>Version</th>
                                            <th>Status</th>
                                            <th>Time</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {deployments.map((deployment) => (
                                            <tr key={deployment.id}>
                                                <td className="fw-bold">{deployment.service}</td>
                                                <td>
                                                    <span className={`badge ${
                                                        deployment.environment === 'Production' ? 'bg-danger' : 'bg-warning'
                                                    }`}>
                                                        {deployment.environment}
                                                    </span>
                                                </td>
                                                <td><code>{deployment.version}</code></td>
                                                <td>
                                                    <span className={`badge ${
                                                        deployment.status === 'Success' ? 'bg-success' : 'bg-danger'
                                                    }`}>
                                                        {deployment.status}
                                                    </span>
                                                </td>
                                                <td className="text-muted">{deployment.time}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Service Health */}
            <div className="row">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Service Health</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Service</th>
                                            <th>Status</th>
                                            <th>Uptime</th>
                                            <th>Response Time</th>
                                            <th>Requests</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {serviceHealth.map((service, index) => (
                                            <tr key={index}>
                                                <td className="fw-bold">{service.name}</td>
                                                <td>
                                                    <span className={`badge ${
                                                        service.status === 'Healthy' ? 'bg-success' : 'bg-warning'
                                                    }`}>
                                                        {service.status}
                                                    </span>
                                                </td>
                                                <td>{service.uptime}</td>
                                                <td>
                                                    <span className={service.responseTime > 300 ? 'text-danger' : 'text-success'}>
                                                        {service.responseTime}ms
                                                    </span>
                                                </td>
                                                <td>{service.requests.toLocaleString()}</td>
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

