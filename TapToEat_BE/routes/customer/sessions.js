const express = require('express');
const router = express.Router();
const tableController = require('../../controllers/tableController');

// UC-01: Tạo/lấy session cho bàn
router.post('/', tableController.createOrGetSession);

// Lấy chi tiết session
router.get('/:sessionId', tableController.getSessionById);

// UC-10: Thanh toán và giải phóng session
router.post('/:sessionId/payment', tableController.completeSessionAndPayment);

module.exports = router;