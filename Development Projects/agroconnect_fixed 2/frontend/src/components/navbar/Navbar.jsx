import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import cubeLogo from '/agro_cube.png';

/**
 * Elegant and Professional Navbar
 * 
 * Clean, modern design with consolidated navigation items.
 */
export default function Navbar() {
    const location = useLocation();
    const navigate = useNavigate();
    const { getCartItemsCount, setIsCartOpen } = useCart();
    const { isAuthenticated, user, logout } = useAuth();
    const { showSuccess } = useToast();
    const cartCount = getCartItemsCount();
    
    const [showSolutions, setShowSolutions] = useState(false);
    const [showProducts, setShowProducts] = useState(false);
    const [showUserMenu, setShowUserMenu] = useState(false);

    const handleLogout = () => {
        logout();
        showSuccess('Logged out successfully');
        navigate('/');
        setShowUserMenu(false);
    };

    const isActive = (path) => {
        return location.pathname === path || location.pathname.startsWith(path + '/');
    };

    const solutionsItems = [
        { path: '/buyer', label: 'For Buyers', icon: '🛒' },
        { path: '/supplier', label: 'For Suppliers', icon: '🏭' },
        { path: '/industry', label: 'Industry Solutions', icon: '🏢' }
    ];

    const productCategories = [
        { path: '/products?category=Fresh Food', label: 'Fresh Food' },
        { path: '/products?category=Dried Food', label: 'Dried Food' },
        { path: '/products?category=Frozen Food', label: 'Frozen Food' },
        { path: '/products?category=Beverage', label: 'Beverage' },
        { path: '/products?category=Seasonings & Ingredients', label: 'Seasonings & Ingredients' },
        { path: '/products?category=Ready to Cook/Eat/Drink', label: 'Ready to Cook/Eat/Drink' },
        { path: '/products?category=Snack & Dessert', label: 'Snack & Dessert' },
        { path: '/products?category=Daily Food Products', label: 'Daily Food Products' }
    ];

    return (
        <nav className="navbar navbar-expand-lg navbar-custom">
            <div className="container">
                {/* Logo */}
                <Link className="navbar-brand" to="/">
                    <img
                        src={cubeLogo}
                        className="navbar-logo"
                        alt="AgroConnectWorld"
                        style={{ height: '40px', width: 'auto' }}
                    />
                    <span className="nav-brand-text">
                        <span className="brand-primary">AgroConnect</span>
                        <span className="brand-secondary">World</span>
                    </span>
                </Link>

                {/* Mobile Toggle */}
                <button
                    className="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarNav"
                    aria-controls="navbarNav"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"></span>
                </button>

                {/* Navigation Menu */}
                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav me-auto">
                        <li className="nav-item">
                            <Link 
                                className={`nav-link ${isActive('/') ? 'active' : ''}`}
                                to="/"
                            >
                                Home
                            </Link>
                        </li>
                        
                        <li className="nav-item">
                            <Link 
                                className={`nav-link ${isActive('/products') ? 'active' : ''}`}
                                to="/products"
                            >
                                Products
                            </Link>
                        </li>

                        {/* Solutions Dropdown */}
                        <li 
                            className="nav-item dropdown"
                            onMouseEnter={() => setShowSolutions(true)}
                            onMouseLeave={() => setShowSolutions(false)}
                        >
                            <Link 
                                className={`nav-link dropdown-toggle ${isActive('/buyer') || isActive('/supplier') || isActive('/industry') ? 'active' : ''}`}
                                to="#"
                                onClick={(e) => e.preventDefault()}
                            >
                                Solutions
                            </Link>
                            <ul className={`dropdown-menu ${showSolutions ? 'show' : ''}`}>
                                {solutionsItems.map((item, index) => (
                                    <li key={index}>
                                        <Link 
                                            className="dropdown-item"
                                            to={item.path}
                                            onClick={() => setShowSolutions(false)}
                                        >
                                            <span className="me-2">{item.icon}</span>
                                            {item.label}
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        </li>

                        {/* Products Dropdown - Only show on desktop */}
                        <li 
                            className="nav-item dropdown d-none d-lg-block"
                            onMouseEnter={() => setShowProducts(true)}
                            onMouseLeave={() => setShowProducts(false)}
                        >
                            <Link 
                                className="nav-link dropdown-toggle"
                                to="/products"
                                onClick={(e) => {
                                    e.preventDefault();
                                    setShowProducts(true);
                                }}
                            >
                                Categories
                            </Link>
                            <ul className={`dropdown-menu ${showProducts ? 'show' : ''}`}>
                                {productCategories.map((item, index) => (
                                    <li key={index}>
                                        <Link 
                                            className="dropdown-item"
                                            to={item.path}
                                            onClick={() => setShowProducts(false)}
                                        >
                                            {item.label}
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        </li>

                        <li className="nav-item">
                            <Link 
                                className={`nav-link ${isActive('/about') ? 'active' : ''}`}
                                to="/about"
                            >
                                About
                            </Link>
                        </li>
                    </ul>

                    {/* Right Side Actions */}
                    <div className="navbar-actions d-flex align-items-center gap-3">
                        {/* Cart */}
                        <button
                            className="nav-action-btn cart-btn"
                            onClick={() => setIsCartOpen(true)}
                            aria-label="Shopping Cart"
                        >
                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 .491.592l-1.5 8A.5.5 0 0 1 13 12H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 1-.5-.5zM3.102 4l1.22 6.326a.5.5 0 0 0 .496.374h8.662a.5.5 0 0 0 .496-.374L14.898 4H3.102zM5 14a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm-2 1a2 2 0 1 1 4 0 2 2 0 0 1-4 0zm9-1a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm-2 1a2 2 0 1 1 4 0 2 2 0 0 1-4 0z"/>
                            </svg>
                            {cartCount > 0 && (
                                <span className="cart-badge">{cartCount}</span>
                            )}
                        </button>

                        {/* User Menu */}
                        {isAuthenticated ? (
                            <div 
                                className="nav-user-menu"
                                onMouseEnter={() => setShowUserMenu(true)}
                                onMouseLeave={() => setShowUserMenu(false)}
                            >
                                <button
                                    className="nav-user-btn"
                                    type="button"
                                    onClick={() => setShowUserMenu(!showUserMenu)}
                                >
                                    <div className="user-avatar">
                                        {user?.name?.charAt(0).toUpperCase() || 'U'}
                                    </div>
                                    <span className="user-name d-none d-md-inline">
                                        {user?.name?.split(' ')[0] || 'User'}
                                    </span>
                                    <svg width="12" height="12" fill="currentColor" viewBox="0 0 16 16" className="ms-1">
                                        <path d="M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"/>
                                    </svg>
                                </button>
                                <ul className={`user-dropdown ${showUserMenu ? 'show' : ''}`}>
                                    <li className="user-info">
                                        <div className="user-name-full">{user?.name || 'User'}</div>
                                        <div className="user-email">{user?.email || ''}</div>
                                    </li>
                                    <li><hr className="dropdown-divider" /></li>
                                    <li>
                                        <Link className="dropdown-item" to="/profile" onClick={() => setShowUserMenu(false)}>
                                            <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/>
                                                <path fillRule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"/>
                                            </svg>
                                            Profile
                                        </Link>
                                    </li>
                                    <li>
                                        <Link className="dropdown-item" to="/orders" onClick={() => setShowUserMenu(false)}>
                                            <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                <path d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 .491.592l-1.5 8A.5.5 0 0 1 13 12H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 1-.5-.5zM3.102 4l1.22 6.326a.5.5 0 0 0 .496.374h8.662a.5.5 0 0 0 .496-.374L14.898 4H3.102zM5 14a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm-2 1a2 2 0 1 1 4 0 2 2 0 0 1-4 0zm9-1a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm-2 1a2 2 0 1 1 4 0 2 2 0 0 1-4 0z"/>
                                            </svg>
                                            My Orders
                                        </Link>
                                    </li>
                                    <li>
                                        <Link className="dropdown-item" to="/quotes" onClick={() => setShowUserMenu(false)}>
                                            <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                <path d="M14 1a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H4.414A2 2 0 0 0 3 11.586l-2 2V2a1 1 0 0 1 1-1h12zM2 0a2 2 0 0 0-2 2v12.793a.5.5 0 0 0 .854.353l2.853-2.853A1 1 0 0 1 4.414 12H14a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H2z"/>
                                            </svg>
                                            My Quotes
                                        </Link>
                                    </li>
                                    {user?.role === 'SUPPLIER' && (
                                        <>
                                            <li><hr className="dropdown-divider" /></li>
                                            <li>
                                                <Link className="dropdown-item" to="/supplier-dashboard" onClick={() => setShowUserMenu(false)}>
                                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                        <path d="M1 2.5A1.5 1.5 0 0 1 2.5 1h3A1.5 1.5 0 0 1 7 2.5v3A1.5 1.5 0 0 1 5.5 7h-3A1.5 1.5 0 0 1 1 5.5v-3zM2.5 2a.5.5 0 0 0-.5.5v3a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-.5-.5h-3zm6.5.5A1.5 1.5 0 0 1 10.5 1h3A1.5 1.5 0 0 1 15 2.5v3A1.5 1.5 0 0 1 13.5 7h-3A1.5 1.5 0 0 1 9 5.5v-3zm1.5-.5a.5.5 0 0 0-.5.5v3a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-.5-.5h-3zM1 10.5A1.5 1.5 0 0 1 2.5 9h3A1.5 1.5 0 0 1 7 10.5v3A1.5 1.5 0 0 1 5.5 15h-3A1.5 1.5 0 0 1 1 13.5v-3zm1.5-.5a.5.5 0 0 0-.5.5v3a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-.5-.5h-3zm6.5.5A1.5 1.5 0 0 1 10.5 9h3a1.5 1.5 0 0 1 1.5 1.5v3a1.5 1.5 0 0 1-1.5 1.5h-3A1.5 1.5 0 0 1 9 13.5v-3zm1.5-.5a.5.5 0 0 0-.5.5v3a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-.5-.5h-3z"/>
                                                    </svg>
                                                    Supplier Dashboard
                                                </Link>
                                            </li>
                                            <li>
                                                <Link className="dropdown-item" to="/quotes/manage" onClick={() => setShowUserMenu(false)}>
                                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                        <path d="M14 1a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H4.414A2 2 0 0 0 3 11.586l-2 2V2a1 1 0 0 1 1-1h12zM2 0a2 2 0 0 0-2 2v12.793a.5.5 0 0 0 .854.353l2.853-2.853A1 1 0 0 1 4.414 12H14a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H2z"/>
                                                    </svg>
                                                    Manage Quotes
                                                </Link>
                                            </li>
                                        </>
                                    )}
                                    {user?.role === 'ADMIN' && (
                                        <>
                                            <li><hr className="dropdown-divider" /></li>
                                            <li>
                                                <Link className="dropdown-item" to="/admin-dashboard" onClick={() => setShowUserMenu(false)}>
                                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                        <path d="M9.405 1.05c-.413-1.4-2.397-1.4-2.81 0l-.1.34a1.464 1.464 0 0 1-2.105.872l-.31-.17c-1.283-.698-2.686.705-1.987 1.987l.169.311c.446.82.023 1.841-.872 2.105l-.34.1c-1.4.413-1.4 2.397 0 2.81l.34.1a1.464 1.464 0 0 1 .872 2.105l-.17.31c-.698 1.283.705 2.686 1.987 1.987l.311-.169a1.464 1.464 0 0 1 2.105.872l.1.34c.413 1.4 2.397 1.4 2.81 0l.1-.34a1.464 1.464 0 0 1 2.105-.872l.31.17c1.283.698 2.686-.705 1.987-1.987l-.169-.311a1.464 1.464 0 0 1 .872-2.105l.34-.1c1.4-.413 1.4-2.397 0-2.81l-.34-.1a1.464 1.464 0 0 1-.872-2.105l.17-.31c.698-1.283-.705-2.686-1.987-1.987l-.311.169a1.464 1.464 0 0 1-2.105-.872l-.1-.34zM8 10.93a2.929 2.929 0 1 1 0-5.86 2.929 2.929 0 0 1 0 5.858z"/>
                                                    </svg>
                                                    Admin Dashboard
                                                </Link>
                                            </li>
                                        </>
                                    )}
                                    <li><hr className="dropdown-divider" /></li>
                                    <li>
                                        <button className="dropdown-item text-danger" onClick={handleLogout}>
                                            <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16" className="me-2">
                                                <path fillRule="evenodd" d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0v2z"/>
                                                <path fillRule="evenodd" d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3z"/>
                                            </svg>
                                            Logout
                                        </button>
                                    </li>
                                </ul>
                            </div>
                        ) : (
                            <div className="auth-buttons d-flex gap-2">
                                <Link to="/login" className="btn btn-link nav-link-btn">
                                    Login
                                </Link>
                                <Link to="/register" className="btn btn-success btn-sm px-3">
                                    Sign Up
                                </Link>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
}
