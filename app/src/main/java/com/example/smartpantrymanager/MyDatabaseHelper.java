package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SmartPantryDb";
    private static final int VERSION = 2;

    //pantry items table
    public static final String PANTRYITEM_TABLE = "PantryItems";
    public static final String PANTRYITEM_ID = "id";
    public static final String PANTRYITEM_NAME = "item_name";
    public static final String PANTRYITEM_NORMALIZED = "normalized_name";
    public static final String PANTRYITEM_QUANTITY = "quantity";
    public static final String PANTRYITEM_UNIT = "unit";
    public static final String PANTRYITEM_EXPIRY_DATE = "expiry_date";

    // recipe table
    public static final String RECIPE_TABLE = "Recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_STEPS = "recipe_steps";

    //recipe ingredients table
    public static final String RECIPEINGRED_TABLE = "RecipeIngredients";
    public static final String RECIPE_ID_FK = "recipe_id";
    public static final String RECIPEINGRED_NORMALIZED = "normalized_name";
    public static final String RECIPEINGRED_QUANTITY = "quantity";
    public static final String RECIPEINGRED_UNIT = "unit";


    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    //create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create pantry table
        String PantryTableQuery = "CREATE TABLE " + PANTRYITEM_TABLE + " (" +
                PANTRYITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PANTRYITEM_NAME + " TEXT NOT NULL, " +
                PANTRYITEM_NORMALIZED + " TEXT NOT NULL, " +
                PANTRYITEM_QUANTITY + " REAL NOT NULL DEFAULT 1, " +
                PANTRYITEM_UNIT + " TEXT NOT NULL, " +
                PANTRYITEM_EXPIRY_DATE + " TEXT" +
                ");";
        db.execSQL(PantryTableQuery);

        //create recipe table
        String RecipeTableQuery = "CREATE TABLE " + RECIPE_TABLE + " (" +
                RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RECIPE_NAME + " TEXT NOT NULL, " +
                RECIPE_STEPS + " TEXT " +
                ");";
        db.execSQL(RecipeTableQuery);

        //create recipe ingredients table
        String RecipeIngredTableQuery = "CREATE TABLE " + RECIPEINGRED_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RECIPE_ID_FK + " INTEGER NOT NULL, " +
                RECIPEINGRED_NORMALIZED + " TEXT NOT NULL, " +
                RECIPEINGRED_QUANTITY + " REAL NOT NULL, " +
                RECIPEINGRED_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + RECIPE_ID_FK + ") REFERENCES " +
                RECIPE_TABLE + "(" + RECIPE_ID + ") ON DELETE CASCADE);";
        db.execSQL(RecipeIngredTableQuery);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RECIPEINGRED_TABLE);   // child first
        db.execSQL("DROP TABLE IF EXISTS " + RECIPE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PANTRYITEM_TABLE);
        onCreate(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Tomato Omelette", "1. Beat the eggs.\n2. Chop the tomatoes.\n3. Cook until set.",
                new String[][]{{"Eggs", "2", "pcs"}, {"Tomatoes", "200", "g"}});
        addRecipe(db, "Pancakes", "1. Whisk into a batter.\n2. Fry until golden.",
                new String[][]{{"Flour", "200", "g"}, {"Milk", "300", "ml"}, {"Eggs", "2", "pcs"}});

    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, String[][] ingredients) {
        ContentValues rv = new ContentValues();
        rv.put(RECIPE_NAME, name);
        rv.put(RECIPE_STEPS, steps);
        long recipeId = db.insert(RECIPE_TABLE, null, rv);

        for (String[] ing : ingredients) {
            ContentValues iv = new ContentValues();
            iv.put(RECIPE_ID_FK, recipeId);
            iv.put(RECIPEINGRED_NORMALIZED, TextHelper.normalize(ing[0]));
            iv.put(RECIPEINGRED_QUANTITY, Double.parseDouble(ing[1]));
            iv.put(RECIPEINGRED_UNIT, ing[2]);
            db.insert(RECIPEINGRED_TABLE, null, iv);
        }
    }
}
