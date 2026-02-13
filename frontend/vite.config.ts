import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    resolve: {
        alias: {
            '@': '/src',
        },
    },
    server: {
        port: 3000,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
            // Proxy short URL redirects to backend
            // Matches single-segment alphanumeric paths that aren't known frontend routes
            '^/(?!login|register|dashboard|urls|analytics|assets|node_modules|@|src)[a-zA-Z0-9]{1,10}$': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
        },
    },
})
