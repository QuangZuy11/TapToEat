package vn.edu.fpt.taptoeat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private ImageButton btnRefresh;
    
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
        
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setSubtitle("Bàn " + tableNumber);
        
        btnRefresh.setOnClickListener(v -> loadOrders());
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
        
        apiService.getSessionOrders(sessionId).enqueue(new Callback<ApiResponse<List<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderResponse>>> call, 
                                 Response<ApiResponse<List<OrderResponse>>> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<OrderResponse>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<OrderResponse> orders = apiResponse.getData();
                        Log.d(TAG, "Loaded " + orders.size() + " orders");
                        
                        orderList.clear();
                        orderList.addAll(orders);
                        orderAdapter.updateOrders(orderList);
                        
                        // Show/hide empty state
                        if (orderList.isEmpty()) {
                            layoutEmptyOrders.setVisibility(View.VISIBLE);
                            recyclerViewOrders.setVisibility(View.GONE);
                        } else {
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
