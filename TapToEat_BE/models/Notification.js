const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema({
    type: {
        type: String,
        enum: ['new_order', 'order_ready', 'order_cancelled', 'order_served'],
        required: true
    },
    orderId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Order',
        required: true
    },
    tableNumber: {
        type: Number,
        required: true,
        min: 1
    },
    message: {
        type: String,
        required: true,
        trim: true,
        maxlength: 500
    },
    isRead: {
        type: Boolean,
        default: false
    },
    targetRole: {
        type: String,
        enum: ['chef', 'admin', 'all'],
        default: 'chef'
    }
}, {
    timestamps: true
});

// Index để tìm kiếm nhanh
notificationSchema.index({ orderId: 1 });
notificationSchema.index({ tableNumber: 1 });
notificationSchema.index({ type: 1 });
notificationSchema.index({ isRead: 1 });
notificationSchema.index({ targetRole: 1 });
notificationSchema.index({ createdAt: -1 });

// Virtual để lấy thông tin order
notificationSchema.virtual('order', {
    ref: 'Order',
    localField: 'orderId',
    foreignField: '_id',
    justOne: true
});

// Method để đánh dấu đã đọc
notificationSchema.methods.markAsRead = function () {
    this.isRead = true;
    return this.save();
};

// Static method để tạo notification cho order mới
notificationSchema.statics.createNewOrderNotification = function (order) {
    return this.create({
        type: 'new_order',
        orderId: order._id,
        tableNumber: order.tableNumber,
        message: `Đơn hàng mới từ bàn ${order.tableNumber} - ${order.orderNumber}`,
        targetRole: 'chef'
    });
};

// Static method để tạo notification cho order ready
notificationSchema.statics.createOrderReadyNotification = function (order) {
    return this.create({
        type: 'order_ready',
        orderId: order._id,
        tableNumber: order.tableNumber,
        message: `Đơn hàng ${order.orderNumber} đã sẵn sàng - Bàn ${order.tableNumber}`,
        targetRole: 'all'
    });
};

// Static method để tạo notification cho order cancelled
notificationSchema.statics.createOrderCancelledNotification = function (order) {
    return this.create({
        type: 'order_cancelled',
        orderId: order._id,
        tableNumber: order.tableNumber,
        message: `Đơn hàng ${order.orderNumber} đã bị hủy - Bàn ${order.tableNumber}`,
        targetRole: 'chef'
    });
};

// Static method để tạo notification cho order served
notificationSchema.statics.createOrderServedNotification = function (order) {
    return this.create({
        type: 'order_served',
        orderId: order._id,
        tableNumber: order.tableNumber,
        message: `Đơn hàng ${order.orderNumber} đã được phục vụ - Bàn ${order.tableNumber}`,
        targetRole: 'all'
    });
};

// Static method để lấy notifications chưa đọc
notificationSchema.statics.getUnreadNotifications = function (targetRole = 'all') {
    const query = { isRead: false };
    if (targetRole !== 'all') {
        query.targetRole = { $in: [targetRole, 'all'] };
    }

    return this.find(query)
        .populate('order')
        .sort({ createdAt: -1 });
};

// Static method để lấy notifications theo role
notificationSchema.statics.getNotificationsByRole = function (targetRole, limit = 50) {
    const query = {};
    if (targetRole !== 'all') {
        query.targetRole = { $in: [targetRole, 'all'] };
    }

    return this.find(query)
        .populate('order')
        .sort({ createdAt: -1 })
        .limit(limit);
};

// Static method để đánh dấu tất cả notifications đã đọc
notificationSchema.statics.markAllAsRead = function (targetRole = 'all') {
    const query = { isRead: false };
    if (targetRole !== 'all') {
        query.targetRole = { $in: [targetRole, 'all'] };
    }

    return this.updateMany(query, { isRead: true });
};

// Static method để xóa notifications cũ (older than 7 days)
notificationSchema.statics.cleanupOldNotifications = function () {
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

    return this.deleteMany({
        createdAt: { $lt: sevenDaysAgo },
        isRead: true
    });
};

module.exports = mongoose.model('Notification', notificationSchema);