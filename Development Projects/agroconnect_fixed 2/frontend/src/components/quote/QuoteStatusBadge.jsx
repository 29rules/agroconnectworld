import React from 'react';

/**
 * Quote Status Badge Component
 * 
 * Displays a colored badge based on quote status.
 */
export default function QuoteStatusBadge({ status }) {
    const getStatusConfig = (status) => {
        const normalizedStatus = status?.toUpperCase();
        
        switch (normalizedStatus) {
            case 'PENDING':
                return {
                    label: 'Pending',
                    variant: 'warning',
                    bgClass: 'bg-warning text-dark'
                };
            case 'REVIEWING':
                return {
                    label: 'Reviewing',
                    variant: 'info',
                    bgClass: 'bg-info text-white'
                };
            case 'APPROVED':
                return {
                    label: 'Approved',
                    variant: 'success',
                    bgClass: 'bg-success text-white'
                };
            case 'REJECTED':
                return {
                    label: 'Rejected',
                    variant: 'danger',
                    bgClass: 'bg-danger text-white'
                };
            default:
                return {
                    label: status || 'Unknown',
                    variant: 'secondary',
                    bgClass: 'bg-secondary text-white'
                };
        }
    };

    const config = getStatusConfig(status);

    return (
        <span className={`badge ${config.bgClass}`}>
            {config.label}
        </span>
    );
}



