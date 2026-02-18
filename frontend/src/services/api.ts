import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

class ApiService {
    private api: AxiosInstance;

    constructor() {
        this.api = axios.create({
            baseURL: API_BASE_URL,
            headers: {
                'Content-Type': 'application/json',
            },
        });

        // Request interceptor to add JWT token
        this.api.interceptors.request.use(
            (config: InternalAxiosRequestConfig) => {
                const token = localStorage.getItem('token');
                if (token && config.headers) {
                    config.headers.Authorization = `Bearer ${token}`;
                }
                return config;
            },
            (error) => Promise.reject(error)
        );

        // Response interceptor to handle 401 errors
        this.api.interceptors.response.use(
            (response) => response,
            (error: AxiosError) => {
                if (error.response?.status === 401) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    window.location.href = '/login';
                }
                return Promise.reject(error);
            }
        );
    }

    // Auth endpoints
    async login(email: string, password: string) {
        const response = await this.api.post('/auth/login', { email, password });
        return response.data;
    }

    async register(username: string, email: string, password: string) {
        const response = await this.api.post('/auth/register', { username, email, password });
        return response.data;
    }

    // URL endpoints
    async createShortUrl(longUrl: string, expiryDate?: string) {
        // Calculate expiry hours from the datetime-local input
        let expiryHours: number | undefined;
        if (expiryDate) {
            const expiryTime = new Date(expiryDate).getTime();
            const now = new Date().getTime();
            const diffMs = expiryTime - now;
            expiryHours = Math.max(1, Math.ceil(diffMs / (1000 * 60 * 60))); // Convert to hours
        }

        const response = await this.api.post('/urls/shorten', {
            url: longUrl, // Backend expects 'url' not 'longUrl'
            expiryHours: expiryHours || null
        });
        return response.data;
    }

    async getUserUrls(page: number = 0, size: number = 10) {
        const response = await this.api.get('/urls/my-urls', {
            params: { page, size }
        });
        return response.data;
    }

    async deleteUrl(shortCode: string) {
        const response = await this.api.delete(`/urls/${shortCode}`);
        return response.data;
    }

    // Analytics endpoints
    async getUrlAnalytics(shortCode: string) {
        const response = await this.api.get(`/analytics/${shortCode}`);
        return response.data;
    }

    async getAllAnalytics() {
        const response = await this.api.get('/analytics/all');
        return response.data;
    }
}

export default new ApiService();
