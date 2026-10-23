package com.example.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SmartPantryDb";
    private static final int VERSION = 1;

    //pantry items table
    public static final String PANTRYITEM_TABLE = "PantryItems";
    public static final String PANTRYITEM_ID = "id";
    public static final String PANTRYITEM_NAME = "item_name";
    public static final String PANTRYITEM_QUANTITY = "quantity";
    public static final String PANTRYITEM_EXPIRY_DATE = "expiry_date";

    // recipe table
    public static final String RECIPE_TABLE = "Recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_STEPS = "recipe_steps";

    //recipe ingredients table
    public static final String RECIPEINGRED_TABLE = "RecipeIngredients";
    public static final  String RECIPE_ID_FK = "recipe_id";
    public static final String PANTRYITEM_ID_FK = "item_id";
    public static final String AMOUNT_NEEDED = "amount_needed";


    public MyDatabaseHelper(@Nullable Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }


    //create table
    @Override
    public void onCreate(SQLiteDatabase db){
        //create pantry table
        String PantryTableQuery = "CREATE TABLE " + PANTRYITEM_TABLE + " (" +
                PANTRYITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PANTRYITEM_NAME + " TEXT NOT NULL, " +
                PANTRYITEM_QUANTITY + " INTEGER DEFAULT 1, " +
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
                RECIPE_ID_FK + " INTEGER, " +
                PANTRYITEM_ID_FK + " INTEGER, " +
                AMOUNT_NEEDED + " TEXT, " +
                "PRIMARY KEY (" + RECIPE_ID_FK + ", " + PANTRYITEM_ID_FK + "), " +
                "FOREIGN KEY (" + RECIPE_ID_FK + ") REFERENCES " + RECIPE_TABLE + "(" + RECIPE_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + PANTRYITEM_ID_FK + ") REFERENCES " + PANTRYITEM_TABLE + "(" + PANTRYITEM_ID + ") ON DELETE CASCADE " +
                ");";
        db.execSQL(RecipeIngredTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " +  PANTRYITEM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " +  RECIPE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " +  RECIPEINGRED_TABLE);
        onCreate(db);
    }
}
