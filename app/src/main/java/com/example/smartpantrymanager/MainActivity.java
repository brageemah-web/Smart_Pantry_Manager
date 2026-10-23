package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //initialize floating action button
    FloatingActionButton floatingBtn;
    ListView listView;
    MyDatabaseHelper dbHelper;
    ArrayList<String> displayList = new ArrayList<>();
    ArrayList<Integer> idList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        floatingBtn = findViewById(R.id.floatingActionButton);
        listView = findViewById(R.id.listView);
        dbHelper = new MyDatabaseHelper(this);

        //set OnClickListener to handle activity navigation
        floatingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,AddIngredient.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int selectedDbId = idList.get(position);
                String[] options = {"Edit Quantity", "Delete Item"};

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Manage Item")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // User clicked "Edit Quantity"
                                    showEditQuantityDialog(selectedDbId);
                                } else if (which == 1) {
                                    // User clicked "Delete Item"
                                    showDeleteConfirmDialog(selectedDbId);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadPantryData();
    }

    private void loadPantryData() {
        displayList.clear();
        idList.clear();

        ArrayList<String> displayList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDatabaseHelper.PANTRYITEM_TABLE,
                new String[]{MyDatabaseHelper.PANTRYITEM_ID,MyDatabaseHelper.PANTRYITEM_NAME, MyDatabaseHelper.PANTRYITEM_QUANTITY},
                null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int dbId = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.PANTRYITEM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.PANTRYITEM_NAME));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.PANTRYITEM_QUANTITY));

                idList.add(dbId);
                displayList.add(name + "  (Qty: " + qty + ")");
            }
            cursor.close();
        }
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, displayList);

        listView.setAdapter(adapter);
    }

    private void showEditQuantityDialog(final int dbId) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter new quantity");

        new AlertDialog.Builder(this)
                .setTitle("Edit Quantity")
                .setView(input)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String qtyInput = input.getText().toString().trim();
                        if (!qtyInput.isEmpty()) {
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(MyDatabaseHelper.PANTRYITEM_QUANTITY, Integer.parseInt(qtyInput));

                            db.update(
                                    MyDatabaseHelper.PANTRYITEM_TABLE,
                                    values,
                                    MyDatabaseHelper.PANTRYITEM_ID + " = ?",
                                    new String[]{String.valueOf(dbId)}
                            );
                            db.close();

                            Toast.makeText(MainActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                            loadPantryData();
                        } else {
                            Toast.makeText(MainActivity.this, "Quantity cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void showDeleteConfirmDialog(final int dbId) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this ingredient?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete(
                                MyDatabaseHelper.PANTRYITEM_TABLE,
                                MyDatabaseHelper.PANTRYITEM_ID + " = ?",
                                new String[]{String.valueOf(dbId)}
                        );
                        db.close();
                        Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        loadPantryData(); // Refresh layout view instantly
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}