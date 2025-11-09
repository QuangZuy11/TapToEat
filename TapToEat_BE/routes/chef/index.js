const express = require('express');
const router = express.Router();
const chefController = require('../../controllers/chefController');
const menuItemController = require('../../controllers/menuItemController');

// UC-06: Lấy danh sách orders
router.get('/orders', chefController.getChefOrders);

// UC-08: Dashboard thống kê
router.get('/dashboard', chefController.getChefDashboard);

// UC-07: Bắt đầu làm món
router.patch('/orders/:orderId/items/:itemIndex/start', chefController.startPreparingItem);

// UC-08: Hoàn thành món
router.patch('/orders/:orderId/items/:itemIndex/complete', chefController.completeItem);

// Bắt đầu làm toàn bộ order
router.patch('/orders/:orderId/start', chefController.startPreparingOrder);

// UC-09: Cập nhật trạng thái món
router.patch('/menu-items/:id/availability', menuItemController.updateMenuItemAvailability);

module.exports = router;