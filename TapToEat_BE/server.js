const express = require('express');
const bodyParser = require("body-parser");
const morgan = require("morgan");
const cors = require("cors");
require("dotenv").config();

const connectDB = require("./config/db");
const ApiRouter = require("./routes");
const app = express();

// Middleware
app.use(cors()); // Enable CORS for Android app
app.use(bodyParser.json());
app.use(morgan("dev"));

app.use("/api", ApiRouter);

app.get('/', async (req, res) => {
    try {
        res.send({ message: 'Welcome to Prm392!' });
    } catch (error) {
        res.send({ error: error.message });
    }
});


const PORT = process.env.PORT || 9999;
app.listen(PORT, () => { console.log(`Server running on port ${PORT}`), connectDB() });