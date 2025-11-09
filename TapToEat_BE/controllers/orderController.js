const { Order, OrderSession, MenuItem, Notification } = require('../models');

// UC-04: Tạo order mới (Đặt món)
const createOrder = async (req, res) => {
    try {
        const { sessionId, tableNumber, items } = req.body;

        // Validate input
        if (!sessionId || !tableNumber || !items || items.length === 0) {
            return res.status(400).json({
                success: false,
                message: 'Thiếu thông tin đơn hàng'
            });
        }

        // Kiểm tra session có tồn tại và active không
        const session = await OrderSession.findById(sessionId);

        if (!session) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy session'
            });
        }

        if (session.status !== 'active') {
            return res.status(400).json({
                success: false,
                message: 'Session không còn active'
            });
        }

        // Validate và lấy thông tin các món
        const orderItems = [];
        let totalAmount = 0;

        for (const item of items) {
            const menuItem = await MenuItem.findById(item.menuItemId);

            if (!menuItem) {
                return res.status(404).json({
                    success: false,
                    message: `Không tìm thấy món: ${item.menuItemId}`
                });
            }

            if (!menuItem.isAvailable) {
                return res.status(400).json({
                    success: false,
                    message: `Món "${menuItem.name}" hiện không có sẵn`
                });
            }

            const itemTotal = menuItem.price * item.quantity;
            totalAmount += itemTotal;

            orderItems.push({
                menuItemId: menuItem._id,
                menuItemName: menuItem.name,
                quantity: item.quantity,
                price: menuItem.price,
                note: item.note || '',
                status: 'pending',
                orderedAt: new Date()
            });
        }

        // Tạo order number
        const orderNumber = Order.generateOrderNumber();

        // Tạo order
        const order = await Order.create({
            sessionId,
            tableNumber,
            orderNumber,
            items: orderItems,
            totalAmount,
            status: 'pending'
        });

        // Cập nhật tổng tiền session
        await session.calculateTotal();

        // Tạo notification cho chef
        await Notification.createNewOrderNotification(order);

        // Populate thông tin để trả về
        await order.populate('sessionId');

        res.status(201).json({
            success: true,
            message: 'Đặt món thành công',
            data: order
        });
    } catch (error) {
        console.error('Error creating order:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi tạo đơn hàng',
            error: error.message
        });
    }
};

// UC-05: Lấy orders của session (Xem món đã gọi)
const getOrdersBySession = async (req, res) => {
    try {
        const { sessionId } = req.params;

        const orders = await Order.find({ sessionId })
            .populate('sessionId')
            .sort({ createdAt: -1 });

        res.status(200).json({
            success: true,
            data: orders,
            totalOrders: orders.length
        });
    } catch (error) {
        console.error('Error getting orders by session:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh sách đơn hàng',
            error: error.message
        });
    }
};

// Lấy chi tiết order
const getOrderById = async (req, res) => {
    try {
        const { orderId } = req.params;

        const order = await Order.findById(orderId)
            .populate('sessionId')
            .populate('items.menuItemId');

        if (!order) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy đơn hàng'
            });
        }

        res.status(200).json({
            success: true,
            data: order
        });
    } catch (error) {
        console.error('Error getting order:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy thông tin đơn hàng',
            error: error.message
        });
    }
};

// Lấy orders theo bàn
const getOrdersByTable = async (req, res) => {
    try {
        const { tableNumber } = req.params;

        const orders = await Order.find({ tableNumber: parseInt(tableNumber) })
            .populate('sessionId')
            .sort({ createdAt: -1 });

        res.status(200).json({
            success: true,
            data: orders,
            totalOrders: orders.length
        });
    } catch (error) {
        console.error('Error getting orders by table:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy đơn hàng của bàn',
            error: error.message
        });
    }
};

module.exports = {
    createOrder,
    getOrdersBySession,
    getOrderById,
    getOrdersByTable
};