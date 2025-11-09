package vn.edu.fpt.taptoeat;

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

public class TableInputActivity extends AppCompatActivity {

    private TextInputLayout tilTableNumber;
    private TextInputEditText etTableNumber;
    private Button btnStart;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_input);

        // Initialize views
        initViews();

        // Set up button click listener
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStartButton();
            }
        });
    }

    private void initViews() {
        tilTableNumber = findViewById(R.id.tilTableNumber);
        etTableNumber = findViewById(R.id.etTableNumber);
        btnStart = findViewById(R.id.btnStart);
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
        // TODO: Replace this with actual API call
        // Simulating API call with a delay
        btnStart.setEnabled(false);
        btnStart.setText(R.string.loading);

        // Simulate network delay
        btnStart.postDelayed(new Runnable() {
            @Override
            public void run() {
                // For now, accept any table number
                // In real implementation, check if table exists in backend
                onTableVerificationSuccess(tableNumber);
            }
        }, 1000);
    }

    private void onTableVerificationSuccess(int tableNumber) {
        Toast.makeText(this, "Chào mừng đến bàn " + tableNumber, Toast.LENGTH_SHORT).show();

        // TODO: Navigate to Menu Activity
        // Intent intent = new Intent(TableInputActivity.this, MenuActivity.class);
        // intent.putExtra("TABLE_NUMBER", tableNumber);
        // intent.putExtra("SESSION_ID", sessionId); // Get from API response
        // startActivity(intent);
        // finish();

        // For now, just reset the button
        btnStart.setEnabled(true);
        btnStart.setText(R.string.start_button);
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
