import { InputHTMLAttributes, forwardRef, useState, useEffect } from 'react';
import { Eye, EyeOff } from 'lucide-react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
    showPasswordStrength?: boolean;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
    ({ label, error, className = '', type, showPasswordStrength = false, value, ...props }, ref) => {
        const [showPassword, setShowPassword] = useState(false);
        const [passwordStrength, setPasswordStrength] = useState<{
            score: number;
            label: string;
            color: string;
        }>({ score: 0, label: '', color: '' });

        const isPasswordField = type === 'password';

        useEffect(() => {
            if (showPasswordStrength && typeof value === 'string') {
                const strength = calculatePasswordStrength(value);
                setPasswordStrength(strength);
            }
        }, [value, showPasswordStrength]);

        const calculatePasswordStrength = (password: string) => {
            let score = 0;
            if (!password) return { score: 0, label: '', color: '' };

            // Length check
            if (password.length >= 8) score += 1;
            if (password.length >= 12) score += 1;

            // Character variety checks
            if (/[a-z]/.test(password)) score += 1; // lowercase
            if (/[A-Z]/.test(password)) score += 1; // uppercase
            if (/[0-9]/.test(password)) score += 1; // numbers
            if (/[^a-zA-Z0-9]/.test(password)) score += 1; // special chars

            // Determine label and color
            if (score <= 2) return { score, label: 'Weak', color: 'bg-red-500' };
            if (score <= 4) return { score, label: 'Medium', color: 'bg-yellow-500' };
            return { score, label: 'Strong', color: 'bg-green-500' };
        };

        return (
            <div className="w-full">
                {label && <label className="label">{label}</label>}
                <div className="relative">
                    <input
                        ref={ref}
                        type={isPasswordField && showPassword ? 'text' : type}
                        value={value}
                        className={`input ${error ? 'border-red-500 focus:ring-red-500' : ''} ${isPasswordField ? 'pr-10' : ''
                            } ${className}`}
                        {...props}
                    />
                    {isPasswordField && (
                        <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700 focus:outline-none"
                            tabIndex={-1}
                        >
                            {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                        </button>
                    )}
                </div>
                {error && <p className="mt-1 text-sm text-red-600">{error}</p>}

                {/* Password Strength Indicator */}
                {showPasswordStrength && isPasswordField && value && (
                    <div className="mt-2">
                        <div className="flex items-center gap-2 mb-1">
                            <div className="flex-1 h-1.5 bg-gray-200 rounded-full overflow-hidden">
                                <div
                                    className={`h-full transition-all duration-300 ${passwordStrength.color}`}
                                    style={{ width: `${(passwordStrength.score / 6) * 100}%` }}
                                />
                            </div>
                            <span className={`text-xs font-medium ${passwordStrength.label === 'Weak' ? 'text-red-600' :
                                passwordStrength.label === 'Medium' ? 'text-yellow-600' :
                                    'text-green-600'
                                }`}>
                                {passwordStrength.label}
                            </span>
                        </div>
                        <p className="text-xs text-gray-500">
                            Use 8+ characters with uppercase, lowercase, numbers & symbols
                        </p>
                    </div>
                )}
            </div>
        );
    }
);

Input.displayName = 'Input';
