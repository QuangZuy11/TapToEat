const mongoose = require('mongoose');

const orderItemSchema = new mongoose.Schema({
    menuItemId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'MenuItem',
        required: true
    },
    menuItemName: {
        type: String,
        required: true,
        trim: true
    },
    quantity: {
        type: Number,
        required: true,
        min: 1
    },
    price: {
        type: Number,
        required: true,
        min: 0
    },
    note: {
        type: String,
        trim: true,
        maxlength: 200
    },
    status: {
        type: String,
        enum: ['pending', 'preparing', 'ready', 'served'],
        default: 'pending'
    },
    orderedAt: {
        type: Date,
        default: Date.now
    },
    preparedAt: {
        type: Date,
        default: null
    },
    readyAt: {
        type: Date,
        default: null
    },
    servedAt: {
        type: Date,
        default: null
    }
});

const orderSchema = new mongoose.Schema({
    sessionId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'OrderSession',
        required: true
    },
    tableNumber: {
        type: Number,
        required: true,
        min: 1
    },
    orderNumber: {
        type: String,
        required: true,
        unique: true
    },
    items: [orderItemSchema],
    totalAmount: {
        type: Number,
        required: true,
        min: 0
    },
    status: {
        type: String,
        enum: ['pending', 'preparing', 'ready', 'served', 'cancelled'],
        default: 'pending'
    }
}, {
    timestamps: true
});

// Index để tìm kiếm nhanh
orderSchema.index({ sessionId: 1 });
orderSchema.index({ tableNumber: 1 });
orderSchema.index({ orderNumber: 1 });
orderSchema.index({ status: 1 });
orderSchema.index({ createdAt: -1 });
orderSchema.index({ 'items.status': 1 });

// Virtual để lấy thông tin session
orderSchema.virtual('session', {
    ref: 'OrderSession',
    localField: 'sessionId',
    foreignField: '_id',
    justOne: true
});

// Method để tạo order number
orderSchema.statics.generateOrderNumber = function () {
    const now = new Date();
    const dateStr = now.toISOString().slice(0, 10).replace(/-/g, '');
    const timeStr = now.getTime().toString().slice(-6);
    return `ORD-${dateStr}-${timeStr}`;
};

// Method để tính tổng tiền order
orderSchema.methods.calculateTotal = function () {
    this.totalAmount = this.items.reduce((total, item) => {
        return total + (item.price * item.quantity);
    }, 0);
    return this;
};

// Method để update status của toàn bộ order dựa trên items
orderSchema.methods.updateOrderStatus = function () {
    const itemStatuses = this.items.map(item => item.status);

    if (itemStatuses.every(status => status === 'served')) {
        this.status = 'served';
    } else if (itemStatuses.every(status => status === 'ready' || status === 'served')) {
        this.status = 'ready';
    } else if (itemStatuses.some(status => status === 'preparing')) {
        this.status = 'preparing';
    } else {
        this.status = 'pending';
    }

    return this.save();
};

// Method để update status của một item
orderSchema.methods.updateItemStatus = function (itemId, newStatus) {
    const item = this.items.id(itemId);
    if (!item) {
        throw new Error('Item not found');
    }

    item.status = newStatus;

    // Cập nhật timestamp tương ứng
    const now = new Date();
    switch (newStatus) {
        case 'preparing':
            item.preparedAt = now;
            break;
        case 'ready':
            item.readyAt = now;
            break;
        case 'served':
            item.servedAt = now;
            break;
    }

    // Cập nhật status của toàn bộ order
    return this.updateOrderStatus();
};

// Method để lấy tổng thời gian chuẩn bị dự kiến
orderSchema.methods.getEstimatedPrepTime = async function () {
    await this.populate('items.menuItemId');
    return this.items.reduce((total, item) => {
        const prepTime = item.menuItemId ? item.menuItemId.preparationTime : 15;
        return Math.max(total, prepTime); // Lấy thời gian lâu nhất
    }, 0);
};

// Static method để lấy orders của chef (pending, preparing)
orderSchema.statics.getChefOrders = function () {
    return this.find({
        status: { $in: ['pending', 'preparing'] }
    }).populate('sessionId').sort({ createdAt: 1 });
};

// Static method để lấy orders theo bàn
orderSchema.statics.getOrdersByTable = function (tableNumber) {
    return this.find({ tableNumber: tableNumber })
        .populate('sessionId')
        .sort({ createdAt: -1 });
};

// Static method để lấy orders theo session
orderSchema.statics.getOrdersBySession = function (sessionId) {
    return this.find({ sessionId: sessionId })
        .sort({ createdAt: -1 });
};

module.exports = mongoose.model('Order', orderSchema);