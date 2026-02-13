import React, { useEffect, useState } from 'react';
import { Layout } from '../components/Layout';
import { Card } from '../components/Card';
import { Button } from '../components/Button';
import { useToast } from '../context/ToastContext';
import apiService from '../services/api';
import { UrlData } from '../types';
import { Copy, Trash2, ExternalLink, Calendar, MousePointerClick } from 'lucide-react';
import { format } from 'date-fns';

export const UrlsPage: React.FC = () => {
    const [urls, setUrls] = useState<UrlData[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const { showToast } = useToast();

    const fetchUrls = async () => {
        setIsLoading(true);
        try {
            const response = await apiService.getUserUrls(page, 10);
            setUrls(response.content);
            setTotalPages(response.totalPages);
        } catch (error: any) {
            showToast(error.response?.data?.message || 'Failed to fetch URLs', 'error');
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchUrls();
    }, [page]);

    const handleCopy = async (shortCode: string) => {
        const fullUrl = `${window.location.origin}/${shortCode}`;
        try {
            await navigator.clipboard.writeText(fullUrl);
            showToast('Copied to clipboard!', 'success');
        } catch {
            showToast('Failed to copy', 'error');
        }
    };

    const handleDelete = async (shortCode: string) => {
        if (!confirm('Are you sure you want to delete this URL?')) return;

        try {
            await apiService.deleteUrl(shortCode);
            showToast('URL deleted successfully', 'success');
            fetchUrls();
        } catch (error: any) {
            showToast(error.response?.data?.message || 'Failed to delete URL', 'error');
        }
    };

    return (
        <Layout>
            <div className="space-y-6">
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-900">My URLs</h1>
                        <p className="text-gray-600 mt-1">Manage all your shortened URLs</p>
                    </div>
                </div>

                {isLoading ? (
                    <div className="flex items-center justify-center py-12">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
                    </div>
                ) : urls.length === 0 ? (
                    <Card className="text-center py-12">
                        <div className="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center mx-auto mb-4">
                            <ExternalLink className="text-gray-400" size={28} />
                        </div>
                        <h3 className="text-lg font-medium text-gray-900 mb-2">No URLs Yet</h3>
                        <p className="text-gray-500 text-sm mb-4">
                            Create your first short URL from the dashboard
                        </p>
                        <Button onClick={() => (window.location.href = '/dashboard')}>
                            Go to Dashboard
                        </Button>
                    </Card>
                ) : (
                    <>
                        <div className="grid gap-4">
                            {urls.map((url) => (
                                <Card key={url.id} className="hover:shadow-md transition-shadow">
                                    <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
                                        <div className="flex-1 min-w-0">
                                            <div className="flex items-center gap-2 mb-2">
                                                <h3 className="text-base sm:text-lg font-semibold text-primary-600 font-mono break-all">
                                                    /{url.shortCode}
                                                </h3>
                                                <button
                                                    onClick={() => handleCopy(url.shortCode)}
                                                    className="p-1 hover:bg-gray-100 rounded transition-colors flex-shrink-0"
                                                    title="Copy URL"
                                                >
                                                    <Copy size={16} className="text-gray-500" />
                                                </button>
                                            </div>

                                            <p className="text-sm sm:text-base text-gray-700 mb-3 break-all">{url.originalUrl}</p>

                                            <div className="flex flex-wrap gap-3 sm:gap-4 text-xs sm:text-sm text-gray-500">
                                                <div className="flex items-center gap-1">
                                                    <Calendar size={14} />
                                                    <span>
                                                        Created {format(new Date(url.createdAt), 'MMM dd, yyyy')}
                                                    </span>
                                                </div>

                                                {url.expiresAt && (
                                                    <div className="flex items-center gap-1">
                                                        <Calendar size={14} />
                                                        <span>
                                                            Expires {format(new Date(url.expiresAt), 'MMM dd, yyyy')}
                                                        </span>
                                                    </div>
                                                )}

                                                <div className="flex items-center gap-1">
                                                    <MousePointerClick size={14} />
                                                    <span>{url.clicks} clicks</span>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="flex gap-2 self-end sm:self-start">
                                            <button
                                                onClick={() => window.open(`/${url.shortCode}`, '_blank')}
                                                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                                                title="Open URL"
                                            >
                                                <ExternalLink size={18} className="text-gray-600" />
                                            </button>
                                            <button
                                                onClick={() => handleDelete(url.shortCode)}
                                                className="p-2 hover:bg-red-50 rounded-lg transition-colors"
                                                title="Delete URL"
                                            >
                                                <Trash2 size={18} className="text-red-600" />
                                            </button>
                                        </div>
                                    </div>
                                </Card>
                            ))}
                        </div>

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <div className="flex items-center justify-center gap-2">
                                <Button
                                    variant="secondary"
                                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                                    disabled={page === 0}
                                >
                                    Previous
                                </Button>
                                <span className="text-sm text-gray-600">
                                    Page {page + 1} of {totalPages}
                                </span>
                                <Button
                                    variant="secondary"
                                    onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                                    disabled={page === totalPages - 1}
                                >
                                    Next
                                </Button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </Layout>
    );
};
