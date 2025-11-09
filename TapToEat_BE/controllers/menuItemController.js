const { MenuItem, Category } = require('../models');

// UC-02: Lấy tất cả món (có filter)
const getAllMenuItems = async (req, res) => {
    try {
        const { categoryId, tags, search, isAvailable = true } = req.query;

        // Build query
        let query = { isAvailable };

        if (categoryId) {
            query.categoryId = categoryId;
        }

        if (tags) {
            const tagArray = tags.split(',');
            query.tags = { $in: tagArray };
        }

        if (search) {
            query.$or = [
                { name: { $regex: search, $options: 'i' } },
                { description: { $regex: search, $options: 'i' } }
            ];
        }

        const menuItems = await MenuItem.find(query)
            .populate('categoryId', 'name displayOrder')
            .sort({ name: 1 })
            .select('-__v');

        res.status(200).json({
            success: true,
            data: menuItems,
            totalItems: menuItems.length
        });
    } catch (error) {
        console.error('Error getting menu items:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh sách món',
            error: error.message
        });
    }
};

// UC-02: Lấy chi tiết món
const getMenuItemById = async (req, res) => {
    try {
        const { id } = req.params;

        const menuItem = await MenuItem.findById(id)
            .populate('categoryId', 'name description');

        if (!menuItem) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy món'
            });
        }

        res.status(200).json({
            success: true,
            data: menuItem
        });
    } catch (error) {
        console.error('Error getting menu item:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy thông tin món',
            error: error.message
        });
    }
};

// UC-09: Cập nhật trạng thái món (Chef/Admin)
const updateMenuItemAvailability = async (req, res) => {
    try {
        const { id } = req.params;
        const { isAvailable } = req.body;

        if (typeof isAvailable !== 'boolean') {
            return res.status(400).json({
                success: false,
                message: 'isAvailable phải là boolean'
            });
        }

        const menuItem = await MenuItem.findByIdAndUpdate(
            id,
            { isAvailable },
            { new: true }
        );

        if (!menuItem) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy món'
            });
        }

        res.status(200).json({
            success: true,
            message: `Món đã được ${isAvailable ? 'bật' : 'tắt'}`,
            data: menuItem
        });
    } catch (error) {
        console.error('Error updating menu item availability:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi cập nhật trạng thái món',
            error: error.message
        });
    }
};

module.exports = {
    getAllMenuItems,
    getMenuItemById,
    updateMenuItemAvailability
};