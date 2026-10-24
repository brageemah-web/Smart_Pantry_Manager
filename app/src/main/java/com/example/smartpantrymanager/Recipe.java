package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Recipe extends AppCompatActivity {
    ListView recipeListView;
    TextView emptyView;
    MyDatabaseHelper dbHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> recipeNames = new ArrayList<>();
    ArrayList<Integer> recipeIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.recipe_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipe), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        dbHelper = new MyDatabaseHelper(this);
        recipeListView = findViewById(R.id.recipeListView);
        emptyView = findViewById(R.id.emptyView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeNames);
        recipeListView.setAdapter(adapter);
        recipeListView.setEmptyView(emptyView);   // shown when the list is empty
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }


    private void loadSuggestions() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Map<String, Double> pantry = new HashMap<>();
        Cursor p = db.query(MyDatabaseHelper.PANTRYITEM_TABLE,
                new String[]{MyDatabaseHelper.PANTRYITEM_NORMALIZED,
                        MyDatabaseHelper.PANTRYITEM_QUANTITY,
                        MyDatabaseHelper.PANTRYITEM_UNIT},
                null, null, null, null, null);
        while (p.moveToNext()) {
            String key = makeKey(p.getString(0), p.getString(2));
            double qty = toBase(p.getDouble(1), p.getString(2));
            Double old = pantry.get(key);
            pantry.put(key, (old == null ? 0 : old) + qty);   // same item added twice = summed
        }
        p.close();

        recipeNames.clear();
        recipeIds.clear();
        Cursor r = db.query(MyDatabaseHelper.RECIPE_TABLE,
                new String[]{MyDatabaseHelper.RECIPE_ID, MyDatabaseHelper.RECIPE_NAME},
                null, null, null, null, MyDatabaseHelper.RECIPE_NAME);
        while (r.moveToNext()) {
            if (canMake(db, r.getInt(0), pantry)) {
                recipeIds.add(r.getInt(0));
                recipeNames.add(r.getString(1));
            }
        }
        r.close();
        adapter.notifyDataSetChanged();
    }


    private boolean canMake(SQLiteDatabase db, int recipeId, Map<String, Double> pantry) {
        Cursor c = db.query(MyDatabaseHelper.RECIPEINGRED_TABLE,
                new String[]{MyDatabaseHelper.RECIPEINGRED_NORMALIZED,
                        MyDatabaseHelper.RECIPEINGRED_QUANTITY,
                        MyDatabaseHelper.RECIPEINGRED_UNIT},
                MyDatabaseHelper.RECIPE_ID_FK + " = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        boolean ok = c.getCount() > 0;
        while (ok && c.moveToNext()) {
            Double have = pantry.get(makeKey(c.getString(0), c.getString(2)));
            double need = toBase(c.getDouble(1), c.getString(2));
            if (have == null || have < need) {
                ok = false;
            }
        }
        c.close();
        return ok;
    }


    private double toBase(double qty, String unit) {
        if (unit.equalsIgnoreCase("kg") || unit.equalsIgnoreCase("l")) return qty * 1000;
        return qty;
    }


    private String makeKey(String normalizedName, String unit) {
        String u = unit.toLowerCase();
        String type = (u.equals("g") || u.equals("kg")) ? "weight"
                : (u.equals("ml") || u.equals("l")) ? "volume" : "count";
        return normalizedName + "|" + type;
    }
}