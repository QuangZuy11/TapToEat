const mongoose = require('mongoose');

const tableSchema = new mongoose.Schema({
    tableNumber: {
        type: Number,
        required: true,
        unique: true,
        min: 1
    },
    capacity: {
        type: Number,
        required: true,
        min: 1
    },
    status: {
        type: String,
        enum: ['available', 'occupied', 'reserved'],
        default: 'available'
    },
    currentSession: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'OrderSession',
        default: null
    }
}, {
    timestamps: true // Tự động tạo createdAt và updatedAt
});

// Index để tìm kiếm nhanh
tableSchema.index({ tableNumber: 1 });
tableSchema.index({ status: 1 });

// Virtual để lấy thông tin session hiện tại
tableSchema.virtual('session', {
    ref: 'OrderSession',
    localField: 'currentSession',
    foreignField: '_id',
    justOne: true
});

// Method để kiểm tra bàn có trống không
tableSchema.methods.isAvailable = function () {
    return this.status === 'available';
};

// Method để đặt bàn
tableSchema.methods.occupy = function (sessionId) {
    this.status = 'occupied';
    this.currentSession = sessionId;
    return this.save();
};

// Method để giải phóng bàn
tableSchema.methods.release = function () {
    this.status = 'available';
    this.currentSession = null;
    return this.save();
};

module.exports = mongoose.model('Table', tableSchema);