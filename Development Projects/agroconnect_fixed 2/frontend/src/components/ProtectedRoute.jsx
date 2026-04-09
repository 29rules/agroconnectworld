import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Protected Route Component
 * 
 * Wraps routes that require authentication.
 * Redirects to login if user is not authenticated.
 * Supports both single role (requiredRole) and multiple roles (requiredRoles array).
 */
const ProtectedRoute = ({ children, requiredRole = null, requiredRoles = null }) => {
    const { isAuthenticated, loading, user } = useAuth();
    const location = useLocation();

    // Show loading state while checking authentication
    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '50vh' }}>
                <div className="spinner-border text-success" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    // Redirect to login if not authenticated
    if (!isAuthenticated) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // Determine which roles to check
    const rolesToCheck = requiredRoles || (requiredRole ? [requiredRole] : null);

    // Check role-based access if required
    if (rolesToCheck && rolesToCheck.length > 0) {
        const userRole = user?.role;
        const hasRequiredRole = rolesToCheck.includes(userRole);
        
        if (!hasRequiredRole) {
            return (
                <div className="container py-5" style={{ paddingTop: '120px' }}>
                    <div className="alert alert-danger text-center" role="alert">
                        <h4 className="alert-heading">Access Denied</h4>
                        <p>You do not have permission to access this page.</p>
                        <p className="mb-0">Required role(s): <strong>{rolesToCheck.join(', ')}</strong></p>
                        <p className="mb-0 mt-2">Your role: <strong>{userRole || 'None'}</strong></p>
                    </div>
                </div>
            );
        }
    }

    return children;
};

export default ProtectedRoute;

