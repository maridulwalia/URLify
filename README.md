# URLify - Scalable URL Shortening & Analytics Platform

<p align="center">
  <strong>A production-ready URL shortening service built with enterprise-grade security, caching, and analytics</strong>
</p>

## 🎯 Overview

URLify is a high-performance URL shortening platform designed with system design best practices. It demonstrates:
- **Scalable Architecture** with caching and database optimization
- **Enterprise Security** with JWT authentication and rate limiting
- **Real-time Analytics** for tracking URL performance
- **Clean Code** following SOLID principles and Spring Boot best practices

---

## 🏗️ System Architecture

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│     Spring Boot Application         │
│  ┌──────────────────────────────┐   │
│  │   REST Controllers           │   │
│  │  - Auth, URL, Analytics      │   │
│  └────────────┬─────────────────┘   │
│               │                     │
│  ┌────────────▼─────────────────┐   │
│  │   Service Layer              │   │
│  │  - Business Logic            │   │
│  │  - Base62 Encoding           │   │
│  └────────────┬─────────────────┘   │
│               │                     │
│  ┌────────────▼─────────────────┐   │
│  │   Security Layer             │   │
│  │  - JWT Authentication        │   │
│  │  - Rate Limiting (Bucket4j)  │   │
│  └────────────┬─────────────────┘   │
└───────────────┼─────────────────────┘
                │
        ┌───────┴────────┐
        │                │
   ┌────▼────┐      ┌────▼────┐
   │  Redis  │      │ MongoDB │
   │  Cache  │      │   DB    │
   └─────────┘      └─────────┘
```

### Key Components

| Component | Technology | Purpose | Time Complexity |
|-----------|-----------|---------|-----------------|
| **URL Generation** | Base62 Encoding | Convert IDs to short codes | O(log n) |
| **Redirect Service** | Redis Cache | Fast URL lookups | O(1) cache hit |
| **Authentication** | JWT + BCrypt | Secure user sessions | O(1) verification |
| **Rate Limiting** | Bucket4j | Prevent abuse | O(1) per request |
| **Analytics** | MongoDB + Indexes | Track clicks & metadata | O(log n) queries |

---

## 🛠️ Tech Stack

### Backend
- **Java 21** - Modern Java features
- **Spring Boot 3.2.2** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data MongoDB** - Database abstraction

### Frontend
- **React 18** - UI Library
- **TypeScript** - Static typing
- **Vite** - Build tool
- **Tailwind CSS** - Utility-first CSS framework
- **Lucide React** - Icons
- **Axios** - HTTP client
- **Recharts** - Charting library

### Database & Caching
- **MongoDB** - Primary data store
- **Redis 7** - High-speed caching layer

### Security & Performance
- **JWT (JJWT 0.12.3)** - Token-based authentication
- **Bucket4j 8.7.0** - Rate limiting with sliding window
- **BCrypt** - Password hashing

### Build & Dependencies
- **Maven** - Dependency management
- **Lombok** - Reduce boilerplate code
- **Docker** - Containerized development environment

---

## 🐳 Docker Setup

### Why Docker?

URLify uses **Docker Compose** to run its infrastructure dependencies (MongoDB and Redis). Here's why:

| Benefit | Explanation |
|---------|-------------|
| **One-command setup** | `docker-compose up -d` starts everything — no manual installation of MongoDB or Redis |
| **Environment consistency** | Every developer and every machine gets the exact same MongoDB 7 and Redis 7 versions |
| **Isolation** | Containers run in their own network (`urlify-network`), avoiding port conflicts with other local services |
| **Data persistence** | MongoDB data is stored in a named Docker volume (`mongodb_data`), so your data survives container restarts |
| **Easy teardown** | `docker-compose down` cleanly stops everything; add `-v` to also wipe data and start fresh |

### Docker Compose Services

```yaml
# docker-compose.yml defines two services:
services:
  mongodb:    # MongoDB 7 on port 27017, with persistent volume
  redis:      # Redis 7 Alpine on port 6379
```

### Docker Commands

```bash
# Start all services in the background
docker-compose up -d

# Check running containers
docker ps

# View logs
docker-compose logs -f

# Stop all services (data preserved)
docker-compose down

# Stop and remove all data (fresh start)
docker-compose down -v

# Restart a specific service
docker-compose restart redis
```

## 🔐 Security Features

### 1. **JWT Authentication**
- Stateless authentication using HS512 algorithm
- Token expiration (24 hours default)
- Secure password hashing with BCrypt

### 2. **Rate Limiting**
- **Public endpoints**: 10 requests/minute per IP
- **Authenticated endpoints**: 100 requests/minute per user
- Sliding window algorithm using Bucket4j

### 3. **Input Validation**
- URL format validation
- Blocks localhost and private IP addresses
- Prevents open redirect attacks
- Maximum URL length enforcement (2048 chars)

### 4. **Secure Redirects**
- Validates URLs before shortening
- Blocks malicious domains
- Only allows HTTP/HTTPS protocols

---

## 📊 Data & Algorithms (DSA Concepts)

### Hashing - Base62 Encoding
```java
// Converts numeric ID to alphanumeric short code
// Characters: 0-9, a-z, A-Z (62 total)
// Time Complexity: O(log n)
long id = 123456789;
String shortCode = base62Encoder.encode(id); // "8M0kX"
```

### Caching Strategy
```
Request Flow:
1. Check Redis cache → O(1)
2. If miss, query MongoDB → O(log n) with index
3. Warm cache for future requests
4. Return result
```

### Database Indexing
- **Primary Keys**: Auto-increment IDs
- **Unique Index**: `short_code` for fast lookups
- **Composite Index**: `user_id` + `created_at` for user queries
- **TTL Index**: `expires_at` for automatic cleanup

### Space-Time Trade-offs
- **Redis Cache**: Uses memory for O(1) lookups (space for speed)
- **Short Codes**: 7 characters = 62^7 = 3.5 trillion URLs
- **Analytics**: Denormalized click count for faster reads

---

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- Docker & Docker Compose (for MongoDB and Redis)
- Node.js 18+ & npm (for frontend)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/urlify.git
cd urlify
```

2. **Start MongoDB and Redis using Docker**
```bash
docker-compose up -d
```

3. **Install Frontend Dependencies**
```bash
cd frontend
npm install
```

4. **Configure application properties** (Optional)
```bash
# The application is pre-configured to work with the Docker setup
# MongoDB: mongodb://localhost:27017/urlify_db
# Redis: localhost:6379
# You can customize settings in src/main/resources/application.properties
```

4. **Build the project**
```bash
mvn clean install
```

5. **Run the application**
```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

6. **Run the Frontend**
```bash
# In the frontend directory
npm run dev
```

The frontend will start on `http://localhost:5173`

---

## 📡 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response: 201 Created
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "user@example.com",
  "message": "User registered successfully"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "user@example.com",
  "message": "Login successful"
}
```

### URL Management Endpoints

#### Shorten URL
```http
POST /api/urls/shorten
Authorization: Bearer <token>
Content-Type: application/json

{
  "url": "https://www.example.com/very/long/url",
  "customAlias": "mylink",  // Optional
  "expiryHours": 24         // Optional
}

Response: 201 Created
{
  "id": 1,
  "originalUrl": "https://www.example.com/very/long/url",
  "shortCode": "mylink",
  "shortUrl": "http://localhost:8080/mylink",
  "clicks": 0,
  "expiresAt": "2026-02-10T11:30:00",
  "createdAt": "2026-02-09T11:30:00"
}
```

#### Get My URLs
```http
GET /api/urls/my-urls
Authorization: Bearer <token>

Response: 200 OK
[
  {
    "id": 1,
    "originalUrl": "https://www.example.com",
    "shortCode": "abc123",
    "shortUrl": "http://localhost:8080/abc123",
    "clicks": 42,
    "expiresAt": null,
    "createdAt": "2026-02-09T10:00:00"
  }
]
```

#### Delete URL
```http
DELETE /api/urls/{shortCode}
Authorization: Bearer <token>

Response: 200 OK
"URL deleted successfully"
```

### Redirect Endpoint

#### Redirect to Original URL
```http
GET /{shortCode}

Response: 302 Found
Location: https://www.example.com
```

### Analytics Endpoint

#### Get URL Analytics
```http
GET /api/analytics/{shortCode}
Authorization: Bearer <token>

Response: 200 OK
{
  "shortCode": "abc123",
  "originalUrl": "https://www.example.com",
  "totalClicks": 42,
  "createdAt": "2026-02-09T10:00:00",
  "expiresAt": null,
  "recentClicks": [
    {
      "timestamp": "2026-02-09T11:30:00",
      "ipAddress": "192.168.1.1",
      "userAgent": "Mozilla/5.0...",
      "referer": "https://google.com"
    }
  ]
}
```

---

## 🔧 Configuration

### Application Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8080 |
| `spring.data.mongodb.uri` | MongoDB connection URI | mongodb://localhost:27017/urlify_db |
| `spring.data.redis.host` | Redis host | localhost |
| `spring.data.redis.port` | Redis port | 6379 |
| `jwt.secret` | JWT signing key | (pre-configured) |
| `jwt.expiration` | Token expiration (ms) | 86400000 (24h) |
| `rate.limit.public.capacity` | Public rate limit | 10 req/min |
| `rate.limit.authenticated.capacity` | Auth rate limit | 100 req/min |

---

## 📈 Scaling Strategy

### Horizontal Scaling
1. **Stateless Design**: JWT tokens enable multiple server instances
2. **Load Balancer**: Distribute traffic across instances
3. **Shared Cache**: Redis cluster for distributed caching
4. **Database Replication**: MongoDB replica set

### Vertical Scaling
1. **Database Optimization**: Add indexes, query optimization
2. **Connection Pooling**: MongoDB connection pool tuning
3. **Cache Tuning**: Adjust Redis memory and eviction policies

### Recent Improvements
- ✅ **MongoDB Migration**: Migrated from MySQL to MongoDB for better scalability
- ✅ **Error Handling**: Added graceful Redis fallback when cache is unavailable
- ✅ **Security Fixes**: Fixed 403 errors on public redirect endpoints
- ✅ **Analytics Permissions**: Fixed ownership verification for analytics access

### Future Improvements
- **CDN Integration**: Serve redirects from edge locations
- **Database Sharding**: Partition data by user ID or hash
- **Message Queue**: Async analytics processing (Kafka/RabbitMQ)
- **Microservices**: Separate redirect, analytics, and management services
- **Custom Domains**: Allow users to use their own domains
- **QR Code Generation**: Generate QR codes for short URLs
- **Geolocation Tracking**: Track user locations
- **A/B Testing**: Support multiple destinations per short code
- **Bulk URL Creation**: API for creating multiple URLs at once
- **API Versioning**: Support multiple API versions

---

## 🧪 Testing

### Manual Testing with cURL

**Register**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

**Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

**Shorten URL**
```bash
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"url":"https://www.google.com"}'
```

**Test Redirect**
```bash
curl -L http://localhost:8080/abc123
```

---

## 🔧 Troubleshooting

### Common Issues

#### 1. Redis Connection Error (500)
**Error:** `Unable to connect to Redis`

**Solution:**
```bash
# Start Redis using Docker
docker-compose up -d redis

# Verify Redis is running
docker exec urlify-redis redis-cli ping
# Expected output: PONG
```

#### 2. MongoDB Connection Error
**Error:** `Unable to connect to MongoDB`

**Solution:**
```bash
# Start MongoDB using Docker
docker-compose up -d mongodb

# Verify MongoDB is running
docker ps | findstr mongodb
```

#### 3. Analytics Permission Denied
**Error:** `You don't have permission to view analytics for this URL`

**Cause:** You're trying to access analytics for a URL you don't own.

**Solution:** Only the user who created the short URL can view its analytics.

#### 4. 403 Forbidden on Redirect
**Error:** `Forbidden (status=403)` when accessing `/{shortCode}`

**Solution:** This has been fixed in the latest version. Make sure you're running the updated code.

---

## 📝 Project Structure

```
urlify/
├── src/main/java/com/urlify/
│   ├── UrlifyApplication.java          # Main application
│   ├── config/
│   │   ├── SecurityConfig.java         # Spring Security setup
│   │   ├── RedisConfig.java            # Redis configuration
│   │   └── RateLimitConfig.java        # Rate limiting
│   ├── controller/
│   │   ├── AuthController.java         # Auth endpoints
│   │   ├── UrlController.java          # URL management
│   │   ├── RedirectController.java     # Redirect handler
│   │   └── AnalyticsController.java    # Analytics endpoints
│   ├── service/
│   │   ├── AuthService.java            # Auth business logic
│   │   ├── UrlService.java             # URL operations
│   │   ├── RedirectService.java        # Cache-first redirect
│   │   └── AnalyticsService.java       # Click tracking
│   ├── entity/
│   │   ├── User.java                   # User MongoDB entity
│   │   ├── Url.java                    # URL MongoDB entity
│   │   └── Analytics.java              # Analytics MongoDB entity
│   ├── repository/
│   │   ├── UserRepository.java         # User data access
│   │   ├── UrlRepository.java          # URL data access
│   │   └── AnalyticsRepository.java    # Analytics data access
│   ├── security/
│   │   ├── JwtTokenProvider.java       # JWT utilities
│   │   ├── JwtAuthenticationFilter.java # JWT filter
│   │   └── CustomUserDetailsService.java # User loading
│   ├── dto/
│   │   ├── RegisterRequest.java        # Registration DTO
│   │   ├── LoginRequest.java           # Login DTO
│   │   ├── ShortenUrlRequest.java      # Shorten URL DTO
│   │   ├── UrlResponse.java            # URL response DTO
│   │   ├── AuthResponse.java           # Auth response DTO
│   │   └── AnalyticsResponse.java      # Analytics response DTO
│   ├── util/
│   │   ├── Base62Encoder.java          # Base62 algorithm
│   │   └── UrlValidator.java           # URL validation
│   └── exception/
│       ├── ResourceNotFoundException.java
│       └── GlobalExceptionHandler.java # Error handling
├── src/main/resources/
│   ├── application.properties          # Main config
│   ├── application-dev.properties      # Dev config
│   └── schema.sql                      # Database schema
├── docker-compose.yml                  # Docker setup
├── pom.xml                             # Maven dependencies
├── frontend/                           # Frontend application
│   ├── src/                            # Source code
│   ├── public/                         # Static assets
│   ├── package.json                    # Frontend dependencies
│   ├── tsconfig.json                   # TypeScript config
│   ├── vite.config.ts                  # Vite config
│   └── tailwind.config.js              # Tailwind config
└── README.md                           # This file
```

---

## 🎓 Learning Outcomes

This project demonstrates understanding of:

### System Design
- ✅ Scalable architecture patterns
- ✅ Caching strategies (cache-first)
- ✅ Database design and indexing
- ✅ Load balancing considerations

### Data Structures & Algorithms
- ✅ Hashing (Base62 encoding)
- ✅ Time complexity analysis
- ✅ Space-time trade-offs
- ✅ Collision handling

### Security
- ✅ Authentication & authorization
- ✅ Rate limiting algorithms
- ✅ Input validation
- ✅ Secure password storage

### Backend Development
- ✅ RESTful API design
- ✅ Spring Boot best practices
- ✅ Spring Data MongoDB
- ✅ Redis caching with graceful fallback

---

## 👨‍💻 Author

Built with ❤️ as a demonstration of system design and backend development skills.

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bucket4j for rate limiting implementation
- Redis for blazing-fast caching
- MongoDB for flexible, scalable data persistence
