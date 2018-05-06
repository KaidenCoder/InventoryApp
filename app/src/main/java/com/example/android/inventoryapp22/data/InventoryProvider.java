package com.example.android.inventoryapp22.data;
/**
 * This project is done by Khaidem Sandip Singha under the Udacity Android Foundations Nanodegree program.
 *
 * I confirm that this submission is my own work. I have not used code from any other Udacity student's or graduate's submission of the same project.
 * I understand that Udacity will check my submission for plagiarism, and that failure to adhere to the Udacity Honor Code may result in the cancellation of my
 * enrollment.
 */
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * {@link ContentProvider} for Puzzle data app.
 */
public class InventoryProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the puzzles table
     */
    private static final int PUZZLES = 100;

    private static final int PUZZLE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PUZZLES, PUZZLES);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PUZZLES + "/#", PUZZLE_ID);
    }

    /**
     * Database helper object
     */
    private InventoryDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PUZZLES:
                // For the PUZZLES code, query the puzzles data table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the puzzles table.
                cursor = database.query(InventoryContract.PuzzleEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PUZZLE_ID:
                // For the PUZZLE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventoryapppuzzles/puzzles/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryContract.PuzzleEntry._ID + "+?";////?????????????
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the puzzles data table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryContract.PuzzleEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for,
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
        //return null;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PUZZLES:
                return InventoryContract.PuzzleEntry.CONTENT_LIST_TYPE;
            case PUZZLE_ID:
                return InventoryContract.PuzzleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PUZZLES:
                return insertPuzzle(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri.toString());
                //return null;
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPuzzle(Uri uri, ContentValues values) {
        // Check that the puzzle book name is not null
        String name = values.getAsString(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Puzzle requires a name");
        }
        // Check that the author name is not null
        String authorName = values.getAsString(InventoryContract.PuzzleEntry.COLUMN_AUTHOR_PUZZLE);
        if (authorName == null) {
            throw new IllegalArgumentException("Puzzle requires an author name");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Puzzle requires valid quantity");
        }

        // Check that the price is valid
        Integer price = values.getAsInteger(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Puzzle requires valid price");
        }////CHECH HERE FOR LAUNCHING ON APP

        // Check that the address name is not null
        String address = values.getAsString(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_ADDRESS);
        if (address == null) {
            throw new IllegalArgumentException("Puzzle requires an address to be delivered");
        }

        // Check that the image is not null
        String image = values.getAsString(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Puzzle requires a valid image");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(InventoryContract.PuzzleEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        int match = sUriMatcher.match(uri); //it was final int match, not int match
        switch (match) {
            case PUZZLES:
                // Delete all rows that match the selection and selection args
                //return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted = database.delete(InventoryContract.PuzzleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PUZZLE_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryContract.PuzzleEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryContract.PuzzleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot update empty values");
        }

        switch (match) {
            case PUZZLES:
                rowsUpdated = database.update(InventoryContract.PuzzleEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case PUZZLE_ID:
                rowsUpdated = database.update(InventoryContract.PuzzleEntry.TABLE_NAME,
                        contentValues,
                        InventoryContract.PuzzleEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return rowsUpdated;
    }
}
