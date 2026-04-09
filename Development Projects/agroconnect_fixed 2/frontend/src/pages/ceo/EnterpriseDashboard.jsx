import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import api from '../../services/api';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * Enterprise CEO Dashboard
 * Comprehensive dashboard showing all critical information
 */
export default function EnterpriseDashboard() {
    const { user } = useAuth();
    const { showError } = useToast();
    
    const [loading, setLoading] = useState(true);
    const [activeSection, setActiveSection] = useState('overview');
    
    // Data states
    const [environmentStatus, setEnvironmentStatus] = useState(null);
    const [deploymentHistory, setDeploymentHistory] = useState([]);
    const [cicdFailures, setCicdFailures] = useState([]);
    const [sprintOverview, setSprintOverview] = useState(null);
    const [dailyReport, setDailyReport] = useState(null);
    const [risksWarnings, setRisksWarnings] = useState({ risks: [], warnings: [] });
    const [requiredDecisions, setRequiredDecisions] = useState([]);
    
    useEffect(() => {
        fetchAllData();
        // Auto-refresh every 30 seconds
        const interval = setInterval(fetchAllData, 30000);
        return () => clearInterval(interval);
    }, []);
    
    const fetchAllData = async () => {
        try {
            setLoading(true);
            
            const [
                envStatus,
                deployments,
                failures,
                sprint,
                report,
                risks,
                decisions
            ] = await Promise.all([
                api.ai.getEnvironmentStatus().catch(() => null),
                api.ai.getDeploymentHistory().catch(() => ({ deployments: [] })),
                api.ai.getCICDFailures().catch(() => ({ failures: [] })),
                api.ai.getSprintOverview().catch(() => null),
                api.ai.getDailyReport().catch(() => null),
                api.ai.getRisksWarnings().catch(() => ({ risks: [], warnings: [] })),
                api.ai.getRequiredDecisions().catch(() => ({ decisions: [] }))
            ]);
            
            setEnvironmentStatus(envStatus);
            setDeploymentHistory(deployments?.deployments || []);
            setCicdFailures(failures?.failures || []);
            setSprintOverview(sprint);
            setDailyReport(report);
            setRisksWarnings(risks);
            setRequiredDecisions(decisions?.decisions || []);
            
        } catch (error) {
            console.error('Error fetching dashboard data:', error);
            showError('Failed to load dashboard data');
        } finally {
            setLoading(false);
        }
    };
    
    if (loading && !environmentStatus) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '50vh' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }
    
    const sections = [
        { id: 'overview', label: 'Overview', icon: 'bi-speedometer2' },
        { id: 'environments', label: 'Environments', icon: 'bi-server' },
        { id: 'deployments', label: 'Deployments', icon: 'bi-rocket-takeoff' },
        { id: 'cicd', label: 'CI/CD', icon: 'bi-gear-wide-connected' },
        { id: 'sprint', label: 'Sprint', icon: 'bi-kanban' },
        { id: 'daily-report', label: 'Daily Report', icon: 'bi-file-earmark-text' },
        { id: 'risks', label: 'Risks & Warnings', icon: 'bi-exclamation-triangle' },
        { id: 'decisions', label: 'Decisions', icon: 'bi-clipboard-check' }
    ];
    
    const renderContent = () => {
        switch (activeSection) {
            case 'environments':
                return <EnvironmentStatusPanel data={environmentStatus} />;
            case 'deployments':
                return <DeploymentHistoryPanel data={deploymentHistory} />;
            case 'cicd':
                return <CICDFailuresPanel data={cicdFailures} />;
            case 'sprint':
                return <SprintOverviewPanel data={sprintOverview} />;
            case 'daily-report':
                return <DailyReportPanel data={dailyReport} />;
            case 'risks':
                return <RisksWarningsPanel data={risksWarnings} />;
            case 'decisions':
                return <RequiredDecisionsPanel data={requiredDecisions} onDecision={fetchAllData} />;
            default:
                return <OverviewPanel 
                    environmentStatus={environmentStatus}
                    deploymentHistory={deploymentHistory}
                    cicdFailures={cicdFailures}
                    sprintOverview={sprintOverview}
                    dailyReport={dailyReport}
                    risksWarnings={risksWarnings}
                    requiredDecisions={requiredDecisions}
                />;
        }
    };
    
    return (
        <div className="container-fluid py-4">
            {/* Section Navigation */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <div className="d-flex flex-wrap gap-2">
                                {sections.map(section => (
                                    <button
                                        key={section.id}
                                        className={`btn ${activeSection === section.id ? 'btn-primary' : 'btn-outline-secondary'}`}
                                        onClick={() => setActiveSection(section.id)}
                                    >
                                        <i className={`${section.icon} me-2`}></i>
                                        {section.label}
                                    </button>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            {/* Content */}
            {renderContent()}
        </div>
    );
}

// Overview Panel Component
function OverviewPanel({ environmentStatus, deploymentHistory, cicdFailures, sprintOverview, dailyReport, risksWarnings, requiredDecisions }) {
    return (
        <div className="row g-4">
            {/* Environment Status Cards */}
            <div className="col-12">
                <div className="card border-0 shadow-sm">
                    <div className="card-header bg-white border-bottom py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-server me-2"></i>
                            Environment Status
                        </h5>
                    </div>
                    <div className="card-body">
                        <div className="row g-3">
                            {environmentStatus?.environments && Object.entries(environmentStatus.environments).map(([key, env]) => (
                                <div key={key} className="col-md-3">
                                    <div className={`card h-100 ${env.status === 'HEALTHY' ? 'border-success' : 'border-danger'}`}>
                                        <div className="card-body">
                                            <div className="d-flex justify-content-between align-items-center mb-2">
                                                <h6 className="mb-0 fw-bold">{env.name}</h6>
                                                <span className={`badge ${env.status === 'HEALTHY' ? 'bg-success' : 'bg-danger'}`}>
                                                    {env.status}
                                                </span>
                                            </div>
                                            <p className="text-muted small mb-2">{env.url}</p>
                                            <div className="d-flex justify-content-between">
                                                <small className="text-muted">Uptime: {env.uptime}</small>
                                                <small className="text-muted">
                                                    {env.services?.filter(s => s.status === 'UP').length || 0}/{env.services?.length || 0} services
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
            
            {/* Key Metrics */}
            <div className="col-md-6">
                <div className="card border-0 shadow-sm h-100">
                    <div className="card-header bg-white border-bottom py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-graph-up me-2"></i>
                            Key Metrics
                        </h5>
                    </div>
                    <div className="card-body">
                        {dailyReport?.metrics && (
                            <div className="row g-3">
                                <div className="col-6">
                                    <div className="text-center">
                                        <h3 className="mb-0 text-success">{dailyReport.metrics.codeQuality}%</h3>
                                        <small className="text-muted">Code Quality</small>
                                    </div>
                                </div>
                                <div className="col-6">
                                    <div className="text-center">
                                        <h3 className="mb-0 text-info">{dailyReport.metrics.testCoverage}%</h3>
                                        <small className="text-muted">Test Coverage</small>
                                    </div>
                                </div>
                                <div className="col-6">
                                    <div className="text-center">
                                        <h3 className="mb-0 text-warning">{dailyReport.metrics.securityScore}</h3>
                                        <small className="text-muted">Security Score</small>
                                    </div>
                                </div>
                                <div className="col-6">
                                    <div className="text-center">
                                        <h3 className="mb-0 text-primary">{dailyReport.metrics.deploymentSuccessRate}%</h3>
                                        <small className="text-muted">Deployment Success</small>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
            
            {/* Critical Alerts */}
            <div className="col-md-6">
                <div className="card border-0 shadow-sm h-100">
                    <div className="card-header bg-white border-bottom py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-exclamation-triangle me-2 text-warning"></i>
                            Critical Alerts
                        </h5>
                    </div>
                    <div className="card-body">
                        <div className="d-flex flex-column gap-2">
                            {requiredDecisions.filter(d => d.priority === 'CRITICAL').slice(0, 3).map(decision => (
                                <div key={decision.id} className="alert alert-danger mb-0 py-2">
                                    <strong>{decision.title}</strong>
                                    <br />
                                    <small>{decision.description}</small>
                                </div>
                            ))}
                            {risksWarnings.risks?.slice(0, 2).map((risk, idx) => (
                                <div key={idx} className="alert alert-warning mb-0 py-2">
                                    <strong>{risk.title}</strong>
                                    <br />
                                    <small>{risk.description}</small>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
            
            {/* Recent Deployments */}
            <div className="col-md-6">
                <div className="card border-0 shadow-sm">
                    <div className="card-header bg-white border-bottom py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-rocket-takeoff me-2"></i>
                            Recent Deployments
                        </h5>
                    </div>
                    <div className="card-body">
                        {deploymentHistory.slice(0, 5).map((deployment, idx) => (
                            <div key={idx} className="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom">
                                <div>
                                    <strong>{deployment.toEnvironment?.toUpperCase()}</strong>
                                    <br />
                                    <small className="text-muted">{deployment.buildId} • {new Date(deployment.date).toLocaleString()}</small>
                                </div>
                                <span className={`badge ${deployment.status === 'PROMOTED' ? 'bg-success' : 'bg-danger'}`}>
                                    {deployment.status}
                                </span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
            
            {/* CI/CD Status */}
            <div className="col-md-6">
                <div className="card border-0 shadow-sm">
                    <div className="card-header bg-white border-bottom py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-gear-wide-connected me-2"></i>
                            CI/CD Status
                        </h5>
                    </div>
                    <div className="card-body">
                        {cicdFailures.length > 0 ? (
                            <div>
                                <div className="alert alert-danger">
                                    <strong>{cicdFailures.length} Recent Failures</strong>
                                </div>
                                {cicdFailures.slice(0, 3).map((failure, idx) => (
                                    <div key={idx} className="mb-2">
                                        <small><strong>{failure.environment}</strong>: {failure.reason}</small>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="alert alert-success mb-0">
                                <strong>All CI/CD pipelines healthy</strong>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

// Environment Status Panel
function EnvironmentStatusPanel({ data }) {
    if (!data?.environments) {
        return <div className="alert alert-info">No environment data available</div>;
    }
    
    return (
        <div className="row g-4">
            {Object.entries(data.environments).map(([key, env]) => (
                <div key={key} className="col-md-6">
                    <div className={`card border-0 shadow-sm ${env.status === 'HEALTHY' ? 'border-success' : 'border-danger'}`}>
                        <div className="card-header bg-white border-bottom">
                            <div className="d-flex justify-content-between align-items-center">
                                <h5 className="mb-0 fw-semibold">{env.name}</h5>
                                <span className={`badge ${env.status === 'HEALTHY' ? 'bg-success' : 'bg-danger'}`}>
                                    {env.status}
                                </span>
                            </div>
                        </div>
                        <div className="card-body">
                            <p className="text-muted mb-3">
                                <i className="bi bi-link-45deg me-1"></i>
                                <a href={env.url} target="_blank" rel="noopener noreferrer">{env.url}</a>
                            </p>
                            
                            <div className="mb-3">
                                <strong>Services:</strong>
                                <div className="mt-2">
                                    {env.services?.map((service, idx) => (
                                        <div key={idx} className="d-flex justify-content-between align-items-center mb-2">
                                            <span>{service.name}</span>
                                            <span className={`badge ${service.status === 'UP' ? 'bg-success' : 'bg-danger'}`}>
                                                {service.status}
                                            </span>
                                        </div>
                                    ))}
                                </div>
                            </div>
                            
                            <div className="d-flex justify-content-between">
                                <div>
                                    <small className="text-muted">Uptime</small>
                                    <div className="fw-bold">{env.uptime}</div>
                                </div>
                                <div>
                                    <small className="text-muted">Last Deployment</small>
                                    <div className="fw-bold">{new Date(env.lastDeployment?.date).toLocaleString()}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}

// Deployment History Panel
function DeploymentHistoryPanel({ data }) {
    return (
        <div className="card border-0 shadow-sm">
            <div className="card-header bg-white border-bottom py-3">
                <h5 className="mb-0 fw-semibold">
                    <i className="bi bi-rocket-takeoff me-2"></i>
                    Deployment History
                </h5>
            </div>
            <div className="card-body">
                <div className="table-responsive">
                    <table className="table table-hover">
                        <thead>
                            <tr>
                                <th>Build ID</th>
                                <th>From → To</th>
                                <th>Branch</th>
                                <th>Status</th>
                                <th>Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((deployment, idx) => (
                                <tr key={idx}>
                                    <td><code>{deployment.buildId}</code></td>
                                    <td>
                                        <span className="badge bg-secondary">{deployment.fromEnvironment}</span>
                                        {' → '}
                                        <span className="badge bg-primary">{deployment.toEnvironment}</span>
                                    </td>
                                    <td><code>{deployment.branch}</code></td>
                                    <td>
                                        <span className={`badge ${deployment.status === 'PROMOTED' ? 'bg-success' : 'bg-danger'}`}>
                                            {deployment.status}
                                        </span>
                                    </td>
                                    <td>{new Date(deployment.date).toLocaleString()}</td>
                                    <td>
                                        {deployment.deploymentUrl && (
                                            <a href={deployment.deploymentUrl} target="_blank" rel="noopener noreferrer" className="btn btn-sm btn-outline-primary">
                                                <i className="bi bi-box-arrow-up-right"></i>
                                            </a>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

// CI/CD Failures Panel
function CICDFailuresPanel({ data }) {
    return (
        <div className="card border-0 shadow-sm">
            <div className="card-header bg-white border-bottom py-3">
                <h5 className="mb-0 fw-semibold">
                    <i className="bi bi-gear-wide-connected me-2"></i>
                    CI/CD Failures
                </h5>
            </div>
            <div className="card-body">
                {data.length === 0 ? (
                    <div className="alert alert-success mb-0">
                        <i className="bi bi-check-circle me-2"></i>
                        No recent CI/CD failures
                    </div>
                ) : (
                    <div className="list-group">
                        {data.map((failure, idx) => (
                            <div key={idx} className={`list-group-item ${failure.severity === 'CRITICAL' ? 'list-group-item-danger' : failure.severity === 'HIGH' ? 'list-group-item-warning' : ''}`}>
                                <div className="d-flex justify-content-between align-items-start">
                                    <div>
                                        <h6 className="mb-1">
                                            <span className="badge bg-secondary me-2">{failure.environment}</span>
                                            {failure.workflow}
                                        </h6>
                                        <p className="mb-1">{failure.reason}</p>
                                        <small className="text-muted">Build: {failure.buildId} • {failure.date}</small>
                                    </div>
                                    <span className={`badge ${failure.severity === 'CRITICAL' ? 'bg-danger' : failure.severity === 'HIGH' ? 'bg-warning' : 'bg-secondary'}`}>
                                        {failure.severity}
                                    </span>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

// Sprint Overview Panel
function SprintOverviewPanel({ data }) {
    if (!data) {
        return <div className="alert alert-info">No sprint data available</div>;
    }
    
    const currentSprint = data.currentSprint;
    const metrics = data.metrics;
    
    return (
        <div className="row g-4">
            <div className="col-md-8">
                <div className="card border-0 shadow-sm">
                    <div className="card-header bg-white border-bottom py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-kanban me-2"></i>
                            Current Sprint
                        </h5>
                    </div>
                    <div className="card-body">
                        <h4 className="mb-3">{currentSprint.sprintGoal}</h4>
                        <div className="row g-3 mb-3">
                            <div className="col-md-4">
                                <div className="text-center p-3 bg-light rounded">
                                    <h3 className="mb-0 text-primary">{currentSprint.committedStories}</h3>
                                    <small className="text-muted">Committed Stories</small>
                                </div>
                            </div>
                            <div className="col-md-4">
                                <div className="text-center p-3 bg-light rounded">
                                    <h3 className="mb-0 text-success">{currentSprint.storyPointTotal}</h3>
                                    <small className="text-muted">Story Points</small>
                                </div>
                            </div>
                            <div className="col-md-4">
                                <div className="text-center p-3 bg-light rounded">
                                    <h3 className="mb-0 text-info">{metrics.sprintProgress}%</h3>
                                    <small className="text-muted">Progress</small>
                                </div>
                            </div>
                        </div>
                        <div className="progress mb-3" style={{ height: '20px' }}>
                            <div className="progress-bar" role="progressbar" style={{ width: `${metrics.sprintProgress}%` }}>
                                {metrics.sprintProgress}%
                            </div>
                        </div>
                        <div className="d-flex justify-content-between">
                            <small className="text-muted">Start: {new Date(currentSprint.startDate).toLocaleDateString()}</small>
                            <small className="text-muted">End: {new Date(currentSprint.endDate).toLocaleDateString()}</small>
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-md-4">
                <div className="card border-0 shadow-sm">
                    <div className="card-header bg-white border-bottom py-3">
                        <h5 className="mb-0 fw-semibold">Sprint Metrics</h5>
                    </div>
                    <div className="card-body">
                        <div className="mb-3">
                            <strong>Velocity:</strong> {metrics.velocity}
                        </div>
                        <div className="mb-3">
                            <strong>Blockers:</strong> <span className="badge bg-danger">{metrics.blockers}</span>
                        </div>
                        <div className="mb-3">
                            <strong>Risks:</strong> <span className="badge bg-warning">{metrics.risks}</span>
                        </div>
                        <div className="mb-3">
                            <strong>Completed Sprints:</strong> {metrics.completedSprints}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

// Daily Report Panel
function DailyReportPanel({ data }) {
    if (!data) {
        return <div className="alert alert-info">No daily report available</div>;
    }
    
    return (
        <div className="row g-4">
            <div className="col-12">
                <div className="card border-0 shadow-sm">
                    <div className="card-header bg-white border-bottom py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-file-earmark-text me-2"></i>
                            AI Company Daily Report - {data.date}
                        </h5>
                    </div>
                    <div className="card-body">
                        <div className="row g-4 mb-4">
                            <div className="col-md-3">
                                <div className="text-center p-3 bg-light rounded">
                                    <h3 className="mb-0 text-primary">{data.activities?.deployments || 0}</h3>
                                    <small className="text-muted">Deployments</small>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="text-center p-3 bg-light rounded">
                                    <h3 className="mb-0 text-success">{data.activities?.codeCommits || 0}</h3>
                                    <small className="text-muted">Code Commits</small>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="text-center p-3 bg-light rounded">
                                    <h3 className="mb-0 text-info">{data.activities?.pullRequests || 0}</h3>
                                    <small className="text-muted">Pull Requests</small>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="text-center p-3 bg-light rounded">
                                    <h3 className="mb-0 text-warning">{data.activities?.testsRun || 0}</h3>
                                    <small className="text-muted">Tests Run</small>
                                </div>
                            </div>
                        </div>
                        
                        {data.audit && (
                            <div className="mb-4">
                                <h6>Latest Audit</h6>
                                <div className="row g-3">
                                    <div className="col-md-3">
                                        <strong>Total Issues:</strong> {data.audit.totalIssues}
                                    </div>
                                    <div className="col-md-3">
                                        <strong>Critical:</strong> <span className="badge bg-danger">{data.audit.criticalIssues}</span>
                                    </div>
                                    <div className="col-md-3">
                                        <strong>High:</strong> <span className="badge bg-warning">{data.audit.highIssues}</span>
                                    </div>
                                    <div className="col-md-3">
                                        <strong>Security:</strong> <span className="badge bg-danger">{data.audit.securityVulnerabilities}</span>
                                    </div>
                                </div>
                            </div>
                        )}
                        
                        {data.devopsValidation && (
                            <div>
                                <h6>DevOps Validation</h6>
                                <div className="row g-3">
                                    <div className="col-md-2">
                                        <strong>Overall Score:</strong> {data.devopsValidation.overallScore}%
                                    </div>
                                    <div className="col-md-2">
                                        <strong>Docker:</strong> {data.devopsValidation.dockerValid ? '✅' : '❌'}
                                    </div>
                                    <div className="col-md-2">
                                        <strong>CI/CD:</strong> {data.devopsValidation.cicdValid ? '✅' : '❌'}
                                    </div>
                                    <div className="col-md-2">
                                        <strong>Environment:</strong> {data.devopsValidation.environmentValid ? '✅' : '❌'}
                                    </div>
                                    <div className="col-md-2">
                                        <strong>Nginx:</strong> {data.devopsValidation.nginxValid ? '✅' : '❌'}
                                    </div>
                                    <div className="col-md-2">
                                        <strong>Certificates:</strong> {data.devopsValidation.certificatesValid ? '✅' : '❌'}
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

// Risks & Warnings Panel
function RisksWarningsPanel({ data }) {
    return (
        <div className="row g-4">
            <div className="col-md-6">
                <div className="card border-0 shadow-sm border-danger">
                    <div className="card-header bg-danger text-white py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-exclamation-triangle me-2"></i>
                            Risks ({data.risks?.length || 0})
                        </h5>
                    </div>
                    <div className="card-body">
                        {data.risks?.length === 0 ? (
                            <div className="alert alert-success mb-0">No critical risks</div>
                        ) : (
                            <div className="list-group">
                                {data.risks.map((risk, idx) => (
                                    <div key={idx} className="list-group-item list-group-item-danger">
                                        <div className="d-flex justify-content-between align-items-start">
                                            <div>
                                                <h6 className="mb-1">
                                                    <span className="badge bg-danger me-2">{risk.type}</span>
                                                    {risk.title}
                                                </h6>
                                                <p className="mb-1 small">{risk.description}</p>
                                                <small className="text-muted">{risk.location}</small>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
            <div className="col-md-6">
                <div className="card border-0 shadow-sm border-warning">
                    <div className="card-header bg-warning text-dark py-3">
                        <h5 className="mb-0 fw-semibold">
                            <i className="bi bi-exclamation-circle me-2"></i>
                            Warnings ({data.warnings?.length || 0})
                        </h5>
                    </div>
                    <div className="card-body">
                        {data.warnings?.length === 0 ? (
                            <div className="alert alert-success mb-0">No warnings</div>
                        ) : (
                            <div className="list-group">
                                {data.warnings.map((warning, idx) => (
                                    <div key={idx} className="list-group-item list-group-item-warning">
                                        <div className="d-flex justify-content-between align-items-start">
                                            <div>
                                                <h6 className="mb-1">
                                                    <span className="badge bg-warning text-dark me-2">{warning.type}</span>
                                                    {warning.title}
                                                </h6>
                                                <p className="mb-1 small">{warning.description}</p>
                                                <small className="text-muted">{warning.location}</small>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

// Required Decisions Panel
function RequiredDecisionsPanel({ data, onDecision }) {
    const handleApprove = async (decisionId, type) => {
        // TODO: Implement approval logic
        alert(`Approving decision: ${decisionId}`);
        if (onDecision) onDecision();
    };
    
    const handleReject = async (decisionId) => {
        // TODO: Implement rejection logic
        alert(`Rejecting decision: ${decisionId}`);
        if (onDecision) onDecision();
    };
    
    return (
        <div className="card border-0 shadow-sm">
            <div className="card-header bg-white border-bottom py-3">
                <h5 className="mb-0 fw-semibold">
                    <i className="bi bi-clipboard-check me-2"></i>
                    Required CEO Decisions ({data.length})
                </h5>
            </div>
            <div className="card-body">
                {data.length === 0 ? (
                    <div className="alert alert-success mb-0">
                        <i className="bi bi-check-circle me-2"></i>
                        No pending decisions
                    </div>
                ) : (
                    <div className="list-group">
                        {data.map((decision) => (
                            <div key={decision.id} className={`list-group-item ${decision.priority === 'CRITICAL' ? 'list-group-item-danger' : decision.priority === 'HIGH' ? 'list-group-item-warning' : ''}`}>
                                <div className="d-flex justify-content-between align-items-start">
                                    <div className="flex-grow-1">
                                        <div className="d-flex align-items-center mb-2">
                                            <span className={`badge ${decision.priority === 'CRITICAL' ? 'bg-danger' : decision.priority === 'HIGH' ? 'bg-warning' : 'bg-secondary'} me-2`}>
                                                {decision.priority}
                                            </span>
                                            <span className="badge bg-info me-2">{decision.type}</span>
                                            <h6 className="mb-0">{decision.title}</h6>
                                        </div>
                                        <p className="mb-2">{decision.description}</p>
                                        <small className="text-muted">
                                            Reference: {decision.referenceId} • {new Date(decision.createdAt).toLocaleString()}
                                        </small>
                                    </div>
                                    <div className="ms-3">
                                        <div className="btn-group-vertical">
                                            <button 
                                                className="btn btn-sm btn-success"
                                                onClick={() => handleApprove(decision.id, decision.type)}
                                            >
                                                <i className="bi bi-check-circle me-1"></i>
                                                Approve
                                            </button>
                                            <button 
                                                className="btn btn-sm btn-danger"
                                                onClick={() => handleReject(decision.id)}
                                            >
                                                <i className="bi bi-x-circle me-1"></i>
                                                Reject
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}



