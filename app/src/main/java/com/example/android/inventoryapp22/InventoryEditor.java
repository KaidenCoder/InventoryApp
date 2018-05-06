package com.example.android.inventoryapp22;
/**
 * This project is done by Khaidem Sandip Singha under the Udacity Android Foundations Nanodegree program.
 *
 * I confirm that this submission is my own work. I have not used code from any other Udacity student's or graduate's submission of the same project.
 * I understand that Udacity will check my submission for plagiarism, and that failure to adhere to the Udacity Honor Code may result in the cancellation of my
 * enrollment.
 */
import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.inventoryapp22.data.InventoryContract;

import java.io.File;

public class InventoryEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;

    /**
     * Identifier for the puzzle data loader
     */
    private static final int EXISTING_PUZZLE_LOADER = 1;//Intially 0 not 1

    /**
     * Identifier for the puzzle image loader
     */
    private static final int PUZZLE_IMAGE = 20;

    /**
     * Content URI for the existing puzzle (null if it's a new puzzle)
     */
    private Uri mCurrentPuzzleUri = null;

    /**
     * EditText field to enter the puzzle book name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the puzzle author's name
     */
    private EditText mAuthorEditText;

    /**
     * EditText field to enter the puzzle book quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the puzzle price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the puzzle book delivery address
     */
    private EditText mAddressEditText;

    /**
     * EditText field to enter the puzzle image
     */
    private ImageView mImageView;

    private String mCurrentPhotoUri = "no images";

    /**
     * Boolean flag that keeps track of whether the puzzle data has been edited (true) or not (false)
     */
    private boolean mPuzzleHasChanged = false;

    Uri imagePuzzleUri;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPuzzleHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPuzzleHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new puzzle or editing an existing one.
        Intent intent = getIntent();
        mCurrentPuzzleUri = intent.getData();

        // If the internet DOES NOT contain a puzzle content URI, then we know that we are
        // creating a new puzzle data.
        if (mCurrentPuzzleUri == null) {
            imagePuzzleUri = Uri.parse("android.resource://" + this.getPackageName() + "/drawable/pic");
            // This is a new puzzle data, so change the app bar to say "Add a puzzle data"
            setTitle(getString(R.string.add_puzzle_book_editor));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a puzzle data that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing puzzle data, so change app bar to say "Add a puzzle data"
            setTitle(getString(R.string.edit_puzzle_book_editor));

            // Initialize a loader to read the puzzle data data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PUZZLE_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_puzzle_name);
        mAuthorEditText = findViewById(R.id.edit_author_name);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mPriceEditText = findViewById(R.id.edit_price);
        mAddressEditText = findViewById(R.id.edit_address);
        mImageView = findViewById(R.id.image_view);

        //monitor activity so we can protect user
        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mAddressEditText.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);

        //Make the photo click listener to update itself
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhotoProductUpdate(view);
            }
        });

        //Button for adding the number of puzzle book image
        Button imageButton = findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(Intent.createChooser(intent, "Select Image from"), PUZZLE_IMAGE);
                }
            }
        });

        //Button for adding the number of puzzle books to be ordered
        Button addPuzzle = findViewById(R.id.add_button);
        addPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int addQuantity;
                String orderQuantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(orderQuantity)){
                    mQuantityEditText.setText("0");
                }
                addQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                addQuantity++;
                mQuantityEditText.setText(new StringBuilder().append("").append(addQuantity).toString());
            }
        });

        //Button for subtracting the number of puzzle books to be ordered
        Button subPuzzle = findViewById(R.id.sub_button);
        subPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int subQuantity;
                String orderQuantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(orderQuantity)){
                    mQuantityEditText.setText("0");
                }
                subQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                if (subQuantity <= 0) {
                    Toast.makeText(InventoryEditor.this, "Ordered item cannot be less than 0", Toast.LENGTH_SHORT).show();
                } else {
                    subQuantity--;
                }
                mQuantityEditText.setText(new StringBuilder().append("").append(subQuantity).toString());
            }
        });

        //Sending intent to mail for ordering puzzle book
        final Button order = findViewById(R.id.order_button);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderSupplier();
             }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save puzzle book to database
                savePuzzle();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (InventoryActivity)
                //NavUtils.navigateUpFromSameTask(this);
                // If the puzzle book hasn't changed, continue with navigating up to parent activity
                // which is the {@link InventoryActivity}.
                if (!mPuzzleHasChanged) {
                    NavUtils.navigateUpFromSameTask(InventoryEditor.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(InventoryEditor.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPhotoProductUpdate(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //We are on M or above so we need to ask for runtime permissions
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                invokeGetPhoto();
            } else {
                // we are here if we do not all ready have permissions
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permisionRequest, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
            }
        } else {
            //We are on an older devices so we dont have to ask for runtime permissions
            invokeGetPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //We got a GO from the user
            invokeGetPhoto();
        } else {
            Toast.makeText(this, R.string.err_external_storage_permissions, Toast.LENGTH_LONG).show();
        }
    }

    private void invokeGetPhoto() {
        // invoke the image gallery using an implicit intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, PUZZLE_IMAGE);
    }

    @Override
    public void onBackPressed(){
        if(!mPuzzleHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PUZZLE_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                //If we are here, everything processed successfully and we have an Uri data
                Uri imageUri = data.getData();
                mCurrentPhotoUri = imageUri.toString();
                mImageView.setImageURI(imageUri);

                //We use Glide to import photo images
                Glide.with(this).load(imageUri)
                        .placeholder(R.drawable.ic_insert_placeholder)
                        .crossFade()
                        .fitCenter()
                        .into(mImageView);
            }
        }
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new puzzle book, hide the "Delete" menu item.
        if (mCurrentPuzzleUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        // Since the editor shows all puzzle book attributes, define a projection that contains
        // all columns from the puzzle data table
        String[] projection = {
                InventoryContract.PuzzleEntry._ID,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_NAME,
                InventoryContract.PuzzleEntry.COLUMN_AUTHOR_PUZZLE,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_QUANTITY,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_PRICE,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_ADDRESS,
                InventoryContract.PuzzleEntry.COLUMN_PUZZLE_IMAGE};


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentPuzzleUri,         // Query the content URI for the current puzzle data
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of puzzle book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_NAME);
            int authorColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_AUTHOR_PUZZLE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_PRICE);
            int addressColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_ADDRESS);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            mCurrentPhotoUri = cursor.getString(imageColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAuthorEditText.setText(author);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            mAddressEditText.setText(address);

            //Update photo using Glide
            Glide.with(this).load(mCurrentPhotoUri)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.ic_insert_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(mImageView);
        }
    }

    /**
     * * Get user input from editor and save puzzle data into database.
     */
    private void savePuzzle() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        String priceString = mPriceEditText.getText().toString().trim();
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        String addressString = mAddressEditText.getText().toString().trim();
        String puzzleImage = mCurrentPhotoUri.toString();

        if (mCurrentPuzzleUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(authorString) || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(addressString) || TextUtils.isEmpty(puzzleImage)) {
            Toast.makeText(InventoryEditor.this, R.string.err_missing_textfields, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and puzzle attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_NAME, nameString);
        values.put(InventoryContract.PuzzleEntry.COLUMN_AUTHOR_PUZZLE, authorString);
        values.put(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_QUANTITY, quantity);
        values.put(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_PRICE, price);
        values.put(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_ADDRESS, addressString);
        values.put(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_IMAGE, puzzleImage); //previously puzzleImage, not mCurrentPhotoUri

        // Determine if this is a new or existing puzzle by checking if mCurrentPuzzleUri is null or not
        if (mCurrentPuzzleUri == null) {
            // This is a NEW puzzle, so insert a new puzzle into the provider,
            // returning the content URI for the new puzzle.
            Uri newUri = getContentResolver().insert(InventoryContract.PuzzleEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_saving_puzzle_edit),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.edit_saved_puzzle),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, InventoryActivity.class);
                startActivity(intent);
            }
        } else {
            // Otherwise this is an EXISTING puzzle, so update the puzzle with content URI: mCurrentPuzzleUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPuzzleUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentPuzzleUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.error_updating_puzzle_edit),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.puzzle_updated_edit),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, InventoryActivity.class); //Something new line
                startActivity(intent); //Something new line
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mAddressEditText.setText("");
        }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the puzzle data.
                if (dialog != null)

                {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Show a dialog that warns the user the puzzle data will be deleted
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the puzzle data.
                deletePuzzle();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the puzzle data.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the puzzle data in the database.
     */
    private void deletePuzzle() {
        // Only perform the delete if this is an existing puzzle book.
        if (mCurrentPuzzleUri != null) {
            // Call the ContentResolver to delete the puzzle data at the given content URI.
            // Pass in null for the selection and selection args because the mCurrenPuzzleUri
            // content URI already identifies the puzzle data that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPuzzleUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_puzzle_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_puzzle_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    //Order from supplier
    private void orderSupplier() {
        String orderName = mNameEditText.getText().toString();
        String orderAuthor = mAuthorEditText.getText().toString();
        String orderQuantity = mQuantityEditText.getText().toString();
        String orderPrice = mPriceEditText.getText().toString();
        String orderAddress = mAddressEditText.getText().toString();

        Intent orderIntent = new Intent(Intent.ACTION_SENDTO);
        orderIntent.setData(Uri.parse("mailto:khaidemsandipsingha@gmail.com"));
        orderIntent.putExtra(Intent.EXTRA_SUBJECT, "Order Puzzle Books");
        orderIntent.putExtra(Intent.EXTRA_TEXT, "Ordered Puzzle Book: " + orderName + " \n Author name: " + orderAuthor +
                "\n Price/item: " + orderPrice + " \n Quantity: " + orderQuantity + " \n" +
                " Delivery Address: " + orderAddress);
        startActivity(orderIntent);
    }
}

