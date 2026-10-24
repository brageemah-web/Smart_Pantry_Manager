package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecipeDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipeDetail), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        TextView tvTitle = findViewById(R.id.tvRecipeTitle);
        TextView tvIngredients = findViewById(R.id.tvIngredients);
        TextView tvSteps = findViewById(R.id.tvSteps);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        if (recipeId == -1) {
            finish();   // opened without an id, nothing to show
            return;
        }

        SQLiteDatabase db = new MyDatabaseHelper(this).getReadableDatabase();

        // the recipe's name and steps
        Cursor r = db.query(MyDatabaseHelper.RECIPE_TABLE,
                new String[]{MyDatabaseHelper.RECIPE_NAME, MyDatabaseHelper.RECIPE_STEPS},
                MyDatabaseHelper.RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        if (r.moveToFirst()) {
            tvTitle.setText(r.getString(0));
            tvSteps.setText(r.getString(1));
        }
        r.close();

        // the recipe's ingredients
        Cursor c = db.query(MyDatabaseHelper.RECIPEINGRED_TABLE,
                new String[]{MyDatabaseHelper.RECIPEINGRED_NORMALIZED,
                        MyDatabaseHelper.RECIPEINGRED_QUANTITY,
                        MyDatabaseHelper.RECIPEINGRED_UNIT},
                MyDatabaseHelper.RECIPE_ID_FK + " = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        StringBuilder list = new StringBuilder();
        while (c.moveToNext()) {
            double qty = c.getDouble(1);
            String qtyText = (qty == Math.floor(qty)) ? String.valueOf((long) qty) : String.valueOf(qty);
            list.append("•  ").append(qtyText).append(" ").append(c.getString(2))
                    .append("  ").append(c.getString(0)).append("\n");
        }
        c.close();
        tvIngredients.setText(list.toString().trim());
    }
}