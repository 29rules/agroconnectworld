import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * Engineering Panel
 * 
 * Technical overview and engineering metrics for the CEO.
 */
export default function EngineeringPanel() {
    const [engineeringStats, setEngineeringStats] = useState({
        activeDevelopers: 12,
        codeCommits: 1247,
        pullRequests: 89,
        codeReviewTime: '2.5 days',
        testCoverage: 78,
        buildSuccessRate: 94,
        deploymentFrequency: 'Daily',
        meanTimeToRecovery: '15 minutes'
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchEngineeringStatus();
    }, []);

    const fetchEngineeringStatus = async () => {
        try {
            setLoading(true);
            const status = await api.ai.getEngineeringStatus();
            if (status?.metrics) {
                setEngineeringStats(status.metrics);
            }
        } catch (error) {
            console.error('Error fetching engineering status:', error);
            // Keep default values on error
        } finally {
            setLoading(false);
        }
    };

    const recentCommits = [
        { id: 1, author: 'John Doe', message: 'Fix authentication bug', branch: 'main', time: '2 hours ago' },
        { id: 2, author: 'Jane Smith', message: 'Add product search feature', branch: 'feature/search', time: '4 hours ago' },
        { id: 3, author: 'Bob Wilson', message: 'Update API documentation', branch: 'docs/api', time: '6 hours ago' },
        { id: 4, author: 'Alice Brown', message: 'Refactor payment service', branch: 'refactor/payment', time: '8 hours ago' },
    ];

    const activeProjects = [
        { name: 'User Authentication', progress: 85, status: 'In Progress', team: 3 },
        { name: 'Product Catalog', progress: 92, status: 'Testing', team: 4 },
        { name: 'Payment Integration', progress: 65, status: 'In Progress', team: 2 },
        { name: 'Mobile App', progress: 45, status: 'In Progress', team: 5 },
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
                        <i className="bi bi-code-slash text-primary me-2"></i>
                        Engineering Panel
                    </h2>
                    <p className="text-muted mb-0">Technical metrics and development overview</p>
                </div>
            </div>

            {/* Key Metrics */}
            <div className="row g-4 mb-4">
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Active Developers</h6>
                            <h3 className="mb-0">{engineeringStats.activeDevelopers}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Code Commits</h6>
                            <h3 className="mb-0">{engineeringStats.codeCommits.toLocaleString()}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Pull Requests</h6>
                            <h3 className="mb-0">{engineeringStats.pullRequests}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Test Coverage</h6>
                            <h3 className="mb-0">{engineeringStats.testCoverage}%</h3>
                        </div>
                    </div>
                </div>
            </div>

            {/* Progress Metrics */}
            <div className="row g-4 mb-4">
                <div className="col-md-6">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Build Success Rate</h5>
                        </div>
                        <div className="card-body">
                            <div className="d-flex justify-content-between mb-2">
                                <span>Current Rate</span>
                                <span className="fw-bold">{engineeringStats.buildSuccessRate}%</span>
                            </div>
                            <div className="progress" style={{ height: '25px' }}>
                                <div 
                                    className="progress-bar bg-success" 
                                    role="progressbar" 
                                    style={{ width: `${engineeringStats.buildSuccessRate}%` }}
                                    aria-valuenow={engineeringStats.buildSuccessRate}
                                    aria-valuemin="0"
                                    aria-valuemax="100"
                                >
                                    {engineeringStats.buildSuccessRate}%
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-6">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Deployment Metrics</h5>
                        </div>
                        <div className="card-body">
                            <div className="row">
                                <div className="col-6">
                                    <small className="text-muted">Frequency</small>
                                    <p className="mb-0 fw-bold">{engineeringStats.deploymentFrequency}</p>
                                </div>
                                <div className="col-6">
                                    <small className="text-muted">MTTR</small>
                                    <p className="mb-0 fw-bold">{engineeringStats.meanTimeToRecovery}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Active Projects */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Active Projects</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Project Name</th>
                                            <th>Progress</th>
                                            <th>Status</th>
                                            <th>Team Size</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {activeProjects.map((project, index) => (
                                            <tr key={index}>
                                                <td className="fw-bold">{project.name}</td>
                                                <td>
                                                    <div className="progress" style={{ height: '20px', width: '150px' }}>
                                                        <div 
                                                            className="progress-bar" 
                                                            role="progressbar" 
                                                            style={{ width: `${project.progress}%` }}
                                                            aria-valuenow={project.progress}
                                                            aria-valuemin="0"
                                                            aria-valuemax="100"
                                                        >
                                                            {project.progress}%
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <span className={`badge ${project.status === 'Testing' ? 'bg-warning' : 'bg-primary'}`}>
                                                        {project.status}
                                                    </span>
                                                </td>
                                                <td>{project.team} developers</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Recent Commits */}
            <div className="row">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Recent Commits</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Author</th>
                                            <th>Message</th>
                                            <th>Branch</th>
                                            <th>Time</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {recentCommits.map((commit) => (
                                            <tr key={commit.id}>
                                                <td>{commit.author}</td>
                                                <td>{commit.message}</td>
                                                <td><code>{commit.branch}</code></td>
                                                <td className="text-muted">{commit.time}</td>
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

