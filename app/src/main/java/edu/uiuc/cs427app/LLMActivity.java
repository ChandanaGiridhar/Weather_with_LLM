package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// other imports...
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * LLMActivity handles user interactions with generative AI to generate context-specific
 * questions based on weather information and provides responses to user prompts.
 */
public class LLMActivity extends AppCompatActivity implements View.OnClickListener {
    // Declare member variables for database interaction and UI components
    private DatabaseHelper dbHelper;
    private EditText usernameOrEmailEditText, passwordEditText;
    private Button loginButton;
    private TextView responseTextView;
    private TextView registerTextView;
    private String question1, question2;
    private Button firstQuestionButton, secondQuestionButton;
    Bundle extras;

    /**
     * Called when the LLMActivity is created.
     * onCreate() method sets up the user interface, initializes the DatabaseHelper,
     * connects UI elements, and sets up button click listeners for user login
     * and navigation to the registration page.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout resource to be used by this activity
        setContentView(R.layout.activity_llm);

        // Retrieve data passed via intent
        extras = getIntent().getExtras();

        if (extras != null) {
            // Extract weather-related data from extras
            String description = extras.getString("description", "No description available");
            long dateTime = extras.getLong("dateTime", 0);
            int humidity = extras.getInt("humidity", 0);
            double wind = extras.getDouble("wind", 0);
            double temperature = extras.getDouble("temperature", 0);

            // Log the retrieved values for debugging
            System.out.println("Description: " + description);
            System.out.println("DateTime: " + dateTime);
            System.out.println("Humidity: " + humidity);
            System.out.println("Wind: " + wind);
            System.out.println("Temperature: " + temperature);

            // Create a formatted weather information string for generating questions
            String weatherInfo = String.format("Current weather: %s. Date and Time: %s. Humidity: %d%%. Wind Speed: %.2f m/s. Temperature: %.2f°C.",
                    description, new java.util.Date(dateTime).toString(), humidity, wind, temperature);

            // Create a more detailed prompt using the formatted weather data
            Content content1 = new Content.Builder().addText("Today's weather is as follows: " + weatherInfo +
                    " Please generate two context-specific questions that a user might ask to help them make decisions about their day. " +
                    "Please follow this format: 1. Question_First 2. Question_Second. Add this at the end of the questions so that the answers will be concise: answer within 40 words").build();

            // Continue with the rest of your code to generate the questions and handle the responses
            GenerativeModel gm = new GenerativeModel("models/gemini-1.5-flash", "AIzaSyCb27Em_-nheDDc2yrxps-RD8Xz9N6rE-g");
            GenerativeModelFutures model = GenerativeModelFutures.from(gm);

            // Execute the content generation on a separate thread
            Executor executor = Executors.newSingleThreadExecutor();
            ListenableFuture<GenerateContentResponse> response1 = model.generateContent(content1);

            // Handle the asynchronous response
            Futures.addCallback(
                    response1,
                    new FutureCallback<GenerateContentResponse>() {
                        /**
                         * Handles the successful response from the generative AI model.
                         *
                         * @param result The response from the AI model, containing the generated text.
                         *               This text is processed to extract the questions or content
                         *               required for the application.
                         */
                        @Override
                        public void onSuccess(GenerateContentResponse result) {
                            String resultText = result.getText();

                            // Split the response based on "2." since the questions start with "1." and "2."
                            String[] parts = resultText.split("2\\. ");

                            if (parts.length > 1) {
                                question1 = parts[0].replace("1. ", "").trim(); // Extract the first question and remove "1. "
                                question2 = parts[1].trim(); // Second question should already be clean after splitting
                            } else {
                                question1 = "";
                                question2 = "";
                                System.out.println("Response does not match expected format.");
                            }

                            // Output the questions to console (for debugging purposes)
                            System.out.println("Question 1: " + question1);
                            System.out.println("Question 2: " + question2);

                            // Update UI on the main thread
                            runOnUiThread(() -> {
                                firstQuestionButton.setText(question1);
                                secondQuestionButton.setText(question2);
                            });
                        }

                        /**
                         * Handles failures during communication with the generative AI model.
                         *
                         * @param t The exception or error encountered during the process. This could
                         *          include network errors, API failures, or unexpected server responses.
                         */
                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            System.out.println("Failed: cannot get any response");
                        }
                    },
                    executor);
        } else {
            // Handle the case where no data was passed
            System.err.println("No extras passed to LLMActivity");
        }

        // Initialize question buttons as member variables
        firstQuestionButton = findViewById(R.id.button_question_one);
        secondQuestionButton = findViewById(R.id.button_question_two);

        // Initialize responseTextView
        responseTextView = findViewById(R.id.response_text_view);

        // Set click listener for question buttons
        firstQuestionButton.setOnClickListener(this);
        secondQuestionButton.setOnClickListener(this);
    }
    /**
     * Handles click events for question buttons.
     * Generates a response based on the clicked question using generative AI.
     *
     * @param view The button that was clicked
     */
    @Override
    public void onClick(View view) {
        // Determine which question was clicked and set the prompt for response
        String prompt;
        if (view.getId() == R.id.button_question_one) {
            prompt = question1;
        } else if (view.getId() == R.id.button_question_two) {
            prompt = question2;
        } else {
            return;
        }

        // Check if prompt is not null before passing it to addText
        if (prompt == null || prompt.isEmpty()) {
            System.out.println("Prompt is null or empty");
            responseTextView.setText("No question available.");
            return;
        }

        // Set up generative model for response generation
        GenerativeModel gm = new GenerativeModel("models/gemini-1.5-flash-8b", "AIzaSyCb27Em_-nheDDc2yrxps-RD8Xz9N6rE-g");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder().addText(prompt).build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Fetch and display the response
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    /**
                     * Handles the successful response for generating AI content based on user interaction.
                     *
                     * @param result The response from the AI model containing the text generated
                     *               for the selected question.
                     */
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        runOnUiThread(() -> responseTextView.setText(result.getText()));
                    }

                    /**
                     * Handles failures during the response generation for user-selected questions.
                     *
                     * @param t The exception or error encountered during the response generation process.
                     */
                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        runOnUiThread(() -> responseTextView.setText("Failed to retrieve response."));
                    }
                },
                executor
        );

    }
}