package edu.uiuc.cs427app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DatabaseHelper is a SQLiteOpenHelper class that manages the creation and management
 * of a local SQLite database for storing user credentials.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database information
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 1;
    public static String strSeparator = ",";
    // Table and column names
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_CITIES = "cities";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_THEME = "theme";

    /**
     * Constructor for DatabaseHelper.
     * @param context The context in which the database operates.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * This method creates the users table with id, username, email, and password columns.
     * @param db The SQLite database instance.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_CITIES + " TEXT, " +
                COLUMN_THEME  + " TEXT)";
        db.execSQL(createTable);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method drops the existing users table and recreates it.
     * @param db The SQLite database instance.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method can be used for future upgrades if need.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /**
     * Adds a new user to the database.
     * @param username The username of the new user.
     * @param email The email of the new user.
     * @param password The password of the new user.
     * @return true if the user was added successfully, false otherwise.
     */
    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_THEME, "Theme.MyFirstApp"); // Default theme

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // Return true if insertion is successful
    }

    /**
     * Adds a new city to a user's entry.
     * @param user The username of the user.
     * @param location location to add for the user
     * @return true if the city was added successfully, false otherwise.
     */
    public boolean addCity(String user, String location) {
        SQLiteDatabase db = this.getReadableDatabase();
        String citiesFromDb = getCities(user);

        if(citiesFromDb != null && citiesFromDb.contains(location)){
            return false;
        }

        String cities = (getCities(user) + "," + location).replaceAll("null,","");

        if (cities.startsWith(",")){
            cities = cities.substring(1);
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_CITIES,cities);

        long result = db.update(TABLE_USERS,values, COLUMN_USERNAME + " = ?", new String[]{user});

        return result != 0;
    }

    /**
     * removes a  city to a user's entry.
     * @param user The username of the user.
     * @param location location to add for the user
     * @return true if the city was removed successfully, false otherwise.
     */
    public boolean removeCity(String user, String location) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Fetch the current list of cities for the user
        String citiesString = getCities(user);
        if (citiesString == null || citiesString.isEmpty()) {
            return false; // No cities to remove
        }

        // Convert string to array, remove the specified city
        String[] citiesArray = convertStringToArray(citiesString);
        List<String> citiesList = new ArrayList<>(Arrays.asList(citiesArray));
        if (!citiesList.remove(location)) {
            return false; // City not found in the list
        }

        // Convert array back to a comma-separated string
        String updatedCitiesString = convertArrayToString(citiesList.toArray(new String[0]));

        // Prepare the updated values
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITIES, updatedCitiesString);

        // Update the database for this user
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{user});

        return rowsAffected > 0;
    }

    /**
     * returns all the cities assigned to a user
     * @param user The username of the user.
     * @return string of cities for user
     */
    public String getCities(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_CITIES + " FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ?" ;

        Cursor cursor = db.rawQuery(query, new String[]{user});
        cursor.moveToFirst();
        String returnCities = "";
        while (!cursor.isAfterLast()) {
            returnCities = cursor.getString(0);
            cursor.moveToNext();
        }

        cursor.close();
        return returnCities;
    }



    /**
     * Updates the user's theme preference in the database.
     * @param username The username of the user.
     * @param theme The theme preference to be set.
     * @return true if the theme was updated successfully, false otherwise.
     */
    public boolean updateUserTheme(String username, String theme) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_THEME, theme);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        return rowsAffected > 0; // Return true if the update was successful
    }

    /**
     * Retrieves the user's theme preference from the database.
     * @param username The username of the user.
     * @return The user's theme preference or null if not found.
     */
    public String getUserTheme(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_THEME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        String theme = null;
        if (cursor.moveToFirst()) {
            theme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THEME));
        }
        cursor.close();
        return theme; // Return the user's theme preference
    }

    /**
     * Checks if a user with the given username or email exists in the database.
     * This method is used during registration to prevent duplicate usernames or emails.
     * @param username The username to check.
     * @param email The email to check.
     * @return true if a user with the given username or email exists, false otherwise.
     */
    public boolean checkUserExistence(String username, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, email});
        boolean isUserFound = cursor.getCount() > 0;
        cursor.close();
        return isUserFound;
    }

    /**
     * Validates if a user with the given username or email and password exists in the database.
     * This method is used during login to ensure the provided credentials are correct.
     * @param usernameOrEmail The username or email to check.
     * @param password The password to check.
     * @return true if the credentials are valid, false otherwise.
     */
    public String validateUserCredentials(String usernameOrEmail, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE (" +
                COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?) AND " +
                COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{usernameOrEmail, usernameOrEmail, password});
        String username = null;
        if (cursor.moveToFirst()) { // Ensure the cursor has data before accessing it
            username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        }
        cursor.close();
        return username; // Return null if no matching data is found
    }

    /**
     * Convert an array of strings to a single string
     * @param array array of strings  to change
     * @return string of array
     */

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    /**
     * Convert a string to an array of strings.
     * @param str string to convert to array
     * @return array of strings
     */
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }
}