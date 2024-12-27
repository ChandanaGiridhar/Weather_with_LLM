package edu.uiuc.cs427app;

import android.content.Context;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * This test class verifies that the errorHandler is correctly set when not all fields are filled
 * in the RegisterActivity.
 */
@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    private RegisterActivity registerActivity;

//    @Test
//    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("edu.uiuc.cs427app", appContext.getPackageName());
//    }

    @Before
    public void setUp() {
        // Launch the LoginActivity first
        ActivityScenario.launch(LoginActivity.class);

        // Simulate clicking on the "Register here" TextView to go to RegisterActivity
        onView(withId(R.id.tv_register)).perform(click());

        // After the click, we need to access the RegisterActivity instance
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);
        scenario.onActivity(activity -> registerActivity = activity);
    }

    @Test
    public void testErrorHandlerWhenFieldsAreEmpty() {
        // Simulate entering only the username and password, leaving the other fields empty
        onView(withId(R.id.et_username)).perform(typeText("testuser"));
        onView(withId(R.id.et_password)).perform(typeText("Password123"));

        // Simulate clicking the register button
        onView(withId(R.id.btn_register)).perform(click());

        // Check if the errorHandler is set to "All fields not filled"
        assertEquals("All fields not filled", registerActivity.getErrorHandler());
    }
}
