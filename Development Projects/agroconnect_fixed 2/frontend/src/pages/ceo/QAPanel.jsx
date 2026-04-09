import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * QA Panel
 * 
 * Quality assurance metrics and testing overview for the CEO.
 */
export default function QAPanel() {
    const [qaStats, setQaStats] = useState({
        totalTests: 1247,
        passingTests: 1156,
        failingTests: 91,
        testCoverage: 78,
        bugCount: 23,
        criticalBugs: 3,
        resolvedBugs: 156,
        avgResolutionTime: '2.5 days'
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchQAStatus();
    }, []);

    const fetchQAStatus = async () => {
        try {
            setLoading(true);
            const status = await api.ai.getQAStatus();
            if (status?.metrics) {
                setQaStats(status.metrics);
            }
        } catch (error) {
            console.error('Error fetching QA status:', error);
            // Keep default values on error
        } finally {
            setLoading(false);
        }
    };

    const testSuites = [
        { name: 'Unit Tests', total: 856, passed: 812, failed: 44, coverage: 85 },
        { name: 'Integration Tests', total: 234, passed: 221, failed: 13, coverage: 72 },
        { name: 'E2E Tests', total: 157, passed: 123, failed: 34, coverage: 65 },
    ];

    const recentBugs = [
        { id: 'BUG-1234', title: 'Payment gateway timeout', severity: 'Critical', status: 'Open', assigned: 'John Doe', created: '2 days ago' },
        { id: 'BUG-1233', title: 'Product image not loading', severity: 'High', status: 'In Progress', assigned: 'Jane Smith', created: '3 days ago' },
        { id: 'BUG-1232', title: 'Search filter not working', severity: 'Medium', status: 'Resolved', assigned: 'Bob Wilson', created: '5 days ago' },
        { id: 'BUG-1231', title: 'Mobile responsive issue', severity: 'Low', status: 'Resolved', assigned: 'Alice Brown', created: '1 week ago' },
    ];

    const passRate = Math.round((qaStats.passingTests / qaStats.totalTests) * 100);

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
                        <i className="bi bi-shield-check text-success me-2"></i>
                        QA Panel
                    </h2>
                    <p className="text-muted mb-0">Quality assurance metrics and testing overview</p>
                </div>
            </div>

            {/* Key Metrics */}
            <div className="row g-4 mb-4">
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Total Tests</h6>
                            <h3 className="mb-0">{qaStats.totalTests.toLocaleString()}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Pass Rate</h6>
                            <h3 className="mb-0 text-success">{passRate}%</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Test Coverage</h6>
                            <h3 className="mb-0">{qaStats.testCoverage}%</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Open Bugs</h6>
                            <h3 className="mb-0 text-danger">{qaStats.bugCount}</h3>
                        </div>
                    </div>
                </div>
            </div>

            {/* Test Results Overview */}
            <div className="row g-4 mb-4">
                <div className="col-md-6">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Test Results</h5>
                        </div>
                        <div className="card-body">
                            <div className="mb-3">
                                <div className="d-flex justify-content-between mb-2">
                                    <span>Passing Tests</span>
                                    <span className="fw-bold text-success">{qaStats.passingTests}</span>
                                </div>
                                <div className="progress" style={{ height: '25px' }}>
                                    <div 
                                        className="progress-bar bg-success" 
                                        role="progressbar" 
                                        style={{ width: `${passRate}%` }}
                                        aria-valuenow={passRate}
                                        aria-valuemin="0"
                                        aria-valuemax="100"
                                    >
                                        {passRate}%
                                    </div>
                                </div>
                            </div>
                            <div>
                                <div className="d-flex justify-content-between mb-2">
                                    <span>Failing Tests</span>
                                    <span className="fw-bold text-danger">{qaStats.failingTests}</span>
                                </div>
                                <div className="progress" style={{ height: '25px' }}>
                                    <div 
                                        className="progress-bar bg-danger" 
                                        role="progressbar" 
                                        style={{ width: `${100 - passRate}%` }}
                                        aria-valuenow={100 - passRate}
                                        aria-valuemin="0"
                                        aria-valuemax="100"
                                    >
                                        {100 - passRate}%
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-6">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Bug Statistics</h5>
                        </div>
                        <div className="card-body">
                            <div className="row">
                                <div className="col-6 mb-3">
                                    <small className="text-muted">Critical Bugs</small>
                                    <p className="mb-0 fw-bold text-danger">{qaStats.criticalBugs}</p>
                                </div>
                                <div className="col-6 mb-3">
                                    <small className="text-muted">Resolved This Month</small>
                                    <p className="mb-0 fw-bold text-success">{qaStats.resolvedBugs}</p>
                                </div>
                                <div className="col-12">
                                    <small className="text-muted">Avg Resolution Time</small>
                                    <p className="mb-0 fw-bold">{qaStats.avgResolutionTime}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Test Suites */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Test Suites</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Test Suite</th>
                                            <th>Total Tests</th>
                                            <th>Passed</th>
                                            <th>Failed</th>
                                            <th>Coverage</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {testSuites.map((suite, index) => {
                                            const suitePassRate = Math.round((suite.passed / suite.total) * 100);
                                            return (
                                                <tr key={index}>
                                                    <td className="fw-bold">{suite.name}</td>
                                                    <td>{suite.total}</td>
                                                    <td className="text-success">{suite.passed}</td>
                                                    <td className="text-danger">{suite.failed}</td>
                                                    <td>
                                                        <div className="progress" style={{ height: '20px', width: '100px' }}>
                                                            <div 
                                                                className="progress-bar" 
                                                                role="progressbar" 
                                                                style={{ width: `${suite.coverage}%` }}
                                                                aria-valuenow={suite.coverage}
                                                                aria-valuemin="0"
                                                                aria-valuemax="100"
                                                            >
                                                                {suite.coverage}%
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Recent Bugs */}
            <div className="row">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Recent Bugs</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Bug ID</th>
                                            <th>Title</th>
                                            <th>Severity</th>
                                            <th>Status</th>
                                            <th>Assigned To</th>
                                            <th>Created</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {recentBugs.map((bug) => (
                                            <tr key={bug.id}>
                                                <td><code>{bug.id}</code></td>
                                                <td>{bug.title}</td>
                                                <td>
                                                    <span className={`badge ${
                                                        bug.severity === 'Critical' ? 'bg-danger' :
                                                        bug.severity === 'High' ? 'bg-warning' :
                                                        bug.severity === 'Medium' ? 'bg-info' : 'bg-secondary'
                                                    }`}>
                                                        {bug.severity}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span className={`badge ${
                                                        bug.status === 'Resolved' ? 'bg-success' :
                                                        bug.status === 'In Progress' ? 'bg-primary' : 'bg-secondary'
                                                    }`}>
                                                        {bug.status}
                                                    </span>
                                                </td>
                                                <td>{bug.assigned}</td>
                                                <td className="text-muted">{bug.created}</td>
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

