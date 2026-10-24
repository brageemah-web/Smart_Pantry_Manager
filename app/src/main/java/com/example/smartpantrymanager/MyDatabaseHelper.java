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
        addRecipe(db, "Tomato Omelette",
                "1. Beat the eggs.\n2. Chop the tomatoes.\n3. Cook in a pan until set.",
                new String[][]{{"Eggs", "2", "pcs"}, {"Tomatoes", "200", "g"}});

        addRecipe(db, "Pancakes",
                "1. Whisk everything into a batter.\n2. Fry ladlefuls until golden on both sides.",
                new String[][]{{"Flour", "200", "g"}, {"Milk", "300", "ml"}, {"Eggs", "2", "pcs"}});

        addRecipe(db, "Scrambled Eggs",
                "1. Melt the butter in a pan.\n2. Add the beaten eggs and milk.\n3. Stir gently until just set.",
                new String[][]{{"Eggs", "3", "pcs"}, {"Butter", "10", "g"}, {"Milk", "30", "ml"}});

        addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt the butter and pour in the eggs.\n3. Add the cheese, fold, and serve.",
                new String[][]{{"Eggs", "3", "pcs"}, {"Cheese", "50", "g"}, {"Butter", "10", "g"}});

        addRecipe(db, "Mushroom Omelette",
                "1. Fry the sliced mushrooms in butter.\n2. Pour in the beaten eggs.\n3. Fold and serve.",
                new String[][]{{"Eggs", "3", "pcs"}, {"Mushrooms", "100", "g"}, {"Butter", "10", "g"}});

        addRecipe(db, "Potato Omelette",
                "1. Fry the sliced potatoes and onion in oil until soft.\n2. Pour over the beaten eggs.\n3. Cook until set and flip.",
                new String[][]{{"Potatoes", "400", "g"}, {"Eggs", "4", "pcs"}, {"Onions", "1", "pcs"}, {"Oil", "30", "ml"}});

        addRecipe(db, "Mashed Potatoes",
                "1. Boil the peeled potatoes until soft.\n2. Drain and mash with butter and milk.",
                new String[][]{{"Potatoes", "500", "g"}, {"Butter", "30", "g"}, {"Milk", "100", "ml"}});

        addRecipe(db, "Fried Rice",
                "1. Fry the chopped onion and carrot in oil.\n2. Add the cooked rice.\n3. Push aside, scramble the eggs, and mix together.",
                new String[][]{{"Rice", "200", "g"}, {"Eggs", "2", "pcs"}, {"Carrots", "1", "pcs"},
                        {"Onions", "1", "pcs"}, {"Oil", "15", "ml"}});

        addRecipe(db, "Chicken Rice Bowl",
                "1. Fry the chicken and onion in oil until cooked through.\n2. Serve over cooked rice.",
                new String[][]{{"Chicken", "300", "g"}, {"Rice", "200", "g"}, {"Onions", "1", "pcs"}, {"Oil", "15", "ml"}});

        addRecipe(db, "Lemon Chicken",
                "1. Fry the chicken in oil with the crushed garlic.\n2. Squeeze over the lemon juice.\n3. Cook until golden.",
                new String[][]{{"Chicken", "300", "g"}, {"Lemons", "1", "pcs"}, {"Garlic", "2", "pcs"}, {"Oil", "15", "ml"}});

        addRecipe(db, "Tomato Pasta",
                "1. Boil the pasta.\n2. Fry the garlic and tomatoes in oil into a sauce.\n3. Toss the pasta in the sauce.",
                new String[][]{{"Pasta", "200", "g"}, {"Tomatoes", "300", "g"}, {"Garlic", "2", "pcs"}, {"Oil", "15", "ml"}});

        addRecipe(db, "Garlic Butter Pasta",
                "1. Boil the pasta.\n2. Melt the butter and fry the garlic.\n3. Toss the pasta in the garlic butter.",
                new String[][]{{"Pasta", "200", "g"}, {"Butter", "40", "g"}, {"Garlic", "3", "pcs"}});

        addRecipe(db, "Tuna Pasta",
                "1. Boil the pasta.\n2. Warm the tuna in oil.\n3. Mix the pasta and tuna together.",
                new String[][]{{"Pasta", "200", "g"}, {"Tuna", "1", "pcs"}, {"Oil", "10", "ml"}});

        addRecipe(db, "Cheese Toastie",
                "1. Butter the bread.\n2. Put the cheese between the slices.\n3. Toast in a pan until golden.",
                new String[][]{{"Bread", "2", "pcs"}, {"Cheese", "40", "g"}, {"Butter", "10", "g"}});

        addRecipe(db, "French Toast",
                "1. Whisk the eggs and milk.\n2. Dip the bread slices in the mixture.\n3. Fry in butter until golden.",
                new String[][]{{"Bread", "4", "pcs"}, {"Eggs", "2", "pcs"}, {"Milk", "100", "ml"}, {"Butter", "10", "g"}});

        addRecipe(db, "Beans on Toast",
                "1. Toast and butter the bread.\n2. Heat the beans.\n3. Spoon the beans over the toast.",
                new String[][]{{"Beans", "200", "g"}, {"Bread", "2", "pcs"}, {"Butter", "10", "g"}});

        addRecipe(db, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix in the eggs and flour.\n3. Fry small spoonfuls until golden.",
                new String[][]{{"Bananas", "2", "pcs"}, {"Eggs", "2", "pcs"}, {"Flour", "100", "g"}});

        addRecipe(db, "Porridge",
                "1. Simmer the oats in the milk for 5 minutes, stirring.\n2. Sweeten with sugar and serve.",
                new String[][]{{"Oats", "80", "g"}, {"Milk", "300", "ml"}, {"Sugar", "10", "g"}});

        addRecipe(db, "Yoghurt Fruit Bowl",
                "1. Spoon the yoghurt into a bowl.\n2. Slice the banana on top.\n3. Drizzle with honey.",
                new String[][]{{"Yoghurt", "200", "g"}, {"Bananas", "1", "pcs"}, {"Honey", "15", "ml"}});
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
