# Smart Pantry Manager

A Java Android app that helps reduce food waste. The user keeps a list of the ingredients they already have at home (their "pantry"), and the app suggests only the recipes they can cook **right now** with those ingredients. No shopping trip required.

---

## The strict-matching rule

A recipe is only suggested if **every** ingredient it needs is in the pantry, in at least the required quantity.

- A recipe that needs 5 ingredients when the pantry has 4 is **not** shown.
- Matching copes with simple real-world messiness:
  - **Singular and plural names:** "Tomatoes", "tomato" and " TOMATOES " all match (names are normalized before comparing).
  - **Unit differences:** 1 kg of rice satisfies a recipe that needs 200 g (kg is converted to g, and l to ml).
  - **Incompatible units:** weight, volume and count are separate groups, so grams are never compared with pieces.
  - **Duplicate entries:** the same ingredient added twice is totalled.

---

## Features

- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Pantry list screen backed by the database
- 20 recipes pre-loaded into the database on first run
- Suggested Recipes screen that lists only recipes the user can make now
- Recipe Detail screen with the full ingredient list and method
- Friendly message when no recipes match ("No recipes match your pantry yet - add more ingredients")
- Input validation on the Add Ingredient form
- Data persists after the app is closed and reopened

---

## Database choice: SQLite

The app uses **SQLite** through `SQLiteOpenHelper`.

Why SQLite:

- It is built into Android and stores data on the device, so the app works fully offline with no server, account or API keys.
- The data is relational (each recipe has many ingredients), which suits tables and foreign keys.
- Data persists between sessions automatically.
- It is the approach covered in the module's persistent data chapter, and it keeps the demo reliable.
- Firebase would need a cloud project and an internet connection, and PostgreSQL would need a separate backend, and neither adds anything for a single-user pantry app.

### Tables

| Table | Columns |
|---|---|
| `PantryItems` | `id`, `item_name`, `normalized_name`, `quantity`, `unit`, `expiry_date` |
| `Recipes` | `id`, `recipe_name`, `recipe_steps` |
| `RecipeIngredients` | `id`, `recipe_id` (foreign key to `Recipes`, cascade delete), `normalized_name`, `quantity`, `unit` |

`normalized_name` is the lowercase, singular form of the ingredient name (for example "Tomatoes" is stored as `tomato`) and is the value used for matching.

---

## Screens

| Screen | Class | Purpose |
|---|---|---|
| Pantry List | `MainActivity` | Shows all pantry items; tap an item to edit its quantity or delete it |
| Add Ingredient | `AddIngredient` | Form with validation to add an item |
| Suggested Recipes | `Recipe` | Runs the strict-matching logic and lists only makeable recipes |
| Recipe Detail | `RecipeDetail` | Shows the ingredients and method for the selected recipe |

Screens are connected with Intents (for example, the selected recipe's id is passed to `RecipeDetail` as an Intent extra).

---

## Project structure

```
app/src/main/java/com/example/smartpantrymanager/
    MainActivity.java        Pantry list, edit quantity and delete dialogs
    AddIngredient.java       Add Ingredient form and validation
    Recipe.java              Suggested Recipes screen and the strict-matching logic
    RecipeDetail.java        Recipe ingredients and method
    MyDatabaseHelper.java    SQLite schema, recipe seeding, version upgrades
    TextHelper.java          Ingredient name normalization
app/src/main/res/layout/     XML layouts for each screen
```

---

## Requirements

- Android Studio (current stable release)
- JDK 17 or newer (the JetBrains Runtime bundled with Android Studio works)
- Android SDK with an emulator image or a physical device running **Android 7.0 (API 24) or newer**
- Language: **Java** (no Kotlin)

---

## Setup and run instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/brageemah-web/Smart_Pantry_Manager.git
   ```
2. **Open the project** in Android Studio: **File > Open**, then select the cloned folder.
3. **Wait for Gradle sync** to finish (progress is shown at the bottom). If prompted, set the Gradle JDK to the bundled JetBrains Runtime under **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**.
4. **Choose a device:** create an emulator in **Device Manager**, or connect a phone with USB debugging enabled.
5. **Press Run** (the green triangle). The app installs and opens on the Pantry List screen.

The database is created automatically on first launch, and the 20 recipes are loaded at the same time.

### Quick test of the matching rule

1. Open **Suggested Recipes** with an empty pantry and check that the "No recipes match" message appears.
2. Add **Eggs 3 pcs**, **Butter 10 g** and **Milk 30 ml**. Scrambled Eggs appears.
3. Add **Cheese 50 g**. Cheese Omelette appears.
4. Edit the eggs down to **1**. Both recipes disappear.
5. Close and reopen the app. The pantry items are still there.

### Resetting the database during development

If you change a table, raise `VERSION` in `MyDatabaseHelper`. `onUpgrade` drops and rebuilds all tables (child table first, because foreign keys are enforced). This clears any saved pantry items.

---

## Academic integrity

This app concept and its implementation are my own original work, completed for Mobile App Development 700.

