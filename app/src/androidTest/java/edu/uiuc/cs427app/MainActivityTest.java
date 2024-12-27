package edu.uiuc.cs427app;

import static androidx.core.content.res.TypedArrayUtils.getText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import android.content.Context;
import android.widget.TextView;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private DatabaseHelper dbHelper;
    private MainActivity mainActivity;
    @BeforeClass
    public static void register()
    {
        // Launch the LoginActivity first
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.tv_register)).perform(click());
        // Simulate clicking on the "Register here" TextView to go to RegisterActivity
        onView(withId(R.id.et_username)).perform(typeText("testuser_main"));
        onView(withId(R.id.et_email)).perform(typeText("testuser_main@gmail.com"));
        onView(withId(R.id.et_password)).perform(typeText("Password123"));
        onView(withId(R.id.et_retype_password)).perform(typeText("Password123"));
        onView(withId(R.id.btn_register)).perform(click());
    }
    public void login()
    {
        // Launch the LoginActivity first
        ActivityScenario.launch(LoginActivity.class);
        //Simulate login
        onView(withId(R.id.et_login_username_or_email)).perform(typeText("testuser_main"));
        onView(withId(R.id.et_login_password)).perform(typeText("Password123"));
        onView(withId(R.id.btn_login)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DatabaseHelper(context);
    }

    public void testWeatherAFeatureDisplaysCorrectCity() throws InterruptedException {
        login();
        String cityName = "Chicago";
        addCity(cityName);
        navigateToMapActivity();
        Thread.sleep(2000);
        onView((withId(R.id.cityTextView))).check(matches(withText(containsString(cityName))));
    }

    @Test
    public void testFullSetupIntegrationSteps() throws InterruptedException {
         login();
        addCityAndShowAllActivities("Boston");
        addCityAndShowAllActivities("Cincinnati");
        logout();
    }

    /**
     * Test remove city
     * @throws InterruptedException
     */
    @Test
    public void testAddCity() throws InterruptedException {
        //Initial user setup
        login();

        //Add cities
        addCity("Orlando");
        addCity("Austin");

        //Retrieve cities from the database
        String citiesString = dbHelper.getCities("testuser_main");
        List<String> cities = Arrays.asList(citiesString.split(","));

        //Assert that cities are present in the database
        assertTrue("The city list should contain 'Orlando'", cities.contains("Orlando"));
        assertTrue("The city list should contain 'Austin'", cities.contains("Austin"));

        //Assert that Orlando is present on the screen
        onView(withSubstring("Orlando")).check(matches(isDisplayed()));
        //Assert that Austin is present on the screen
        onView(withSubstring("Austin")).check(matches(isDisplayed()));

        //user logout
        logout();
    }

    /**
     * Test to add city
     * @throws InterruptedException
     */
    @Test
    public void testRemoveCity() throws InterruptedException {
        //Initial user setup
        login();

        //Add cities
        addCity("Orlando");
        addCity("Austin");

        //Retrieve cities from the database
        String citiesString = dbHelper.getCities("testuser_main");
        List<String> cities = Arrays.asList(citiesString.split(","));

        //Assert that cities are present in the database
        assertTrue("The city list should contain 'Orlando'", cities.contains("Orlando"));
        assertTrue("The city list should contain 'Austin'", cities.contains("Austin"));

        //Assert that Orlando is present on the screen
        onView(withSubstring("Orlando")).check(matches(isDisplayed()));
        //Assert that Austin is present on the screen
        onView(withSubstring("Austin")).check(matches(isDisplayed()));

        //Remove cities
        removeCity("Orlando");
        removeCity("Austin");

        //Retrieve cities from the database
        citiesString = dbHelper.getCities("testuser_main");
        cities = Arrays.asList(citiesString.split(","));

        //Assert that cities are removed from the database
        assertFalse("The city list should not contain 'Orlando'", cities.contains("Orlando"));
        assertFalse("The city list should not contain 'Austin'", cities.contains("Austin"));

        //Assert that Orlando is not present on the screen
        onView(withSubstring("Orlando")).check(doesNotExist());
        //Assert that Austin is not present on the screen
        onView(withSubstring("Orlando")).check(doesNotExist());

        //user logout
        logout();
    }

    public void addCityAndShowAllActivities(String name) throws InterruptedException{
        addCity(name);
        //See Map activity for Boston
        navigateToMapActivity();
        Thread.sleep(3500);
        pressBackButton();
        pressBackButton();
        //See Weather activity for Boston
        navigateToWeatherActivity();
        Thread.sleep(2000);
        navigateToWeatherInsights();
        Thread.sleep(2000);
        operateWeatherInsightsButtons();
        //Navigate to main activity
        pressBackButton();
        pressBackButton();
        pressBackButton();

        removeCity(name);
    }

    public void operateWeatherInsightsButtons() throws InterruptedException{
        onView((withId(R.id.button_question_one))).perform(click());
        Thread.sleep(2000);
        onView((withId(R.id.button_question_two))).perform(click());
        Thread.sleep(2000);
    }

    public static void logout(){
        onView((withId(R.id.buttonLogout))).perform(click());
    }

    public void navigateToWeatherInsights(){
        onView((withId(R.id.weatherInsightsButton))).perform(click());
    }
    public void pressBackButton(){
        onView(isRoot()).perform(ViewActions.pressBack());//        onView((withId(android.R.id.home))).perform(click());
    }
    //Helper Functions
    public void navigateToMapActivity(){
        onView((withText("Show Details"))).perform(click());
        onView((withId(R.id.mapButton))).perform(click());
    }
    public void navigateToWeatherActivity(){
        onView((withText("Show Details"))).perform(click());
        onView((withId(R.id.weatherButton))).perform(click());
    }
    public void removeCity(String cityName){
        onView(withId(R.id.buttonRemoveLocation)).perform(click());
        onView(withHint("City Name")).perform(typeText(cityName));
        onView(withText("Remove")).perform(click());
    }

    public void addCity(String cityName){
        onView(withId(R.id.buttonAddLocation)).perform(click());
        onView(withHint("City Name")).perform(typeText(cityName));
        onView(withText("Add")).perform(click());
    }


}
