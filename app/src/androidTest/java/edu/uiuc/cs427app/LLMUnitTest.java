package edu.uiuc.cs427app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * Unit tests for the Login functionality of the app.
 * This class tests various scenarios for validating user credentials stored in a SQLite database,
 * including valid and invalid logins, case sensitivity, and special cases.
 */
public class LLMUnitTest {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Prepares the test environment by initializing the database helper
     * and clearing any existing data in the users table.
     */
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        clearDatabase();
    }

    /**
     * Cleans up the test environment by clearing the users table
     * and closing the database helper after each test.
     */
    @After
    public void tearDown() {
        clearDatabase();
        dbHelper.close();
    }

    /**
     * Deletes all records from the users table to ensure a clean database
     * state before and after each test.
     */
    private void clearDatabase() {
        db.delete("users", null, null);
    }

    /**
     * Tests that a user can be successfully inserted into the database.
     * Verifies that the insertion returns a valid row ID.
     */
    @Test
    public void insertTestData() {
        ContentValues values = new ContentValues();
        values.put("username", "testUser");
        values.put("email", "test@example.com");
        values.put("password", "password123");
        long id = db.insert("users", null, values);
        // Verify that the insert operation was successful by checking the returned row ID.
        assert(id != -1);
    }

    /**
     * Tests that a valid username and password combination is correctly authenticated.
     * Expects the username to be returned when credentials are valid.
     */
    @Test
    public void validLogin() {
        insertTestData();
        String result = dbHelper.validateUserCredentials("testUser", "password123");
        assertEquals("Expected valid login to return username", "testUser", result);
    }

    /**
     * Tests that an incorrect password for a valid username returns null,
     * indicating failed authentication.
     */
    @Test
    public void invalidPassword() {
        insertTestData();
        String result = dbHelper.validateUserCredentials("testUser", "wrongpassword");
        assertNull("Expected invalid password to return null", result);
    }

    /**
     * Tests that attempting to log in with a non-existent username returns null.
     */
    @Test
    public void nonExistentUser() {
        String result = dbHelper.validateUserCredentials("unknownUser", "password123");
        assertNull("Expected non-existent user to return null", result);
    }

    /**
     * Tests that the username is treated as case-sensitive during authentication.
     * Expects login to fail if the case does not match exactly.
     */
    @Test
    public void caseSensitivity() {
        insertTestData();
        String result = dbHelper.validateUserCredentials("TESTUSER", "password123");
        // The username "TESTUSER" does not match "testUser" due to case sensitivity.
        assertNull("Expected username case sensitivity to return null", result);
    }

    /**
     * Tests that a password is case-sensitive during authentication.
     * Expects login to fail when the password is provided in a different case.
     */
    @Test
    public void caseInsensitivePasswordFails() {
        insertTestData();

        // Attempt login with a password that differs only in case
        String result = dbHelper.validateUserCredentials("testUser", "Password123");

        // The password "Password123" should fail since it doesn't match the case of "password123"
        assertNull("Expected login to fail with a case-insensitive password", result);
    }

    /**
     * Tests that users can log in using their email address instead of their username.
     * Expects the username to be returned for valid email and password credentials.
     */
    @Test
    public void loginWithEmail() {
        insertTestData();
        String result = dbHelper.validateUserCredentials("test@example.com", "password123");
        assertEquals("Expected login with email to return username", "testUser", result);
    }

    /**
     * Tests that attempting to log in with an empty username or password
     * returns null, indicating failed authentication.
     */
    @Test
    public void emptyUsernameOrPassword() {
        insertTestData();
        String result = dbHelper.validateUserCredentials("", "password123");
        assertNull("Expected empty username to return null", result);

        result = dbHelper.validateUserCredentials("testUser", "");
        assertNull("Expected empty password to return null", result);
    }

    /**
     * Tests that usernames, emails, and passwords containing special characters
     * are correctly handled during authentication.
     */
    @Test
    public void specialCharactersInCredentials() {
        ContentValues values = new ContentValues();
        values.put("username", "special@User");
        values.put("email", "special@example.com");
        values.put("password", "p@ssw0rd!");
        db.insert("users", null, values);
        String result = dbHelper.validateUserCredentials("special@User", "p@ssw0rd!");
        assertEquals("Expected special characters to return username", "special@User", result);
    }

    /**
     * Tests that multiple users can coexist in the database and that
     * each user's credentials are validated correctly.
     */
    @Test
    public void multipleUsers() {
        ContentValues firstUser = new ContentValues();
        firstUser.put("username", "user1");
        firstUser.put("email", "email1@example.com");
        firstUser.put("password", "password1");
        db.insert("users", null, firstUser);

        ContentValues secondUser = new ContentValues();
        secondUser.put("username", "user2");
        secondUser.put("email", "email2@example.com");
        secondUser.put("password", "password2");
        db.insert("users", null, secondUser);

        assertEquals("Validating user1 credentials", "user1", dbHelper.validateUserCredentials("user1", "password1"));
        assertEquals("Validating user2 credentials", "user2", dbHelper.validateUserCredentials("user2", "password2"));
    }

    /**
     * Tests that user sign-up fails when the username already exists in the database.
     */
    @Test
    public void usernameConflict() {
        // Insert the first user
        ContentValues firstUser = new ContentValues();
        firstUser.put("username", "user1");
        firstUser.put("email", "email1@example.com");
        firstUser.put("password", "password1");
        db.insert("users", null, firstUser);

        // Attempt to insert a second user with the same username, which should fail
        ContentValues secondUser = new ContentValues();
        secondUser.put("username", "user1"); // Same username as first user
        secondUser.put("email", "email2@example.com");
        secondUser.put("password", "password2");

        // Assuming a method that checks if the insert was successful. The insert should fail here.
        long result = db.insert("users", null, secondUser);

        // Validate that the insertion failed (result should be -1 if it failed)
        assertEquals("Inserting user with a conflicting username should fail", -1, result);
    }
}
