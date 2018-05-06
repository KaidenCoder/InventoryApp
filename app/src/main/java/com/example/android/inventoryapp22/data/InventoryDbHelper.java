package com.example.android.inventoryapp22.data;
/**
 * This project is done by Khaidem Sandip Singha under the Udacity Android Foundations Nanodegree program.
 *
 * I confirm that this submission is my own work. I have not used code from any other Udacity student's or graduate's submission of the same project.
 * I understand that Udacity will check my submission for plagiarism, and that failure to adhere to the Udacity Honor Code may result in the cancellation of my
 * enrollment.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for Puzzles app. Manages database creation and version management.
 */
public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "puzzles.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link InventoryDbHelper}.
     *
     * @param context of the app
     */
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        // Create a String that contains the SQL statement to create the puzzles table
        String SQL_CREATE_PUZZLES_TABLE = "CREATE TABLE " + InventoryContract.PuzzleEntry.TABLE_NAME + " (" +
                InventoryContract.PuzzleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_NAME + " TEXT NOT NULL, " +
                InventoryContract.PuzzleEntry.COLUMN_AUTHOR_PUZZLE + " TEXT NOT NULL," +
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_PRICE + " REAL NOT NULL DEFAULT 0.0, " +
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_ADDRESS + " TEXT NOT NULL, " +
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_IMAGE + " TEXT NOT NULL DEFAULT 'No images' );";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PUZZLES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DELETE_PRODUCTS_TABLE = " DROP TABLE IF EXISTS " + InventoryContract.PuzzleEntry.TABLE_NAME + ";";
        db.execSQL(DELETE_PRODUCTS_TABLE);
        onCreate(db);
    }
}
