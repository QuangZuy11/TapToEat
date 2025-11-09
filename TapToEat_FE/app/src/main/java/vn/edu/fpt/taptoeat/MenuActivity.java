package vn.edu.fpt.taptoeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.taptoeat.adapters.MenuPagerAdapter;
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.api.RetrofitClient;
import vn.edu.fpt.taptoeat.fragments.MenuItemsFragment;
import vn.edu.fpt.taptoeat.models.Category;
import vn.edu.fpt.taptoeat.utils.CartManager;

public class MenuActivity extends AppCompatActivity implements CartManager.CartChangeListener {

    private Toolbar toolbar;
    private TextView tvTableNumber;
    private TextView tvCartBadge;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;
    private ImageView ivCart;
    private View cartBadgeLayout;
    
    // Search and Filter
    private EditText etSearch;
    private EditText etPriceFrom;
    private EditText etPriceTo;
    private Button btnSearch;
    private Button btnClearFilter;

    private MenuPagerAdapter pagerAdapter;
    private List<Category> categories;

    private int tableNumber;
    private String sessionId;
    
    // Current search/filter state
    private String currentSearchQuery = "";
    private int currentPriceFrom = 0;
    private int currentPriceTo = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get data from intent
        tableNumber = getIntent().getIntExtra("TABLE_NUMBER", 0);
        sessionId = getIntent().getStringExtra("SESSION_ID");

        initViews();
        setupToolbar();
        setupSearchAndFilter();
        loadCategories();
        
        // Register cart change listener
        CartManager.getInstance().addCartChangeListener(this);
        updateCartBadge();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister cart change listener
        CartManager.getInstance().removeCartChangeListener(this);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTableNumber = findViewById(R.id.tvTableNumber);
        tvCartBadge = findViewById(R.id.tvCartBadge);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        progressBar = findViewById(R.id.progressBar);
        ivCart = findViewById(R.id.ivCart);
        ImageView ivOrderStatus = findViewById(R.id.ivOrderStatus);
        
        // Search and Filter views
        etSearch = findViewById(R.id.etSearch);
        etPriceFrom = findViewById(R.id.etPriceFrom);
        etPriceTo = findViewById(R.id.etPriceTo);
        btnSearch = findViewById(R.id.btnSearch);
        btnClearFilter = findViewById(R.id.btnClearFilter);

        tvTableNumber.setText("Bàn " + tableNumber);
        
        // Order status icon click - navigate to order status activity
        ivOrderStatus.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, OrderStatusActivity.class);
            intent.putExtra("tableNumber", tableNumber);
            intent.putExtra("sessionId", sessionId);
            startActivity(intent);
        });
        
        // Cart icon click - navigate to cart activity
        ivCart.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CartActivity.class);
            intent.putExtra("TABLE_NUMBER", tableNumber);
            intent.putExtra("SESSION_ID", sessionId);
            startActivity(intent);
        });
    }
    
    private void setupSearchAndFilter() {
        // Search button click
        btnSearch.setOnClickListener(v -> applyFilter());
        
        // Search on Enter key
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                applyFilter();
                return true;
            }
            return false;
        });
        
        // Clear filter button
        btnClearFilter.setOnClickListener(v -> clearFilter());
    }
    
    private void applyFilter() {
        currentSearchQuery = etSearch.getText().toString().trim();
        
        // Parse price range
        try {
            String priceFromStr = etPriceFrom.getText().toString().trim();
            currentPriceFrom = priceFromStr.isEmpty() ? 0 : Integer.parseInt(priceFromStr);
        } catch (NumberFormatException e) {
            currentPriceFrom = 0;
        }
        
        try {
            String priceToStr = etPriceTo.getText().toString().trim();
            currentPriceTo = priceToStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(priceToStr);
        } catch (NumberFormatException e) {
            currentPriceTo = Integer.MAX_VALUE;
        }
        
        // Apply filter to current fragment
        applyFilterToCurrentFragment();
        
        Toast.makeText(this, "Đã áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
    }
    
    private void clearFilter() {
        etSearch.setText("");
        etPriceFrom.setText("");
        etPriceTo.setText("");
        currentSearchQuery = "";
        currentPriceFrom = 0;
        currentPriceTo = Integer.MAX_VALUE;
        
        applyFilterToCurrentFragment();
        
        Toast.makeText(this, "Đã xóa bộ lọc", Toast.LENGTH_SHORT).show();
    }
    
    private void applyFilterToCurrentFragment() {
        if (pagerAdapter == null) return;
        
        int currentPosition = viewPager.getCurrentItem();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + currentPosition);
        
        if (fragment instanceof MenuItemsFragment) {
            ((MenuItemsFragment) fragment).applyFilter(currentSearchQuery, currentPriceFrom, currentPriceTo);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<ApiService.ApiResponse<List<Category>>> call = apiService.getCategories();

        call.enqueue(new Callback<ApiService.ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponse<List<Category>>> call,
                                   @NonNull Response<ApiService.ApiResponse<List<Category>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<List<Category>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        categories = apiResponse.getData();
                        
                        // Filter active categories only
                        List<Category> activeCategories = new ArrayList<>();
                        for (Category category : categories) {
                            if (category.isActive()) {
                                activeCategories.add(category);
                            }
                        }
                        categories = activeCategories;
                        
                        setupViewPager();
                    } else {
                        showError("Không thể tải danh mục");
                    }
                } else {
                    showError("Lỗi kết nối server");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponse<List<Category>>> call,
                                  @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupViewPager() {
        pagerAdapter = new MenuPagerAdapter(this, categories);
        viewPager.setAdapter(pagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(categories.get(position).getName());
        }).attach();
    }
    
    // Update cart badge count
    public void updateCartBadge() {
        runOnUiThread(() -> {
            int count = CartManager.getInstance().getTotalItemsCount();
            if (count > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        });
    }
    
    // Implement CartChangeListener
    @Override
    public void onCartChanged() {
        updateCartBadge();
    }
}
