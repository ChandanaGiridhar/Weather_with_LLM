package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LogOffActivityTest {

    @Test
    public void testUserRegistrationLoginAndLogout() {
        // Launching the LoginActivity.class
        ActivityScenario.launch(LoginActivity.class);

        // Performing user registration
        onView(withId(R.id.tv_register)).perform(click());
        onView(withId(R.id.et_username)).perform(typeText("testuser"));
        onView(withId(R.id.et_email)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.et_password)).perform(typeText("Password123"));
        onView(withId(R.id.et_retype_password)).perform(typeText("Password123"));
        onView(withId(R.id.btn_register)).perform(click());

        // Checking successful user registration with assertions - checking for successful redirection after user registration
        boolean isLoginScreenDisplayed = isViewDisplayed(R.id.et_login_username_or_email)
                && isViewDisplayed(R.id.et_login_password);
        assertTrue("Registration failed, login screen not displayed", isLoginScreenDisplayed);

        // Perform login with password and username login details
        onView(withId(R.id.et_login_username_or_email)).perform(typeText("testuser"));
        onView(withId(R.id.et_login_password)).perform(typeText("Password123"));
        onView(withId(R.id.btn_login)).perform(click());


        // Perform logout click
        onView(withId(R.id.buttonLogout)).perform(click());

        // Verifying logout - checking for redirection
        boolean isBackToLoginScreen = isViewDisplayed(R.id.et_login_username_or_email)
                && isViewDisplayed(R.id.et_login_password);
        assertTrue("Logout failed, login screen not displayed", isBackToLoginScreen);
    }

    // Helper method to check if the view is displayed (used to check the login view)
    private boolean isViewDisplayed(int viewId) {
        try {
            onView(withId(viewId)).check(matches(isDisplayed()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
