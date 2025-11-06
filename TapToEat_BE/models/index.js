// Export tất cả models để dễ import
const Table = require('./Table');
const Category = require('./Category');
const MenuItem = require('./MenuItem');
const OrderSession = require('./OrderSession');
const Order = require('./Order');
const Chef = require('./Chef');
const Notification = require('./Notification');

module.exports = {
    Table,
    Category,
    MenuItem,
    OrderSession,
    Order,
    Chef,
    Notification
};