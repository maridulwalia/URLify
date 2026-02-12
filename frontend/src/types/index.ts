export interface User {
    id: string;
    username: string;
    email: string;
}

export interface AuthResponse {
    token: string;
    user: User;
    email: string;
    message: string;
}

export interface UrlData {
    id: string;
    shortCode: string;
    originalUrl: string; // Changed from longUrl
    createdAt: string;
    expiresAt?: string; // Changed from expiryDate
    clicks: number; // Changed from clickCount
}

export interface AnalyticsData {
    shortCode: string;
    originalUrl: string; // Changed from longUrl
    totalClicks: number;
    createdAt: string;
    expiresAt?: string;
    recentClicks: ClickDetail[]; // Changed from dailyClicks
}

export interface ClickDetail {
    timestamp: string;
    ipAddress: string;
    userAgent: string;
    referer: string;
}

export interface PaginatedResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}
