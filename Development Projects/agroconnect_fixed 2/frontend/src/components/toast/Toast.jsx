import React, { useState, useEffect } from 'react';

/**
 * Toast Notification Component
 * 
 * Displays temporary notifications for success, error, info, and warning messages.
 */
const Toast = ({ message, type = 'info', duration = 3000, onClose }) => {
    const [isVisible, setIsVisible] = useState(true);

    useEffect(() => {
        const timer = setTimeout(() => {
            setIsVisible(false);
            setTimeout(() => {
                onClose();
            }, 300); // Wait for fade-out animation
        }, duration);

        return () => clearTimeout(timer);
    }, [duration, onClose]);

    const typeStyles = {
        success: {
            bg: 'bg-success',
            icon: '✓',
            border: 'border-success'
        },
        error: {
            bg: 'bg-danger',
            icon: '✕',
            border: 'border-danger'
        },
        warning: {
            bg: 'bg-warning',
            icon: '⚠',
            border: 'border-warning'
        },
        info: {
            bg: 'bg-info',
            icon: 'ℹ',
            border: 'border-info'
        }
    };

    const style = typeStyles[type] || typeStyles.info;

    return (
        <div 
            className={`toast show align-items-center text-white ${style.bg} ${style.border} border`}
            role="alert"
            style={{
                opacity: isVisible ? 1 : 0,
                transition: 'opacity 0.3s ease-out',
                minWidth: '300px'
            }}
        >
            <div className="d-flex">
                <div className="toast-body d-flex align-items-center">
                    <span className="me-2" style={{ fontSize: '1.2rem' }}>{style.icon}</span>
                    <span>{message}</span>
                </div>
                <button 
                    type="button" 
                    className="btn-close btn-close-white me-2 m-auto"
                    onClick={() => {
                        setIsVisible(false);
                        setTimeout(() => onClose(), 300);
                    }}
                    aria-label="Close"
                ></button>
            </div>
        </div>
    );
};

export default Toast;



