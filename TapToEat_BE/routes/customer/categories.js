const express = require('express');
const router = express.Router();
const categoryController = require('../../controllers/categoryController');

// UC-02: Lấy danh sách categories
router.get('/', categoryController.getAllCategories);

// UC-02: Lấy chi tiết category
router.get('/:id', categoryController.getCategoryById);

// UC-02: Lấy món theo category
router.get('/:id/items', categoryController.getMenuItemsByCategory);

module.exports = router;