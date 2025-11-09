package vn.edu.fpt.taptoeat.api;

public class ApiConfig {
    // Base URL - Update with your backend URL
    public static final String BASE_URL = "http://10.0.2.2:9999/api/"; // For Android Emulator
    // public static final String BASE_URL = "http://192.168.1.xxx:9999/api/"; // For physical device
    
    // UC-01: Table & Session
    public static final String GET_TABLE = "tables/";
    public static final String CREATE_SESSION = "sessions";
    public static final String GET_SESSION = "orders/session/";
    
    // UC-02: Menu
    public static final String GET_CATEGORIES = "categories";
    public static final String GET_CATEGORY_ITEMS = "categories/%s/items";
    public static final String GET_MENU_ITEMS = "menu-items";
    
    // UC-04: Orders
    public static final String CREATE_ORDER = "orders";
    public static final String GET_ORDERS = "orders/session/";
}

