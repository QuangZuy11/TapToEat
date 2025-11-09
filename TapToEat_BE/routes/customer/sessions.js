const express = require('express');
const router = express.Router();
const tableController = require('../../controllers/tableController');

// UC-01: Tạo/lấy session cho bàn
router.post('/', tableController.createOrGetSession);

// Lấy chi tiết session
router.get('/:sessionId', tableController.getSessionById);

module.exports = router;