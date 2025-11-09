package vn.edu.fpt.taptoeat.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.taptoeat.R;
import vn.edu.fpt.taptoeat.adapters.MenuItemAdapter;
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.api.RetrofitClient;
import vn.edu.fpt.taptoeat.models.CategoryItemsResponse;
import vn.edu.fpt.taptoeat.models.MenuItem;

public class MenuItemsFragment extends Fragment {

    private static final String TAG = "MenuItemsFragment";
    private static final String ARG_CATEGORY_ID = "category_id";

    private RecyclerView recyclerView;
    private MenuItemAdapter adapter;
    private List<MenuItem> menuItems;
    private List<MenuItem> allMenuItems; // Store all items for filtering
    private String categoryId;
    
    // Filter state
    private String searchQuery = "";
    private int priceFrom = 0;
    private int priceTo = Integer.MAX_VALUE;

    public static MenuItemsFragment newInstance(String categoryId) {
        MenuItemsFragment fragment = new MenuItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString(ARG_CATEGORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewMenuItems);
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load data when fragment resumes and not loaded yet
        if (allMenuItems == null) {
            loadMenuItems();
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MenuItemAdapter(new ArrayList<>(), menuItem -> {
            // TODO: Handle item click - show detail or add to cart
            // Toast.makeText(getContext(), "Clicked: " + menuItem.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadMenuItems() {
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "Fragment not attached, skipping load");
            return; // Fragment not attached yet
        }
        
        Log.d(TAG, "Loading menu items for category: " + categoryId);
        
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<ApiService.ApiResponse<CategoryItemsResponse>> call = apiService.getCategoryItems(categoryId);

        call.enqueue(new Callback<ApiService.ApiResponse<CategoryItemsResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponse<CategoryItemsResponse>> call,
                                   @NonNull Response<ApiService.ApiResponse<CategoryItemsResponse>> response) {
                if (!isAdded()) {
                    Log.w(TAG, "Fragment detached after response");
                    return; // Check again after callback
                }
                
                Log.d(TAG, "Response received: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<CategoryItemsResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        CategoryItemsResponse data = apiResponse.getData();
                        allMenuItems = data.getItems();
                        Log.d(TAG, "Loaded " + (allMenuItems != null ? allMenuItems.size() : 0) + " items");
                        
                        // Apply initial filter (available items only)
                        applyFilter(searchQuery, priceFrom, priceTo);
                    } else {
                        Log.e(TAG, "API response not successful: " + apiResponse.getMessage());
                        showError("Không thể tải món ăn");
                    }
                } else {
                    Log.e(TAG, "Response error: " + response.code());
                    showError("Lỗi kết nối server");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponse<CategoryItemsResponse>> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "API call failed", t);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }
    
    public void applyFilter(String query, int priceFrom, int priceTo) {
        if (allMenuItems == null) {
            Log.w(TAG, "allMenuItems is null, cannot filter");
            return;
        }
        
        Log.d(TAG, "Applying filter - query: '" + query + "', price: " + priceFrom + "-" + priceTo);
        
        this.searchQuery = query;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        
        List<MenuItem> filteredItems = new ArrayList<>();
        
        for (MenuItem item : allMenuItems) {
            // Filter: must be available
            if (!item.isAvailable()) {
                continue;
            }
            
            // Filter: search query (name contains)
            if (!query.isEmpty() && !item.getName().toLowerCase().contains(query.toLowerCase())) {
                continue;
            }
            
            // Filter: price range
            if (item.getPrice() < priceFrom || item.getPrice() > priceTo) {
                continue;
            }
            
            filteredItems.add(item);
        }
        
        Log.d(TAG, "Filtered items count: " + filteredItems.size());
        
        menuItems = filteredItems;
        
        if (adapter != null) {
            adapter.updateData(filteredItems);
        } else {
            Log.w(TAG, "Adapter is null");
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
