import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * Scrum Panel
 * 
 * Agile project management and sprint overview for the CEO.
 */
export default function ScrumPanel() {
    const [scrumStats, setScrumStats] = useState({
        activeSprints: 3,
        completedSprints: 12,
        totalStoryPoints: 89,
        completedStoryPoints: 67,
        velocity: 22.3,
        teamVelocity: 67,
        sprintProgress: 75,
        blockers: 2
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchScrumStatus();
    }, []);

    const fetchScrumStatus = async () => {
        try {
            setLoading(true);
            const status = await api.ai.getScrumStatus();
            if (status?.metrics) {
                setScrumStats(status.metrics);
            }
        } catch (error) {
            console.error('Error fetching scrum status:', error);
            // Keep default values on error
        } finally {
            setLoading(false);
        }
    };

    const activeSprints = [
        { id: 1, name: 'Sprint 15 - Q4 Features', startDate: '2024-11-25', endDate: '2024-12-08', progress: 68, storyPoints: 34, completed: 23, team: 'Frontend Team' },
        { id: 2, name: 'Sprint 16 - Backend Optimization', startDate: '2024-11-25', endDate: '2024-12-08', progress: 45, storyPoints: 28, completed: 13, team: 'Backend Team' },
        { id: 3, name: 'Sprint 17 - Mobile App', startDate: '2024-11-25', endDate: '2024-12-08', progress: 82, storyPoints: 27, completed: 22, team: 'Mobile Team' },
    ];

    const backlogItems = [
        { id: 'US-1234', title: 'Implement user authentication', priority: 'High', storyPoints: 5, status: 'In Progress', assignee: 'John Doe' },
        { id: 'US-1235', title: 'Add product search feature', priority: 'Medium', storyPoints: 3, status: 'To Do', assignee: 'Jane Smith' },
        { id: 'US-1236', title: 'Update payment gateway', priority: 'High', storyPoints: 8, status: 'In Progress', assignee: 'Bob Wilson' },
        { id: 'US-1237', title: 'Mobile responsive design', priority: 'Low', storyPoints: 2, status: 'Done', assignee: 'Alice Brown' },
    ];

    const burndownData = [
        { day: 'Day 1', planned: 89, actual: 89 },
        { day: 'Day 2', planned: 85, actual: 87 },
        { day: 'Day 3', planned: 81, actual: 84 },
        { day: 'Day 4', planned: 77, actual: 80 },
        { day: 'Day 5', planned: 73, actual: 75 },
        { day: 'Today', planned: 69, actual: 67 },
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
                        <i className="bi bi-kanban text-info me-2"></i>
                        Scrum Panel
                    </h2>
                    <p className="text-muted mb-0">Agile project management and sprint metrics</p>
                </div>
            </div>

            {/* Key Metrics */}
            <div className="row g-4 mb-4">
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Active Sprints</h6>
                            <h3 className="mb-0">{scrumStats.activeSprints}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Sprint Progress</h6>
                            <h3 className="mb-0">{scrumStats.sprintProgress}%</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Team Velocity</h6>
                            <h3 className="mb-0">{scrumStats.teamVelocity} SP</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm">
                        <div className="card-body">
                            <h6 className="text-muted text-uppercase mb-2" style={{ fontSize: '0.75rem' }}>Blockers</h6>
                            <h3 className="mb-0 text-danger">{scrumStats.blockers}</h3>
                        </div>
                    </div>
                </div>
            </div>

            {/* Active Sprints */}
            <div className="row mb-4">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Active Sprints</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Sprint Name</th>
                                            <th>Team</th>
                                            <th>Progress</th>
                                            <th>Story Points</th>
                                            <th>Completed</th>
                                            <th>End Date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {activeSprints.map((sprint) => (
                                            <tr key={sprint.id}>
                                                <td className="fw-bold">{sprint.name}</td>
                                                <td>{sprint.team}</td>
                                                <td>
                                                    <div className="d-flex align-items-center">
                                                        <div className="progress me-2" style={{ height: '20px', width: '150px' }}>
                                                            <div 
                                                                className="progress-bar" 
                                                                role="progressbar" 
                                                                style={{ width: `${sprint.progress}%` }}
                                                                aria-valuenow={sprint.progress}
                                                                aria-valuemin="0"
                                                                aria-valuemax="100"
                                                            >
                                                                {sprint.progress}%
                                                            </div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>{sprint.storyPoints} SP</td>
                                                <td className="text-success">{sprint.completed} / {sprint.storyPoints}</td>
                                                <td>{sprint.endDate}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Story Points Progress */}
            <div className="row g-4 mb-4">
                <div className="col-md-6">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Story Points Progress</h5>
                        </div>
                        <div className="card-body">
                            <div className="mb-3">
                                <div className="d-flex justify-content-between mb-2">
                                    <span>Completed</span>
                                    <span className="fw-bold text-success">{scrumStats.completedStoryPoints} / {scrumStats.totalStoryPoints}</span>
                                </div>
                                <div className="progress" style={{ height: '30px' }}>
                                    <div 
                                        className="progress-bar bg-success" 
                                        role="progressbar" 
                                        style={{ width: `${(scrumStats.completedStoryPoints / scrumStats.totalStoryPoints) * 100}%` }}
                                        aria-valuenow={scrumStats.completedStoryPoints}
                                        aria-valuemin="0"
                                        aria-valuemax={scrumStats.totalStoryPoints}
                                    >
                                        {Math.round((scrumStats.completedStoryPoints / scrumStats.totalStoryPoints) * 100)}%
                                    </div>
                                </div>
                            </div>
                            <div className="row text-center">
                                <div className="col-6">
                                    <small className="text-muted">Remaining</small>
                                    <p className="mb-0 fw-bold">{scrumStats.totalStoryPoints - scrumStats.completedStoryPoints} SP</p>
                                </div>
                                <div className="col-6">
                                    <small className="text-muted">Velocity</small>
                                    <p className="mb-0 fw-bold">{scrumStats.velocity} SP/day</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-6">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Burndown Chart</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-sm mb-0">
                                    <thead>
                                        <tr>
                                            <th>Day</th>
                                            <th>Planned</th>
                                            <th>Actual</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {burndownData.map((data, index) => (
                                            <tr key={index}>
                                                <td>{data.day}</td>
                                                <td>{data.planned} SP</td>
                                                <td>{data.actual} SP</td>
                                                <td>
                                                    <span className={`badge ${data.actual <= data.planned ? 'bg-success' : 'bg-warning'}`}>
                                                        {data.actual <= data.planned ? 'On Track' : 'Behind'}
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

            {/* Backlog Items */}
            <div className="row">
                <div className="col-12">
                    <div className="card border-0 shadow-sm">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Backlog Items</h5>
                        </div>
                        <div className="card-body">
                            <div className="table-responsive">
                                <table className="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>User Story</th>
                                            <th>Title</th>
                                            <th>Priority</th>
                                            <th>Story Points</th>
                                            <th>Status</th>
                                            <th>Assignee</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {backlogItems.map((item) => (
                                            <tr key={item.id}>
                                                <td><code>{item.id}</code></td>
                                                <td>{item.title}</td>
                                                <td>
                                                    <span className={`badge ${
                                                        item.priority === 'High' ? 'bg-danger' :
                                                        item.priority === 'Medium' ? 'bg-warning' : 'bg-secondary'
                                                    }`}>
                                                        {item.priority}
                                                    </span>
                                                </td>
                                                <td>{item.storyPoints} SP</td>
                                                <td>
                                                    <span className={`badge ${
                                                        item.status === 'Done' ? 'bg-success' :
                                                        item.status === 'In Progress' ? 'bg-primary' : 'bg-secondary'
                                                    }`}>
                                                        {item.status}
                                                    </span>
                                                </td>
                                                <td>{item.assignee}</td>
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

