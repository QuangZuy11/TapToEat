// Simple test script to call AI endpoint
const http = require('http');

async function testAIEndpoint() {
    console.log('🧪 Testing AI Recommendations endpoint...\n');

    const options = {
        hostname: 'localhost',
        port: 9999,
        path: '/api/ai-recommendations/weather',
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const req = http.request(options, (res) => {
        console.log(`✅ Status Code: ${res.statusCode}\n`);

        let data = '';

        res.on('data', (chunk) => {
            data += chunk;
        });

        res.on('end', () => {
            try {
                const jsonData = JSON.parse(data);
                console.log('📊 Response Data:');
                console.log(JSON.stringify(jsonData, null, 2));
            } catch (e) {
                console.log('Raw Response:', data);
            }
        });
    });

    req.on('error', (error) => {
        console.error('❌ ERROR:', error.message);
        console.error('Full error:', error);
    });

    req.end();
}

testAIEndpoint();
