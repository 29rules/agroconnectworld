import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import api from '../services/api';

const AuthContext = createContext();

/**
 * Auth Context Provider
 * 
 * Manages authentication state and provides authentication methods throughout the application.
 */
export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    // Initialize auth state from localStorage
    useEffect(() => {
        const initializeAuth = async () => {
            try {
                const storedUser = api.getUser();
                const token = api.getAuthToken();

                if (storedUser && token) {
                    // Verify token is still valid by fetching profile
                    try {
                        const profile = await api.auth.getProfile();
                        setUser(profile);
                        setIsAuthenticated(true);
                    } catch (error) {
                        // Token invalid, clear auth
                        api.clearAuth();
                        setUser(null);
                        setIsAuthenticated(false);
                    }
                } else {
                    setUser(null);
                    setIsAuthenticated(false);
                }
            } catch (error) {
                console.error('Error initializing auth:', error);
                api.clearAuth();
                setUser(null);
                setIsAuthenticated(false);
            } finally {
                setLoading(false);
            }
        };

        initializeAuth();
    }, []);

    const login = useCallback(async (credentials) => {
        try {
            const response = await api.auth.login(credentials);
            // Backend returns {token, role} - we need to construct user object
            if (response.token) {
                // Create user object from response
                const user = {
                    email: credentials.email,
                    role: response.role || 'BUYER'
                };
                // Store token and user
                api.setAuthToken(response.token);
                api.setUser(user);
                setUser(user);
                setIsAuthenticated(true);
                return { success: true, user: user };
            }
            return { success: false, error: 'Invalid response from server' };
        } catch (error) {
            return { 
                success: false, 
                error: error.message || 'Login failed. Please check your credentials.' 
            };
        }
    }, []);

    const register = useCallback(async (userData) => {
        try {
            const response = await api.auth.register(userData);
            if (response.user && response.token) {
                setUser(response.user);
                setIsAuthenticated(true);
                return { success: true, user: response.user };
            }
            return { success: false, error: 'Registration failed' };
        } catch (error) {
            return { 
                success: false, 
                error: error.message || 'Registration failed. Please try again.' 
            };
        }
    }, []);

    const logout = useCallback(() => {
        api.auth.logout();
        setUser(null);
        setIsAuthenticated(false);
    }, []);

    const updateUser = useCallback((userData) => {
        setUser(userData);
        api.setUser(userData);
    }, []);

    const refreshUser = useCallback(async () => {
        try {
            const profile = await api.auth.getProfile();
            setUser(profile);
            api.setUser(profile);
            return profile;
        } catch (error) {
            console.error('Error refreshing user:', error);
            logout();
            throw error;
        }
    }, [logout]);

    const value = {
        user,
        loading,
        isAuthenticated,
        login,
        register,
        logout,
        updateUser,
        refreshUser
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

/**
 * Hook to use authentication
 */
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

