const express = require('express');
const router = express.Router();
const menuItemController = require('../../controllers/menuItemController');

// UC-02: Lấy tất cả món (có filter)
router.get('/', menuItemController.getAllMenuItems);

// UC-02: Lấy chi tiết món
router.get('/:id', menuItemController.getMenuItemById);

module.exports = router;