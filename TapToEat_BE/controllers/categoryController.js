const { Category, MenuItem } = require('../models');

// UC-02: Lấy danh sách categories
const getAllCategories = async (req, res) => {
    try {
        const categories = await Category.find({ isActive: true })
            .sort({ displayOrder: 1 })
            .select('-__v');

        res.status(200).json({
            success: true,
            data: categories
        });
    } catch (error) {
        console.error('Error getting categories:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh mục',
            error: error.message
        });
    }
};

// UC-02: Lấy chi tiết category
const getCategoryById = async (req, res) => {
    try {
        const { id } = req.params;

        const category = await Category.findById(id);

        if (!category) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy danh mục'
            });
        }

        res.status(200).json({
            success: true,
            data: category
        });
    } catch (error) {
        console.error('Error getting category:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy thông tin danh mục',
            error: error.message
        });
    }
};

// UC-02: Lấy món theo category
const getMenuItemsByCategory = async (req, res) => {
    try {
        const { id } = req.params;

        const category = await Category.findById(id);

        if (!category) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy danh mục'
            });
        }

        const menuItems = await MenuItem.find({
            categoryId: id,
            isAvailable: true
        }).select('-__v');

        res.status(200).json({
            success: true,
            data: {
                category: category,
                items: menuItems,
                totalItems: menuItems.length
            }
        });
    } catch (error) {
        console.error('Error getting menu items by category:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh sách món',
            error: error.message
        });
    }
};

module.exports = {
    getAllCategories,
    getCategoryById,
    getMenuItemsByCategory
};