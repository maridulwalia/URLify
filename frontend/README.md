# URLify Frontend

Modern, production-grade frontend for the URLify URL Shortening & Analytics Platform.

## Tech Stack

- **React 18** with TypeScript
- **Vite** for fast development and building
- **Tailwind CSS** for styling
- **React Router** for navigation
- **Axios** for API calls
- **Recharts** for analytics visualization
- **Lucide React** for icons

## Features

- 🔐 JWT-based authentication (Login/Register)
- 🔗 URL shortening with optional expiry
- 📋 Copy-to-clipboard functionality
- 📊 Analytics dashboard with charts
- 📱 Responsive design
- 🎨 Modern, clean UI inspired by lovable.dev
- ⚡ Fast and optimized

## Getting Started

### Prerequisites

- Node.js 16+ and npm

### Installation

```bash
cd frontend
npm install
```

### Development

```bash
npm run dev
```

The app will run on `http://localhost:3000`

### Build for Production

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

## Project Structure

```
frontend/
├── src/
│   ├── components/       # Reusable UI components
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Card.tsx
│   │   ├── Layout.tsx
│   │   └── ProtectedRoute.tsx
│   ├── context/          # React contexts
│   │   ├── AuthContext.tsx
│   │   └── ToastContext.tsx
│   ├── pages/            # Page components
│   │   ├── LoginPage.tsx
│   │   ├── RegisterPage.tsx
│   │   ├── DashboardPage.tsx
│   │   ├── UrlsPage.tsx
│   │   └── AnalyticsPage.tsx
│   ├── services/         # API service layer
│   │   └── api.ts
│   ├── types/            # TypeScript types
│   │   └── index.ts
│   ├── App.tsx           # Main app component
│   ├── main.tsx          # Entry point
│   └── index.css         # Global styles
├── index.html
├── package.json
├── tailwind.config.js
├── tsconfig.json
└── vite.config.ts
```

## Environment Variables

Create a `.env` file in the frontend directory:

```
VITE_API_URL=http://localhost:8080/api
```

## API Integration

The frontend connects to the backend API running on `http://localhost:8080`. Make sure the backend is running before starting the frontend.

The API service automatically:
- Attaches JWT tokens to requests
- Handles 401 errors and redirects to login
- Manages authentication state

## Design Philosophy

- **Minimal & Modern**: Clean SaaS aesthetic
- **User-Friendly**: Intuitive navigation and clear feedback
- **Responsive**: Works on all screen sizes
- **Performance**: Optimized for speed
- **Accessible**: Semantic HTML and proper ARIA labels
