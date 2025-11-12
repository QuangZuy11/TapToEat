package vn.edu.fpt.taptoeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.api.RetrofitClient;

public class TableInputActivity extends AppCompatActivity {

    private TextInputLayout tilTableNumber;
    private TextInputEditText etTableNumber;
    private Button btnStart;
    private Button btnChef;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_input);

        // Initialize views
        initViews();

        // Set up button click listeners
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStartButton();
            }
        });
        
        btnChef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Chef Dashboard
                Intent intent = new Intent(TableInputActivity.this, ChefDashboardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        tilTableNumber = findViewById(R.id.tilTableNumber);
        etTableNumber = findViewById(R.id.etTableNumber);
        btnStart = findViewById(R.id.btnStart);
        btnChef = findViewById(R.id.btnChef);
        tvError = findViewById(R.id.tvError);
    }

    private void handleStartButton() {
        // Hide previous error
        tvError.setVisibility(View.GONE);
        tilTableNumber.setError(null);

        // Get table number input
        String tableNumberStr = etTableNumber.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(tableNumberStr)) {
            showError(getString(R.string.error_empty_table));
            return;
        }

        try {
            int tableNumber = Integer.parseInt(tableNumberStr);

            if (tableNumber <= 0) {
                showError(getString(R.string.error_invalid_table));
                return;
            }

            // TODO: Call API to verify table exists and create/get session
            // For now, we'll simulate success
            verifyTableAndStartSession(tableNumber);

        } catch (NumberFormatException e) {
            showError(getString(R.string.error_invalid_table));
        }
    }

    private void verifyTableAndStartSession(int tableNumber) {
        btnStart.setEnabled(false);
        btnStart.setText(R.string.loading);

        // Step 1: Verify table exists
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<ApiService.ApiResponse<ApiService.TableInfo>> verifyCall = 
                apiService.getTableInfo(tableNumber);

        verifyCall.enqueue(new Callback<ApiService.ApiResponse<ApiService.TableInfo>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponse<ApiService.TableInfo>> call,
                                   @NonNull Response<ApiService.ApiResponse<ApiService.TableInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<ApiService.TableInfo> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        ApiService.TableInfo tableInfo = apiResponse.getData();
                        
                        // Check table status - allow both available and occupied
                        // If occupied, customer can view order status and existing session
                        if ("available".equals(tableInfo.getStatus()) || "occupied".equals(tableInfo.getStatus())) {
                            // Step 2: Create or get existing session
                            createSession(tableNumber);
                        } else {
                            onTableVerificationError("Bàn " + tableNumber + " không hợp lệ");
                        }
                    } else {
                        onTableVerificationError("Bàn không tồn tại");
                    }
                } else {
                    onTableVerificationError("Không thể kết nối đến server");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponse<ApiService.TableInfo>> call,
                                  @NonNull Throwable t) {
                onTableVerificationError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void createSession(int tableNumber) {
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        ApiService.SessionRequest request = new ApiService.SessionRequest(tableNumber);
        Call<ApiService.ApiResponse<ApiService.SessionInfo>> sessionCall = 
                apiService.createSession(request);

        sessionCall.enqueue(new Callback<ApiService.ApiResponse<ApiService.SessionInfo>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponse<ApiService.SessionInfo>> call,
                                   @NonNull Response<ApiService.ApiResponse<ApiService.SessionInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<ApiService.SessionInfo> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        ApiService.SessionInfo sessionInfo = apiResponse.getData();
                        onTableVerificationSuccess(tableNumber, sessionInfo.getId());
                    } else {
                        onTableVerificationError("Không thể tạo phiên làm việc");
                    }
                } else {
                    onTableVerificationError("Lỗi tạo phiên làm việc");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponse<ApiService.SessionInfo>> call,
                                  @NonNull Throwable t) {
                onTableVerificationError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void onTableVerificationSuccess(int tableNumber, String sessionId) {
        Toast.makeText(this, "Chào mừng đến bàn " + tableNumber, Toast.LENGTH_SHORT).show();

        // Navigate to Menu Activity
        Intent intent = new Intent(TableInputActivity.this, MenuActivity.class);
        intent.putExtra("TABLE_NUMBER", tableNumber);
        intent.putExtra("SESSION_ID", sessionId);
        startActivity(intent);
        finish();
    }

    private void onTableVerificationError(String errorMessage) {
        btnStart.setEnabled(true);
        btnStart.setText(R.string.start_button);
        showError(errorMessage);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        tilTableNumber.setError(" "); // Show error state on input
    }
}
