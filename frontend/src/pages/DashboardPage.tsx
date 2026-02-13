import React, { useState } from 'react';
import { Layout } from '../components/Layout';
import { Card } from '../components/Card';
import { Input } from '../components/Input';
import { Button } from '../components/Button';
import { useToast } from '../context/ToastContext';
import apiService from '../services/api';
import { Link2, Copy, Check } from 'lucide-react';

export const DashboardPage: React.FC = () => {
    const [longUrl, setLongUrl] = useState('');
    const [expiryDate, setExpiryDate] = useState('');
    const [shortUrl, setShortUrl] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [copied, setCopied] = useState(false);
    const [error, setError] = useState('');

    const { showToast } = useToast();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (!longUrl) {
            setError('Please enter a URL');
            return;
        }

        // Basic URL validation
        try {
            new URL(longUrl);
        } catch {
            setError('Please enter a valid URL');
            return;
        }

        setIsLoading(true);
        try {
            const response = await apiService.createShortUrl(longUrl, expiryDate || undefined);
            const fullShortUrl = `${window.location.origin}/${response.shortCode}`;
            setShortUrl(fullShortUrl);
            showToast('Short URL created successfully!', 'success');
            setLongUrl('');
            setExpiryDate('');
        } catch (error: any) {
            showToast(error.response?.data?.message || 'Failed to create short URL', 'error');
        } finally {
            setIsLoading(false);
        }
    };

    const handleCopy = async () => {
        try {
            await navigator.clipboard.writeText(shortUrl);
            setCopied(true);
            showToast('Copied to clipboard!', 'success');
            setTimeout(() => setCopied(false), 2000);
        } catch {
            showToast('Failed to copy', 'error');
        }
    };

    return (
        <Layout>
            <div className="space-y-6">
                <div>
                    <h1 className="text-2xl sm:text-3xl font-bold text-gray-900">Dashboard</h1>
                    <p className="text-gray-600 mt-1">Create and manage your short URLs</p>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Create Short URL Card */}
                    <Card>
                        <div className="flex items-center gap-3 mb-6">
                            <div className="w-10 h-10 bg-primary-100 rounded-lg flex items-center justify-center">
                                <Link2 className="text-primary-600" size={20} />
                            </div>
                            <div>
                                <h2 className="text-xl font-semibold text-gray-900">Create Short URL</h2>
                                <p className="text-sm text-gray-500">Shorten your long URLs instantly</p>
                            </div>
                        </div>

                        <form onSubmit={handleSubmit} className="space-y-4">
                            <Input
                                label="Long URL"
                                type="url"
                                value={longUrl}
                                onChange={(e) => setLongUrl(e.target.value)}
                                placeholder="https://example.com/very/long/url"
                                error={error}
                            />

                            <Input
                                label="Expiry Date (Optional)"
                                type="datetime-local"
                                value={expiryDate}
                                onChange={(e) => setExpiryDate(e.target.value)}
                            />

                            <Button type="submit" isLoading={isLoading} className="w-full">
                                Shorten URL
                            </Button>
                        </form>
                    </Card>

                    {/* Result Card */}
                    {shortUrl && (
                        <Card className="bg-gradient-to-br from-primary-50 to-primary-100 border-primary-200">
                            <div className="flex items-center gap-3 mb-6">
                                <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center">
                                    <Check className="text-green-600" size={20} />
                                </div>
                                <div>
                                    <h2 className="text-xl font-semibold text-gray-900">Your Short URL</h2>
                                    <p className="text-sm text-gray-600">Ready to share!</p>
                                </div>
                            </div>

                            <div className="bg-white rounded-lg p-4 mb-4">
                                <p className="text-sm text-gray-500 mb-1">Short URL</p>
                                <div className="flex items-center justify-between gap-4">
                                    <p className="text-lg font-mono text-primary-700 break-all">{shortUrl}</p>
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <Button onClick={handleCopy} variant="secondary" className="w-full">
                                    {copied ? (
                                        <>
                                            <Check size={16} className="mr-2" />
                                            Copied!
                                        </>
                                    ) : (
                                        <>
                                            <Copy size={16} className="mr-2" />
                                            Copy
                                        </>
                                    )}
                                </Button>
                                <Button
                                    onClick={() => {
                                        setShortUrl('');
                                        setLongUrl('');
                                        setExpiryDate('');
                                    }}
                                    variant="outline"
                                    className="w-full bg-white"
                                >
                                    Create Another
                                </Button>
                            </div>
                        </Card>
                    )}

                    {/* Empty State */}
                    {!shortUrl && (
                        <Card className="flex items-center justify-center bg-gray-50 border-dashed">
                            <div className="text-center py-12">
                                <div className="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <Link2 className="text-gray-400" size={28} />
                                </div>
                                <h3 className="text-lg font-medium text-gray-900 mb-2">No URL Created Yet</h3>
                                <p className="text-gray-500 text-sm">
                                    Enter a long URL and click "Shorten URL" to get started
                                </p>
                            </div>
                        </Card>
                    )}
                </div>
            </div>
        </Layout>
    );
};
