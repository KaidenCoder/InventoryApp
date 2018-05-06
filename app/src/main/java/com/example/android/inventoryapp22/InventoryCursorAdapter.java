package com.example.android.inventoryapp22;
/**
 * This project is done by Khaidem Sandip Singha under the Udacity Android Foundations Nanodegree program.
 *
 * I confirm that this submission is my own work. I have not used code from any other Udacity student's or graduate's submission of the same project.
 * I understand that Udacity will check my submission for plagiarism, and that failure to adhere to the Udacity Honor Code may result in the cancellation of my
 * enrollment.
 */
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.inventoryapp22.data.InventoryContract;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter{

    private static final String TAG = InventoryCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param cursor       The cursor from which to get the data.
     */
    protected InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
    }

    /**
     * This method binds the puzzle data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView authorTextView = view.findViewById(R.id.author);
        final TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView addressTextView = view.findViewById(R.id.address_text_view);
        ImageView puzzleImageView = view.findViewById(R.id.image_view_list);

        // Find the columns of puzzle attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_NAME);
        int authorColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_AUTHOR_PUZZLE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_PRICE);
        int addressColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_ADDRESS);
        int imageColumnIndex = cursor.getColumnIndex(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_IMAGE);

        // Read the puzzle attributes from the Cursor for the current puzzle
        int id = cursor.getInt(cursor.getColumnIndex(InventoryContract.PuzzleEntry._ID));
        final String puzzleName = cursor.getString(nameColumnIndex);
        final String puzzleAuthor = cursor.getString(authorColumnIndex);
        final int puzzleQuantity = cursor.getInt(quantityColumnIndex);
        final int puzzlePrice = cursor.getInt(priceColumnIndex);
        String puzzleAddress = cursor.getString(addressColumnIndex);
        Uri imageUri = Uri.parse(cursor.getString(imageColumnIndex));

        final Uri currentPuzzleUri = ContentUris.withAppendedId(InventoryContract.PuzzleEntry.CONTENT_URI, id);

        // Update the TextViews with the attributes for the current puzzle
        nameTextView.setText(puzzleName);
        authorTextView.setText(puzzleAuthor);
        quantityTextView.setText(new StringBuilder().append("Quantity: ").append(puzzleQuantity).toString());
        priceTextView.setText(new StringBuilder().append("Rs.").append(puzzlePrice).append("/item").toString());
        addressTextView.setText(puzzleAddress);
        //We use Glide to import photo images
        Glide.with(context).load(imageUri)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_insert_placeholder)
                .crossFade()
                .centerCrop()
                .into(puzzleImageView);

        //Button for selling puzzle books
        Button soldButton = view.findViewById(R.id.sell_button);
        soldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                if (puzzleQuantity > 0){
                    int qq = puzzleQuantity;
                    values.put(InventoryContract.PuzzleEntry.COLUMN_PUZZLE_QUANTITY, --qq);
                    resolver.update(
                             currentPuzzleUri,
                            values,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(currentPuzzleUri, null);
                } else {
                    Toast.makeText(context, "Puzzle book out of stock", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

