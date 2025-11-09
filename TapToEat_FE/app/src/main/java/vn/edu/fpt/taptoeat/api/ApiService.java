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
}
