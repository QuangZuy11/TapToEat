const express = require('express');
const router = express.Router();
const orderController = require('../../controllers/orderController');

// UC-04: Tạo order mới
router.post('/', orderController.createOrder);

// UC-05: Lấy orders của session
router.get('/session/:sessionId', orderController.getOrdersBySession);

// Lấy orders theo bàn
router.get('/table/:tableNumber', orderController.getOrdersByTable);

// Lấy chi tiết order
router.get('/:orderId', orderController.getOrderById);

module.exports = router;