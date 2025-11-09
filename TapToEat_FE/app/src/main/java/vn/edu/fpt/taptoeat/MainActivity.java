package vn.edu.fpt.taptoeat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import vn.edu.fpt.taptoeat.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if there's an active session
        if (sessionManager.hasActiveSession()) {
            // TODO: Navigate to Menu Activity with existing session
            // For now, go to table input
            navigateToTableInput();
        } else {
            // No active session, go to table input
            navigateToTableInput();
        }
    }

    private void navigateToTableInput() {
        Intent intent = new Intent(MainActivity.this, TableInputActivity.class);
        startActivity(intent);
        finish();
    }
}