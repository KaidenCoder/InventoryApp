package com.example.android.inventoryapp22.data;
/**
 * This project is done by Khaidem Sandip Singha under the Udacity Android Foundations Nanodegree program.
 *
 * I confirm that this submission is my own work. I have not used code from any other Udacity student's or graduate's submission of the same project.
 * I understand that Udacity will check my submission for plagiarism, and that failure to adhere to the Udacity Honor Code may result in the cancellation of my
 * enrollment.
 */
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp22";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.inventoryapp22/puzzles/ is a valid path for
     * looking at pet data. content://com.example.android.inventoryapp22/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PUZZLES = "puzzles";

    /**
     * Inner class that defines constant values for the puzzles database table.
     * Each entry in the table represents a single pet.
     */
    public static final class PuzzleEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of puzzle books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PUZZLES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single puzzle book order.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PUZZLES;

        /** The content URI to access the puzzle book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PUZZLES);

        /** Name of database table for puzzles */
        public final static String TABLE_NAME = "puzzles";

        /**
         * Unique ID number for the puzzle book order (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the puzzle book.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PUZZLE_NAME ="name";

        /**
         * Author of the puzzle.
         *
         * Type: TEXT
         */
        public final static String COLUMN_AUTHOR_PUZZLE = "author";

        /**
         * QUANTITY of the puzzle books.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PUZZLE_QUANTITY = "quantity";

        /**
         * PRICE of a puzzle book.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PUZZLE_PRICE = "price";

        /**
         * Address where the puzzle is to be delivered.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PUZZLE_ADDRESS = "address";

        /**
         * Image of the puzzle book.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PUZZLE_IMAGE = "image";

    }
}

