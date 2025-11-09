const { Order, Notification, MenuItem } = require('../models');

// UC-06: Lấy danh sách orders cho chef
const getChefOrders = async (req, res) => {
    try {
        const { status } = req.query;

        let query = {};

        if (status) {
            // Có thể filter nhiều status: ?status=pending&status=preparing
            const statusArray = Array.isArray(status) ? status : [status];
            query.status = { $in: statusArray };
        } else {
            // Mặc định lấy pending và preparing
            query.status = { $in: ['pending', 'preparing'] };
        }

        const orders = await Order.find(query)
            .populate('sessionId')
            .sort({ createdAt: 1 }); // Cũ nhất lên trước

        // Tính thời gian chờ cho mỗi order
        const ordersWithWaitTime = orders.map(order => {
            const waitTime = Math.floor((Date.now() - order.createdAt) / (1000 * 60)); // phút
            return {
                ...order.toObject(),
                waitTime,
                isUrgent: waitTime > 30 // Đánh dấu nếu chờ quá 30 phút
            };
        });

        // Thống kê
        const stats = {
            total: orders.length,
            pending: orders.filter(o => o.status === 'pending').length,
            preparing: orders.filter(o => o.status === 'preparing').length,
            ready: orders.filter(o => o.status === 'ready').length
        };

        res.status(200).json({
            success: true,
            data: ordersWithWaitTime,
            stats
        });
    } catch (error) {
        console.error('Error getting chef orders:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh sách đơn hàng',
            error: error.message
        });
    }
};

// UC-07: Bắt đầu làm món
const startPreparingItem = async (req, res) => {
    try {
        const { orderId, itemIndex } = req.params;

        const order = await Order.findById(orderId);

        if (!order) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy đơn hàng'
            });
        }

        const itemIdx = parseInt(itemIndex);

        if (itemIdx < 0 || itemIdx >= order.items.length) {
            return res.status(400).json({
                success: false,
                message: 'Index món không hợp lệ'
            });
        }

        const item = order.items[itemIdx];

        if (item.status !== 'pending') {
            return res.status(400).json({
                success: false,
                message: `Món đang ở trạng thái "${item.status}", không thể bắt đầu làm`
            });
        }

        // Cập nhật status
        item.status = 'preparing';
        item.preparedAt = new Date();

        // Cập nhật status của order
        await order.updateOrderStatus();

        res.status(200).json({
            success: true,
            message: `Đã bắt đầu làm món "${item.menuItemName}"`,
            data: order
        });
    } catch (error) {
        console.error('Error starting item preparation:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi bắt đầu làm món',
            error: error.message
        });
    }
};

// UC-08: Hoàn thành món
const completeItem = async (req, res) => {
    try {
        const { orderId, itemIndex } = req.params;

        const order = await Order.findById(orderId).populate('sessionId');

        if (!order) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy đơn hàng'
            });
        }

        const itemIdx = parseInt(itemIndex);

        if (itemIdx < 0 || itemIdx >= order.items.length) {
            return res.status(400).json({
                success: false,
                message: 'Index món không hợp lệ'
            });
        }

        const item = order.items[itemIdx];

        if (item.status !== 'preparing') {
            return res.status(400).json({
                success: false,
                message: `Món đang ở trạng thái "${item.status}", không thể hoàn thành`
            });
        }

        // Cập nhật status
        item.status = 'ready';
        item.readyAt = new Date();

        // Cập nhật status của order
        await order.updateOrderStatus();

        // Tạo notification cho khách
        if (order.status === 'ready') {
            await Notification.createOrderReadyNotification(order);
        }

        res.status(200).json({
            success: true,
            message: `Món "${item.menuItemName}" đã sẵn sàng`,
            data: order
        });
    } catch (error) {
        console.error('Error completing item:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi hoàn thành món',
            error: error.message
        });
    }
};

// Bắt đầu làm toàn bộ order
const startPreparingOrder = async (req, res) => {
    try {
        const { orderId } = req.params;

        const order = await Order.findById(orderId);

        if (!order) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy đơn hàng'
            });
        }

        // Cập nhật tất cả items pending thành preparing
        let updatedCount = 0;
        const now = new Date();

        order.items.forEach(item => {
            if (item.status === 'pending') {
                item.status = 'preparing';
                item.preparedAt = now;
                updatedCount++;
            }
        });

        if (updatedCount === 0) {
            return res.status(400).json({
                success: false,
                message: 'Không có món nào cần bắt đầu làm'
            });
        }

        order.status = 'preparing';
        await order.save();

        res.status(200).json({
            success: true,
            message: `Đã bắt đầu làm ${updatedCount} món`,
            data: order
        });
    } catch (error) {
        console.error('Error starting order preparation:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi bắt đầu làm đơn hàng',
            error: error.message
        });
    }
};

// Dashboard thống kê cho chef
const getChefDashboard = async (req, res) => {
    try {
        // Đếm orders theo status
        const pending = await Order.countDocuments({ status: 'pending' });
        const preparing = await Order.countDocuments({ status: 'preparing' });
        const ready = await Order.countDocuments({ status: 'ready' });

        // Lấy orders đang chờ lâu nhất
        const urgentOrders = await Order.find({
            status: { $in: ['pending', 'preparing'] },
            createdAt: { $lt: new Date(Date.now() - 30 * 60 * 1000) } // > 30 phút
        })
            .populate('sessionId')
            .sort({ createdAt: 1 })
            .limit(10);

        // Thống kê hôm nay
        const startOfDay = new Date();
        startOfDay.setHours(0, 0, 0, 0);

        const todayOrders = await Order.countDocuments({
            createdAt: { $gte: startOfDay }
        });

        const completedToday = await Order.countDocuments({
            status: 'served',
            updatedAt: { $gte: startOfDay }
        });

        res.status(200).json({
            success: true,
            data: {
                currentOrders: {
                    pending,
                    preparing,
                    ready,
                    total: pending + preparing + ready
                },
                urgentOrders: urgentOrders.map(order => ({
                    ...order.toObject(),
                    waitTime: Math.floor((Date.now() - order.createdAt) / (1000 * 60))
                })),
                todayStats: {
                    totalOrders: todayOrders,
                    completed: completedToday
                }
            }
        });
    } catch (error) {
        console.error('Error getting chef dashboard:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy thống kê',
            error: error.message
        });
    }
};

module.exports = {
    getChefOrders,
    startPreparingItem,
    completeItem,
    startPreparingOrder,
    getChefDashboard
};