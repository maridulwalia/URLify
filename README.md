# URLify - Scalable URL Shortening & Analytics Platform

<p align="center">
  <strong>A scalable URL shortening service built with caching, authentication, and analytics.
</strong>
</p>

## 🎯 Overview

URLify is a high-performance URL shortening platform designed with system design best practices. It demonstrates:
- **Scalable Architecture** with caching and database optimization
- **Enterprise Security** with JWT authentication and rate limiting
- **Real-time Analytics** for tracking URL performance
- **Clean Code** following SOLID principles and Spring Boot best practices

---

## 🚀 Live Demo & Deployment

See the application in action: **[Live Demo](https://urlify-beta.vercel.app/login)**

### Deployment Stack
| Component | Service |
|-----------|---------|
| **Frontend** | [Vercel](https://vercel.com/) |
| **Backend** | [Render](https://render.com/) |
| **Database** | [MongoDB Atlas](https://www.mongodb.com/atlas) |
| **Caching** | [Upstash Redis](https://upstash.com/) |

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

## ⚖️ Design Decisions & Tradeoffs

### Why Redis for Redirects?

URL redirects are the highest-traffic operation in any shortener. Redis stores short-code-to-URL mappings in memory, giving O(1) lookups without touching the database on every request. This keeps redirect latency low and reduces load on MongoDB.

**Tradeoff:** Redis adds operational complexity and memory cost. Every cached URL consumes memory, and if Redis goes down, the system falls back to MongoDB (slower, but still functional). There's also a cache-invalidation concern — when a URL is deleted or updated, the Redis entry must be explicitly evicted to avoid stale redirects.

### Why MongoDB?

MongoDB was chosen over a relational database for several practical reasons:
- **Schema flexibility** — URL metadata and analytics documents can evolve without migrations
- **Document model** — analytics click events map naturally to embedded or linked documents
- **Horizontal scalability** — MongoDB supports sharding out of the box, which matters if the URL dataset grows significantly
- **TTL indexes** — native support for automatic expiry of documents (used for URL expiration)

**Tradeoff:** MongoDB lacks full ACID transactions across collections (though single-document atomicity is guaranteed). For this application's access patterns — mostly inserts and key-based lookups — this is an acceptable tradeoff. Relational joins are not needed here, so the absence of them is not a limitation.

### Why Base62 Encoding?

Base62 uses `[0-9a-zA-Z]` — 62 characters that are all URL-safe without encoding. A 7-character Base62 code supports 62^7 ≈ 3.5 trillion unique URLs, which is more than enough. The encoding is deterministic given an input ID and produces short, human-readable codes.

**Tradeoff:** Base62 codes are sequential if derived from auto-incrementing IDs, which can make URL patterns guessable. In a production system, you might combine this with a random offset or use a hash-based approach to reduce predictability.

### Why JWT Stateless Auth Over Sessions?

JWT-based authentication was chosen because it removes the need for server-side session storage. The token carries the user's identity and is verified on each request using the signing key — no database or Redis lookup is needed for authentication.

This is critical for horizontal scaling: any server instance can validate the token independently, so requests can be load-balanced freely without sticky sessions.

**Tradeoff:** Token revocation is harder. Since there's no server-side session store, you can't easily invalidate a specific token before it expires. Mitigation options include short token lifetimes (currently 24 hours) or maintaining a token blocklist, but neither is free. Token size is also larger than a simple session cookie — each request carries the full JWT in the `Authorization` header.

---

## 🚀 Performance Considerations

### Cache-First Redirect Flow

The redirect path is the most latency-sensitive operation. The flow is:

1. **Check Redis** — look up the short code in cache (O(1))
2. **Cache hit** — return the original URL immediately, no database involved
3. **Cache miss** — query MongoDB using the indexed `shortCode` field (O(log n)), cache the result in Redis for subsequent requests, then return

This means the first access to a short URL after a cold start or cache eviction hits MongoDB, but all subsequent accesses are served from memory.

### MongoDB Index Usage

The following indexes are defined to keep query performance predictable:
- **Unique index on `shortCode`** — ensures O(log n) lookups for redirect and deduplication
- **Composite index on `userId` + `createdAt`** — supports the "my URLs" listing query without a collection scan
- **TTL index on `expiresAt`** — MongoDB automatically removes expired URL documents, no background job needed

Without these indexes, queries on a growing collection would degrade to full scans.

### Scalability Implications

- **Stateless backend** — JWT auth means you can run multiple Spring Boot instances behind a load balancer with no session affinity
- **Shared Redis** — all instances point to the same Redis, so cache is consistent across the cluster
- **MongoDB replica set** — read-heavy workloads (analytics queries, URL lookups) can be distributed across replicas

### Potential Bottlenecks

- **Redis memory** — if the URL dataset grows very large, not all mappings can fit in Redis. An eviction policy (e.g., LRU) would be needed
- **Analytics writes** — every redirect triggers an analytics write to MongoDB. Under high traffic, this could become a write bottleneck. Batching or async writes (e.g., via a message queue) would help but are not currently implemented
- **Single MongoDB instance** — the current Docker Compose setup runs a single MongoDB node. For production, a replica set or sharded cluster would be necessary

---

## ❗ Error Handling Strategy

### Centralized Exception Handling

All exceptions are routed through `GlobalExceptionHandler`, a `@RestControllerAdvice` class. This ensures that no raw stack traces or Spring default error pages leak to the client. Every error response follows a consistent structure.

### Consistent HTTP Status Codes

| Scenario | Status Code | Example |
|----------|-------------|---------|
| Resource not found | `404` | Short code doesn't exist |
| Validation failure | `400` | Invalid URL format, missing fields |
| Unauthorized | `401` | Missing or expired JWT |
| Forbidden | `403` | Accessing another user's analytics |
| Rate limited | `429` | Too many requests |
| Server error | `500` | Unexpected failures |

### Validation Handling

Input validation happens at two levels:
1. **DTO-level** — Spring's `@Valid` annotations catch missing or malformed fields before the request reaches the service layer
2. **Business-level** — custom `UrlValidator` checks for blocked protocols, localhost URLs, private IPs, and excessively long URLs

Validation errors return structured responses with specific field-level messages, not generic "Bad Request" strings.

### Graceful Redis Fallback

If Redis is unavailable (connection refused, timeout, etc.), the redirect service catches the exception and falls back to querying MongoDB directly. The user still gets redirected — just without the caching benefit. This fail-open approach ensures that a Redis outage doesn't take down the entire redirect flow.

### Why Structured Error Responses Matter

API consumers (including the React frontend) rely on predictable error formats to display meaningful messages. A consistent shape — with status code, error type, and message — lets the frontend handle errors generically without special-casing each endpoint.

---

## 🔄 Frontend–Backend Integration

### JWT Token Handling

On login or registration, the backend returns a JWT token. The frontend stores this token in `localStorage` and attaches it to every subsequent API request via the `Authorization: Bearer <token>` header.

On page load, the frontend reads the token from `localStorage` and initializes the auth state synchronously — this prevents the "flash of logged-out state" that would occur if auth state were only fetched asynchronously.

### Axios Interceptor Usage

An Axios request interceptor automatically injects the JWT token into outgoing requests:

```typescript
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

This keeps token handling centralized — individual API calls don't need to manage headers manually.

### Handling 401 Responses

An Axios response interceptor watches for `401 Unauthorized` responses. When one is received, the interceptor clears the stored token and redirects the user to the login page. This handles token expiration gracefully without requiring the user to manually log out.

### Loading States During Async Operations

The frontend tracks loading state for API calls (URL shortening, fetching analytics, loading URL lists). Components render loading indicators while requests are in flight and transition to content or error states once the response arrives. This avoids blank screens and gives the user visual feedback.

### Analytics Visualization

The analytics page consumes click data from the `GET /api/analytics/{shortCode}` endpoint and renders it using Recharts. The backend provides raw click events with timestamps, IP addresses, user agents, and referrers. The frontend aggregates and visualizes this data — click trends over time, referrer breakdowns, etc.

### Separation of Concerns

All API interactions are isolated in a dedicated service layer (`api.ts`). React components call service functions and manage UI state — they don't construct URLs, set headers, or parse raw HTTP responses. This keeps components focused on rendering and makes the API layer independently testable.

---

## 🧪 Testing Strategy

### Manual API Testing

The primary testing approach uses cURL commands to exercise every endpoint. This covers the full request-response cycle including authentication, URL shortening, redirect following, and analytics retrieval. The README includes ready-to-use cURL examples for all endpoints.

### Edge Cases Tested

The following scenarios have been manually verified:
- **Expired URLs** — accessing a short code after its TTL returns a `404`
- **Unauthorized access** — requests without a valid JWT receive `401`
- **Invalid input** — malformed URLs, missing fields, and blocked protocols return `400` with descriptive messages
- **Analytics ownership** — attempting to view analytics for another user's URL returns `403`
- **Rate limiting** — exceeding the request threshold returns `429`
- **Redis unavailability** — redirects still work (via MongoDB fallback) when Redis is down

### Ideal Test Coverage

If unit and integration tests were to be added, they should cover:

- **Unit tests:**
  - `Base62Encoder` — encoding/decoding correctness, edge cases (zero, large numbers)
  - `UrlValidator` — valid URLs pass, blocked protocols/IPs are rejected
  - `JwtTokenProvider` — token generation, validation, expiration handling
  - Service layer methods — business logic in isolation with mocked repositories

- **Integration tests:**
  - Full redirect flow — shorten → redirect → verify analytics recorded
  - Auth flow — register → login → access protected endpoints
  - Rate limiting — verify that exceeding limits triggers `429` responses
  - Cache behavior — verify Redis is populated on first access, served on second

### Why Redirect and Rate Limiting Tests Matter

The redirect flow is the most critical path — if it breaks, every short URL is dead. Testing it end-to-end ensures that caching, fallback, analytics tracking, and expiration all work together correctly.

Rate limiting is easy to misconfigure. Without tests, it's possible to accidentally leave endpoints unprotected or set limits too aggressively. Automated tests can verify that the correct limits apply to both public and authenticated endpoints.

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
