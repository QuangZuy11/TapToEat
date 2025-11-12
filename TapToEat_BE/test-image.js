const http = require('http');

const options = {
  hostname: 'localhost',
  port: 9999,
  path: '/api/ai-recommendations/weather',
  method: 'GET'
};

const req = http.request(options, (res) => {
  let data = '';
  res.on('data', (chunk) => { data += chunk; });
  res.on('end', () => {
    try {
      const json = JSON.parse(data);
      const recs = json.data && json.data.recommendations;
      if (!recs) return console.log('No recommendations');
      recs.forEach((r, i) => {
        console.log(`#${i+1}`);
        console.log('menuItem keys:', Object.keys(r.menuItem || {}));
        console.log('menuItem.imageUrl:', (r.menuItem || {}).imageUrl);
      });
    } catch (e) { console.error(e); console.log('raw:', data); }
  });
});
req.on('error', (e) => console.error(e));
req.end();
