package edu.uiuc.cs427app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 The MapActivity class is responsible for displaying city information
 */
public class MapActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * Called when the MapActivity is created.
     * This method sets up the layout, retrieves city details, fetches geographical coordinates,
     * and displays an embedded map view for the specified city.
     *
     * @param savedInstanceState Saved state from a previous instance of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the city name to display
        String cityName = getIntent().getStringExtra("city");

        // store lat and long
        double longitude = -1d;
        double latitude = -1d;
        List<Address> addressList = new ArrayList<>();

        // Set the layout for map activity
        setContentView(R.layout.activity_map);

        // get lat and long information and show message if not found
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addressList = geocoder.getFromLocationName(cityName, 1);
            longitude = addressList.get(0).getLongitude();
            latitude = addressList.get(0).getLatitude();
        } catch (IOException e) {
            Toast.makeText(MapActivity.this, "Cannot find longitude / latitude for location", Toast.LENGTH_SHORT).show();
        }

        // get text view to add basic information such as city name and long/lat
        TextView viewMessage = findViewById(R.id.mapViewText);
        String displayString = String.format("Explore %s!\n\n\nLongitude: %s, Latitude %s", cityName, longitude , latitude);
        viewMessage.setText(displayString);

        // Display city name
        TextView cityNameTextView = findViewById(R.id.cityNameTextView); // New TextView for city name
        cityNameTextView.setText(String.format("City: %s", cityName));


        // create a web view and format the url accordingly
        WebView webView = findViewById(R.id.mapview);
        webView.getSettings().setJavaScriptEnabled(true);
        String url = String.format("https://www.google.com/maps/place/\"%s\"", cityName);
        webView.loadUrl(url);

    }

    /**
     * Handles click events for the activity.
     * Since there are no clickable buttons or actions to handle in this activity,
     * this method remains unimplemented.
     *
     * @param view The view that was clicked (not used here).
     */
    @Override
    public void onClick(View view) {
        // Not needed since there are no actions besides interacting with the view
    }
}