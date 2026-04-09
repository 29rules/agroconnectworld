import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { CartProvider } from './context/CartContext';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import ErrorBoundary from './components/ErrorBoundary';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/navbar/Navbar';
import Footer from './components/footer/Footer';
import Cart from './components/cart/Cart';
import Chatbot from './components/chatbot/Chatbot';
import './components/chatbot/Chatbot.css';

import Home from './pages/Home';
import About from './pages/About.jsx';
import Buyer from './pages/Buyer.jsx';
import Supplier from './pages/Supplier.jsx'
import Products from './pages/Products';
import ProductDetail from './pages/ProductDetail';
import Industry from './pages/Industry';
import NewsUpdates from './pages/NewsUpdates.jsx'
import Contact from './pages/Contact';
import CartPage from './pages/CartPage';
import Login from './pages/Login';
import Register from './pages/Register';
import MyQuotes from './pages/MyQuotes';
import QuoteDetail from './pages/QuoteDetail';
import QuoteManagement from './pages/QuoteManagement';
import Checkout from './pages/Checkout';
import OrderConfirmation from './pages/OrderConfirmation';
import MyOrders from './pages/MyOrders';
import OrderDetail from './pages/OrderDetail';
import SupplierDashboard from './pages/SupplierDashboard';
import AdminDashboard from './pages/AdminDashboard';
import CEODashboard from './pages/ceo/CEODashboard';

function App() {
    return (
        <ErrorBoundary>
            <ToastProvider>
                <AuthProvider>
                    <CartProvider>
                        <Router>
                            <Navbar />
                            <Cart />
                            <Chatbot />

                            <Routes>
                                <Route path="/" element={<Home />} />
                                <Route path="/about" element={<About />} />
                                <Route path="/buyer" element={<Buyer />} />
                                <Route path="/supplier" element={<Supplier />} />
                                <Route path="/industry" element={<Industry />} />
                                <Route path="/products" element={<Products />} />
                                <Route path="/products/:id" element={<ProductDetail />} />
                                <Route path="/news-updates" element={<NewsUpdates />} />
                                <Route path="/contact" element={<Contact />} />
                                <Route path="/cart" element={<CartPage />} />
                                <Route path="/login" element={<Login />} />
                                <Route path="/register" element={<Register />} />
                                
                                {/* Quote Management Routes */}
                                <Route 
                                    path="/quotes" 
                                    element={
                                        <ProtectedRoute>
                                            <MyQuotes />
                                        </ProtectedRoute>
                                    } 
                                />
                                <Route 
                                    path="/quotes/:id" 
                                    element={
                                        <ProtectedRoute>
                                            <QuoteDetail />
                                        </ProtectedRoute>
                                    } 
                                />
                                <Route 
                                    path="/quotes/manage" 
                                    element={
                                        <ProtectedRoute requiredRoles={['SUPPLIER']}>
                                            <QuoteManagement />
                                        </ProtectedRoute>
                                    } 
                                />
                                
                                {/* Order Management Routes */}
                                <Route 
                                    path="/checkout" 
                                    element={
                                        <ProtectedRoute>
                                            <Checkout />
                                        </ProtectedRoute>
                                    } 
                                />
                                <Route 
                                    path="/orders" 
                                    element={
                                        <ProtectedRoute>
                                            <MyOrders />
                                        </ProtectedRoute>
                                    } 
                                />
                                <Route 
                                    path="/orders/:id" 
                                    element={
                                        <ProtectedRoute>
                                            <OrderDetail />
                                        </ProtectedRoute>
                                    } 
                                />
                                <Route 
                                    path="/orders/:id/confirmation" 
                                    element={
                                        <ProtectedRoute>
                                            <OrderConfirmation />
                                        </ProtectedRoute>
                                    } 
                                />
                                
                                {/* Supplier Dashboard */}
                                <Route 
                                    path="/supplier-dashboard" 
                                    element={
                                        <ProtectedRoute requiredRoles={['SUPPLIER']}>
                                            <SupplierDashboard />
                                        </ProtectedRoute>
                                    } 
                                />
                                
                                {/* Admin Dashboard */}
                                <Route 
                                    path="/admin-dashboard" 
                                    element={
                                        <ProtectedRoute requiredRoles={['ADMIN']}>
                                            <AdminDashboard />
                                        </ProtectedRoute>
                                    } 
                                />
                                
                                {/* CEO Dashboard */}
                                <Route 
                                    path="/admin/ceo" 
                                    element={
                                        <ProtectedRoute requiredRoles={['CEO']}>
                                            <CEODashboard />
                                        </ProtectedRoute>
                                    } 
                                />
                                
                                {/* Protected Routes - Add more as needed */}
                                {/* Example:
                                <Route 
                                    path="/profile" 
                                    element={
                                        <ProtectedRoute>
                                            <Profile />
                                        </ProtectedRoute>
                                    } 
                                />
                                <Route 
                                    path="/admin" 
                                    element={
                                        <ProtectedRoute requiredRole="ADMIN">
                                            <AdminDashboard />
                                        </ProtectedRoute>
                                    } 
                                />
                                */}
                            </Routes>

                            <Footer />
                        </Router>
                    </CartProvider>
                </AuthProvider>
            </ToastProvider>
        </ErrorBoundary>
    );
}

export default App;
