import React, { useEffect, useState } from 'react';
import { Layout } from '../components/Layout';
import { Card } from '../components/Card';
import apiService from '../services/api';
import { AnalyticsData } from '../types';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { TrendingUp, MousePointerClick, Link2 } from 'lucide-react';
import { format } from 'date-fns';

export const AnalyticsPage: React.FC = () => {
    const [analytics, setAnalytics] = useState<AnalyticsData[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchAnalytics = async () => {
            setIsLoading(true);
            try {
                const response = await apiService.getAllAnalytics();
                setAnalytics(response);
            } catch (error: any) {
                // Don't show error toast for empty analytics - it's a normal state
                // Only log to console for debugging
                console.error('Analytics fetch error:', error);
                setAnalytics([]);
            } finally {
                setIsLoading(false);
            }
        };

        fetchAnalytics();
    }, []);

    const totalClicks = analytics.reduce((sum, item) => sum + item.totalClicks, 0);
    const totalUrls = analytics.length;

    return (
        <Layout>
            <div className="space-y-6">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Analytics</h1>
                    <p className="text-gray-600 mt-1">Track your URL performance</p>
                </div>

                {isLoading ? (
                    <div className="flex items-center justify-center py-12">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
                    </div>
                ) : analytics.length === 0 ? (
                    <Card className="text-center py-12">
                        <div className="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center mx-auto mb-4">
                            <TrendingUp className="text-gray-400" size={28} />
                        </div>
                        <h3 className="text-lg font-medium text-gray-900 mb-2">No Analytics Data</h3>
                        <p className="text-gray-500 text-sm">
                            Create some URLs and share them to see analytics
                        </p>
                    </Card>
                ) : (
                    <>
                        {/* Stats Cards */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                            <Card className="bg-gradient-to-br from-blue-50 to-blue-100 border-blue-200">
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 bg-blue-500 rounded-lg flex items-center justify-center">
                                        <Link2 className="text-white" size={24} />
                                    </div>
                                    <div>
                                        <p className="text-sm text-blue-700 font-medium">Total URLs</p>
                                        <p className="text-3xl font-bold text-blue-900">{totalUrls}</p>
                                    </div>
                                </div>
                            </Card>

                            <Card className="bg-gradient-to-br from-green-50 to-green-100 border-green-200">
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 bg-green-500 rounded-lg flex items-center justify-center">
                                        <MousePointerClick className="text-white" size={24} />
                                    </div>
                                    <div>
                                        <p className="text-sm text-green-700 font-medium">Total Clicks</p>
                                        <p className="text-3xl font-bold text-green-900">{totalClicks}</p>
                                    </div>
                                </div>
                            </Card>

                            <Card className="bg-gradient-to-br from-purple-50 to-purple-100 border-purple-200">
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 bg-purple-500 rounded-lg flex items-center justify-center">
                                        <TrendingUp className="text-white" size={24} />
                                    </div>
                                    <div>
                                        <p className="text-sm text-purple-700 font-medium">Avg. Clicks/URL</p>
                                        <p className="text-3xl font-bold text-purple-900">
                                            {totalUrls > 0 ? Math.round(totalClicks / totalUrls) : 0}
                                        </p>
                                    </div>
                                </div>
                            </Card>
                        </div>

                        {/* Individual URL Analytics */}
                        <div className="space-y-6">
                            {analytics.map((item) => (
                                <Card key={item.shortCode}>
                                    <div className="mb-4">
                                        <div className="flex items-center justify-between mb-2">
                                            <h3 className="text-lg font-semibold text-primary-600 font-mono">
                                                /{item.shortCode}
                                            </h3>
                                            <span className="text-sm font-medium text-gray-600">
                                                {item.totalClicks} total clicks
                                            </span>
                                        </div>
                                        <p className="text-gray-600 text-sm break-all">{item.originalUrl}</p>
                                    </div>

                                    {item.recentClicks && item.recentClicks.length > 0 ? (
                                        <div className="mt-4">
                                            <h4 className="text-sm font-medium text-gray-700 mb-3">Recent Clicks (Last 7 Days)</h4>
                                            <ResponsiveContainer width="100%" height={200}>
                                                <BarChart
                                                    data={(() => {
                                                        const clicksByDate = item.recentClicks.reduce((acc: any, click) => {
                                                            const date = click.timestamp.split('T')[0];
                                                            acc[date] = (acc[date] || 0) + 1;
                                                            return acc;
                                                        }, {});

                                                        return Object.entries(clicksByDate)
                                                            .map(([date, clicks]) => ({ date, clicks }))
                                                            .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
                                                            .slice(-7); // Last 7 days
                                                    })()}
                                                >
                                                    <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                                                    <XAxis
                                                        dataKey="date"
                                                        tickFormatter={(date) => format(new Date(date), 'MMM dd')}
                                                        stroke="#6b7280"
                                                        fontSize={12}
                                                    />
                                                    <YAxis stroke="#6b7280" fontSize={12} allowDecimals={false} />
                                                    <Tooltip
                                                        labelFormatter={(date) => format(new Date(date), 'MMM dd, yyyy')}
                                                        contentStyle={{
                                                            backgroundColor: 'white',
                                                            border: '1px solid #e5e7eb',
                                                            borderRadius: '8px',
                                                        }}
                                                    />
                                                    <Bar dataKey="clicks" name="Clicks" fill="#0ea5e9" radius={[4, 4, 0, 0]} />
                                                </BarChart>
                                            </ResponsiveContainer>
                                        </div>
                                    ) : (
                                        <div className="mt-4 text-center py-8 bg-gray-50 rounded-lg">
                                            <p className="text-sm text-gray-500">No click data available yet</p>
                                        </div>
                                    )}
                                </Card>
                            ))}
                        </div>
                    </>
                )}
            </div>
        </Layout>
    );
};
