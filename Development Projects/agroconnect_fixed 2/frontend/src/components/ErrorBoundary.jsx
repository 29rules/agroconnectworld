import React from 'react';

/**
 * Error Boundary Component
 * 
 * Catches JavaScript errors anywhere in the child component tree,
 * logs those errors, and displays a fallback UI instead of crashing.
 */
class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = { 
            hasError: false, 
            error: null,
            errorInfo: null 
        };
    }

    static getDerivedStateFromError(error) {
        // Update state so the next render will show the fallback UI
        return { hasError: true };
    }

    componentDidCatch(error, errorInfo) {
        // Log error to console and potentially to error reporting service
        console.error('ErrorBoundary caught an error:', error, errorInfo);
        
        this.setState({
            error: error,
            errorInfo: errorInfo
        });

        // TODO: Send error to error reporting service (e.g., Sentry)
        // if (process.env.NODE_ENV === 'production') {
        //     logErrorToService(error, errorInfo);
        // }
    }

    handleReset = () => {
        this.setState({ 
            hasError: false, 
            error: null, 
            errorInfo: null 
        });
    };

    render() {
        if (this.state.hasError) {
            // Custom fallback UI
            return (
                <div className="container py-5" style={{ paddingTop: '120px' }}>
                    <div className="row justify-content-center">
                        <div className="col-md-8">
                            <div className="card border-danger shadow">
                                <div className="card-body text-center py-5">
                                    <div className="mb-4" style={{ fontSize: '4rem' }}>
                                        ⚠️
                                    </div>
                                    <h2 className="card-title text-danger mb-3">
                                        Something went wrong
                                    </h2>
                                    <p className="card-text text-muted mb-4">
                                        We're sorry, but something unexpected happened. 
                                        Please try refreshing the page or contact support if the problem persists.
                                    </p>
                                    
                                    {process.env.NODE_ENV === 'development' && this.state.error && (
                                        <details className="text-start mb-4">
                                            <summary className="text-danger cursor-pointer">
                                                Error Details (Development Only)
                                            </summary>
                                            <pre className="bg-light p-3 rounded mt-2" style={{ fontSize: '0.85rem', maxHeight: '300px', overflow: 'auto' }}>
                                                <strong>Error:</strong> {this.state.error.toString()}
                                                {this.state.errorInfo && (
                                                    <>
                                                        <br /><br />
                                                        <strong>Stack Trace:</strong>
                                                        <br />
                                                        {this.state.errorInfo.componentStack}
                                                    </>
                                                )}
                                            </pre>
                                        </details>
                                    )}

                                    <div className="d-flex gap-2 justify-content-center">
                                        <button 
                                            className="btn btn-primary"
                                            onClick={this.handleReset}
                                        >
                                            Try Again
                                        </button>
                                        <button 
                                            className="btn btn-outline-secondary"
                                            onClick={() => window.location.href = '/'}
                                        >
                                            Go Home
                                        </button>
                                        <button 
                                            className="btn btn-outline-secondary"
                                            onClick={() => window.location.reload()}
                                        >
                                            Refresh Page
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            );
        }

        return this.props.children;
    }
}

export default ErrorBoundary;



