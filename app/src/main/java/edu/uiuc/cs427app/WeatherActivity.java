package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * The WeatherActivity class fetches and displays weather information for a specified city.
 * It retrieves weather data from the OpenWeatherMap API and provides the option to navigate
 * to a detailed insights activity.
 */
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private String cityName;
    private TextView cityTextView, dateTextView, tempTextView, weatherDescTextView, humidityTextView, windTextView;
    private ImageView weatherIconImageView;
    private Bundle llmBundle;
    private static final Map<String, Integer> ICON_MAP = new HashMap<>();

    // Map for retrieving drawable icon based on string name
    static {
        ICON_MAP.put("w_icon_01d", R.drawable.w_icon_01d);
        ICON_MAP.put("w_icon_01n", R.drawable.w_icon_01n);
        ICON_MAP.put("w_icon_02d", R.drawable.w_icon_02d);
        ICON_MAP.put("w_icon_02n", R.drawable.w_icon_02n);
        ICON_MAP.put("w_icon_03d", R.drawable.w_icon_03d);
        ICON_MAP.put("w_icon_03n", R.drawable.w_icon_03n);
        ICON_MAP.put("w_icon_04d", R.drawable.w_icon_04d);
        ICON_MAP.put("w_icon_04n", R.drawable.w_icon_04n);
        ICON_MAP.put("w_icon_09d", R.drawable.w_icon_09d);
        ICON_MAP.put("w_icon_09n", R.drawable.w_icon_09n);
        ICON_MAP.put("w_icon_10d", R.drawable.w_icon_10d);
        ICON_MAP.put("w_icon_10n", R.drawable.w_icon_10n);
        ICON_MAP.put("w_icon_11d", R.drawable.w_icon_11d);
        ICON_MAP.put("w_icon_11n", R.drawable.w_icon_11n);
        ICON_MAP.put("w_icon_13d", R.drawable.w_icon_13d);
        ICON_MAP.put("w_icon_13n", R.drawable.w_icon_13n);
        ICON_MAP.put("w_icon_50d", R.drawable.w_icon_50d);
        ICON_MAP.put("w_icon_50n", R.drawable.w_icon_50n);
    }

    /**
     * Retrieves the drawable resource associated with the specified icon name.
     * @param iconName name of icon to retrieve drawable icon component
     * @return The drawable resource ID corresponding to the provided icon name.
     */
    public static int getDrawableIconId(String iconName) {
        return ICON_MAP.getOrDefault(iconName, null);
    }

    /**
     * Initialize the activity
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityName = getIntent().getStringExtra("city").toString();
        Log.d("WeatherActivity", "City Name: " + cityName);

        // Initialize TextViews for displaying weather details.
        cityTextView = findViewById(R.id.cityTextView);
        dateTextView = findViewById(R.id.dateTextView);
        tempTextView = findViewById(R.id.tempTextView);
        weatherDescTextView = findViewById(R.id.weatherDescTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        windTextView = findViewById(R.id.windTextView);

        weatherIconImageView = findViewById(R.id.weatherIcon);

        // Weather Insight button
        Button weatherInsightsButton = findViewById(R.id.weatherInsightsButton);
        weatherInsightsButton.setOnClickListener(this);

        // Get weather data for a given cityName
        getWeatherDataBasedOnCityName(cityName);
    }

    /**
     * Handles retrieving weather from the open weather map service. Data is retrieved in a json format
     * and UI components are updated with relevant info.
     * @param cityName The city name to retrieve weather information for
     */
    private void getWeatherDataBasedOnCityName(String cityName) {
        String apiKey = "839dc13a6e25c0c0b3a85c4ee157c653";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=imperial";

        Log.d("WeatherActivity", "API URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject main = response.getJSONObject("main");
                            double temperature = main.getDouble("temp"); // Get the temperature
                            int humidity = main.getInt("humidity"); // Get the humidity

                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject weather = weatherArray.getJSONObject(0);
                            String description = weather.getString("description"); // Get the weather description
                            String iconCode = weather.getString("icon"); // Get the weather description

                            JSONObject wind = response.getJSONObject("wind");
                            double windSpeed = wind.getDouble("speed"); // Get the wind speed

                            LocalDateTime localDateTime = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                            cityTextView.setText("City: " + cityName);
                            tempTextView.setText("Temperature: " + temperature + "°F");
                            weatherDescTextView.setText("Weather: " + description);
                            humidityTextView.setText("Humidity: " + humidity + "%");
                            windTextView.setText("Wind Speed: " + windSpeed + " mph");
                            dateTextView.setText("Date & Time: " + localDateTime.format(formatter));

                            llmBundle = new Bundle();
                            //Add data to bundle
                            llmBundle.putDouble("temperature", temperature);
                            llmBundle.putDouble("wind",windSpeed);
                            llmBundle.putInt("humidity",humidity);
                            llmBundle.putString("description",description);
                            llmBundle.putLong("dateTime", localDateTime.atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli());

                            String icon = "w_icon_" + iconCode;

                            weatherIconImageView.setImageResource(getDrawableIconId(icon));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WeatherActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            /**
             * Handles errors that occur during the API request.
             * This method is triggered when the API call fails for reasons such as network issues,
             * an invalid API URL, or server errors.
             *
             * @param error The VolleyError object containing details about the error.
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(WeatherActivity.this, "Failed to get weather data", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    /**
     * Set an OnClickListener for the Weather Insights button
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weatherInsightsButton:
                //Create the `intent`
                Intent i = new Intent(this, LLMActivity.class);
                //Add the bundle to the intent
                i.putExtras(llmBundle);
                //Fire that second activity
                startActivity(i);
                break;
        }
    }

    /**
     * prefetch weather data based on city name onStart
     */
    @Override
    protected void onStart() {
        super.onStart();

        cityName = getIntent().getStringExtra("city").toString();

        // Get weather data for a given cityName
        getWeatherDataBasedOnCityName(cityName);
    }
}
