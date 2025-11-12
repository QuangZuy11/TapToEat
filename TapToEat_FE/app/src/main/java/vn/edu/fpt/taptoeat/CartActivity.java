package vn.edu.fpt.taptoeat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.taptoeat.adapters.CartAdapter;
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.api.RetrofitClient;
import vn.edu.fpt.taptoeat.models.CartItem;
import vn.edu.fpt.taptoeat.utils.CartManager;

/**
 * Activity for displaying and managing shopping cart
 */
public class CartActivity extends AppCompatActivity implements CartManager.CartChangeListener {

    private static final String TAG = "CartActivity";
    
    private Toolbar toolbar;
    private RecyclerView recyclerViewCart;
    private LinearLayout layoutEmptyCart;
    private View layoutCartContent;
    private LinearLayout layoutBottomSummary;
    private TextView tvTotalItems;
    private TextView tvTotalPrice;
    private MaterialButton btnPlaceOrder;
    private MaterialButton btnBackToMenu;

    private CartAdapter adapter;
    private CartManager cartManager;

    private int tableNumber;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "CartActivity onCreate started");
            setContentView(R.layout.activity_cart);

            // Get data from intent
            tableNumber = getIntent().getIntExtra("TABLE_NUMBER", 0);
            sessionId = getIntent().getStringExtra("SESSION_ID");
            
            Log.d(TAG, "TableNumber: " + tableNumber + ", SessionId: " + sessionId);

            cartManager = CartManager.getInstance();

            initViews();
            setupToolbar();
            setupRecyclerView();
            updateUI();

            // Register cart change listener
            cartManager.addCartChangeListener(this);
            
            Log.d(TAG, "CartActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeCartChangeListener(this);
    }

    private void initViews() {
        Log.d(TAG, "Initializing views...");
        
        toolbar = findViewById(R.id.toolbar);
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutCartContent = findViewById(R.id.layoutCartContent);
        layoutBottomSummary = findViewById(R.id.layoutBottomSummary);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);

        // Check for null views
        if (btnBackToMenu == null) {
            Log.e(TAG, "btnBackToMenu is NULL!");
        }
        if (btnPlaceOrder == null) {
            Log.e(TAG, "btnPlaceOrder is NULL!");
        }

        // Back to menu button
        if (btnBackToMenu != null) {
            btnBackToMenu.setOnClickListener(v -> finish());
        }

        // Place order button
        if (btnPlaceOrder != null) {
            btnPlaceOrder.setOnClickListener(v -> showPlaceOrderConfirmation());
        }
        
        Log.d(TAG, "Views initialized successfully");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Giỏ Hàng");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartManager.getCartItems(), new CartAdapter.OnCartItemActionListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                if (newQuantity <= 0) {
                    // Show delete confirmation
                    showDeleteConfirmation(item);
                } else {
                    cartManager.updateQuantity(item.getMenuItem().get_id(), newQuantity);
                }
            }

            @Override
            public void onItemDeleted(CartItem item) {
                showDeleteConfirmation(item);
            }
        });
        recyclerViewCart.setAdapter(adapter);
    }

    private void updateUI() {
        List<CartItem> items = cartManager.getCartItems();

        if (items.isEmpty()) {
            // Show empty cart view
            layoutEmptyCart.setVisibility(View.VISIBLE);
            layoutCartContent.setVisibility(View.GONE);
            layoutBottomSummary.setVisibility(View.GONE);
        } else {
            // Show cart content
            layoutEmptyCart.setVisibility(View.GONE);
            layoutCartContent.setVisibility(View.VISIBLE);
            layoutBottomSummary.setVisibility(View.VISIBLE);

            // Update adapter
            adapter.updateData(items);

            // Update summary
            int totalItems = cartManager.getTotalItemsCount();
            tvTotalItems.setText(totalItems + " món");
            tvTotalPrice.setText(cartManager.getFormattedTotalPrice());
        }
    }

    private void showDeleteConfirmation(CartItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa món")
                .setMessage("Bạn có chắc muốn xóa \"" + item.getMenuItem().getName() + "\" khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    cartManager.removeItem(item.getMenuItem().get_id());
                    Toast.makeText(this, "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showPlaceOrderConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt món")
                .setMessage("Bạn có chắc muốn đặt " + cartManager.getTotalItemsCount() + 
                        " món với tổng tiền " + cartManager.getFormattedTotalPrice() + "?")
                .setPositiveButton("Đặt món", (dialog, which) -> {
                    placeOrder();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void placeOrder() {
        if (sessionId == null || sessionId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không có sessionId", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang gửi đơn hàng...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Prepare order request
        List<ApiService.OrderItemRequest> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartManager.getCartItems()) {
            ApiService.OrderItemRequest item = new ApiService.OrderItemRequest(
                    cartItem.getMenuItem().get_id(),
                    cartItem.getQuantity(),
                    cartItem.getNotes()
            );
            orderItems.add(item);
            Log.d(TAG, "Order item: " + cartItem.getMenuItem().getName() + 
                    " x" + cartItem.getQuantity() + 
                    (cartItem.getNotes() != null ? " (" + cartItem.getNotes() + ")" : ""));
        }

        ApiService.OrderRequest request = new ApiService.OrderRequest(sessionId, tableNumber, orderItems);
        
        Log.d(TAG, "Placing order with sessionId: " + sessionId + 
                ", tableNumber: " + tableNumber + ", items count: " + orderItems.size());

        // Call API
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<ApiService.ApiResponse<ApiService.OrderResponse>> call = apiService.placeOrder(request);

        call.enqueue(new Callback<ApiService.ApiResponse<ApiService.OrderResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponse<ApiService.OrderResponse>> call,
                                   @NonNull Response<ApiService.ApiResponse<ApiService.OrderResponse>> response) {
                progressDialog.dismiss();

                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response successful: " + response.isSuccessful());

                if (response.isSuccessful()) {
                    try {
                        ApiService.ApiResponse<ApiService.OrderResponse> apiResponse = response.body();
                        
                        if (apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null) {
                            ApiService.OrderResponse orderResponse = apiResponse.getData();
                            
                            Log.d(TAG, "Order placed successfully: " + orderResponse.getId());
                            
                            // Show success dialog
                            showOrderSuccessDialog(orderResponse);
                            
                            // Clear cart
                            cartManager.clearCart();
                        } else {
                            String errorMsg = apiResponse != null && apiResponse.getMessage() != null ? 
                                    apiResponse.getMessage() : "Đặt món thất bại";
                            Toast.makeText(CartActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(CartActivity.this, 
                                "Đặt món thành công nhưng không thể đọc thông tin đơn hàng", 
                                Toast.LENGTH_LONG).show();
                        // Clear cart anyway since order was successful
                        cartManager.clearCart();
                        finish();
                    }
                } else {
                    // Log error details
                    Log.e(TAG, "Order failed with code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            Toast.makeText(CartActivity.this, 
                                    "Lỗi " + response.code() + ": " + errorBody, 
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CartActivity.this, 
                                    "Lỗi server: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                        Toast.makeText(CartActivity.this, 
                                "Lỗi server: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponse<ApiService.OrderResponse>> call,
                                  @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "Place order failed", t);
                Toast.makeText(CartActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showOrderSuccessDialog(ApiService.OrderResponse orderResponse) {
        String orderId = orderResponse.getOrderNumber() != null ? 
                orderResponse.getOrderNumber() : 
                orderResponse.getId().substring(Math.max(0, orderResponse.getId().length() - 6));
        
        String message = String.format(
                "Đơn hàng: %s\n" +
                "Trạng thái: %s\n" +
                "Tổng tiền: %,.0f đ\n\n" +
                "Món ăn đang được chuẩn bị!",
                orderId,
                getStatusText(orderResponse.getStatus()),
                orderResponse.getTotalAmount()
        );

        new AlertDialog.Builder(this)
                .setTitle("✅ Đặt món thành công!")
                .setMessage(message)
                .setPositiveButton("Xem trạng thái", (dialog, which) -> {
                    // Navigate to OrderStatusActivity
                    Intent intent = new Intent(CartActivity.this, OrderStatusActivity.class);
                    intent.putExtra("sessionId", sessionId);
                    intent.putExtra("tableNumber", tableNumber);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ xử lý";
            case "preparing": return "Đang chuẩn bị";
            case "ready": return "Đã sẵn sàng";
            case "served": return "Đã phục vụ";
            default: return status;
        }
    }

    // Implement CartChangeListener
    @Override
    public void onCartChanged() {
        runOnUiThread(this::updateUI);
    }
}
