import React, { useState, useRef, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import api from '../../services/api';

/**
 * Chatbot Component
 * 
 * AI-powered chatbot using OpenRouter API to help customers with
 * questions about AgroConnectWorld services and schedule appointments.
 */
export default function Chatbot() {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const [schedulingAppointment, setSchedulingAppointment] = useState(false);
    const [appointmentData, setAppointmentData] = useState({
        name: '',
        email: '',
        phone: '',
        date: '',
        time: '',
        reason: ''
    });
    const messagesEndRef = useRef(null);
    const { isAuthenticated, user } = useAuth();
    const { showSuccess, showError } = useToast();

    useEffect(() => {
        // Initialize with welcome message when chatbot opens
        if (isOpen && messages.length === 0) {
            const welcomeMessage = {
                role: 'assistant',
                content: `Hello! 👋 Welcome to AgroConnectWorld!

I'm your AI assistant, and I'm here to help you with:

• 📦 Information about our products and services
• 🛒 How to buy or sell on our platform
• 💰 Quote requests and order placement
• 👤 Account management
• 📅 Scheduling appointments with our team
• ❓ Any questions about our B2B marketplace

How can I assist you today?`
            };
            setMessages([welcomeMessage]);
        }
    }, [isOpen]);

    useEffect(() => {
        // Auto-scroll to bottom when new messages arrive
        setTimeout(() => {
            messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
        }, 100);
    }, [messages, schedulingAppointment]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const getSystemPrompt = () => {
        return `You are a helpful customer service assistant for AgroConnectWorld, a B2B marketplace for food and agricultural products.

COMPANY INFORMATION:
- AgroConnectWorld Company Limited is a B2B marketplace connecting buyers and suppliers of food and agricultural products
- Address: 141/63 Skulthai Surawong Tower, 40th Floor, Bangrak, Bangkok 10500 Thailand
- Email: inquiry@agroconnectworld.com
- We offer products in 8 main categories: Fresh Food, Dried Food, Frozen Food, Beverage, Seasonings & Ingredients, Ready to Cook/Eat/Drink, Snack & Dessert, Daily Food Products

SERVICES FOR BUYERS:
- Browse extensive product catalog with detailed information
- Request custom quotes for bulk orders
- Place orders directly through the platform
- Track orders and shipments
- Access industry-specific solutions (Food Manufacturers, Distributors, Restaurants, Retail)
- Secure payment processing
- Multi-language support

SERVICES FOR SUPPLIERS:
- List and manage products
- Receive and respond to quote requests
- Manage orders and fulfillment
- Access analytics dashboard
- Supplier verification and certification
- Marketing and promotion tools

PLATFORM FEATURES:
- User authentication with role-based access (Buyer, Supplier, Admin)
- Real-time quote management (Pending → Reviewing → Approved/Rejected)
- Order tracking with status updates (Pending → Confirmed → Processing → Shipped → Delivered)
- Shopping cart functionality
- Product search and filtering
- Category-based browsing

APPOINTMENT SCHEDULING:
- When a user wants to schedule an appointment, acknowledge their request and inform them that an appointment form will appear
- The form collects: name, email, phone, preferred date, preferred time, and reason for appointment
- Appointments are for: Product consultations, Bulk order discussions, Supplier onboarding, Platform training, Technical support

RESPONSE GUIDELINES:
- Be friendly, professional, and concise
- Provide accurate information about our services
- If asked about specific products, direct them to browse the Products page
- If asked about becoming a supplier, mention the registration process and supplier dashboard
- If asked about pricing, explain that quotes are customized based on quantity and requirements
- Always offer to help with scheduling an appointment if they need personalized assistance
- End responses by asking if there's anything else you can help with

IMPORTANT:
- Never make up information you don't know
- Always suggest scheduling an appointment for complex inquiries
- Be helpful and solution-oriented`;
    };

    const sendMessage = async () => {
        if (!input.trim() || loading) return;

        const userMessage = input.trim();
        setInput('');
        
        // Add user message
        const newUserMessage = {
            role: 'user',
            content: userMessage
        };
        setMessages(prev => [...prev, newUserMessage]);
        setLoading(true);

        // Check if user wants to schedule appointment (before API call to save tokens)
        const wantsAppointment = userMessage.toLowerCase().includes('schedule') || 
            userMessage.toLowerCase().includes('appointment') ||
            userMessage.toLowerCase().includes('meeting') ||
            userMessage.toLowerCase().includes('book a') ||
            userMessage.toLowerCase().includes('set up a call');
            
        if (wantsAppointment) {
            setSchedulingAppointment(true);
            setLoading(false);
            setMessages(prev => [...prev, {
                role: 'assistant',
                content: `I'd be happy to help you schedule an appointment! 📅

Please fill out the form below with your details:
• Your name and contact information
• Preferred date and time
• Reason for the appointment

Our team will contact you within 24 hours to confirm the appointment.`
            }]);
            return;
        }

        try {
            const apiKey = import.meta.env.VITE_OPENROUTER_API_KEY;
            
            // Check if API key is configured
            if (!apiKey || apiKey.trim() === '') {
                throw new Error('API_KEY_MISSING');
            }

            // Call OpenRouter API
            const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${apiKey}`,
                    'HTTP-Referer': window.location.origin,
                    'X-Title': 'AgroConnectWorld Chatbot'
                },
                body: JSON.stringify({
                    model: 'openai/gpt-4o-mini',
                    messages: [
                        { role: 'system', content: getSystemPrompt() },
                        ...messages.map(m => ({ role: m.role, content: m.content })),
                        { role: 'user', content: userMessage }
                    ],
                    temperature: 0.7,
                    max_tokens: 500
                })
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                const errorMsg = errorData.error?.message || `API error: ${response.status}`;
                
                // Check for specific error types
                if (response.status === 401 || errorMsg.toLowerCase().includes('api key') || errorMsg.toLowerCase().includes('unauthorized')) {
                    throw new Error('API_KEY_INVALID');
                }
                
                throw new Error(errorMsg);
            }

            const data = await response.json();
            const assistantMessage = data.choices[0]?.message?.content || 'I apologize, but I encountered an error. Please try again.';

            setMessages(prev => [...prev, {
                role: 'assistant',
                content: assistantMessage
            }]);

            // Check if response suggests scheduling appointment
            if (assistantMessage.toLowerCase().includes('schedule') || 
                assistantMessage.toLowerCase().includes('appointment')) {
                // Don't auto-open form, let user decide
            }
        } catch (error) {
            console.error('Chatbot error:', error);
            let errorMessage = '';
            
            // Provide specific error messages
            if (error.message === 'API_KEY_MISSING') {
                errorMessage = `🔧 **Configuration Required**

The chatbot API key is not configured. To enable the chatbot:

1. Create a file named \`.env\` in the \`frontend\` folder
2. Add this line: \`VITE_OPENROUTER_API_KEY=your_api_key_here\`
3. Restart the development server

For now, you can contact us directly at inquiry@agroconnectworld.com or use the contact form on our website.`;
            } else if (error.message === 'API_KEY_INVALID' || error.message?.toLowerCase().includes('api key')) {
                errorMessage = `🔑 **API Key Issue**

The chatbot API key appears to be invalid or expired. Please check your \`.env\` file and ensure the \`VITE_OPENROUTER_API_KEY\` is correct.

You can contact us at inquiry@agroconnectworld.com for assistance.`;
            } else if (error.message?.includes('fetch') || error.message?.includes('network')) {
                errorMessage = `🌐 **Network Error**

I'm having trouble connecting to the AI service. Please check your internet connection and try again.

If the problem persists, contact us at inquiry@agroconnectworld.com`;
            } else {
                errorMessage = `⚠️ **Service Temporarily Unavailable**

I apologize, but I'm having trouble connecting right now. Error: ${error.message || 'Unknown error'}

Please try again in a moment, or contact our support team at inquiry@agroconnectworld.com`;
            }
            
            setMessages(prev => [...prev, {
                role: 'assistant',
                content: errorMessage
            }]);
            showError('Failed to get response from chatbot');
        } finally {
            setLoading(false);
        }
    };

    const handleAppointmentSubmit = async (e) => {
        e.preventDefault();
        
        if (!appointmentData.name || !appointmentData.email || !appointmentData.date || !appointmentData.time) {
            showError('Please fill in all required fields');
            return;
        }

        setLoading(true);
        try {
            // Submit appointment request via contact API
            await api.contact.submit({
                name: appointmentData.name,
                email: appointmentData.email,
                phone: appointmentData.phone || '',
                subject: 'Appointment Request - Chatbot',
                message: `Appointment Request (via Chatbot):
                
Preferred Date: ${appointmentData.date}
Preferred Time: ${appointmentData.time}
Reason: ${appointmentData.reason || 'General inquiry'}
Phone: ${appointmentData.phone || 'Not provided'}

This appointment request was submitted through the website chatbot.`
            });

            const submittedEmail = appointmentData.email;
            
            showSuccess('Appointment request submitted! Our team will contact you soon to confirm.');
            setSchedulingAppointment(false);
            setAppointmentData({
                name: '',
                email: '',
                phone: '',
                date: '',
                time: '',
                reason: ''
            });
            
            setMessages(prev => [...prev, {
                role: 'assistant',
                content: `Great! Your appointment request has been submitted successfully. 📅

Our team will contact you at ${submittedEmail} within 24 hours to confirm the appointment details.

Is there anything else I can help you with?`
            }]);
        } catch (error) {
            console.error('Appointment submission error:', error);
            showError('Failed to submit appointment request. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };

    return (
        <>
            {/* Chatbot Toggle Button */}
            <button
                className="chatbot-toggle"
                onClick={() => setIsOpen(!isOpen)}
                aria-label="Open chatbot"
            >
                {isOpen ? (
                    <svg width="24" height="24" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                    </svg>
                ) : (
                    <svg width="24" height="24" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M16 8c0 3.866-3.582 7-8 7a8.841 8.841 0 0 1-4.083-.98L2 16l1.338-3.123C2.493 11.543 1 9.973 1 8c0-3.866 3.582-7 8-7s8 3.134 8 7zM5 8a1 1 0 1 0-2 0 1 1 0 0 0 2 0zm4 0a1 1 0 1 0-2 0 1 1 0 0 0 2 0zm3 1a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/>
                    </svg>
                )}
                {!isOpen && messages.length > 1 && (
                    <span className="chatbot-notification"></span>
                )}
            </button>

            {/* Chatbot Window */}
            {isOpen && (
                <div className="chatbot-window">
                    <div className="chatbot-header">
                        <div className="d-flex align-items-center">
                            <div className="chatbot-avatar">🤖</div>
                            <div className="ms-2">
                                <div className="chatbot-title">AgroConnectWorld Assistant</div>
                                <div className="chatbot-status">Online</div>
                            </div>
                        </div>
                        <button
                            className="chatbot-close"
                            onClick={() => setIsOpen(false)}
                            aria-label="Close chatbot"
                        >
                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                <path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8 2.146 2.854Z"/>
                            </svg>
                        </button>
                    </div>

                    <div className="chatbot-messages">
                        {messages.map((message, index) => (
                            <div
                                key={index}
                                className={`chatbot-message ${message.role === 'user' ? 'user-message' : 'assistant-message'}`}
                            >
                                <div className="message-content">
                                    {message.content.split('\n').map((line, i) => (
                                        <React.Fragment key={i}>
                                            {line}
                                            {i < message.content.split('\n').length - 1 && <br />}
                                        </React.Fragment>
                                    ))}
                                </div>
                            </div>
                        ))}
                        {loading && (
                            <div className="chatbot-message assistant-message">
                                <div className="message-content">
                                    <div className="typing-indicator">
                                        <span></span>
                                        <span></span>
                                        <span></span>
                                    </div>
                                </div>
                            </div>
                        )}
                        <div ref={messagesEndRef} />
                    </div>

                    {/* Appointment Scheduling Form */}
                    {schedulingAppointment && (
                        <div className="chatbot-appointment-form">
                            <div className="appointment-form-header">
                                <h6>Schedule an Appointment</h6>
                                <button
                                    className="btn-close-sm"
                                    onClick={() => setSchedulingAppointment(false)}
                                >
                                    ×
                                </button>
                            </div>
                            <form onSubmit={handleAppointmentSubmit}>
                                <div className="mb-2">
                                    <input
                                        type="text"
                                        className="form-control form-control-sm"
                                        placeholder="Your Name *"
                                        value={appointmentData.name}
                                        onChange={(e) => setAppointmentData(prev => ({ ...prev, name: e.target.value }))}
                                        required
                                    />
                                </div>
                                <div className="mb-2">
                                    <input
                                        type="email"
                                        className="form-control form-control-sm"
                                        placeholder="Email *"
                                        value={appointmentData.email}
                                        onChange={(e) => setAppointmentData(prev => ({ ...prev, email: e.target.value }))}
                                        required
                                    />
                                </div>
                                <div className="mb-2">
                                    <input
                                        type="tel"
                                        className="form-control form-control-sm"
                                        placeholder="Phone"
                                        value={appointmentData.phone}
                                        onChange={(e) => setAppointmentData(prev => ({ ...prev, phone: e.target.value }))}
                                    />
                                </div>
                                <div className="row g-2 mb-2">
                                    <div className="col-6">
                                        <input
                                            type="date"
                                            className="form-control form-control-sm"
                                            value={appointmentData.date}
                                            onChange={(e) => setAppointmentData(prev => ({ ...prev, date: e.target.value }))}
                                            required
                                            min={new Date().toISOString().split('T')[0]}
                                            disabled={loading}
                                        />
                                    </div>
                                    <div className="col-6">
                                        <input
                                            type="time"
                                            className="form-control form-control-sm"
                                            value={appointmentData.time}
                                            onChange={(e) => setAppointmentData(prev => ({ ...prev, time: e.target.value }))}
                                            required
                                            disabled={loading}
                                        />
                                    </div>
                                </div>
                                <div className="mb-2">
                                    <textarea
                                        className="form-control form-control-sm"
                                        rows="2"
                                        placeholder="Reason for appointment (optional)"
                                        value={appointmentData.reason}
                                        onChange={(e) => setAppointmentData(prev => ({ ...prev, reason: e.target.value }))}
                                        disabled={loading}
                                    />
                                </div>
                                <div className="d-flex gap-2">
                                    <button
                                        type="button"
                                        className="btn btn-sm btn-outline-secondary flex-fill"
                                        onClick={() => setSchedulingAppointment(false)}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        type="submit"
                                        className="btn btn-sm btn-success flex-fill"
                                        disabled={loading}
                                    >
                                        {loading ? 'Submitting...' : 'Submit'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    )}

                    {/* Input Area */}
                    {!schedulingAppointment && (
                        <div className="chatbot-input">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Type your message..."
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                onKeyPress={handleKeyPress}
                                disabled={loading}
                            />
                            <button
                                className="chatbot-send-btn"
                                onClick={sendMessage}
                                disabled={loading || !input.trim()}
                            >
                                <svg width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                                    <path d="M15.854.146a.5.5 0 0 1 .11.54l-5.819 14.547a.75.75 0 0 1-1.329.124l-3.178-4.995L.643 7.184a.75.75 0 0 1 .124-1.33L15.314.037a.5.5 0 0 1 .54.11ZM6.636 10.07l2.761 4.338L14.13 2.576 6.636 10.07Zm-1.138-1.138L13.424 1.87 9.592 7.226l-4.094-1.294Z"/>
                                </svg>
                            </button>
                        </div>
                    )}
                </div>
            )}
        </>
    );
}

