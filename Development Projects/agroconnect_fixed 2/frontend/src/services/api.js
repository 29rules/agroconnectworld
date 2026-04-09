/**
 * API Service Layer for AgroConnectWorld
 * 
 * Handles all backend API communication with:
 * - Base URL configuration
 * - Request/response interceptors
 * - Authentication token management
 * - Error handling
 * - Retry logic
 */

// Use relative URL when running in dev (Vite proxy handles it)
// Use full URL in production or if VITE_API_BASE_URL is set
// For now, use full URL to bypass potential proxy issues
// Use Vite proxy in development (bypasses CORS), full URL in production
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 
  (import.meta.env.DEV ? '/api' : 'http://localhost:8080/api');

/**
 * Get authentication token from localStorage
 */
const getAuthToken = () => {
  return localStorage.getItem('authToken');
};

/**
 * Set authentication token in localStorage
 */
const setAuthToken = (token) => {
  if (token) {
    localStorage.setItem('authToken', token);
  } else {
    localStorage.removeItem('authToken');
  }
};

/**
 * Get user information from localStorage
 */
const getUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

/**
 * Set user information in localStorage
 */
const setUser = (user) => {
  if (user) {
    localStorage.setItem('user', JSON.stringify(user));
  } else {
    localStorage.removeItem('user');
  }
};

/**
 * Clear authentication data
 */
const clearAuth = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
};

/**
 * Handle API errors
 * Note: fetch API doesn't throw for HTTP error statuses, only for network errors
 */
const handleError = (error) => {
  // If error has a response property (from our custom error object)
  if (error.response) {
    const { status, data } = error.response;
    return {
      message: data?.message || data?.error || `HTTP error! status: ${status}`,
      status,
      data: data
    };
  }
  
  // Network error (fetch throws TypeError for network failures)
  if (error instanceof TypeError || error.message?.includes('fetch') || error.message?.includes('network')) {
    return {
      message: 'Network error. Please check if the backend services are running.',
      status: 0,
      data: null
    };
  }
  
  // Other errors
  return {
    message: error.message || 'An unexpected error occurred',
    status: error.status || 0,
    data: null
  };
};

/**
 * Make API request with authentication and error handling
 */
const apiRequest = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  const token = getAuthToken();

  const defaultHeaders = {
    'Content-Type': 'application/json',
  };

  // Don't add Authorization header for auth endpoints (register, login, refresh)
  // These endpoints don't have tokens yet
  const isAuthEndpoint = endpoint.includes('/auth/register') || 
                         endpoint.includes('/auth/login') || 
                         endpoint.includes('/auth/refresh');
  
  if (token && !isAuthEndpoint) {
    defaultHeaders['Authorization'] = `Bearer ${token}`;
  }

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
  };

  try {
    console.log('[API] Making request to:', url);
    console.log('[API] Config:', { method: config.method || 'GET', headers: { ...config.headers, Authorization: config.headers.Authorization ? 'Bearer ***' : undefined } });
    
    const response = await fetch(url, config);
    
    console.log('[API] Response received:', {
      status: response.status,
      statusText: response.statusText,
      ok: response.ok,
      headers: Object.fromEntries(response.headers.entries())
    });
    
    // Handle 401 Unauthorized - token expired or invalid
    if (response.status === 401) {
      clearAuth();
      // Redirect to login if not already there
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
      const error = new Error('Authentication required');
      error.status = 401;
      throw error;
    }

    // Handle other error statuses
    if (!response.ok) {
      let errorData = {};
      try {
        const text = await response.text();
        errorData = text ? JSON.parse(text) : {};
      } catch (e) {
        // Response is not JSON, use empty object
      }
      
      const error = new Error(errorData.message || `HTTP error! status: ${response.status}`);
      error.status = response.status;
      error.response = {
        status: response.status,
        data: errorData
      };
      throw error;
    }

    // Handle empty responses
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      const text = await response.text();
      // Handle empty JSON response
      if (!text || text.trim() === '') {
        return [];
      }
      try {
        return JSON.parse(text);
      } catch (e) {
        console.warn('Failed to parse JSON response:', text);
        return [];
      }
    } else {
      const text = await response.text();
      return text || null;
    }
  } catch (error) {
    // Re-throw if it's already our custom error
    if (error.status || error.response) {
      throw error;
    }
    // Otherwise, handle as network/unknown error
    const handledError = handleError(error);
    throw handledError;
  }
};

/**
 * API Service Object
 */
const api = {
  // Authentication endpoints
  auth: {
    register: async (userData) => {
      return apiRequest('/auth/register', {
        method: 'POST',
        body: JSON.stringify(userData),
      });
    },

    login: async (credentials) => {
      const response = await apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify(credentials),
      });
      
      // Store token and user data
      if (response.token) {
        setAuthToken(response.token);
      }
      if (response.user) {
        setUser(response.user);
      }
      
      return response;
    },

    logout: () => {
      clearAuth();
    },

    getProfile: async () => {
      return apiRequest('/auth/profile', {
        method: 'GET',
      });
    },

    refreshToken: async () => {
      return apiRequest('/auth/refresh', {
        method: 'POST',
      });
    },
  },

  // Product endpoints
  products: {
    getAll: async (params = {}) => {
      const queryString = new URLSearchParams(params).toString();
      const endpoint = queryString ? `/products?${queryString}` : '/products';
      return apiRequest(endpoint, {
        method: 'GET',
      });
    },

    getById: async (id) => {
      return apiRequest(`/products/${id}`, {
        method: 'GET',
      });
    },

    create: async (productData) => {
      return apiRequest('/products', {
        method: 'POST',
        body: JSON.stringify(productData),
      });
    },

    update: async (id, productData) => {
      return apiRequest(`/products/${id}`, {
        method: 'PUT',
        body: JSON.stringify(productData),
      });
    },

    delete: async (id) => {
      return apiRequest(`/products/${id}`, {
        method: 'DELETE',
      });
    },
  },

  // Supplier endpoints
  suppliers: {
    getAll: async () => {
      return apiRequest('/suppliers', {
        method: 'GET',
      });
    },

    getById: async (id) => {
      return apiRequest(`/suppliers/${id}`, {
        method: 'GET',
      });
    },

    create: async (supplierData) => {
      return apiRequest('/suppliers', {
        method: 'POST',
        body: JSON.stringify(supplierData),
      });
    },

    update: async (id, supplierData) => {
      return apiRequest(`/suppliers/${id}`, {
        method: 'PUT',
        body: JSON.stringify(supplierData),
      });
    },
  },

  // Quote endpoints
  quotes: {
    getAll: async () => {
      return apiRequest('/quotes', {
        method: 'GET',
      });
    },

    getById: async (id) => {
      return apiRequest(`/quotes/${id}`, {
        method: 'GET',
      });
    },

    create: async (quoteData) => {
      return apiRequest('/quotes', {
        method: 'POST',
        body: JSON.stringify(quoteData),
      });
    },

    updateStatus: async (id, status) => {
      return apiRequest(`/quotes/${id}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status }),
      });
    },
  },

  // Order endpoints
  orders: {
    getAll: async () => {
      return apiRequest('/orders', {
        method: 'GET',
      });
    },

    getById: async (id) => {
      return apiRequest(`/orders/${id}`, {
        method: 'GET',
      });
    },

    create: async (orderData) => {
      return apiRequest('/orders', {
        method: 'POST',
        body: JSON.stringify(orderData),
      });
    },
  },

  // Contact endpoints
  contact: {
    getAll: async () => {
      return apiRequest('/contact', {
        method: 'GET',
      });
    },

    submit: async (messageData) => {
      return apiRequest('/contact', {
        method: 'POST',
        body: JSON.stringify(messageData),
      });
    },
  },

  // AI Company endpoints (CEO-only, requires authentication)
  ai: {
    // Status endpoints
    getCEOStatus: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/status/ceo`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getEngineeringStatus: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/status/engineering`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getQAStatus: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/status/qa`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getProductStatus: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/status/product`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getScrumStatus: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/status/scrum`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getDevOpsStatus: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/status/devops`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    // CTO Chat endpoint
    ctoChat: async (message, sessionId = null) => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      
      if (!token) {
        throw new Error('Authentication required: Please log in again.');
      }
      
      // Create AbortController for timeout (5 minutes for system audit)
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5 * 60 * 1000); // 5 minutes
      
      try {
        const response = await fetch(`${aiBaseUrl}/ai/ctochat`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
          body: JSON.stringify({
            message,
            sessionId: sessionId || `session-${Date.now()}`,
          }),
          signal: controller.signal,
        });
        
        clearTimeout(timeoutId);
        
        // Handle 401 Unauthorized - token expired or invalid
        if (response.status === 401) {
          const errorText = await response.text();
          let errorData;
          try {
            errorData = JSON.parse(errorText);
          } catch (e) {
            errorData = { message: 'Authentication failed' };
          }
          // Clear invalid token
          clearAuth();
          throw new Error('Authentication failed: Your session has expired. Please log in again.');
        }
        
        // Handle 403 Forbidden - not CEO role
        if (response.status === 403) {
          const errorText = await response.text();
          let errorData;
          try {
            errorData = JSON.parse(errorText);
          } catch (e) {
            errorData = { message: 'Access denied' };
          }
          throw new Error('Access denied: CEO role required to use this feature.');
        }
        
        if (!response.ok) {
          const errorText = await response.text();
          let errorData;
          try {
            errorData = JSON.parse(errorText);
          } catch (e) {
            errorData = { message: errorText || `HTTP error! status: ${response.status}` };
          }
          throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
      } catch (error) {
        clearTimeout(timeoutId);
        
        if (error.name === 'AbortError') {
          throw new Error('Request timeout: The system audit is taking longer than expected. Please check the AI Company API logs.');
        }
        
        if (error.message.includes('fetch') || error.message.includes('Failed to fetch') || error.message.includes('Load failed')) {
          throw new Error('Network error: Could not connect to AI Company API on port 8087. Please ensure it is running.');
        }
        
        // Re-throw authentication errors as-is
        if (error.message.includes('Authentication') || error.message.includes('Access denied')) {
          throw error;
        }
        
        throw error;
      }
    },

    // Enterprise Dashboard endpoints
    getEnvironmentStatus: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/ceo/environments/status`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getDeploymentHistory: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/ceo/deployments/history`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getCICDFailures: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/ceo/cicd/failures`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getSprintOverview: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/ceo/sprint/overview`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getDailyReport: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/ceo/daily-report`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getRisksWarnings: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/ceo/risks-warnings`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },

    getRequiredDecisions: async () => {
      const aiBaseUrl = import.meta.env.VITE_AI_API_BASE_URL || 'http://localhost:8087';
      const token = getAuthToken();
      return fetch(`${aiBaseUrl}/ai/ceo/decisions/required`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
        },
      }).then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
      });
    },
  },

  // Utility methods
  getAuthToken,
  setAuthToken,
  getUser,
  setUser,
  clearAuth,
};

export default api;

