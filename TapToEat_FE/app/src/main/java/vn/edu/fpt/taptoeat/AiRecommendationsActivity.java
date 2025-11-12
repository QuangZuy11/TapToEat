package vn.edu.fpt.taptoeat;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.fpt.taptoeat.adapters.RecommendationsAdapter;
import vn.edu.fpt.taptoeat.api.ApiService;
import vn.edu.fpt.taptoeat.api.RetrofitClient;
import vn.edu.fpt.taptoeat.models.MenuItem;

public class AiRecommendationsActivity extends AppCompatActivity {

    private TextView tvWeatherIcon;
    private TextView tvWeatherDesc;
    private TextView tvWeatherDetail;
    private RecyclerView rvRecommendations;
    private ProgressBar progressBar;

    private RecommendationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_recommendations);

    tvWeatherIcon = findViewById(R.id.tvWeatherIcon);
    tvWeatherDesc = findViewById(R.id.tvWeatherDescription);
    tvWeatherDetail = findViewById(R.id.tvHumidity);
    rvRecommendations = findViewById(R.id.recyclerViewRecommendations);
        progressBar = findViewById(R.id.progressBar);

        rvRecommendations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecommendationsAdapter(new ArrayList<>());
        rvRecommendations.setAdapter(adapter);

        loadRecommendations();
    }

    private void loadRecommendations() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<ApiService.ApiResponse<ApiService.AIRecommendationResponse>> call = apiService.getAIRecommendations();

        call.enqueue(new Callback<ApiService.ApiResponse<ApiService.AIRecommendationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.ApiResponse<ApiService.AIRecommendationResponse>> call, @NonNull Response<ApiService.ApiResponse<ApiService.AIRecommendationResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<ApiService.AIRecommendationResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        ApiService.AIRecommendationResponse data = apiResponse.getData();
                        if (data.getWeather() != null) {
                            tvWeatherIcon.setText(data.getWeather().getIcon() != null ? data.getWeather().getIcon() : "");
                            tvWeatherDesc.setText(data.getWeather().getDescription() + " - " + data.getWeather().getTemperature() + "°C");
                            tvWeatherDetail.setText("Độ ẩm: " + data.getWeather().getHumidity() + "%");
                        }

                        if (data.getRecommendations() != null) {
                            adapter.updateData(data.getRecommendations());
                        }
                    } else {
                        showError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Không có dữ liệu");
                    }
                } else {
                    showError("Lỗi kết nối server");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.ApiResponse<ApiService.AIRecommendationResponse>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
