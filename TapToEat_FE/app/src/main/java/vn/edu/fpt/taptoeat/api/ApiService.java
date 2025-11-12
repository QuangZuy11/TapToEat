package vn.edu.fpt.taptoeat.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
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
    
    // UC-05: Get session orders
    @GET("orders/session/{sessionId}")
    Call<ApiResponse<List<OrderResponse>>> getSessionOrders(@Path("sessionId") String sessionId);
    
    // UC-06: Get all orders for chef
    @GET("orders")
    Call<ApiResponse<List<OrderResponse>>> getAllOrders();
    
    // UC-06: Get orders for chef with status filter
    @GET("chef/orders")
    Call<ChefOrdersResponse> getChefOrders(@Query("status") List<String> statuses);
    
    // UC-06: Get chef dashboard stats
    @GET("chef/dashboard")
    Call<ApiResponse<ChefDashboardResponse>> getChefDashboard();
    
    // UC-07 & UC-08: Update order status (legacy)
    @PUT("orders/{orderId}/status")
    Call<ApiResponse<OrderResponse>> updateOrderStatus(@Path("orderId") String orderId, @Body StatusUpdateRequest request);
    
    // UC-07: Start preparing order item
    @PATCH("chef/orders/{orderId}/items/{itemIndex}/start")
    Call<ApiResponse<OrderResponse>> startOrderItem(@Path("orderId") String orderId, @Path("itemIndex") int itemIndex, @Body StatusUpdateRequest request);
    
    // UC-07: Start preparing entire order
    @PATCH("chef/orders/{orderId}/start")
    Call<ApiResponse<OrderResponse>> startOrder(@Path("orderId") String orderId);
    
    // UC-08: Complete order item
    @PATCH("chef/orders/{orderId}/items/{itemIndex}/complete")
    Call<ApiResponse<OrderResponse>> completeOrderItem(@Path("orderId") String orderId, @Path("itemIndex") int itemIndex, @Body StatusUpdateRequest request);
    
    // UC-09: Update menu item availability
    @PATCH("chef/menu-items/{id}/availability")
    Call<ApiResponse<MenuItem>> updateMenuItemAvailability(@Path("id") String menuItemId, @Body AvailabilityRequest request);

    // UC-10: Complete session and payment
    @POST("sessions/{sessionId}/payment")
    Call<ApiResponse<PaymentResponse>> completeSessionPayment(@Path("sessionId") String sessionId, @Body PaymentRequest request);

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
        private int tableNumber;
        private List<OrderItemRequest> items;

        public OrderRequest(String sessionId, int tableNumber, List<OrderItemRequest> items) {
            this.sessionId = sessionId;
            this.tableNumber = tableNumber;
            this.items = items;
        }

        public String getSessionId() {
            return sessionId;
        }

        public int getTableNumber() {
            return tableNumber;
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
    
    // UC-07 & UC-08: Status update request
    class StatusUpdateRequest {
        private String status;

        public StatusUpdateRequest(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }
    
    // UC-06: Chef orders response
    class ChefOrdersResponse {
        private List<OrderResponse> data;
        private ChefStats stats;

        public List<OrderResponse> getData() {
            return data;
        }

        public ChefStats getStats() {
            return stats;
        }
    }
    
    // UC-06: Chef dashboard response
    class ChefDashboardResponse {
        private ChefCurrentOrders currentOrders;
        private List<OrderResponse> urgentOrders;
        private ChefTodayStats todayStats;

        public ChefCurrentOrders getCurrentOrders() {
            return currentOrders;
        }

        public List<OrderResponse> getUrgentOrders() {
            return urgentOrders;
        }

        public ChefTodayStats getTodayStats() {
            return todayStats;
        }
    }
    
    // UC-06: Chef stats
    class ChefStats {
        private int total;
        private int pending;
        private int preparing;
        private int ready;

        public int getTotal() { return total; }
        public int getPending() { return pending; }
        public int getPreparing() { return preparing; }
        public int getReady() { return ready; }
    }
    
    class ChefCurrentOrders {
        private int pending;
        private int preparing;
        private int ready;
        private int total;

        public int getPending() { return pending; }
        public int getPreparing() { return preparing; }
        public int getReady() { return ready; }
        public int getTotal() { return total; }
    }
    
    class ChefTodayStats {
        private int totalOrders;
        private int completed;

        public int getTotalOrders() { return totalOrders; }
        public int getCompleted() { return completed; }
    }
    
    // UC-09: Availability update request
    class AvailabilityRequest {
        private boolean isAvailable;

        public AvailabilityRequest(boolean isAvailable) {
            this.isAvailable = isAvailable;
        }

        public boolean isAvailable() {
            return isAvailable;
        }
    }
    
    class OrderResponse {
        @SerializedName("_id")
        private String _id;
        
        private Object sessionId;  // Change to Object since backend returns object
        private int tableNumber;
        private String orderNumber;
        private String status;
        private double totalAmount;
        private List<OrderItemResponse> items;

        public String getId() {
            return _id;
        }

        public String getSessionId() {
            // Handle sessionId as object, extract string if needed
            if (sessionId instanceof String) {
                return (String) sessionId;
            } else if (sessionId != null) {
                return sessionId.toString();
            }
            return null;
        }

        public int getTableNumber() {
            return tableNumber;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public String getStatus() {
            return status;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public String getCreatedAt() {
            // Return orderedAt from first item if available
            if (items != null && !items.isEmpty() && items.get(0).getOrderedAt() != null) {
                return items.get(0).getOrderedAt();
            }
            return null;
        }

        public List<OrderItemResponse> getItems() {
            return items;
        }
    }
    
    class OrderItemResponse {
        private String menuItemId;
        private String menuItemName;
        private int quantity;
        private double price;
        private String note;
        private String status;
        private String orderedAt;

        public String getMenuItemId() {
            return menuItemId;
        }

        public String getName() {
            return menuItemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public String getNotes() {
            return note;
        }

        public String getStatus() {
            return status;
        }

        public String getOrderedAt() {
            return orderedAt;
        }
    }
    
    // UC-10: Payment Request/Response
    class PaymentRequest {
        private String paymentMethod;

        public PaymentRequest(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }
    }
    
    class PaymentResponse {
        private String sessionId;
        private String sessionCode;
        private int tableNumber;
        private double totalAmount;
        private String paymentMethod;
        private String startTime;
        private String endTime;

        public String getSessionId() {
            return sessionId;
        }

        public String getSessionCode() {
            return sessionCode;
        }

        public int getTableNumber() {
            return tableNumber;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }
    }
}
