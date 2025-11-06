const mongoose = require('mongoose');

const orderSessionSchema = new mongoose.Schema({
    tableNumber: {
        type: Number,
        required: true,
        min: 1
    },
    sessionCode: {
        type: String,
        required: true,
        unique: true
    },
    status: {
        type: String,
        enum: ['active', 'completed', 'cancelled'],
        default: 'active'
    },
    startTime: {
        type: Date,
        default: Date.now
    },
    endTime: {
        type: Date,
        default: null
    },
    totalAmount: {
        type: Number,
        default: 0,
        min: 0
    }
}, {
    timestamps: true
});

// Index để tìm kiếm nhanh
orderSessionSchema.index({ tableNumber: 1 });
orderSessionSchema.index({ sessionCode: 1 });
orderSessionSchema.index({ status: 1 });
orderSessionSchema.index({ startTime: -1 });

// Virtual để lấy danh sách orders trong session
orderSessionSchema.virtual('orders', {
    ref: 'Order',
    localField: '_id',
    foreignField: 'sessionId'
});

// Method để tạo session code
orderSessionSchema.statics.generateSessionCode = function (tableNumber) {
    const now = new Date();
    const dateStr = now.toISOString().slice(0, 10).replace(/-/g, '');
    const timeStr = now.getTime().toString().slice(-3);
    return `TB${tableNumber.toString().padStart(2, '0')}-${dateStr}-${timeStr}`;
};

// Method để tính tổng tiền từ các orders
orderSessionSchema.methods.calculateTotal = async function () {
    const Order = mongoose.model('Order');
    const orders = await Order.find({
        sessionId: this._id,
        status: { $ne: 'cancelled' }
    });

    this.totalAmount = orders.reduce((total, order) => total + order.totalAmount, 0);
    return this.save();
};

// Method để kết thúc session
orderSessionSchema.methods.complete = function () {
    this.status = 'completed';
    this.endTime = new Date();
    return this.save();
};

// Method để hủy session
orderSessionSchema.methods.cancel = function () {
    this.status = 'cancelled';
    this.endTime = new Date();
    return this.save();
};

// Method để kiểm tra session có active không
orderSessionSchema.methods.isActive = function () {
    return this.status === 'active';
};

// Method để lấy thời gian session (phút)
orderSessionSchema.methods.getDuration = function () {
    const endTime = this.endTime || new Date();
    return Math.round((endTime - this.startTime) / (1000 * 60));
};

// Static method để tìm session active của bàn
orderSessionSchema.statics.findActiveByTable = function (tableNumber) {
    return this.findOne({
        tableNumber: tableNumber,
        status: 'active'
    });
};

module.exports = mongoose.model('OrderSession', orderSessionSchema);