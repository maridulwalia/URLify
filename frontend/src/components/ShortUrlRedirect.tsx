import { useEffect } from 'react';
import { useParams } from 'react-router-dom';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

/**
 * Catch-all component for unknown routes.
 * Assumes the path is a short code and redirects the browser
 * to the backend's RedirectController.
 */
export function ShortUrlRedirect() {
    const { '*': shortCode } = useParams();

    useEffect(() => {
        if (shortCode) {
            // Strip '/api' suffix from base URL to get the backend root
            const backendRoot = API_BASE_URL.replace(/\/api\/?$/, '');
            window.location.href = `${backendRoot}/${shortCode}`;
        }
    }, [shortCode]);

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',
            color: '#888',
            fontSize: '1.1rem',
        }}>
            Redirecting…
        </div>
    );
}
