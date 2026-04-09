import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';

export default function Register() {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        role: 'BUYER' // Default role
    });
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});

    const { isAuthenticated, updateUser } = useAuth();
    const { showSuccess, showError } = useToast();
    const navigate = useNavigate();

    // Redirect if already authenticated
    React.useEffect(() => {
        if (isAuthenticated) {
            navigate('/', { replace: true });
        }
    }, [isAuthenticated, navigate]);

    const validateForm = () => {
        const newErrors = {};

        if (!formData.name.trim()) {
            newErrors.name = 'Name is required';
        } else if (formData.name.trim().length < 2) {
            newErrors.name = 'Name must be at least 2 characters';
        }

        if (!formData.email.trim()) {
            newErrors.email = 'Email is required';
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Email is invalid';
        }

        if (!formData.phone.trim()) {
            newErrors.phone = 'Phone number is required';
        } else if (!/^\+?[\d\s-()]+$/.test(formData.phone)) {
            newErrors.phone = 'Phone number is invalid';
        }

        if (!formData.password) {
            newErrors.password = 'Password is required';
        } else if (formData.password.length < 6) {
            newErrors.password = 'Password must be at least 6 characters';
        }

        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Passwords do not match';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }

        setLoading(true);
        setErrors({});

        try {
            // Prepare registration data (exclude confirmPassword)
            const { confirmPassword, ...registrationData } = formData;
            
            // TEMPORARY: Console logging to verify correct routing
            console.log("Submitting registration via api.auth.register:", registrationData);
            
            // Use API wrapper directly to ensure correct routing through /api/auth/register
            // This will use API_BASE_URL which is '/api' in dev mode, making the final URL: /api/auth/register
            // The apiRequest function will construct: /api + /auth/register = /api/auth/register
            const response = await api.auth.register(registrationData);
            
            console.log("Registration successful:", response);
            
            // Store token and user if provided
            if (response.token) {
                api.setAuthToken(response.token);
            }
            if (response.user) {
                api.setUser(response.user);
            }
            
            // Update auth context state manually to avoid duplicate API call
            // The register() from context would also call api.auth.register(), so we update state directly
            if (response.user && response.token) {
                // Update context state
                updateUser(response.user);
                // Note: AuthContext will automatically set isAuthenticated when token is detected
            }
            
            if (response.token || response.user) {
                showSuccess('Registration successful! Welcome to AgroConnectWorld!');
                navigate('/', { replace: true });
            } else {
                showError('Registration failed - invalid response');
                setErrors({ submit: 'Registration failed - invalid response' });
            }
        } catch (error) {
            console.error("Registration failed:", error);
            const errorMessage = error.message || 'An unexpected error occurred';
            showError(errorMessage);
            setErrors({ submit: errorMessage });
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // Clear error for this field when user starts typing
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    return (
        <div className="py-5" style={{ paddingTop: '120px', minHeight: '80vh' }}>
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-8 col-lg-6">
                        <div className="card shadow">
                            <div className="card-body p-5">
                                <h2 className="card-title text-center mb-4 fw-bold">Create Account</h2>
                                <p className="text-center text-muted mb-4">
                                    Join AgroConnectWorld to connect with global suppliers and buyers
                                </p>

                                {errors.submit && (
                                    <div className="alert alert-danger" role="alert">
                                        {errors.submit}
                                    </div>
                                )}

                                <form onSubmit={handleSubmit}>
                                    <div className="row">
                                        <div className="col-md-12 mb-3">
                                            <label htmlFor="name" className="form-label">
                                                Full Name *
                                            </label>
                                            <input
                                                type="text"
                                                className={`form-control ${errors.name ? 'is-invalid' : ''}`}
                                                id="name"
                                                name="name"
                                                value={formData.name}
                                                onChange={handleChange}
                                                placeholder="Enter your full name"
                                                disabled={loading}
                                            />
                                            {errors.name && (
                                                <div className="invalid-feedback">
                                                    {errors.name}
                                                </div>
                                            )}
                                        </div>
                                    </div>

                                    <div className="row">
                                        <div className="col-md-6 mb-3">
                                            <label htmlFor="email" className="form-label">
                                                Email Address *
                                            </label>
                                            <input
                                                type="email"
                                                className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                                                id="email"
                                                name="email"
                                                value={formData.email}
                                                onChange={handleChange}
                                                placeholder="Enter your email"
                                                disabled={loading}
                                            />
                                            {errors.email && (
                                                <div className="invalid-feedback">
                                                    {errors.email}
                                                </div>
                                            )}
                                        </div>

                                        <div className="col-md-6 mb-3">
                                            <label htmlFor="phone" className="form-label">
                                                Phone Number *
                                            </label>
                                            <input
                                                type="tel"
                                                className={`form-control ${errors.phone ? 'is-invalid' : ''}`}
                                                id="phone"
                                                name="phone"
                                                value={formData.phone}
                                                onChange={handleChange}
                                                placeholder="Enter your phone"
                                                disabled={loading}
                                            />
                                            {errors.phone && (
                                                <div className="invalid-feedback">
                                                    {errors.phone}
                                                </div>
                                            )}
                                        </div>
                                    </div>

                                    <div className="mb-3">
                                        <label htmlFor="role" className="form-label">
                                            Account Type *
                                        </label>
                                        <select
                                            className="form-select"
                                            id="role"
                                            name="role"
                                            value={formData.role}
                                            onChange={handleChange}
                                            disabled={loading}
                                        >
                                            <option value="BUYER">Buyer</option>
                                            <option value="SUPPLIER">Supplier</option>
                                        </select>
                                        <small className="form-text text-muted">
                                            Choose whether you want to buy or sell products
                                        </small>
                                    </div>

                                    <div className="row">
                                        <div className="col-md-6 mb-3">
                                            <label htmlFor="password" className="form-label">
                                                Password *
                                            </label>
                                            <input
                                                type="password"
                                                className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                                                id="password"
                                                name="password"
                                                value={formData.password}
                                                onChange={handleChange}
                                                placeholder="Enter password"
                                                disabled={loading}
                                            />
                                            {errors.password && (
                                                <div className="invalid-feedback">
                                                    {errors.password}
                                                </div>
                                            )}
                                        </div>

                                        <div className="col-md-6 mb-3">
                                            <label htmlFor="confirmPassword" className="form-label">
                                                Confirm Password *
                                            </label>
                                            <input
                                                type="password"
                                                className={`form-control ${errors.confirmPassword ? 'is-invalid' : ''}`}
                                                id="confirmPassword"
                                                name="confirmPassword"
                                                value={formData.confirmPassword}
                                                onChange={handleChange}
                                                placeholder="Confirm password"
                                                disabled={loading}
                                            />
                                            {errors.confirmPassword && (
                                                <div className="invalid-feedback">
                                                    {errors.confirmPassword}
                                                </div>
                                            )}
                                        </div>
                                    </div>

                                    <button
                                        type="submit"
                                        className="btn btn-success w-100 mb-3"
                                        disabled={loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                                Creating Account...
                                            </>
                                        ) : (
                                            'Create Account'
                                        )}
                                    </button>
                                </form>

                                <div className="text-center">
                                    <p className="mb-0">
                                        Already have an account?{' '}
                                        <Link to="/login" className="text-success fw-bold">
                                            Sign in
                                        </Link>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

