package edu.uiuc.cs427app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

/**
 * the DetailsActivity class provides detailed information about a selected city,
 * including weather and map options, and allows navigation to other related activities
 */
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private DatabaseHelper dbHelper;
    private String username;

    /**
     * called when the DetailsActivity is created.
     * initializes the UI elements, applies the user's theme, and sets up click listeners for buttons
     *
     * @param savedInstanceState saved instance state from the previous configuration, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity using the 'activity_details' XML file
        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        // Apply the user's selected theme
        applyUserTheme();

        // Set the layout for DetailsActivity
        setContentView(R.layout.activity_details);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        String cityName = getIntent().getStringExtra("city").toString();
        String welcome = "Welcome to the "+cityName;
        String cityWeatherInfo = "Detailed information about the weather of "+cityName;

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        // Set the text for the TextView components to display the welcome and weather information
        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);
        // Get the weather information from a Service that connects to a weather server and show the results

        // Initialize the button for viewing the map and set its click listener
        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(this); // Set this class to handle the click event

        Button weatherButton = findViewById(R.id.weatherButton);
        weatherButton.setOnClickListener(this); // Set this class to handle the click event
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

        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theme", themeResId);
        editor.apply();
        setTheme(themeResId);
    }

    /**
     * handles click events for buttons in the activity
     * navigates to the appropriate activity based on the button clicked
     *
     * @param view The view that was clicked
     */
    @Override
    public void onClick(View view) {

        // Declare an Intent for handling navigation between activities
        Intent intent;
        switch (view.getId()) {
            // if the user wishes to see the city, we change the screen
            case R.id.mapButton:
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("city", getIntent().getStringExtra("city"));
                startActivity(intent);
                break;
                // if the user wishes to see the city, we change the screen
            case R.id.weatherButton:
                intent = new Intent(this, WeatherActivity.class);
                intent.putExtra("city", getIntent().getStringExtra("city"));
                startActivity(intent);
                break;
        }
    }
}

