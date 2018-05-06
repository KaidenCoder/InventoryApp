package com.example.android.inventoryapp22;
/**
 * This project is done by Khaidem Sandip Singha under the Udacity Android Foundations Nanodegree program.
 *
 * I confirm that this submission is my own work. I have not used code from any other Udacity student's or graduate's submission of the same project.
 * I understand that Udacity will check my submission for plagiarism, and that failure to adhere to the Udacity Honor Code may result in the cancellation of my
 * enrollment.
 */
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp22.data.InventoryContract;

public class InventoryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PUZZLE_LOADER = 0;

    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to open InventoryEditor
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, InventoryEditor.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the puzzle book data
        ListView puzzleListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        puzzleListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of puzzle book data in the Cursor
        // There is no puzzle book data yet (until the loader finished) so pass in null for the Cursor.
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        puzzleListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        puzzleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new  intent to go to {@link InventoryEditor)
                Intent intent = new Intent(InventoryActivity.this, InventoryEditor.class);

                // Form the content URI that represents the specific puzzle that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PuzzleEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.inventoryapp22/puzzles/2"
                // if the puzzle with ID 2 was clicked on.
                Uri currentPuzzleUri = ContentUris.withAppendedId(InventoryContract.PuzzleEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPuzzleUri);

                // Launch the {@link InventoryEditor} to display the data for the current puzzle book.
                startActivity(intent);
            }
        });

        //Kick off the loader
        getLoaderManager().initLoader(PUZZLE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryContract.PuzzleEntry._ID,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_NAME,
                InventoryContract.PuzzleEntry.COLUMN_AUTHOR_PUZZLE,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_QUANTITY,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_PRICE,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_ADDRESS,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,  // Parent activity context
                InventoryContract.PuzzleEntry.CONTENT_URI,         // Provider content URI to query
                projection,                   // Columns to include in the resulting Cursor
                null,                 // No selection clause
                null,              // No selection arguments
                null);               // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update (@link InventoryCursorAdapter) with this new cursor containing updated puzzle data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    /**
     * Helper method to delete all puzzles in the database.
     */
    private void deleteAllPuzzles() {
        int rowsDeleted = getContentResolver().delete(InventoryContract.PuzzleEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from puzzles database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPuzzles();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
