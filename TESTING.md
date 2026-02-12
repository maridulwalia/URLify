# URLify - Quick Testing Guide

## ✅ Application is Running!

The application is now running on **http://localhost:8080**

---

## 🧪 Test the API

### 1. **Check Application Status**
```bash
curl http://localhost:8080/
```

Expected response:
```json
{
  "application": "URLify - URL Shortening Platform",
  "version": "1.0.0",
  "status": "running",
  "endpoints": {
    "register": "POST /api/auth/register",
    "login": "POST /api/auth/login",
    "shorten": "POST /api/urls/shorten (requires auth)",
    "redirect": "GET /{shortCode}",
    "analytics": "GET /api/analytics/{shortCode} (requires auth)"
  }
}
```

### 2. **Health Check**
```bash
curl http://localhost:8080/health
```

### 3. **Register a User**
```bash
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"password123\"}"
```

**Save the token from the response!**

### 4. **Login**
```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"password123\"}"
```

### 5. **Create Short URL** (Replace YOUR_TOKEN)
```bash
curl -X POST http://localhost:8080/api/urls/shorten ^
  -H "Authorization: Bearer YOUR_TOKEN" ^
  -H "Content-Type: application/json" ^
  -d "{\"url\":\"https://www.google.com\"}"
```

### 6. **Test Redirect** (Replace abc123 with your short code)
Open in browser: `http://localhost:8080/abc123`

Or use curl:
```bash
curl -L http://localhost:8080/abc123
```

### 7. **Get My URLs**
```bash
curl http://localhost:8080/api/urls/my-urls ^
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 8. **Get Analytics** (Replace abc123)
```bash
curl http://localhost:8080/api/analytics/abc123 ^
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🎯 Test in Browser

1. Open: http://localhost:8080/
2. You should see the API information
3. Use Postman or any API client to test the endpoints

---

## 📊 What's Working

✅ **MongoDB** - Embedded, no Docker needed  
✅ **Redis** - Optional, uses simple cache if not available  
✅ **JWT Authentication** - Secure token-based auth  
✅ **Rate Limiting** - 10 req/min public, 100 req/min authenticated  
✅ **URL Shortening** - Base62 encoding  
✅ **Analytics** - Click tracking with IP, user agent, referer  
✅ **Java 21** - Latest LTS version  

---

## 🚀 Next Steps

- Test all endpoints above
- Try creating custom aliases
- Test URL expiration
- Check rate limiting by making 11 quick requests
- View analytics after clicking short URLs

Enjoy your URL shortening platform! 🎉
