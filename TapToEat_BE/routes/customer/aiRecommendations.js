const express = require('express');
const router = express.Router();
const aiRecommendationController = require('../../controllers/aiRecommendationController');

// Get AI weather-based recommendations
router.get('/weather', aiRecommendationController.getWeatherRecommendations);

console.log('✓ AI Recommendation routes registered');

module.exports = router;
