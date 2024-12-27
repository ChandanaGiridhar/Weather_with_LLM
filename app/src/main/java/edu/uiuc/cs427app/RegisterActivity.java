package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

/**
 * RegisterActivity handles the user registration process.
 * It allows users to enter a username, email, and password,
 * checks if the username or email already exists, and registers the user if valid.
 */
public class RegisterActivity extends AppCompatActivity {
    // Declare member variables for database interaction and UI components
    private DatabaseHelper dbHelper;
    private EditText usernameEditText, emailEditText, passwordEditText, retypePasswordEditText;
    private Button registerButton, backToLoginButton;
    public String errorHandler;

    /**
     * Called when the RegisterActivity is created.
     * onCreate() method sets up the user interface, initializes necessary components,
     * connects UI elements, and sets up button click listeners for registration
     * and navigation back to the login page.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout resource to be used by this activity
        setContentView(R.layout.activity_register);

        // Initialize the DatabaseHelper instance
        dbHelper = new DatabaseHelper(this);

        // Connect UI elements from the layout to the code
        usernameEditText = findViewById(R.id.et_username);
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        retypePasswordEditText = findViewById(R.id.et_retype_password);
        registerButton = findViewById(R.id.btn_register);
        backToLoginButton = findViewById(R.id.btn_back_to_login);

        // Set click listener for the registration button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input from text fields and trim any leading/trailing spaces
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String retypePassword = retypePasswordEditText.getText().toString().trim();
                
                // Validate input fields
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    errorHandler = "All fields not filled";
                }
                // Check if the password meets specific requirements (length, uppercase letter, and digit)
                else if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                    // Display an alert dialog if password requirements are not met
                    String message = "Password must:\n" +
                            "• Be at least 8 characters long\n" +
                            "• Contain at least one uppercase letter\n" +
                            "• Contain at least one digit";
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Password Requirements")
                            .setMessage(message)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();             }
                // Check if the password and retype password match
                else if (!password.equals(retypePassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the username or email already exists in the database
                    boolean isUserExist = dbHelper.checkUserExistence(username, email);
                    if (isUserExist) {
                        // Display a message if the username or email is already registered
                        Toast.makeText(RegisterActivity.this, "Username or email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add the user to the database
                        boolean isUserAdded = dbHelper.addUser(username, email, password);
                        if (isUserAdded) {
                            // Show success message and navigate to LoginActivity on successful registration
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            // Navigate to LoginActivity after successful registration
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            // Finish the current activity to prevent returning to it
                            finish();
                        } else {
                            // Display an error message if registration fails
                            Toast.makeText(RegisterActivity.this, "Registration failed, try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // Set an OnClickListener for the back to login button to navigate back to the LoginActivity
        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                // Finish the current activity to remove it from the back stack
                finish();
            }
        });
    }

    public String getErrorHandler() {
        return errorHandler;
    }
}
