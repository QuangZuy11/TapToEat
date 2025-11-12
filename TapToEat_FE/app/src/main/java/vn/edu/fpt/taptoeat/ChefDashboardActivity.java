package vn.edu.fpt.taptoeat;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.taptoeat.adapters.ChefOrderAdapter;
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.api.ApiService.ApiResponse;
import vn.edu.fpt.taptoeat.api.ApiService.OrderResponse;
import vn.edu.fpt.taptoeat.api.RetrofitClient;

public class ChefDashboardActivity extends AppCompatActivity {
    
    private static final String TAG = "ChefDashboard";
    private static final long POLLING_INTERVAL = 10000; // 10 seconds for chef
    
    private TabLayout tabLayout;
    private RecyclerView recyclerViewOrders;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout tvEmptyState;  // Changed from TextView to LinearLayout
    private Toolbar toolbar;
    
    private ChefOrderAdapter orderAdapter;
    private List<OrderResponse> allOrders;
    private List<OrderResponse> filteredOrders;
    
    private ApiService apiService;
    private Handler pollingHandler;
    private Runnable pollingRunnable;
    
    private String currentFilter = "pending"; // pending, preparing, ready

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_chef_dashboard);

            initViews();
            setupRecyclerView();
            setupTabs();
            setupSwipeRefresh();
            setupPolling();
            
            apiService = RetrofitClient.getInstance().getApiService();
            
            loadAllOrders();
        } catch (Exception e) {
            Log.e(TAG, "Error in ChefDashboard onCreate", e);
            Toast.makeText(this, "Lỗi khởi tạo Chef Dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        if (toolbar == null) {
            Log.e(TAG, "toolbar is null!");
            return;
        }
        if (tabLayout == null) {
            Log.e(TAG, "tabLayout is null!");
            return;
        }
        if (recyclerViewOrders == null) {
            Log.e(TAG, "recyclerViewOrders is null!");
            return;
        }
        if (swipeRefreshLayout == null) {
            Log.e(TAG, "swipeRefreshLayout is null!");
            return;
        }
        if (tvEmptyState == null) {
            Log.e(TAG, "tvEmptyState is null!");
            return;
        }
        
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        allOrders = new ArrayList<>();
        filteredOrders = new ArrayList<>();
        orderAdapter = new ChefOrderAdapter(filteredOrders, 
            new ChefOrderAdapter.OnOrderStatusChangeListener() {
                @Override
                public void onStatusChanged(OrderResponse order, String newStatus) {
                    onOrderStatusChanged(order, newStatus);
                }
            },
            new ChefOrderAdapter.OnItemStatusChangeListener() {
                @Override
                public void onItemStatusChanged(String orderId, String itemId, String newStatus) {
                    // Call the Activity's method, not recursively call itself
                    ChefDashboardActivity.this.onItemStatusChanged(orderId, itemId, newStatus);
                }
            });
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Chờ làm").setTag("pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Đang làm").setTag("preparing"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã xong").setTag("ready"));
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFilter = (String) tab.getTag();
                filterOrders();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.orange);
        swipeRefreshLayout.setOnRefreshListener(this::loadAllOrders);
    }

    private void setupPolling() {
        pollingHandler = new Handler();
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadAllOrders();
                pollingHandler.postDelayed(this, POLLING_INTERVAL);
            }
        };
    }

    private void loadAllOrders() {
        Log.d(TAG, "Loading all chef orders (will filter by item status in frontend)");
        swipeRefreshLayout.setRefreshing(true);
        
        // Load ALL orders without status filter, then filter by item status in frontend
        List<String> allStatuses = new ArrayList<>();
        allStatuses.add("pending");
        allStatuses.add("preparing");
        allStatuses.add("ready");
        
        apiService.getChefOrders(allStatuses).enqueue(new Callback<ApiService.ChefOrdersResponse>() {
            @Override
            public void onResponse(Call<ApiService.ChefOrdersResponse> call, 
                                 Response<ApiService.ChefOrdersResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiService.ChefOrdersResponse ordersResponse = response.body();
                        List<OrderResponse> orders = ordersResponse.getData();
                        
                        if (orders != null) {
                            // Update on UI thread to avoid race condition
                            runOnUiThread(() -> {
                                allOrders.clear();
                                allOrders.addAll(orders);
                                filterOrders();
                                Log.d(TAG, "Loaded " + orders.size() + " total orders, filtered to " + filteredOrders.size() + " for tab: " + currentFilter);
                            });
                        } else {
                            Log.w(TAG, "No orders data received");
                            runOnUiThread(() -> {
                                allOrders.clear();
                                filterOrders();
                            });
                        }
                    } else {
                        Log.e(TAG, "Response not successful: " + response.code());
                        if (response.code() == 404) {
                            runOnUiThread(() -> {
                                allOrders.clear();
                                filterOrders();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(ChefDashboardActivity.this, 
                                    "Không thể tải danh sách đơn hàng (Lỗi: " + response.code() + ")", 
                                    Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing chef orders response", e);
                    runOnUiThread(() -> {
                        Toast.makeText(ChefDashboardActivity.this, 
                            "Lỗi xử lý dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiService.ChefOrdersResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Failed to load chef orders", t);
                
                runOnUiThread(() -> {
                    if (t.getMessage() != null && t.getMessage().contains("404")) {
                        Log.w(TAG, "Chef API not implemented, showing empty state");
                        allOrders.clear();
                        filterOrders();
                    } else {
                        Toast.makeText(ChefDashboardActivity.this, 
                            "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    
    // Helper method to get status filter for current tab
    private List<String> getStatusFilterForTab() {
        List<String> statusFilter = new ArrayList<>();
        switch (currentFilter) {
            case "pending":
                statusFilter.add("pending");
                statusFilter.add("confirmed");
                break;
            case "preparing":
                statusFilter.add("preparing");
                break;
            case "ready":
                statusFilter.add("ready");
                statusFilter.add("completed");
                break;
            default:
                statusFilter.add("pending");
                statusFilter.add("confirmed");
                break;
        }
        return statusFilter;
    }
    
    // Helper method to update tab badges with counts
    private void updateTabBadges(ApiService.ChefStats stats) {
        try {
            if (tabLayout != null && stats != null) {
                // Update tab texts with counts
                TabLayout.Tab pendingTab = tabLayout.getTabAt(0);
                if (pendingTab != null) {
                    pendingTab.setText("Chờ (" + stats.getPending() + ")");
                }
                
                TabLayout.Tab preparingTab = tabLayout.getTabAt(1);
                if (preparingTab != null) {
                    preparingTab.setText("Đang làm (" + stats.getPreparing() + ")");
                }
                
                TabLayout.Tab readyTab = tabLayout.getTabAt(2);
                if (readyTab != null) {
                    readyTab.setText("Xong (" + stats.getReady() + ")");
                }
                
                Log.d(TAG, "Updated tab badges - Pending: " + stats.getPending() + 
                     ", Preparing: " + stats.getPreparing() + ", Ready: " + stats.getReady());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating tab badges", e);
        }
    }

    private void filterOrders() {
        Log.d(TAG, "=== FILTERING ORDERS ===");
        Log.d(TAG, "Current filter: " + currentFilter);
        Log.d(TAG, "Total orders before filter: " + allOrders.size());
        
        filteredOrders.clear();
        
        for (OrderResponse order : allOrders) {
            boolean shouldInclude = false;
            
            // Check if order has items matching the current filter
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                int pendingCount = 0;
                int preparingCount = 0;
                int readyCount = 0;
                
                // Count items by status
                for (ApiService.OrderItemResponse item : order.getItems()) {
                    String itemStatus = item.getStatus();
                    if ("pending".equals(itemStatus)) pendingCount++;
                    else if ("preparing".equals(itemStatus)) preparingCount++;
                    else if ("ready".equals(itemStatus)) readyCount++;
                }
                
                int totalItems = order.getItems().size();
                Log.d(TAG, "Order " + order.getId() + " - Total: " + totalItems + 
                    ", Pending: " + pendingCount + ", Preparing: " + preparingCount + ", Ready: " + readyCount);
                
                switch (currentFilter) {
                    case "pending":
                        // Show if ANY item is still pending
                        shouldInclude = (pendingCount > 0);
                        break;
                        
                    case "preparing":
                        // Show if ANY item is being prepared
                        shouldInclude = (preparingCount > 0);
                        break;
                        
                    case "ready":
                        // Show if ALL items are ready
                        shouldInclude = (readyCount == totalItems && totalItems > 0);
                        break;
                }
                
                Log.d(TAG, "Order " + order.getId() + " shouldInclude: " + shouldInclude);
            }
            
            if (shouldInclude) {
                filteredOrders.add(order);
            }
        }
        
        Log.d(TAG, "Orders after filter: " + filteredOrders.size());
        Log.d(TAG, "=== END FILTERING ===");
        
        // Sort orders by creation time (oldest first = priority)
        filteredOrders.sort((order1, order2) -> {
            try {
                String time1Str = order1.getCreatedAt();
                String time2Str = order2.getCreatedAt();
                
                if (time1Str == null || time2Str == null) {
                    return 0;
                }
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date time1 = dateFormat.parse(time1Str);
                Date time2 = dateFormat.parse(time2Str);
                
                if (time1 != null && time2 != null) {
                    return time1.compareTo(time2);
                }
            } catch (ParseException e) {
                Log.w(TAG, "Error parsing order time", e);
            }
            return 0;
        });
        
        orderAdapter.notifyDataSetChanged();
        
        // Show/hide empty state
        if (filteredOrders.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
            // Update the TextView inside the LinearLayout
            TextView emptyText = tvEmptyState.findViewById(R.id.tvEmptyText);
            if (emptyText != null) {
                emptyText.setText(getEmptyMessage());
            }
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewOrders.setVisibility(View.VISIBLE);
        }
    }

    private String getEmptyMessage() {
        switch (currentFilter) {
            case "pending": return "Không có đơn hàng chờ làm";
            case "preparing": return "Không có đơn hàng đang làm";
            case "ready": return "Không có đơn hàng đã xong";
            default: return "Không có đơn hàng";
        }
    }

    private void onOrderStatusChanged(OrderResponse order, String newStatus) {
        // Update order status via API
        updateOrderStatus(order.getId(), newStatus);
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        Log.d(TAG, "Updating order " + orderId + " to status: " + newStatus);
        
        ApiService.StatusUpdateRequest request = new ApiService.StatusUpdateRequest(newStatus);
        
        apiService.updateOrderStatus(orderId, request).enqueue(new Callback<ApiResponse<OrderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderResponse>> call, 
                                 Response<ApiResponse<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrderResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Log.d(TAG, "Order status updated successfully");
                        Toast.makeText(ChefDashboardActivity.this, 
                            "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                        // Refresh orders to show updated status
                        loadAllOrders();
                    } else {
                        Log.e(TAG, "API returned error: " + apiResponse.getMessage());
                        Toast.makeText(ChefDashboardActivity.this, 
                            "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Update status failed: " + response.code());
                    Toast.makeText(ChefDashboardActivity.this, 
                        "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderResponse>> call, Throwable t) {
                Log.e(TAG, "Update status failed", t);
                Toast.makeText(ChefDashboardActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollingHandler.removeCallbacks(pollingRunnable);
    }
    
    // Method to handle individual item status changes
    private void onItemStatusChanged(String orderId, String itemId, String newStatus) {
        // Run in background thread to avoid ANR
        new Thread(() -> {
            try {
                Log.d(TAG, "=== UPDATE ITEM STATUS START ===");
                Log.d(TAG, "OrderId: " + orderId);
                Log.d(TAG, "ItemId: " + itemId);
                Log.d(TAG, "NewStatus: " + newStatus);
                
                // Check if apiService is initialized
                if (apiService == null) {
                    Log.e(TAG, "!!! apiService is NULL !!!");
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Chưa kết nối API", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                if (orderId == null || orderId.isEmpty()) {
                    Log.e(TAG, "OrderId is null or empty!");
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Order ID không hợp lệ", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                if (itemId == null || itemId.isEmpty()) {
                    Log.e(TAG, "ItemId is null or empty!");
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Item ID không hợp lệ", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                // Convert itemId to itemIndex (assuming itemId is the index as string)
                int itemIndex;
                try {
                    itemIndex = Integer.parseInt(itemId);
                    Log.d(TAG, "Parsed itemIndex: " + itemIndex);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid item ID format: " + itemId, e);
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: ID món ăn không hợp lệ", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                ApiService.StatusUpdateRequest request = new ApiService.StatusUpdateRequest(newStatus);
                Call<ApiService.ApiResponse<OrderResponse>> call = null;
                
                // Use appropriate API endpoint based on status
                switch (newStatus) {
                    case "preparing":
                        Log.d(TAG, "Calling startOrderItem API for orderId=" + orderId + ", itemIndex=" + itemIndex);
                        try {
                            call = apiService.startOrderItem(orderId, itemIndex, request);
                        } catch (Exception e) {
                            Log.e(TAG, "Exception creating startOrderItem call", e);
                            runOnUiThread(() -> Toast.makeText(this, "Lỗi tạo API call: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            return;
                        }
                        break;
                    case "ready":
                        Log.d(TAG, "Calling completeOrderItem API for orderId=" + orderId + ", itemIndex=" + itemIndex);
                        try {
                            call = apiService.completeOrderItem(orderId, itemIndex, request);
                        } catch (Exception e) {
                            Log.e(TAG, "Exception creating completeOrderItem call", e);
                            runOnUiThread(() -> Toast.makeText(this, "Lỗi tạo API call: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            return;
                        }
                        break;
                    default:
                        Log.w(TAG, "Unsupported item status update: " + newStatus);
                        runOnUiThread(() -> Toast.makeText(this, "Trạng thái không hợp lệ: " + newStatus, Toast.LENGTH_SHORT).show());
                        return;
                }
                
                if (call == null) {
                    Log.e(TAG, "API call is NULL after switch!");
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi: Không thể tạo API request", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                Log.d(TAG, "Executing API call...");
                call.enqueue(new Callback<ApiService.ApiResponse<OrderResponse>>() {
                @Override
                public void onResponse(Call<ApiService.ApiResponse<OrderResponse>> call, 
                                     Response<ApiService.ApiResponse<OrderResponse>> response) {
                    Log.d(TAG, "API Response received - Code: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        ApiService.ApiResponse<OrderResponse> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            Log.d(TAG, "Item status updated successfully");
                            Toast.makeText(ChefDashboardActivity.this, 
                                "Cập nhật món ăn thành công ✅", Toast.LENGTH_SHORT).show();
                            // Refresh orders to show updated status
                            loadAllOrders();
                        } else {
                            Log.e(TAG, "API returned error: " + apiResponse.getMessage());
                            Toast.makeText(ChefDashboardActivity.this, 
                                "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Update item status failed: " + response.code());
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "Error body: " + errorBody);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        Toast.makeText(ChefDashboardActivity.this, 
                            "Không thể cập nhật món ăn (Lỗi: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ApiResponse<OrderResponse>> call, Throwable t) {
                    Log.e(TAG, "Update item status failed", t);
                    runOnUiThread(() -> Toast.makeText(ChefDashboardActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
            
            Log.d(TAG, "=== UPDATE ITEM STATUS END ===");
            
        } catch (Exception e) {
            Log.e(TAG, "Exception in onItemStatusChanged", e);
            runOnUiThread(() -> Toast.makeText(this, "Lỗi không xác định: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
        }).start(); // Start the background thread
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollingHandler != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }
}