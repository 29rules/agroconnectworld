import React, { useState } from 'react';
import api from '../../services/api';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * Agent Chat
 * 
 * Interface for CEO to interact with AI agents.
 */
export default function AgentChat() {
    const [messages, setMessages] = useState([
        { id: 1, sender: 'system', text: 'Welcome to Agent Chat. You can interact with AI agents here.', time: new Date().toLocaleTimeString() }
    ]);
    const [input, setInput] = useState('');
    const [selectedAgent, setSelectedAgent] = useState('cto');
    const [loading, setLoading] = useState(false);

    const agents = [
        { id: 'general', name: 'General Assistant', description: 'General purpose AI assistant' },
        { id: 'cto', name: 'CTO Agent', description: 'Technical and architecture decisions' },
        { id: 'product', name: 'Product Manager', description: 'Product strategy and planning' },
        { id: 'qa', name: 'QA Agent', description: 'Quality assurance and testing' },
        { id: 'devops', name: 'DevOps Agent', description: 'Infrastructure and deployment' },
    ];

    const handleSend = async (e) => {
        e.preventDefault();
        if (!input.trim() || loading) return;

        const userMessage = {
            id: messages.length + 1,
            sender: 'user',
            text: input,
            time: new Date().toLocaleTimeString()
        };

        setMessages(prev => [...prev, userMessage]);
        setInput('');
        setLoading(true);

        try {
            // Use CTO chat endpoint
            const response = await api.ai.ctoChat(input, `session-${Date.now()}`);
            
            const agentResponse = {
                id: messages.length + 2,
                sender: 'agent',
                text: response.response || 'No response received',
                time: new Date().toLocaleTimeString()
            };

            setMessages(prev => [...prev, agentResponse]);
        } catch (error) {
            console.error('Error sending message to agent:', error);
            let errorMessage = error.message || 'Failed to get response from agent';
            
            // Provide more helpful error messages
            if (errorMessage.includes('Network error') || errorMessage.includes('fetch')) {
                errorMessage = 'Network error: AI Company API is not reachable. Please ensure it is running on port 8087.';
            } else if (errorMessage.includes('timeout')) {
                errorMessage = 'Request timeout: The system audit is taking longer than expected. The audit may still be running in the background. Check /ai-company/reports/ for generated reports.';
            } else if (errorMessage.includes('401') || errorMessage.includes('Unauthorized')) {
                errorMessage = 'Authentication error: Please log in again.';
            } else if (errorMessage.includes('403') || errorMessage.includes('Forbidden')) {
                errorMessage = 'Access denied: CEO role required.';
            }
            
            const errorResponse = {
                id: messages.length + 2,
                sender: 'system',
                text: `Error: ${errorMessage}`,
                time: new Date().toLocaleTimeString()
            };
            setMessages(prev => [...prev, errorResponse]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container-fluid py-4">
            <div className="row mb-4">
                <div className="col-12">
                    <h2 className="h3 mb-1">
                        <i className="bi bi-chat-dots text-primary me-2"></i>
                        Agent Chat
                    </h2>
                    <p className="text-muted mb-0">Interact with AI agents for insights and assistance</p>
                </div>
            </div>

            <div className="row">
                {/* Agent Selection */}
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm h-100">
                        <div className="card-header bg-white border-bottom">
                            <h5 className="mb-0">Select Agent</h5>
                        </div>
                        <div className="card-body p-0">
                            <div className="list-group list-group-flush">
                                {agents.map((agent) => (
                                    <button
                                        key={agent.id}
                                        className={`list-group-item list-group-item-action ${selectedAgent === agent.id ? 'active' : ''}`}
                                        onClick={() => setSelectedAgent(agent.id)}
                                    >
                                        <div className="fw-bold">{agent.name}</div>
                                        <small className="text-muted">{agent.description}</small>
                                    </button>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>

                {/* Chat Interface */}
                <div className="col-md-9">
                    <div className="card border-0 shadow-sm" style={{ height: '600px', display: 'flex', flexDirection: 'column' }}>
                        <div className="card-header bg-white border-bottom">
                            <div className="d-flex justify-content-between align-items-center">
                                <h5 className="mb-0">
                                    {agents.find(a => a.id === selectedAgent)?.name}
                                </h5>
                                <span className="badge bg-success">Online</span>
                            </div>
                        </div>
                        <div className="card-body flex-grow-1 overflow-auto" style={{ maxHeight: '450px' }}>
                            {messages.map((message) => (
                                <div
                                    key={message.id}
                                    className={`mb-3 d-flex ${message.sender === 'user' ? 'justify-content-end' : 'justify-content-start'}`}
                                >
                                    <div
                                        className={`p-3 rounded ${
                                            message.sender === 'user'
                                                ? 'bg-primary text-white'
                                                : message.sender === 'system'
                                                ? 'bg-light text-muted'
                                                : 'bg-light'
                                        }`}
                                        style={{ maxWidth: '70%' }}
                                    >
                                        <div className="mb-1">{message.text}</div>
                                        <small className={message.sender === 'user' ? 'text-white-50' : 'text-muted'}>
                                            {message.time}
                                        </small>
                                    </div>
                                </div>
                            ))}
                        </div>
                        <div className="card-footer bg-white border-top">
                            <form onSubmit={handleSend}>
                                <div className="input-group">
                                    <input
                                        type="text"
                                        className="form-control"
                                        placeholder="Type your message..."
                                        value={input}
                                        onChange={(e) => setInput(e.target.value)}
                                        disabled={loading}
                                    />
                                    <button className="btn btn-primary" type="submit" disabled={loading}>
                                        {loading ? 'Sending...' : 'Send'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

