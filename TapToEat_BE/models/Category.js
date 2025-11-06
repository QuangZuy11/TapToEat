const mongoose = require('mongoose');

const categorySchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
        trim: true,
        maxlength: 100
    },
    description: {
        type: String,
        trim: true,
        maxlength: 500
    },
    imageUrl: {
        type: String,
        trim: true
    },
    displayOrder: {
        type: Number,
        default: 0
    },
    isActive: {
        type: Boolean,
        default: true
    }
}, {
    timestamps: true
});

// Index để sắp xếp và tìm kiếm
categorySchema.index({ displayOrder: 1 });
categorySchema.index({ isActive: 1 });
categorySchema.index({ name: 'text' });

// Virtual để đếm số món trong danh mục
categorySchema.virtual('menuItemCount', {
    ref: 'MenuItem',
    localField: '_id',
    foreignField: 'categoryId',
    count: true
});

// Method để lấy các món trong danh mục
categorySchema.methods.getMenuItems = function () {
    return mongoose.model('MenuItem').find({
        categoryId: this._id,
        isAvailable: true
    });
};

// Static method để lấy danh mục active và sắp xếp
categorySchema.statics.getActiveCategories = function () {
    return this.find({ isActive: true }).sort({ displayOrder: 1 });
};

module.exports = mongoose.model('Category', categorySchema);