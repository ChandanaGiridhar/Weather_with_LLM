package edu.uiuc.cs427app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.content.Context;

public class LoginActivity extends AppCompatActivity {
    // Declare member variables for database interaction and UI components
    private DatabaseHelper dbHelper;
    private EditText usernameOrEmailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;

    /**
     * Called when the LoginActivity is created.
     * onCreate() method sets up the user interface, initializes the DatabaseHelper,
     * connects UI elements, and sets up button click listeners for user login
     * and navigation to the registration page.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout resource to be used by this activity
        setContentView(R.layout.activity_login);

        // Initialize the DatabaseHelper instance
        dbHelper = new DatabaseHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        dbHelper.onUpgrade(db, -1, -1);

        // Connect UI elements from the layout to the code
        usernameOrEmailEditText = findViewById(R.id.et_login_username_or_email);
        passwordEditText = findViewById(R.id.et_login_password);
        loginButton = findViewById(R.id.btn_login);
        registerTextView = findViewById(R.id.tv_register);

        // Set click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input and trim any leading/trailing spaces
                String usernameOrEmail = usernameOrEmailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate input fields
                if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Verify the credentials using the DatabaseHelper method
                    String username = dbHelper.validateUserCredentials(usernameOrEmail, password);
                    if (username != null) {
                        // Display success message if credentials are valid
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // Navigate to the main activity or dashboard after successful login
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        // Store the username in SharedPreferences to persist user data
                        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.apply();

                        // Cl   ose LoginActivity so it's removed from the back stack
                        finish();
                    } else {
                        // Show error message if credentials are invalid
                        Toast.makeText(LoginActivity.this, "Invalid username or email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set an OnClickListener for the register link to navigate to the registration screen
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
