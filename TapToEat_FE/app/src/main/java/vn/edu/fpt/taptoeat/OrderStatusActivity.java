package vn.edu.fpt.taptoeat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;

import vn.edu.fpt.taptoeat.adapters.OrderAdapter;
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.api.ApiService.ApiResponse;
import vn.edu.fpt.taptoeat.api.ApiService.OrderResponse;
import vn.edu.fpt.taptoeat.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusActivity extends AppCompatActivity {
    
    private static final String TAG = "OrderStatusActivity";
    private static final long POLLING_INTERVAL = 30000; // 30 seconds
    
    private RecyclerView recyclerViewOrders;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutEmptyOrders;
    private Toolbar toolbar;
    private MaterialButton btnRefresh;
    private MaterialButton btnHome;
    private MaterialButton btnPayment;
    private TextView tvOrderStatus;
    
    private OrderAdapter orderAdapter;
    private List<OrderResponse> orderList;
    
    private String sessionId;
    private int tableNumber;
    
    private ApiService apiService;
    private Handler pollingHandler;
    private Runnable pollingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Get intent data
        sessionId = getIntent().getStringExtra("sessionId");
        tableNumber = getIntent().getIntExtra("tableNumber", 0);

        if (sessionId == null || sessionId.isEmpty()) {
            Toast.makeText(this, "Session không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupSwipeRefresh();
        setupPolling();
        
        apiService = RetrofitClient.getInstance().getApiService();
        
        loadOrders();
    }

    private void initViews() {
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        layoutEmptyOrders = findViewById(R.id.layoutEmptyOrders);
        toolbar = findViewById(R.id.toolbar);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnHome = findViewById(R.id.btnHome);
        btnPayment = findViewById(R.id.btnPayment);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setSubtitle("Bàn " + tableNumber);
        
        btnRefresh.setOnClickListener(v -> loadOrders());
        
        // Home button - navigate to TableInputActivity
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(OrderStatusActivity.this, TableInputActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
        
        // Payment button
        if (btnPayment != null) {
            btnPayment.setOnClickListener(v -> handlePayment());
        }
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.green_dark);
        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
    }

    private void setupPolling() {
        pollingHandler = new Handler();
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadOrders();
                pollingHandler.postDelayed(this, POLLING_INTERVAL);
            }
        };
    }

    private void loadOrders() {
        Log.d(TAG, "Loading orders for session: " + sessionId);
        Log.d(TAG, "Table number: " + tableNumber);
        
        if (sessionId == null || sessionId.isEmpty()) {
            Log.e(TAG, "SessionId is null or empty!");
            Toast.makeText(this, "Lỗi: Không có session ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        apiService.getSessionOrders(sessionId).enqueue(new Callback<ApiResponse<List<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderResponse>>> call, 
                                 Response<ApiResponse<List<OrderResponse>>> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response successful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<OrderResponse>> apiResponse = response.body();
                    
                    Log.d(TAG, "API Success: " + apiResponse.isSuccess());
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<OrderResponse> orders = apiResponse.getData();
                        Log.d(TAG, "Loaded " + orders.size() + " orders");
                        
                        // Log chi tiết từng order
                        for (int i = 0; i < orders.size(); i++) {
                            OrderResponse order = orders.get(i);
                            Log.d(TAG, "Order " + i + ": ID=" + order.getId() + 
                                  ", Items count=" + (order.getItems() != null ? order.getItems().size() : 0));
                        }
                        
                        orderList.clear();
                        orderList.addAll(orders);
                        orderAdapter.updateOrders(orderList);
                        
                        // Update payment button status
                        updatePaymentButtonStatus();
                        
                        // Show/hide empty state
                        if (orderList.isEmpty()) {
                            Log.d(TAG, "Order list is EMPTY - showing empty state");
                            layoutEmptyOrders.setVisibility(View.VISIBLE);
                            recyclerViewOrders.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Order list has " + orderList.size() + " items - showing RecyclerView");
                            layoutEmptyOrders.setVisibility(View.GONE);
                            recyclerViewOrders.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e(TAG, "API returned error: " + apiResponse.getMessage());
                        Toast.makeText(OrderStatusActivity.this, 
                            "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(OrderStatusActivity.this, 
                        "Không thể tải danh sách đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<OrderResponse>>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Failed to load orders", t);
                Toast.makeText(OrderStatusActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Check if all items are ready (status = "ready")
     */
    private boolean areAllItemsReady() {
        if (orderList == null || orderList.isEmpty()) {
            return false;
        }
        
        for (OrderResponse order : orderList) {
            if (order.getItems() != null) {
                for (ApiService.OrderItemResponse item : order.getItems()) {
                    if (!"ready".equals(item.getStatus())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Get overall order status text
     */
    private String getOverallOrderStatus() {
        if (orderList == null || orderList.isEmpty()) {
            return "Chưa có đơn";
        }
        
        int readyCount = 0;
        int totalItems = 0;
        
        for (OrderResponse order : orderList) {
            if (order.getItems() != null) {
                for (ApiService.OrderItemResponse item : order.getItems()) {
                    totalItems++;
                    if ("ready".equals(item.getStatus())) {
                        readyCount++;
                    }
                }
            }
        }
        
        if (readyCount == totalItems && totalItems > 0) {
            return "Tất cả ra món";
        } else if (readyCount > 0) {
            return "Đang chuẩn bị (" + readyCount + "/" + totalItems + ")";
        } else {
            return "Chờ xác nhận";
        }
    }
    
    /**
     * Update payment button status based on order items
     */
    private void updatePaymentButtonStatus() {
        boolean allReady = areAllItemsReady();
        String statusText = getOverallOrderStatus();
        
        if (tvOrderStatus != null) {
            tvOrderStatus.setText(statusText);
            if (allReady) {
                tvOrderStatus.setTextColor(getResources().getColor(R.color.green_dark, null));
            } else {
                tvOrderStatus.setTextColor(getResources().getColor(R.color.orange, null));
            }
        }
        
        if (btnPayment != null) {
            btnPayment.setEnabled(allReady);
            btnPayment.setAlpha(allReady ? 1.0f : 0.5f);
        }
    }
    
    /**
     * Handle payment button click
     */
    private void handlePayment() {
        // Prepare bill information
        StringBuilder billInfo = new StringBuilder();
        double totalAmount = 0;
        int totalItems = 0;
        
        for (OrderResponse order : orderList) {
            if (order.getItems() != null) {
                for (ApiService.OrderItemResponse item : order.getItems()) {
                    billInfo.append(item.getName())
                            .append(" x").append(item.getQuantity())
                            .append(" - ").append(String.format("%,.0f đ", item.getPrice() * item.getQuantity()))
                            .append("\n");
                    totalAmount += item.getPrice() * item.getQuantity();
                    totalItems += item.getQuantity();
                }
            }
        }
        
        String billMessage = String.format(
                "Thông tin hóa đơn:\n\n%s\n" +
                "-------------------\n" +
                "Tổng tiền: %,.0f đ\n\n" +
                "Bạn muốn thanh toán?",
                billInfo.toString(),
                totalAmount
        );
        
        new AlertDialog.Builder(this)
                .setTitle("💳 Thanh Toán")
                .setMessage(billMessage)
                .setPositiveButton("Đồng Ý", (dialog, which) -> {
                    Log.d(TAG, "User confirmed payment");
                    processPayment();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Process payment and reset session
     */
    private void processPayment() {
        Log.d(TAG, "=== PROCESS PAYMENT START ===");
        Log.d(TAG, "SessionId: " + sessionId);
        
        if (sessionId == null || sessionId.isEmpty()) {
            Log.e(TAG, "SessionId is null or empty!");
            Toast.makeText(this, "Lỗi: Không tìm thấy phiên làm việc", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý thanh toán...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Call API to complete session payment
        ApiService.PaymentRequest request = new ApiService.PaymentRequest("cash");
        Log.d(TAG, "Calling completeSessionPayment API with sessionId: " + sessionId);
        
        apiService.completeSessionPayment(sessionId, request).enqueue(new Callback<ApiResponse<ApiService.PaymentResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiService.PaymentResponse>> call, 
                                 Response<ApiResponse<ApiService.PaymentResponse>> response) {
                progressDialog.dismiss();
                Log.d(TAG, "Payment API response code: " + response.code());
                
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<ApiService.PaymentResponse> apiResponse = response.body();
                        Log.d(TAG, "API response success: " + apiResponse.isSuccess());
                        Log.d(TAG, "API response message: " + apiResponse.getMessage());
                        
                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            ApiService.PaymentResponse paymentData = apiResponse.getData();
                            Log.d(TAG, "Payment successful! Total: " + paymentData.getTotalAmount());
                            
                            // Show success message
                            Toast.makeText(OrderStatusActivity.this, 
                                "✅ Nhân viên đang ra quý khách vui lòng chờ!", Toast.LENGTH_LONG).show();
                            
                            // Navigate back to table input screen
                            Intent intent = new Intent(OrderStatusActivity.this, TableInputActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Payment failed: " + apiResponse.getMessage());
                            Toast.makeText(OrderStatusActivity.this, 
                                "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Response not successful: " + response.code());
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "Error body: " + errorBody);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        Toast.makeText(OrderStatusActivity.this, 
                            "Không thể thanh toán (Lỗi: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception processing payment response", e);
                    Toast.makeText(OrderStatusActivity.this, 
                        "Lỗi xử lý phản hồi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiService.PaymentResponse>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "Payment API call failed", t);
                Toast.makeText(OrderStatusActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
        Log.d(TAG, "=== PROCESS PAYMENT END ===");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start polling when activity is visible
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop polling when activity is not visible
        pollingHandler.removeCallbacks(pollingRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollingHandler != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }
}
