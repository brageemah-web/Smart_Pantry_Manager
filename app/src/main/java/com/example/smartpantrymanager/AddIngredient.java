package com.example.smartpantrymanager;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class AddIngredient extends AppCompatActivity {
    EditText ingredientName;
    EditText ingredientQuantity;
    Spinner unit;
    EditText expiryDate;
    Button addBtn;

    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_ingredient);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addIngredient), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new MyDatabaseHelper(this);

        //bind xml to java variables
        ingredientName = findViewById(R.id.ingredientName);
        ingredientQuantity = findViewById(R.id.ingredientQuantity);
        unit = findViewById(R.id.Unit);
        expiryDate = findViewById(R.id.expiryDate);
        addBtn = findViewById(R.id.addBtn);

        ArrayList<String> unitOptions = new ArrayList<>(Arrays.asList("Select a Unit","kg", "g", "L", "ml", "pack"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit.setAdapter(adapter);

    addBtn.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view){
            String nameInput = ingredientName.getText().toString().trim();
            String quantityInput = ingredientQuantity.getText().toString().trim();
            String unitInput = unit.getSelectedItem().toString();
            if(nameInput.isEmpty()){
                Toast.makeText(AddIngredient.this, "Enter Item Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if(quantityInput.isEmpty()){
                Toast.makeText(AddIngredient.this, "Enter Item Quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            if(unitInput.equals("Select a Unit")){
                Toast.makeText(AddIngredient.this, "Please select a valid unit", Toast.LENGTH_SHORT).show();
                return;
            }

            String finalName = nameInput + " (" + unitInput + ")";

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(MyDatabaseHelper.PANTRYITEM_NAME, finalName);
            values.put(MyDatabaseHelper.PANTRYITEM_QUANTITY, Integer.parseInt(quantityInput));
            values.put(MyDatabaseHelper.PANTRYITEM_EXPIRY_DATE, expiryDate.getText().toString().trim());

            long result = db.insert(MyDatabaseHelper.PANTRYITEM_TABLE, null, values);
            db.close();

            if (result != -1) {
                Toast.makeText(AddIngredient.this, "Added successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddIngredient.this, "Database Insert Error", Toast.LENGTH_SHORT).show();
            }
        }
    });



    }
}