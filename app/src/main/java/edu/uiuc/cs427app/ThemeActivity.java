package edu.uiuc.cs427app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper dbHelper;
    private String username;

    /**
     * Called when the activity is starting. Initializes the DatabaseHelper and retrieves the
     * username from SharedPreferences. Applies the user's theme preference if available and 
     * sets the content view for the theme selection screen. Also, sets up click listeners 
     * for theme selection buttons.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        

        // Initialize DatabaseHelper and SharedPreferences
        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        applyUserTheme();

        setContentView(R.layout.activity_themes);


        Button buttonTheme1 = findViewById(R.id.buttonTheme1);
        Button buttonTheme2 = findViewById(R.id.buttonTheme2);
        Button buttonTheme3 = findViewById(R.id.buttonTheme3);

        // Set listeners for theme selection
        buttonTheme1.setOnClickListener(this);
        buttonTheme2.setOnClickListener(this);
        buttonTheme3.setOnClickListener(this);
    }
    

    /**
     * Applies the user's preferred theme if it exists which defaults to Theme.MyFirstApp.
     * Retrieves the theme from the database using the username, gets the resource ID,
     * and applies it to the activity.
     */
    private void applyUserTheme() {
        String themePreference = dbHelper.getUserTheme(username);

        if (themePreference == null) {
            themePreference = "Theme.MyFirstApp";
        }

        int themeResId = getResources().getIdentifier(themePreference, "style", getPackageName());
        setTheme(themeResId);
    }

    /**
     * Handles click events for the theme buttons.
     * Sets the selected theme based on the button clicked, saves it in the database,
     * and immediately applies the selected theme. Then, restarts MainActivity to apply
     * the theme changes.
     */
    @Override
    public void onClick(View view) {
        String selectedTheme = "";

        switch (view.getId()) {
            case R.id.buttonTheme1:
                selectedTheme = "Theme.MyFirstApp";
                break;
            case R.id.buttonTheme2:
                selectedTheme = "Theme.OrangeGreenTheme";
                break;
            case R.id.buttonTheme3:
                selectedTheme = "Theme.BlueRedTheme";
                break;
        }

        // Save the selected theme in the database for the current user
        dbHelper.updateUserTheme(username, selectedTheme);

        // Apply the selected theme immediately
        applyTheme(selectedTheme);

        // Return to MainActivity with updated theme
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    
    /**
     * Applies the selected theme by saving it in SharedPreferences and setting the theme.
     * Updates SharedPreferences with the new theme, retrieves its resource ID, and applies it.
     */
    private void applyTheme(String theme) {
        // Save theme in SharedPreferences for easy retrieval
        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selected_theme", theme);
        editor.apply();
    
        // Apply the theme
        int themeResId = getResources().getIdentifier(theme, "style", getPackageName());
        setTheme(themeResId);
    }

}
