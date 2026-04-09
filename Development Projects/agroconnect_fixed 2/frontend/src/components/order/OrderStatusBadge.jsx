import React from 'react';

/**
 * Order Status Badge Component
 * 
 * Displays a colored badge based on order status.
 */
export default function OrderStatusBadge({ status }) {
    const getStatusConfig = (status) => {
        const normalizedStatus = status?.toUpperCase();
        
        switch (normalizedStatus) {
            case 'PENDING':
                return {
                    label: 'Pending',
                    variant: 'warning',
                    bgClass: 'bg-warning text-dark'
                };
            case 'CONFIRMED':
                return {
                    label: 'Confirmed',
                    variant: 'info',
                    bgClass: 'bg-info text-white'
                };
            case 'PROCESSING':
                return {
                    label: 'Processing',
                    variant: 'primary',
                    bgClass: 'bg-primary text-white'
                };
            case 'SHIPPED':
                return {
                    label: 'Shipped',
                    variant: 'info',
                    bgClass: 'bg-info text-white'
                };
            case 'DELIVERED':
                return {
                    label: 'Delivered',
                    variant: 'success',
                    bgClass: 'bg-success text-white'
                };
            case 'CANCELLED':
                return {
                    label: 'Cancelled',
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



