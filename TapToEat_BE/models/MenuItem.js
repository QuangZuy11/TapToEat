const mongoose = require('mongoose');

const menuItemSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
        trim: true,
        maxlength: 100
    },
    description: {
        type: String,
        trim: true,
        maxlength: 1000
    },
    price: {
        type: Number,
        required: true,
        min: 0
    },
    imageUrl: {
        type: String,
        trim: true
    },
    categoryId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Category',
        required: true
    },
    isAvailable: {
        type: Boolean,
        default: true
    },
    preparationTime: {
        type: Number,
        default: 15, // Thời gian chuẩn bị mặc định 15 phút
        min: 1
    },
    tags: [{
        type: String,
        trim: true,
        lowercase: true
    }]
}, {
    timestamps: true
});

// Index để tìm kiếm và filter
menuItemSchema.index({ categoryId: 1 });
menuItemSchema.index({ isAvailable: 1 });
menuItemSchema.index({ price: 1 });
menuItemSchema.index({ tags: 1 });
menuItemSchema.index({ name: 'text', description: 'text' });

// Virtual để lấy thông tin category
menuItemSchema.virtual('category', {
    ref: 'Category',
    localField: 'categoryId',
    foreignField: '_id',
    justOne: true
});

// Method để kiểm tra món có available không
menuItemSchema.methods.isInStock = function () {
    return this.isAvailable;
};

// Method để format giá tiền
menuItemSchema.methods.getFormattedPrice = function () {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(this.price);
};

// Static method để tìm món theo category
menuItemSchema.statics.findByCategory = function (categoryId) {
    return this.find({
        categoryId: categoryId,
        isAvailable: true
    }).populate('category');
};

// Static method để tìm món theo tags
menuItemSchema.statics.findByTags = function (tags) {
    return this.find({
        tags: { $in: tags },
        isAvailable: true
    }).populate('category');
};

// Static method để search món
menuItemSchema.statics.searchItems = function (query) {
    return this.find({
        $and: [
            { isAvailable: true },
            {
                $or: [
                    { name: { $regex: query, $options: 'i' } },
                    { description: { $regex: query, $options: 'i' } },
                    { tags: { $in: [new RegExp(query, 'i')] } }
                ]
            }
        ]
    }).populate('category');
};

module.exports = mongoose.model('MenuItem', menuItemSchema);