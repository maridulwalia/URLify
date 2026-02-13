import React, { ReactNode, useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Home, BarChart3, Link2, LogOut, User, Menu, X } from 'lucide-react';

interface LayoutProps {
    children: ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
    const { user, logout } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    // Close sidebar on route change (mobile)
    useEffect(() => {
        setSidebarOpen(false);
    }, [location.pathname]);

    // Prevent body scroll when mobile sidebar is open
    useEffect(() => {
        if (sidebarOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = '';
        }
        return () => {
            document.body.style.overflow = '';
        };
    }, [sidebarOpen]);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const navItems = [
        { path: '/dashboard', label: 'Dashboard', icon: Home },
        { path: '/urls', label: 'My URLs', icon: Link2 },
        { path: '/analytics', label: 'Analytics', icon: BarChart3 },
    ];

    const sidebarContent = (
        <>
            <div className="p-6 border-b border-gray-200">
                <h1 className="text-2xl font-bold text-primary-600">URLify</h1>
                <p className="text-sm text-gray-500 mt-1">URL Shortening Platform</p>
            </div>

            <nav className="flex-1 p-4 space-y-1">
                {navItems.map((item) => {
                    const Icon = item.icon;
                    const isActive = location.pathname === item.path;
                    return (
                        <Link
                            key={item.path}
                            to={item.path}
                            className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-all ${isActive
                                ? 'bg-primary-50 text-primary-700 font-medium'
                                : 'text-gray-600 hover:bg-gray-50'
                                }`}
                        >
                            <Icon size={20} />
                            {item.label}
                        </Link>
                    );
                })}
            </nav>

            <div className="p-4 border-t border-gray-200">
                <div className="flex items-center gap-3 px-4 py-3 mb-2">
                    <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center flex-shrink-0">
                        {user?.username ? (
                            <span className="text-primary-700 font-medium text-sm">
                                {user.username.charAt(0).toUpperCase()}
                            </span>
                        ) : (
                            <User size={16} className="text-primary-700" />
                        )}
                    </div>
                    <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-900 truncate">{user?.username || 'Guest'}</p>
                        <p className="text-xs text-gray-500 truncate">{user?.email || 'Not logged in'}</p>
                    </div>
                </div>
                <button
                    onClick={handleLogout}
                    className="w-full flex items-center gap-3 px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-lg transition-all"
                >
                    <LogOut size={20} />
                    Logout
                </button>
            </div>
        </>
    );

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Mobile Top Bar */}
            <header className="md:hidden fixed top-0 left-0 right-0 z-30 bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between">
                <h1 className="text-xl font-bold text-primary-600">URLify</h1>
                <button
                    onClick={() => setSidebarOpen(true)}
                    className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                    aria-label="Open menu"
                >
                    <Menu size={24} className="text-gray-700" />
                </button>
            </header>

            {/* Mobile Overlay Backdrop */}
            {sidebarOpen && (
                <div
                    className="md:hidden fixed inset-0 z-40 bg-black/50 backdrop-blur-sm transition-opacity"
                    onClick={() => setSidebarOpen(false)}
                />
            )}

            {/* Sidebar */}
            <aside
                className={`
                    fixed top-0 left-0 h-full w-64 bg-white border-r border-gray-200 flex flex-col z-50
                    transition-transform duration-300 ease-in-out
                    ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}
                    md:translate-x-0
                `}
            >
                {/* Mobile close button */}
                <button
                    onClick={() => setSidebarOpen(false)}
                    className="md:hidden absolute top-4 right-4 p-1 hover:bg-gray-100 rounded-lg transition-colors"
                    aria-label="Close menu"
                >
                    <X size={20} className="text-gray-500" />
                </button>

                {sidebarContent}
            </aside>

            {/* Main Content */}
            <main className="md:ml-64 pt-16 md:pt-0 p-4 sm:p-6 md:p-8">
                <div className="max-w-7xl mx-auto">{children}</div>
            </main>
        </div>
    );
};
