const axios = require('axios');
const { GoogleGenerativeAI } = require('@google/generative-ai');
const { MenuItem } = require('../models');

// Initialize Gemini AI
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

/**
 * Get weather-based AI recommendations
 */
const getWeatherRecommendations = async (req, res) => {
    try {
        console.log('=== AI Recommendation Request ===');

        // Step 1: Get weather data
        const weatherData = await getWeatherData();
        console.log('Weather data:', weatherData);

        // Step 2: Get available menu items
        const menuItems = await MenuItem.find({
            isAvailable: true
        }).populate('category');

        if (!menuItems || menuItems.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Không có món ăn khả dụng'
            });
        }

        console.log(`Found ${menuItems.length} available menu items`);

        // Step 3: Build prompt for Gemini
        const prompt = buildPrompt(weatherData, menuItems);
        console.log('Prompt length:', prompt.length);

        // Step 4: Call Gemini AI
        const aiRecommendations = await getGeminiRecommendations(prompt, menuItems);
        console.log(`AI returned ${aiRecommendations.length} recommendations`);

        // Step 5: Return response
        res.status(200).json({
            success: true,
            data: {
                weather: {
                    temperature: weatherData.temperature,
                    humidity: weatherData.humidity,
                    condition: weatherData.condition,
                    description: weatherData.description,
                    icon: weatherData.icon,
                    feelsLike: weatherData.feelsLike
                },
                recommendations: aiRecommendations,
                timestamp: new Date()
            }
        });

    } catch (error) {
        console.error('Error getting AI recommendations:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy gợi ý AI',
            error: error.message
        });
    }
};

/**
 * Get weather data from OpenWeatherMap API
 */
async function getWeatherData() {
    try {
        const apiKey = process.env.WEATHER_API_KEY;
        const lat = process.env.RESTAURANT_LAT;
        const lon = process.env.RESTAURANT_LON;

        if (!apiKey) {
            throw new Error('Weather API key not configured');
        }

        const url = `${process.env.WEATHER_API_URL}?lat=${lat}&lon=${lon}&appid=${apiKey}&units=metric&lang=vi`;

        const response = await axios.get(url);
        const data = response.data;

        return {
            temperature: Math.round(data.main.temp),
            feelsLike: Math.round(data.main.feels_like),
            humidity: data.main.humidity,
            condition: data.weather[0].main,
            description: data.weather[0].description,
            icon: getWeatherIcon(data.weather[0].main),
            windSpeed: data.wind.speed,
            time: new Date()
        };
    } catch (error) {
        console.error('Error fetching weather:', error.message);
        // Return fallback weather data
        return {
            temperature: 28,
            feelsLike: 30,
            humidity: 70,
            condition: 'Clear',
            description: 'Trời nắng',
            icon: '☀️',
            windSpeed: 2,
            time: new Date()
        };
    }
}

/**
 * Get weather icon emoji
 */
function getWeatherIcon(condition) {
    const icons = {
        'Clear': '☀️',
        'Clouds': '☁️',
        'Rain': '🌧️',
        'Drizzle': '🌦️',
        'Thunderstorm': '⛈️',
        'Snow': '❄️',
        'Mist': '🌫️',
        'Fog': '🌫️',
        'Haze': '🌫️'
    };
    return icons[condition] || '🌤️';
}

/**
 * Build prompt for Gemini AI
 */
function buildPrompt(weather, menuItems) {
    const currentHour = new Date().getHours();
    let timeOfDay = 'sáng';
    if (currentHour >= 11 && currentHour < 14) timeOfDay = 'trưa';
    else if (currentHour >= 14 && currentHour < 18) timeOfDay = 'chiều';
    else if (currentHour >= 18) timeOfDay = 'tối';

    const menuItemsList = menuItems.map((item, index) => {
        const tags = [];
        const name = item.name.toLowerCase();
        const desc = (item.description || '').toLowerCase();

        // Auto-tag based on name and description
        if (name.includes('phở') || name.includes('bún') || name.includes('hủ tiếu')) tags.push('nóng', 'nước');
        if (name.includes('gỏi') || name.includes('salad')) tags.push('mát', 'tươi', 'lạnh');
        if (name.includes('nướng') || name.includes('chiên')) tags.push('nóng', 'nặng');
        if (name.includes('chè') || name.includes('kem')) tags.push('tráng miệng', 'lạnh', 'ngọt');
        if (name.includes('trà') || name.includes('nước')) tags.push('đồ uống', 'lạnh');
        if (name.includes('cơm')) tags.push('no', 'chính');
        if (desc.includes('mát') || desc.includes('tươi')) tags.push('mát', 'nhẹ');

        return `${index + 1}. ${item.name} - ${item.description || 'Món ăn ngon'} - ${item.price.toLocaleString()}đ - [${tags.join(', ')}]`;
    }).join('\n');

    return `Bạn là chuyên gia ẩm thực Việt Nam. Dựa vào thời tiết và thời gian, hãy gợi ý 5 món ăn phù hợp nhất.

Bối cảnh thời tiết hiện tại:
- Nhiệt độ: ${weather.temperature}°C (cảm giác như ${weather.feelsLike}°C)
- Độ ẩm: ${weather.humidity}%
- Thời tiết: ${weather.description}
- Thời gian: ${timeOfDay} (${new Date().getHours()}:${String(new Date().getMinutes()).padStart(2, '0')})

Danh sách món ăn có sẵn tại nhà hàng:
${menuItemsList}

Yêu cầu:
1. Gợi ý ĐÚNG 5 món từ danh sách trên (theo số thứ tự từ 1-${menuItems.length})
2. Sắp xếp theo độ phù hợp (món phù hợp nhất đầu tiên)
3. Giải thích ngắn gọn (1-2 câu) tại sao món đó phù hợp
4. Đánh giá độ phù hợp từ 0-100

Trả về JSON format SAU ĐÂY (KHÔNG thêm markdown, code block hay text khác):
{
  "summary": "Tóm tắt thời tiết và xu hướng ăn uống phù hợp (1-2 câu)",
  "recommendations": [
    {
      "itemIndex": 1,
      "reason": "Lý do gợi ý",
      "matchScore": 95
    }
  ]
}`;
}

/**
 * Call Gemini AI and parse response
 */
async function getGeminiRecommendations(prompt, menuItems) {
    try {
        const model = genAI.getGenerativeModel({ model: "gemini-pro" });

        const result = await model.generateContent(prompt);
        const response = await result.response;
        const text = response.text();

        console.log('Raw Gemini response:', text);

        // Parse JSON response
        let jsonText = text.trim();

        // Remove markdown code blocks if present
        if (jsonText.startsWith('```json')) {
            jsonText = jsonText.replace(/```json\n?/g, '').replace(/```\n?/g, '');
        } else if (jsonText.startsWith('```')) {
            jsonText = jsonText.replace(/```\n?/g, '');
        }

        const aiResponse = JSON.parse(jsonText);

        // Map recommendations with actual menu items
        const recommendations = aiResponse.recommendations
            .filter(rec => rec.itemIndex > 0 && rec.itemIndex <= menuItems.length)
            .map(rec => {
                const menuItem = menuItems[rec.itemIndex - 1];
                // convert mongoose doc to plain object
                const mi = menuItem && typeof menuItem.toObject === 'function' ? menuItem.toObject() : menuItem;
                return {
                    menuItem: {
                        _id: mi._id,
                        name: mi.name,
                        description: mi.description,
                        price: mi.price,
                        imageUrl: mi.imageUrl || mi.image || (mi.category && mi.category.imageUrl) || null,
                        category: mi.category
                    },
                    reason: rec.reason,
                    matchScore: rec.matchScore
                };
            });

        // Debug: log first mapped recommendation to verify imageUrl
        if (recommendations && recommendations.length > 0) {
            console.log('Mapped recommendation sample:', JSON.stringify(recommendations[0], null, 2));
        }

        return recommendations;

    } catch (error) {
        console.error('Error calling Gemini:', error);
        // Return fallback recommendations
        return menuItems.slice(0, 5).map((item, index) => ({
            menuItem: {
                _id: item._id,
                name: item.name,
                description: item.description,
                price: item.price,
                imageUrl: item.imageUrl || item.image,
                category: item.category
            },
            reason: 'Món ăn phổ biến được nhiều người yêu thích',
            matchScore: 90 - (index * 5)
        }));
    }
}

module.exports = {
    getWeatherRecommendations
};
