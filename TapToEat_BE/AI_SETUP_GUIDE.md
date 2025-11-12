# AI Recommendation Setup Guide

## 1. Get OpenWeatherMap API Key (FREE)

1. Go to https://openweathermap.org/api
2. Click "Sign Up" (or login if you have account)
3. Verify your email
4. Go to https://home.openweathermap.org/api_keys
5. Copy your API key
6. Add to `.env`:
   ```
   WEATHER_API_KEY=your_api_key_here
   ```

## 2. Get Google Gemini API Key (FREE)

1. Go to https://makersuite.google.com/app/apikey
2. Click "Create API Key"
3. Select or create a Google Cloud project
4. Copy the API key
5. Add to `.env`:
   ```
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

## 3. Test the API

```bash
# Start server
npm start

# Test endpoint (use Postman or curl)
GET http://localhost:9999/api/ai-recommendations/weather
```

## 4. Expected Response

```json
{
  "success": true,
  "data": {
    "weather": {
      "temperature": 28,
      "humidity": 70,
      "condition": "Clear",
      "description": "Trời nắng",
      "icon": "☀️",
      "feelsLike": 30
    },
    "recommendations": [
      {
        "menuItem": {
          "_id": "...",
          "name": "Gỏi Cuốn Tôm Thịt",
          "description": "...",
          "price": 35000,
          "image": "...",
          "category": {...}
        },
        "reason": "Món gỏi cuốn mát lạnh, tươi ngon, rất phù hợp với thời tiết nắng nóng",
        "matchScore": 95
      }
    ],
    "timestamp": "2025-11-12T..."
  }
}
```

## Notes

- OpenWeatherMap free tier: 60 calls/minute, 1,000,000 calls/month
- Gemini AI free tier: 60 requests/minute
- Both are MORE than enough for development and small production apps
