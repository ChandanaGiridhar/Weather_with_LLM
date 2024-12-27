package edu.uiuc.cs427app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import edu.uiuc.cs427app.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Configuration for navigation components (used for app bar and navigation)
    private AppBarConfiguration appBarConfiguration;

    // Binding object to easily access views in activity_main.xml
    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;
    private String username;

    /**
     * Called when the MainActivity is created.
     * onCreate() method sets up the main activity interface,
     * connects UI elements, and sets up button click listeners for adding
     * and removing cities
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize SharedPreferences and DatabaseHelper
        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        
        dbHelper = new DatabaseHelper(this);

        // Apply the user-specific theme before setting the content view
        applyUserTheme();

        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_main);

        // add user cities
        fetchUserCities();


        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically
        Button buttonNew = findViewById(R.id.buttonAddLocation);
        Button buttonLogout = findViewById(R.id.buttonLogout);
        Button buttonChangeTheme = findViewById(R.id.buttonChangeTheme);

        TextView textViewUsername = findViewById(R.id.textViewUsername);
        Button buttonRemove = findViewById(R.id.buttonRemoveLocation);

        // Set click listeners for buttons to handle user interactions
        buttonNew.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        buttonChangeTheme.setOnClickListener(this);
        buttonRemove.setOnClickListener(this);

        String username = sharedPreferences.getString("username", null);

        // Display the retrieved username in the TextView
        textViewUsername.setText(username);
    }

    /**
     * Applies the user's preferred theme if it exists which defaults to Theme.MyFirstApp.
     * Retrieves the theme from the database using the username, gets the resource ID,
     * and applies it to the activity.
     */
    private void applyUserTheme() {
        // Get the theme from the database or SharedPreferences
        String themePreference = dbHelper.getUserTheme(username);
        
        // Fallback to a default theme if no preference is found
        if (themePreference == null) {
            themePreference = "Theme.MyFirstApp";
        }
        
        // Get the resource ID of the theme and apply it
        int themeResId = getResources().getIdentifier(themePreference, "style", getPackageName());
        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theme", themeResId);
        editor.apply();
        setTheme(themeResId);
    }

    /**
     * Fetches and displays the cities associated with the current user.
     * Retrieves the list of cities from the database, processes the data
     * by removing any unwanted characters, and renders them in the UI.
     *
     * @return void
     */
    private void fetchUserCities() {
        // Get the theme from the database or SharedPreferences
        String userCitiesRaw  = dbHelper.getCities(username);

        // Fallback to a default theme if no preference is found
        if (userCitiesRaw == null) {
            return;
        }

        // Remove any leading comma from the cities string if present
        if (userCitiesRaw.startsWith(",")){
            userCitiesRaw = userCitiesRaw.substring(1);
        }

        // If the string is empty after cleanup, exit the method early
        if (userCitiesRaw.isEmpty()){
            return;
        }

        // Split the string of cities by comma into a list
        List<String> userCities = Arrays.asList(userCitiesRaw.split(","));
        // Convert the list to an array for further processing
        String[] citiesList = userCities.toArray(new String[0]);

        // Call a method to display the cities on the screen
        renderCities(citiesList);
    }

    /**
     * Handles click events for various buttons in the view.
     * Depending on the button clicked, it performs actions like logging out,
     * changing the theme, adding/removing a location, or showing details for a city.
     *
     * @param view The view that was clicked.
     */

    @Override
    public void onClick(View view) {
        Intent intent; // Declare an Intent for handling navigation between activities
        switch (view.getId()) {
            case R.id.buttonLogout:
                // Clear user session and navigate back to LoginActivity
                clearUserSessionOnLogout(); // Clears stored session data
                intent = new Intent(this, LoginActivity.class);  // Start the LoginActivity
                startActivity(intent);
                break;
            case R.id.buttonChangeTheme:
                // Navigate to ThemeActivity to allow the user to change the app theme
                intent = new Intent(this, ThemeActivity.class);  // Start the ThemeActivity
                startActivity(intent);
                break;
            case R.id.buttonAddLocation:
                showAddLocationDialog();  // Implement this action to add a new location to the list of locations
                break;
            case R.id.buttonRemoveLocation:
                showRemoveLocationDialog();
                break;
            default:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", (String)view.getTag());
                startActivity(intent);
                break;
        }
    }

    /**
     * Displays a dialog allowing the user to input the name of a city to remove from the list.
     */
    private void showRemoveLocationDialog() {
        // Create an AlertDialog builder to set up the dialog's properties
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Location");

        // Set up an EditText field for user input (the city to remove)
        final EditText input = new EditText(this);
        input.setHint("City Name");
        builder.setView(input);  // Add the input field to the dialog

        // Set up the "Remove" button with a click listener
        builder.setPositiveButton("Remove", (dialog, which) -> {
            String newLocation = input.getText().toString();   // Get the inputted city name
            if (!newLocation.isEmpty()) {
                removeCityView(newLocation);
            } else {
                Toast.makeText(MainActivity.this, "Location name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        // Set up the "Cancel" button to dismiss the dialog without action
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Displays a dialog that allows the user to input the name of a new location to add to the list.
     */
    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Location");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("City Name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newLocation = input.getText().toString();
            if (!newLocation.isEmpty()) {
                addLocation(newLocation); // Add the new location to the list
            } else {
                Toast.makeText(MainActivity.this, "Location name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Adds a new location for the current user to the database and displays it in the list.
     *
     * @param location The name of the location to add.
     */
    private void addLocation(String location) {
        // Add the location to the database for "testUser"
        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        boolean hasAdded = dbHelper.addCity(username, location);
        if (!hasAdded){
            Toast.makeText(MainActivity.this, "City already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] citiesArr = new String[]{ location };
        // Display each city in the list
        renderCities(citiesArr);

        Toast.makeText(this, "Added: " + location, Toast.LENGTH_SHORT).show();
    }


    /**
     * Dynamically renders each city in the provided array by adding it to the layout.
     *
     * @param citiesArr Array of city names to be displayed in the layout.
     */
    public void renderCities(String[] citiesArr) {
        for (String city : citiesArr) {
            if (!city.isEmpty() || !city.startsWith("null")) {
                addCity(city);  // Dynamically add each city to the layout
            }
        }
    }

    /**
    * Will remove a city from the database and handle the UI display
    * of a removed city
    */
    private void removeCityView(String location) {
        LinearLayout bigLayout = findViewById(R.id.cityInformation);
        // Iterate through all child views to find the one that matches the city name
        for (int i = 0; i < bigLayout.getChildCount(); i++) {
            View cityLayout = bigLayout.getChildAt(i);
            if (cityLayout instanceof LinearLayout) {
                LinearLayout cityLinearLayout = (LinearLayout) cityLayout;
                TextView cityTextView = (TextView) cityLinearLayout.getChildAt(0);
                if (cityTextView != null && cityTextView.getText().toString().equals(location)) {
                    dbHelper.removeCity(username,cityTextView.getText().toString());
                    bigLayout.removeView(cityLayout);
                    break;
                }
                }
            }
    }

    /**
     * Adds a new city to the layout and updates the user's selected city in shared preferences.
     *
     * @param location The name of the city to be added to the layout and saved in shared preferences.
     */
    private void addCity(String location){
        // Retrieve shared preferences for user info and store the selected city
        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("citySelected", location);
        editor.apply();
        // Apply the current theme based on stored preferences, defaulting to theme ID 0 if not set
        setTheme(sharedPreferences.getInt("theme", 0));

        // Locate the main layout where city information will be displayed
        LinearLayout bigLayout = findViewById(R.id.cityInformation);
        // Create a new layout for the city entry
        LinearLayout newLayout = new LinearLayout(this);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Use weight to distribute space
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(location);
        newLayout.addView(textView);
        newLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a button for showing more details about the city
        Button linearButton = new Button(this);

        linearButton.setOnClickListener(this);
        linearButton.setText("Show Details");
        linearButton.setTag(location);
        linearButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Use weight to distribute space
        newLayout.addView(linearButton);

        // Add the complete city entry layout to the main layout
        bigLayout.addView(newLayout);

    }

    /**
     * Clears the user session data stored in SharedPreferences to ensure a proper logout.
     */
    public void clearUserSessionOnLogout() {
        // Access the shared preferences where user data is stored
        SharedPreferences sharedPreferences = getSharedPreferences("edu.uiuc.cs427app.userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all data stored in the preferences
        editor.clear();
        // Apply the changes
        editor.apply();
    }
}

