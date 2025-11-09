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

import java.util.ArrayList;
import java.util.List;

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
            finish();
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
                    onItemStatusChanged(orderId, itemId, newStatus);
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
        Log.d(TAG, "Loading chef orders for current filter: " + currentFilter);
        
        // Get status filter for current tab
        List<String> statusFilter = getStatusFilterForTab();
        
        apiService.getChefOrders(statusFilter).enqueue(new Callback<ApiService.ChefOrdersResponse>() {
            @Override
            public void onResponse(Call<ApiService.ChefOrdersResponse> call, 
                                 Response<ApiService.ChefOrdersResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiService.ChefOrdersResponse ordersResponse = response.body();
                        List<OrderResponse> orders = ordersResponse.getData();
                        
                        if (orders != null) {
                            allOrders.clear();
                            allOrders.addAll(orders);
                            filterOrders();
                            Log.d(TAG, "Loaded " + orders.size() + " orders for filter: " + currentFilter);
                            
                            // Update tab badges with stats
                            if (ordersResponse.getStats() != null) {
                                updateTabBadges(ordersResponse.getStats());
                            }
                        } else {
                            Log.w(TAG, "No orders data received");
                            allOrders.clear();
                            filterOrders();
                        }
                    } else {
                        Log.e(TAG, "Response not successful: " + response.code());
                        if (response.code() == 404) {
                            // API endpoint not implemented yet, show empty state
                            Log.i(TAG, "Chef orders endpoint not implemented, showing empty state");
                            allOrders.clear();
                            filterOrders();
                        } else {
                            Toast.makeText(ChefDashboardActivity.this, 
                                "Không thể tải danh sách đơn hàng (Lỗi: " + response.code() + ")", 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing chef orders response", e);
                    Toast.makeText(ChefDashboardActivity.this, 
                        "Lỗi xử lý dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ChefOrdersResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Failed to load chef orders", t);
                
                if (t.getMessage() != null && t.getMessage().contains("404")) {
                    // Chef API not implemented yet, show empty state
                    Log.w(TAG, "Chef API not implemented, showing empty state");
                    allOrders.clear();
                    filterOrders();
                } else {
                    Toast.makeText(ChefDashboardActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
        filteredOrders.clear();
        for (OrderResponse order : allOrders) {
            if (currentFilter.equals(order.getStatus())) {
                filteredOrders.add(order);
            }
        }
        
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
        Log.d(TAG, "Updating item status - OrderId: " + orderId + ", ItemId: " + itemId + ", Status: " + newStatus);
        
        // Convert itemId to itemIndex (assuming itemId is the index as string)
        int itemIndex;
        try {
            itemIndex = Integer.parseInt(itemId);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid item ID format: " + itemId);
            Toast.makeText(this, "Lỗi: ID món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiService.StatusUpdateRequest request = new ApiService.StatusUpdateRequest(newStatus);
        Call<ApiService.ApiResponse<OrderResponse>> call;
        
        // Use appropriate API endpoint based on status
        switch (newStatus) {
            case "preparing":
                call = apiService.startOrderItem(orderId, itemIndex, request);
                break;
            case "ready":
                call = apiService.completeOrderItem(orderId, itemIndex, request);
                break;
            default:
                Log.w(TAG, "Unsupported item status update: " + newStatus);
                return;
        }
        
        call.enqueue(new Callback<ApiService.ApiResponse<OrderResponse>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<OrderResponse>> call, 
                                 Response<ApiService.ApiResponse<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<OrderResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Log.d(TAG, "Item status updated successfully");
                        Toast.makeText(ChefDashboardActivity.this, 
                            "Cập nhật món ăn thành công", Toast.LENGTH_SHORT).show();
                        // Refresh orders to show updated status
                        loadAllOrders();
                    } else {
                        Log.e(TAG, "API returned error: " + apiResponse.getMessage());
                        Toast.makeText(ChefDashboardActivity.this, 
                            "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Update item status failed: " + response.code());
                    Toast.makeText(ChefDashboardActivity.this, 
                        "Không thể cập nhật món ăn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<OrderResponse>> call, Throwable t) {
                Log.e(TAG, "Update item status failed", t);
                Toast.makeText(ChefDashboardActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollingHandler != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }
}