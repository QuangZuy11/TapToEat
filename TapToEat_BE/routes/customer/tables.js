const express = require('express');
const router = express.Router();
const tableController = require('../../controllers/tableController');

// Lấy tất cả bàn
router.get('/', tableController.getAllTables);

// UC-01: Kiểm tra bàn
router.get('/:tableNumber', tableController.getTableByNumber);

module.exports = router;