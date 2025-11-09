package vn.edu.fpt.taptoeat.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import vn.edu.fpt.taptoeat.models.Category;
import vn.edu.fpt.taptoeat.models.CategoryItemsResponse;
import vn.edu.fpt.taptoeat.models.MenuItem;

public interface ApiService {

    // UC-01: Table verification
    @GET("tables/{tableNumber}")
    Call<ApiResponse<TableInfo>> getTableInfo(@Path("tableNumber") int tableNumber);

    // UC-01: Create session
    @POST("sessions")
    Call<ApiResponse<SessionInfo>> createSession(@Body SessionRequest request);

    // UC-02: Get all categories
    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();

    // UC-02: Get menu items by category
    // Response: {"success": true, "data": {"category": {...}, "items": [...]}}
    @GET("categories/{categoryId}/items")
    Call<ApiResponse<CategoryItemsResponse>> getCategoryItems(@Path("categoryId") String categoryId);
    
    // UC-02: Get all menu items (alternative endpoint)
    @GET("menu-items")
    Call<ApiResponse<List<MenuItem>>> getAllMenuItems();
    
    // UC-04: Place order
    @POST("orders")
    Call<ApiResponse<OrderResponse>> placeOrder(@Body OrderRequest request);

    // Response wrapper classes
    class ApiResponse<T> {
        private boolean success;
        private T data;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public T getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }
    }

    // UC-01 Request/Response models
    class SessionRequest {
        private int tableNumber;

        public SessionRequest(int tableNumber) {
            this.tableNumber = tableNumber;
        }

        public int getTableNumber() {
            return tableNumber;
        }
    }

    class TableInfo {
        private String _id;
        private int tableNumber;
        private String status;

        public String getId() {
            return _id;
        }

        public int getTableNumber() {
            return tableNumber;
        }

        public String getStatus() {
            return status;
        }
    }

    class SessionInfo {
        private String _id;
        private int tableNumber;
        private String status;
        private String createdAt;

        public String getId() {
            return _id;
        }

        public int getTableNumber() {
            return tableNumber;
        }

        public String getStatus() {
            return status;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
    
    // UC-04 Order Request/Response models
    class OrderRequest {
        private String sessionId;
        private List<OrderItemRequest> items;

        public OrderRequest(String sessionId, List<OrderItemRequest> items) {
            this.sessionId = sessionId;
            this.items = items;
        }

        public String getSessionId() {
            return sessionId;
        }

        public List<OrderItemRequest> getItems() {
            return items;
        }
    }
    
    class OrderItemRequest {
        private String menuItemId;
        private int quantity;
        private String notes;

        public OrderItemRequest(String menuItemId, int quantity, String notes) {
            this.menuItemId = menuItemId;
            this.quantity = quantity;
            this.notes = notes;
        }

        public String getMenuItemId() {
            return menuItemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getNotes() {
            return notes;
        }
    }
    
    class OrderResponse {
        private String _id;
        private String sessionId;
        private int tableNumber;
        private String status;
        private double totalAmount;
        private String createdAt;
        private List<OrderItemResponse> items;

        public String getId() {
            return _id;
        }

        public String getSessionId() {
            return sessionId;
        }

        public int getTableNumber() {
            return tableNumber;
        }

        public String getStatus() {
            return status;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public List<OrderItemResponse> getItems() {
            return items;
        }
    }
    
    class OrderItemResponse {
        private String menuItemId;
        private String name;
        private int quantity;
        private double price;
        private String notes;

        public String getMenuItemId() {
            return menuItemId;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public String getNotes() {
            return notes;
        }
    }
}
