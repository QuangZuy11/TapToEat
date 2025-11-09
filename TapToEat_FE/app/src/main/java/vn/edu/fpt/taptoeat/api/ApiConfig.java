package vn.edu.fpt.taptoeat.api;

public class ApiConfig {
    // TODO: Update this with your actual backend URL
    public static final String BASE_URL = "http://10.0.2.2:3000/api/"; // For Android Emulator
    // public static final String BASE_URL = "http://localhost:3000/api/"; // For physical device, use your computer's IP
    
    // Endpoints
    public static final String VERIFY_TABLE = "tables/verify";
    public static final String GET_SESSION = "sessions/table/";
    public static final String CREATE_SESSION = "sessions";
    public static final String GET_MENU = "menu";
    public static final String CREATE_ORDER = "orders";
    public static final String GET_ORDERS = "orders/session/";
}
