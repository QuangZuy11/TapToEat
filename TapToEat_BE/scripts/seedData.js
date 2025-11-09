const mongoose = require('mongoose');
require('dotenv').config();

// Import models
const { Table, Category, MenuItem, Chef } = require('../models');

const seedData = async () => {
    try {
        // Connect to MongoDB
        await mongoose.connect(process.env.MONGO_URI);
        console.log('MongoDB connected successfully');

        // Clear existing data (optional)
        await Table.deleteMany({});
        await Category.deleteMany({});
        await MenuItem.deleteMany({});
        await Chef.deleteMany({});
        console.log('Cleared existing data');

        // Seed Tables
        const tables = await Table.insertMany([
            { tableNumber: 1, capacity: 2, status: 'available' },
            { tableNumber: 2, capacity: 4, status: 'available' },
            { tableNumber: 3, capacity: 4, status: 'available' },
            { tableNumber: 4, capacity: 6, status: 'available' },
            { tableNumber: 5, capacity: 2, status: 'available' },
            { tableNumber: 6, capacity: 8, status: 'available' },
            { tableNumber: 7, capacity: 4, status: 'available' },
            { tableNumber: 8, capacity: 2, status: 'available' }
        ]);
        console.log(`✓ Created ${tables.length} tables`);

        // Seed Categories
        const categories = await Category.insertMany([
            {
                name: 'Khai Vị',
                description: 'Món khai vị hấp dẫn',
                imageUrl: 'https://example.com/images/khai-vi.jpg',
                displayOrder: 1,
                isActive: true
            },
            {
                name: 'Món Chính',
                description: 'Các món ăn chính phong phú',
                imageUrl: 'https://example.com/images/mon-chinh.jpg',
                displayOrder: 2,
                isActive: true
            },
            {
                name: 'Đồ Uống',
                description: 'Các loại nước giải khát',
                imageUrl: 'https://example.com/images/do-uong.jpg',
                displayOrder: 3,
                isActive: true
            },
            {
                name: 'Tráng Miệng',
                description: 'Món tráng miệng ngon lành',
                imageUrl: 'https://example.com/images/trang-mieng.jpg',
                displayOrder: 4,
                isActive: true
            }
        ]);
        console.log(`✓ Created ${categories.length} categories`);

        // Get category IDs
        const khaiVi = categories.find(c => c.name === 'Khai Vị');
        const monChinh = categories.find(c => c.name === 'Món Chính');
        const doUong = categories.find(c => c.name === 'Đồ Uống');
        const trangMieng = categories.find(c => c.name === 'Tráng Miệng');

        // Seed Menu Items
        const menuItems = await MenuItem.insertMany([
            // Khai Vị
            {
                name: 'Gỏi Cuốn Tôm Thịt',
                description: 'Gỏi cuốn tươi ngon với tôm, thịt và rau sống',
                price: 35000,
                imageUrl: 'https://example.com/images/goi-cuon.jpg',
                categoryId: khaiVi._id,
                isAvailable: true,
                preparationTime: 10,
                tags: ['fresh', 'healthy', 'vietnamese']
            },
            {
                name: 'Nem Rán',
                description: 'Nem rán giòn rụm với nhân thịt',
                price: 40000,
                imageUrl: 'https://example.com/images/nem-ran.jpg',
                categoryId: khaiVi._id,
                isAvailable: true,
                preparationTime: 15,
                tags: ['fried', 'crispy', 'popular']
            },
            {
                name: 'Salad Rau Củ',
                description: 'Salad rau củ tươi với sốt đặc biệt',
                price: 30000,
                imageUrl: 'https://example.com/images/salad.jpg',
                categoryId: khaiVi._id,
                isAvailable: true,
                preparationTime: 8,
                tags: ['healthy', 'vegetarian', 'fresh']
            },

            // Món Chính
            {
                name: 'Phở Bò',
                description: 'Phở bò truyền thống Hà Nội với nước dùng thơm ngon',
                price: 50000,
                imageUrl: 'https://example.com/images/pho-bo.jpg',
                categoryId: monChinh._id,
                isAvailable: true,
                preparationTime: 20,
                tags: ['popular', 'traditional', 'vietnamese']
            },
            {
                name: 'Cơm Tấm Sườn',
                description: 'Cơm tấm với sườn nướng thơm lừng',
                price: 45000,
                imageUrl: 'https://example.com/images/com-tam.jpg',
                categoryId: monChinh._id,
                isAvailable: true,
                preparationTime: 25,
                tags: ['popular', 'grilled', 'vietnamese']
            },
            {
                name: 'Bún Chả',
                description: 'Bún chả Hà Nội đặc trưng với thịt nướng',
                price: 45000,
                imageUrl: 'https://example.com/images/bun-cha.jpg',
                categoryId: monChinh._id,
                isAvailable: true,
                preparationTime: 25,
                tags: ['popular', 'traditional', 'grilled']
            },
            {
                name: 'Mì Xào Hải Sản',
                description: 'Mì xào giòn với hải sản tươi ngon',
                price: 60000,
                imageUrl: 'https://example.com/images/mi-xao.jpg',
                categoryId: monChinh._id,
                isAvailable: true,
                preparationTime: 20,
                tags: ['seafood', 'stir-fried']
            },
            {
                name: 'Gà Rán',
                description: 'Gà rán giòn tan với nước sốt đặc biệt',
                price: 55000,
                imageUrl: 'https://example.com/images/ga-ran.jpg',
                categoryId: monChinh._id,
                isAvailable: true,
                preparationTime: 30,
                tags: ['fried', 'chicken', 'popular']
            },
            {
                name: 'Bò Lúc Lắc',
                description: 'Thịt bò lúc lắc thơm ngon với khoai tây chiên',
                price: 70000,
                imageUrl: 'https://example.com/images/bo-luc-lac.jpg',
                categoryId: monChinh._id,
                isAvailable: true,
                preparationTime: 25,
                tags: ['beef', 'stir-fried']
            },
            {
                name: 'Cá Kho Tộ',
                description: 'Cá kho tộ đậm đà hương vị',
                price: 55000,
                imageUrl: 'https://example.com/images/ca-kho.jpg',
                categoryId: monChinh._id,
                isAvailable: true,
                preparationTime: 30,
                tags: ['fish', 'traditional']
            },

            // Đồ Uống
            {
                name: 'Trà Đá',
                description: 'Trà đá truyền thống Việt Nam',
                price: 5000,
                imageUrl: 'https://example.com/images/tra-da.jpg',
                categoryId: doUong._id,
                isAvailable: true,
                preparationTime: 2,
                tags: ['traditional', 'cold']
            },
            {
                name: 'Cà Phê Sữa Đá',
                description: 'Cà phê sữa đá Việt Nam đậm đà',
                price: 25000,
                imageUrl: 'https://example.com/images/ca-phe-sua.jpg',
                categoryId: doUong._id,
                isAvailable: true,
                preparationTime: 5,
                tags: ['coffee', 'popular', 'cold']
            },
            {
                name: 'Cà Phê Đen',
                description: 'Cà phê đen nguyên chất',
                price: 20000,
                imageUrl: 'https://example.com/images/ca-phe-den.jpg',
                categoryId: doUong._id,
                isAvailable: true,
                preparationTime: 5,
                tags: ['coffee', 'black']
            },
            {
                name: 'Nước Cam Ép',
                description: 'Nước cam tươi ép 100%',
                price: 30000,
                imageUrl: 'https://example.com/images/nuoc-cam.jpg',
                categoryId: doUong._id,
                isAvailable: true,
                preparationTime: 5,
                tags: ['fresh', 'juice', 'healthy']
            },
            {
                name: 'Sinh Tố Bơ',
                description: 'Sinh tố bơ sánh mịn, béo ngậy',
                price: 35000,
                imageUrl: 'https://example.com/images/sinh-to-bo.jpg',
                categoryId: doUong._id,
                isAvailable: true,
                preparationTime: 5,
                tags: ['smoothie', 'healthy', 'cold']
            },
            {
                name: 'Trà Sữa Trân Châu',
                description: 'Trà sữa trân châu đường đen',
                price: 35000,
                imageUrl: 'https://example.com/images/tra-sua.jpg',
                categoryId: doUong._id,
                isAvailable: true,
                preparationTime: 7,
                tags: ['bubble-tea', 'popular', 'cold']
            },
            {
                name: 'Nước Dừa Tươi',
                description: 'Nước dừa tươi mát lạnh',
                price: 25000,
                imageUrl: 'https://example.com/images/nuoc-dua.jpg',
                categoryId: doUong._id,
                isAvailable: true,
                preparationTime: 3,
                tags: ['fresh', 'healthy', 'cold']
            },
            {
                name: 'Nước Chanh Dây',
                description: 'Nước chanh dây chua ngọt sảng khoái',
                price: 20000,
                imageUrl: 'https://example.com/images/chanh-day.jpg',
                categoryId: doUong._id,
                isAvailable: true,
                preparationTime: 5,
                tags: ['fresh', 'juice', 'cold']
            },

            // Tráng Miệng
            {
                name: 'Chè Ba Màu',
                description: 'Chè ba màu truyền thống với đậu, thạch và nước cốt dừa',
                price: 20000,
                imageUrl: 'https://example.com/images/che-ba-mau.jpg',
                categoryId: trangMieng._id,
                isAvailable: true,
                preparationTime: 5,
                tags: ['traditional', 'sweet', 'vietnamese']
            },
            {
                name: 'Bánh Flan',
                description: 'Bánh flan caramel mềm mịn',
                price: 25000,
                imageUrl: 'https://example.com/images/banh-flan.jpg',
                categoryId: trangMieng._id,
                isAvailable: true,
                preparationTime: 5,
                tags: ['sweet', 'dessert']
            },
            {
                name: 'Kem Chiên',
                description: 'Kem chiên giòn tan, lạnh bên trong',
                price: 30000,
                imageUrl: 'https://example.com/images/kem-chien.jpg',
                categoryId: trangMieng._id,
                isAvailable: true,
                preparationTime: 10,
                tags: ['fried', 'ice-cream', 'hot']
            },
            {
                name: 'Chè Thái',
                description: 'Chè thái nhiều trái cây tươi',
                price: 25000,
                imageUrl: 'https://example.com/images/che-thai.jpg',
                categoryId: trangMieng._id,
                isAvailable: true,
                preparationTime: 5,
                tags: ['sweet', 'fresh', 'cold']
            },
            {
                name: 'Yaourt Dẻo',
                description: 'Yaourt dẻo mát lạnh với topping',
                price: 15000,
                imageUrl: 'https://example.com/images/yaourt.jpg',
                categoryId: trangMieng._id,
                isAvailable: true,
                preparationTime: 3,
                tags: ['healthy', 'sweet', 'cold']
            }
        ]);
        console.log(`✓ Created ${menuItems.length} menu items`);

        // Seed Chef accounts
        const chefs = await Chef.insertMany([
            {
                username: 'chef1',
                password: 'chef123', // Will be hashed automatically
                name: 'Nguyễn Văn A',
                role: 'chef',
                isActive: true
            },
            {
                username: 'chef2',
                password: 'chef123',
                name: 'Trần Thị B',
                role: 'chef',
                isActive: true
            },
            {
                username: 'admin',
                password: 'admin123',
                name: 'Quản Trị Viên',
                role: 'admin',
                isActive: true
            }
        ]);
        console.log(`✓ Created ${chefs.length} chef accounts`);

        console.log('\n✅ Seed data completed successfully!');
        console.log('\n📊 Summary:');
        console.log(`   - Tables: ${tables.length}`);
        console.log(`   - Categories: ${categories.length}`);
        console.log(`   - Menu Items: ${menuItems.length}`);
        console.log(`   - Chef Accounts: ${chefs.length}`);
        console.log('\n📝 Chef Login Credentials:');
        console.log('   Chef 1: username=chef1, password=chef123');
        console.log('   Chef 2: username=chef2, password=chef123');
        console.log('   Admin: username=admin, password=admin123');
        console.log('\n💡 Tip: Check MongoDB Compass to see the collections!');

        process.exit(0);
    } catch (error) {
        console.error('❌ Error seeding data:', error);
        process.exit(1);
    }
};

// Run seed
seedData();