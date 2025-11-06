const mongoose = require('mongoose');
const bcrypt = require('bcrypt');

const chefSchema = new mongoose.Schema({
    username: {
        type: String,
        required: true,
        unique: true,
        trim: true,
        minlength: 3,
        maxlength: 50
    },
    password: {
        type: String,
        required: true,
        minlength: 6
    },
    name: {
        type: String,
        required: true,
        trim: true,
        maxlength: 100
    },
    role: {
        type: String,
        enum: ['chef', 'admin'],
        default: 'chef'
    },
    isActive: {
        type: Boolean,
        default: true
    }
}, {
    timestamps: true
});

// Index để tìm kiếm
chefSchema.index({ username: 1 });
chefSchema.index({ role: 1 });
chefSchema.index({ isActive: 1 });

// Hash password trước khi save
chefSchema.pre('save', async function (next) {
    // Chỉ hash password nếu nó đã được modify
    if (!this.isModified('password')) {
        return next();
    }

    try {
        // Hash password với salt rounds = 12
        const salt = await bcrypt.genSalt(12);
        this.password = await bcrypt.hash(this.password, salt);
        next();
    } catch (error) {
        next(error);
    }
});

// Method để so sánh password
chefSchema.methods.comparePassword = async function (candidatePassword) {
    try {
        return await bcrypt.compare(candidatePassword, this.password);
    } catch (error) {
        throw error;
    }
};

// Method để kiểm tra chef có active không
chefSchema.methods.isAccountActive = function () {
    return this.isActive;
};

// Method để vô hiệu hóa account
chefSchema.methods.deactivate = function () {
    this.isActive = false;
    return this.save();
};

// Method để kích hoạt account
chefSchema.methods.activate = function () {
    this.isActive = true;
    return this.save();
};

// Static method để tìm chef active
chefSchema.statics.findActiveChefs = function () {
    return this.find({
        isActive: true,
        role: 'chef'
    }).select('-password');
};

// Static method để tìm theo username (để login)
chefSchema.statics.findByUsername = function (username) {
    return this.findOne({
        username: username,
        isActive: true
    });
};

// Không trả về password khi convert to JSON
chefSchema.methods.toJSON = function () {
    const chef = this.toObject();
    delete chef.password;
    return chef;
};

module.exports = mongoose.model('Chef', chefSchema);