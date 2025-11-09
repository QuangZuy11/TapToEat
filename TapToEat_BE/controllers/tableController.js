const { Table, OrderSession } = require('../models');

// UC-01: Kiểm tra bàn
const getTableByNumber = async (req, res) => {
    try {
        const { tableNumber } = req.params;

        const table = await Table.findOne({ tableNumber: parseInt(tableNumber) })
            .populate('currentSession');

        if (!table) {
            return res.status(404).json({
                success: false,
                message: `Không tìm thấy bàn số ${tableNumber}`
            });
        }

        res.status(200).json({
            success: true,
            data: table
        });
    } catch (error) {
        console.error('Error getting table:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi kiểm tra bàn',
            error: error.message
        });
    }
};

// UC-01: Tạo hoặc lấy session cho bàn
const createOrGetSession = async (req, res) => {
    try {
        const { tableNumber } = req.body;

        if (!tableNumber) {
            return res.status(400).json({
                success: false,
                message: 'Vui lòng nhập số bàn'
            });
        }

        // Kiểm tra bàn có tồn tại không
        const table = await Table.findOne({ tableNumber });

        if (!table) {
            return res.status(404).json({
                success: false,
                message: `Không tìm thấy bàn số ${tableNumber}`
            });
        }

        // Kiểm tra xem bàn có session active không
        let session = await OrderSession.findActiveByTable(tableNumber);

        if (session) {
            // Đã có session active
            return res.status(200).json({
                success: true,
                message: 'Lấy session hiện tại thành công',
                data: session,
                isNew: false
            });
        }

        // Tạo session mới
        const sessionCode = OrderSession.generateSessionCode(tableNumber);

        session = await OrderSession.create({
            tableNumber,
            sessionCode,
            status: 'active',
            startTime: new Date(),
            totalAmount: 0
        });

        // Cập nhật bàn
        await table.occupy(session._id);

        res.status(201).json({
            success: true,
            message: 'Tạo session mới thành công',
            data: session,
            isNew: true
        });
    } catch (error) {
        console.error('Error creating/getting session:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi tạo/lấy session',
            error: error.message
        });
    }
};

// Lấy chi tiết session
const getSessionById = async (req, res) => {
    try {
        const { sessionId } = req.params;

        const session = await OrderSession.findById(sessionId);

        if (!session) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy session'
            });
        }

        res.status(200).json({
            success: true,
            data: session
        });
    } catch (error) {
        console.error('Error getting session:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy thông tin session',
            error: error.message
        });
    }
};

// Lấy tất cả bàn
const getAllTables = async (req, res) => {
    try {
        const { status } = req.query;

        let query = {};
        if (status) {
            query.status = status;
        }

        const tables = await Table.find(query)
            .populate('currentSession')
            .sort({ tableNumber: 1 });

        res.status(200).json({
            success: true,
            data: tables
        });
    } catch (error) {
        console.error('Error getting tables:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh sách bàn',
            error: error.message
        });
    }
};

module.exports = {
    getTableByNumber,
    createOrGetSession,
    getSessionById,
    getAllTables
};