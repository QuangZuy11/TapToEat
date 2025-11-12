const express = require('express');
const router = express.Router();

// Import customer routes
const categoriesRoute = require('./customer/categories');
const menuItemsRoute = require('./customer/menuItems');
const tablesRoute = require('./customer/tables');
const sessionsRoute = require('./customer/sessions');
const ordersRoute = require('./customer/orders');
const aiRecommendationsRoute = require('./customer/aiRecommendations');

// Import chef routes
const chefRoute = require('./chef');

// API info
router.get('/', (req, res) => {
    res.json({
        message: 'TapToEat API is working!',
        version: '1.0.0',
        endpoints: {
            customer: {
                categories: '/api/categories',
                menuItems: '/api/menu-items',
                tables: '/api/tables',
                sessions: '/api/sessions',
                orders: '/api/orders',
                aiRecommendations: '/api/ai-recommendations'
            },
            chef: {
                orders: '/api/chef/orders',
                dashboard: '/api/chef/dashboard'
            }
        }
    });
});

// Customer routes
router.use('/categories', categoriesRoute);
router.use('/menu-items', menuItemsRoute);
router.use('/tables', tablesRoute);
router.use('/sessions', sessionsRoute);
router.use('/orders', ordersRoute);
router.use('/ai-recommendations', aiRecommendationsRoute);

// Chef routes
router.use('/chef', chefRoute);

module.exports = router;