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

// UC-10: Thanh toán và giải phóng session
const completeSessionAndPayment = async (req, res) => {
    try {
        const { sessionId } = req.params;
        const { paymentMethod = 'cash' } = req.body;

        // Tìm session
        const session = await OrderSession.findById(sessionId);

        if (!session) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy session'
            });
        }

        if (session.status !== 'active') {
            return res.status(400).json({
                success: false,
                message: 'Session không còn active'
            });
        }

        // Tìm tất cả orders của session
        const Order = require('../models').Order;
        const orders = await Order.find({ sessionId: session._id });

        // Kiểm tra tất cả món đã sẵn sàng chưa
        for (const order of orders) {
            for (const item of order.items) {
                if (item.status !== 'ready') {
                    return res.status(400).json({
                        success: false,
                        message: 'Vẫn còn món chưa sẵn sàng. Không thể thanh toán!'
                    });
                }
            }
        }

        // Cập nhật session status
        session.status = 'completed';
        session.endTime = new Date();
        session.paymentMethod = paymentMethod;
        await session.save();

        // Giải phóng bàn
        const table = await Table.findOne({ tableNumber: session.tableNumber });
        if (table) {
            await table.release();
        }

        // Cập nhật tất cả orders sang trạng thái completed
        await Order.updateMany(
            { sessionId: session._id },
            { $set: { status: 'completed' } }
        );

        res.status(200).json({
            success: true,
            message: 'Thanh toán thành công! Cảm ơn quý khách.',
            data: {
                sessionId: session._id,
                sessionCode: session.sessionCode,
                tableNumber: session.tableNumber,
                totalAmount: session.totalAmount,
                paymentMethod: paymentMethod,
                startTime: session.startTime,
                endTime: session.endTime
            }
        });
    } catch (error) {
        console.error('Error completing session payment:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi thanh toán',
            error: error.message
        });
    }
};

module.exports = {
    getTableByNumber,
    createOrGetSession,
    getSessionById,
    getAllTables,
    completeSessionAndPayment
};