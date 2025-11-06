const express = require('express');
const router = express.Router();

// Example route
router.get('/', (req, res) => {
    res.json({ message: 'API is working!' });
});

// Add more routes here as needed
// router.get('/users', userController.getAllUsers);
// router.post('/users', userController.createUser);

module.exports = router;