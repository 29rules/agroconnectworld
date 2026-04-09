import React from 'react';
import Toast from './Toast';

/**
 * Toast Container Component
 * 
 * Manages multiple toast notifications and displays them in a stack.
 */
const ToastContainer = ({ toasts, removeToast }) => {
    return (
        <div 
            className="position-fixed top-0 end-0 p-3"
            style={{ 
                zIndex: 9999,
                maxWidth: '400px',
                width: '100%'
            }}
        >
            <div className="d-flex flex-column gap-2">
                {toasts.map((toast) => (
                    <Toast
                        key={toast.id}
                        message={toast.message}
                        type={toast.type}
                        duration={toast.duration}
                        onClose={() => removeToast(toast.id)}
                    />
                ))}
            </div>
        </div>
    );
};

export default ToastContainer;



