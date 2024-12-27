package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {
    private MainActivity mainActivity;

    /**
     * Sets up the test environment by simulating user registration and login.
     */
    public void setUp() {
        // Launch the LoginActivity first
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.tv_register)).perform(click());
        // Simulate clicking on the "Register here" TextView to go to RegisterActivity
        onView(withId(R.id.et_username)).perform(typeText("testuser"));
        onView(withId(R.id.et_email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.et_password)).perform(typeText("Password123"));
        onView(withId(R.id.et_retype_password)).perform(typeText("Password123"));
        onView(withId(R.id.btn_register)).perform(click());

        // Simulate login
        onView(withId(R.id.et_login_username_or_email)).perform(typeText("testuser"));
        onView(withId(R.id.et_login_password)).perform(typeText("Password123"));
        onView(withId(R.id.btn_login)).perform(click());
    }

    /**
     * Tests the full integration steps including adding, verifying, and removing cities,
     * and logging out.
     */
    @Test
    public void testFullSetupIntegrationSteps() throws InterruptedException {
        setUp();
        // Test for two example cities
        addCityAndShowAllActivities("Boston");
        addCityAndShowAllActivities("Cincinnati");
        logout();
    }

    /**
     * Adds a city, navigates to the map activity to verify the city name, and then removes the city.
     *
     * @param name the name of the city to add, verify, and remove
     */
    public void addCityAndShowAllActivities(String name) throws InterruptedException {
        addCity(name);
        navigateToMapActivity();
        // Assertion test: verify that the city name is displayed correctly in cityNameTextView
        onView(withId(R.id.cityNameTextView)).check(matches(withText(containsString(name))));
        System.out.println("Assertion passed: City name '" + name + "' is displayed correctly in cityNameTextView.");

        Thread.sleep(3500);
        pressBackButton();
        pressBackButton();

        removeCity(name);
    }

    /**
     * Logs out of the application.
     */
    public void logout() {
        onView((withId(R.id.buttonLogout))).perform(click());
    }

    /**
     * Simulates pressing the back button to navigate to the previous screen.
     */
    public void pressBackButton() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    /**
     * Navigates to the city map activity by clicking the "Show Details" button and the map button.
     */
    public void navigateToMapActivity() {
        onView((withText("Show Details"))).perform(click());
        onView((withId(R.id.mapButton))).perform(click());
    }

    /**
     * Removes a city from the list by typing its name and confirming the removal.
     *
     * @param cityName the name of the city to remove
     */
    public void removeCity(String cityName) {
        onView(withId(R.id.buttonRemoveLocation)).perform(click());
        onView(withHint("City Name")).perform(typeText(cityName));
        onView(withText("Remove")).perform(click());
    }

    /**
     * Adds a city to the list by typing its name and confirming the addition.
     *
     * @param cityName the name of the city to add
     */
    public void addCity(String cityName) {
        onView(withId(R.id.buttonAddLocation)).perform(click());
        onView(withHint("City Name")).perform(typeText(cityName));
        onView(withText("Add")).perform(click());
    }
}
