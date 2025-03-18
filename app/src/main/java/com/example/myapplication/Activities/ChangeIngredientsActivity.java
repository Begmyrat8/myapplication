package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.Models.IngredientsModel;
import com.example.myapplication.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChangeIngredientsActivity extends AppCompatActivity {

    SQLiteDatabase database;
    EditText edit_title, edit_value, edit_price;
    String title,  value, price, units, dessert_id;
    int id;
    byte [] img;
    AutoCompleteTextView autoComplete, autoComplete2, autoCompleteDelete, autoCompleteDelete2;
    TextView textInputLayout;
    ArrayAdapter<String> adapterItem;
    ImageView imageView, edit_image, setting;
    Toolbar toolbar;
    ImageButton change_ingredient_img, button, button2;
    Button save_btn;
    private LinearLayout dynamicContainer;
    private final Map<String, View> ingredientViewsMap = new HashMap<>();
    private final Map<String, View> ingredientViewsMap2 = new HashMap<>();
    private Map<String, ArrayList<String>> ingredientCategories = new HashMap<>();
    ArrayList<String> items;
    boolean hasIngredients = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_ingredients);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(this);
        databaseAccess.open();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        findView();
        get_and_set_intent_data();
        imagePick();
        updateDataAndInsertData();

        loadSavedData();

        toolbar.setSubtitle(getString(R.string.change));

        adapterItem = new ArrayAdapter<>(this, R.layout.item_list, items);
        autoComplete.setAdapter(adapterItem);
        autoCompleteDelete.setAdapter(adapterItem);


        imageView.setOnClickListener(v -> onBackPressed());

        setting.setVisibility(View.GONE);

        button2.setOnClickListener(view -> showAddCategoryIngredientDialog());
//        button.setOnClickListener(view -> showAddIngredientDialog());

//        autoComplete.setOnClickListener(view -> {
//            if (ingredientCategories.isEmpty()){
//                Toast.makeText(this, "Список пуст, пожалйуста добавьте категорию", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        autoComplete2.setOnClickListener(view -> {
//            if (ingredientCategories.isEmpty()){
//                Toast.makeText(this, "Список пуст, пожалйуста добавьте ингредиент", Toast.LENGTH_SHORT).show();
//            }
//        });

        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (ingredientCategories.containsKey(selectedItem)) {
                    ArrayList<String> selectedList = ingredientCategories.get(selectedItem);
                    adapterItem = new ArrayAdapter<>(ChangeIngredientsActivity.this, R.layout.item_list, selectedList);
                } else {
                    adapterItem = new ArrayAdapter<>(ChangeIngredientsActivity.this, R.layout.item_list, new ArrayList<>());
                }
                autoComplete2.setAdapter(adapterItem);
                autoCompleteDelete2.setAdapter(adapterItem);
                addIngredientViews(selectedItem);
            }
        });

        autoComplete2.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            addIngredientViews(selectedItem);
        });

        autoCompleteDelete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = parent.getItemAtPosition(position).toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Удалить категорию и ингредиенты?");
            builder.setMessage("Вы уверены, что хотите удалить категорию '" + selectedCategory + "' и все её ингредиенты?");

            builder.setPositiveButton("Да", (dialogInterface, which) -> {
                // Удаление категории и ингредиентов
                if (ingredientCategories.containsKey(selectedCategory)) {
                    ArrayList<String> ingredients = ingredientCategories.get(selectedCategory);
                    if (ingredients != null) {
                        ingredients.clear(); // Очищаем список ингредиентов этой категории
                    }
                    ingredientCategories.remove(selectedCategory); // Удаляем категорию из общего списка
                    items.remove(selectedCategory); // Удаляем категорию из списка категорий

                    View viewToRemove = ingredientViewsMap.get(selectedCategory);
                    if (viewToRemove != null) {
                        dynamicContainer.removeView(viewToRemove);
                        ingredientViewsMap.remove(selectedCategory);
                    }
                }

                // Обновляем данные в AutoCompleteTextView
                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
                autoComplete.setAdapter(autoCompleteAdapter);

                autoComplete.setText(""); // Очищаем текст в AutoCompleteTextView
                autoCompleteDelete.setText(""); // Очищаем текст в AutoCompleteDelete

                adapterItem.notifyDataSetChanged(); // Обновляем адаптер для autoComplete и autoCompleteDelete
                saveDataToSharedPreferences(); // Сохраняем изменения в SharedPreferences

                Toast.makeText(this, "Категория '" + selectedCategory + "' удалена", Toast.LENGTH_SHORT).show();

                dialogInterface.dismiss();
            });

            builder.setNegativeButton("Отмена", (dialogInterface, which) -> {
                dialogInterface.dismiss();
            });

            builder.show();
        });


        autoCompleteDelete2.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.do_you_want_delete)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {

                        String selectedItem = parent.getItemAtPosition(position).toString();


                        ArrayList<String> dynamicIngredients = ingredientCategories.get(autoComplete.getText().toString());
                        if (dynamicIngredients != null) {
                            dynamicIngredients.remove(selectedItem);
                        }


                        // Удаление из общего списка ингредиентов (items), если он там есть
                        items.remove(selectedItem);
                        adapterItem.notifyDataSetChanged(); // Обновляем адаптер для autoComplete и autoCompleteDelete


                        // Также удалите соответствующий вид из dynamicContainer, если это необходимо
                        // Например:
                        View viewToRemove = ingredientViewsMap.get(selectedItem);
                        if (viewToRemove != null) {
                            dynamicContainer.removeView(viewToRemove);
                            ingredientViewsMap.remove(selectedItem);
                        }

                        // Очистка текста в autoCompleteDelete
                        autoCompleteDelete2.setText("");
                        autoComplete2.setText("");
                        saveDataToSharedPreferences();

                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    });

            builder.show();
        });

    }

    private void showAddIngredientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить новый ингредиент");

        // Inflate and set the layout for the dialog
        View viewInflated = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        final EditText editTextIngredient = viewInflated.findViewById(R.id.editTextIngredient);

        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newIngredient = editTextIngredient.getText().toString().trim();
                if (!TextUtils.isEmpty(newIngredient)) {
                    String selectedCategory = autoComplete.getText().toString().trim();
                    if (!ingredientCategories.containsKey(selectedCategory)) {
                        ingredientCategories.put(selectedCategory, new ArrayList<>());
                    }

                    ArrayList<String> selectedList = ingredientCategories.get(selectedCategory);
                    if (!selectedList.contains(newIngredient)) {
                        selectedList.add(newIngredient);
                        // Update the adapter with the new ingredient
                        adapterItem = new ArrayAdapter<>(ChangeIngredientsActivity.this, R.layout.item_list, selectedList);
                        autoComplete2.setAdapter(adapterItem);
                        autoCompleteDelete2.setAdapter(adapterItem);

                        saveDataToSharedPreferences();
                    }
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    @SuppressLint("MissingInflatedId")
    private void showAddCategoryIngredientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить новый ингредиент");

        // Inflate and set the layout for the dialog
        View viewInflated = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        final EditText editTextCategory = viewInflated.findViewById(R.id.editTextIngredient);
         final TextView textCategory = viewInflated.findViewById(R.id.textViewForCategory);

        builder.setView(viewInflated);
        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategory = editTextCategory.getText().toString().trim();
                if (!TextUtils.isEmpty(newCategory) && !items.contains(newCategory)) {
                    items.add(newCategory);
                    adapterItem.notifyDataSetChanged();

                    // Create a new ingredient list for the new category
                    ingredientCategories.put(newCategory, new ArrayList<>());

                    saveDataToSharedPreferences();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void saveDataToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("ingredient_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save categories
        editor.putString("categories", TextUtils.join(",", items));

        // Save ingredients for each category
        for (Map.Entry<String, ArrayList<String>> entry : ingredientCategories.entrySet()) {
            String category = entry.getKey();
            ArrayList<String> ingredients = entry.getValue();
            editor.putString(category, TextUtils.join(",", ingredients));
        }

        editor.apply();
    }
    private void loadSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("ingredient_data", MODE_PRIVATE);

        // Load categories
        String categoriesString = sharedPreferences.getString("categories", "");
        if (!TextUtils.isEmpty(categoriesString)) {
            items = new ArrayList<>(Arrays.asList(categoriesString.split(",")));
        } else {
            items = new ArrayList<>(); // Ensure items is initialized if data is empty
        }

        // Initialize or reset adapterItem
        adapterItem = new ArrayAdapter<>(this, R.layout.item_list, items);
        autoComplete.setAdapter(adapterItem);

        // Load ingredients for each category
        for (String category : items) {
            String ingredientsString = sharedPreferences.getString(category, "");
            if (!TextUtils.isEmpty(ingredientsString)) {
                ArrayList<String> ingredients = new ArrayList<>(Arrays.asList(ingredientsString.split(",")));
                ingredientCategories.put(category, ingredients);
            } else {
                ingredientCategories.put(category, new ArrayList<>()); // Ensure ingredientCategories has an entry
            }
        }
    }

    private void findView() {

        autoCompleteDelete2 = findViewById(R.id.autoCompleteDelete4);
        autoCompleteDelete = findViewById(R.id.autoCompleteDelete3);
        button2 = findViewById(R.id.button4);
        button = findViewById(R.id.button3);
        autoComplete2 = findViewById(R.id.autoComplete22);
        dynamicContainer = findViewById(R.id.dynamic_container2);
        setting = findViewById(R.id.setting);
        textInputLayout = findViewById(R.id.textView8);
        autoComplete = findViewById(R.id.autoComplete11);
        edit_image = findViewById(R.id.edit_image);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        edit_title = findViewById(R.id.edit_title);
        save_btn = findViewById(R.id.save_product);
        change_ingredient_img = findViewById(R.id.change_ingredient_img);

    }
    private void updateDataAndInsertData() {
        save_btn.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("ingredient_preferences", MODE_PRIVATE);
             boolean hasIngredients = validateAndPrepareIngredients(ingredientViewsMap2);

            if (hasIngredients) {
                boolean inserted = updateTitleIfNotExists();
                if (inserted) {
                    Toast.makeText(ChangeIngredientsActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
                    updateProcessIngredients(sharedPreferences);
                    finish();
                } else {
                    Toast.makeText(ChangeIngredientsActivity.this, R.string.ingredient_already_exists, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ChangeIngredientsActivity.this, "Необходимо добавить хотя бы один ингредиент", Toast.LENGTH_SHORT).show();
            }
             hasIngredients = validateAndPrepareIngredients(ingredientViewsMap);

            if (hasIngredients) {
                boolean inserted = updateTitleIfNotExists();
                if (inserted) {
                    Toast.makeText(ChangeIngredientsActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
                    insertProcessIngredients(sharedPreferences);
                    finish();
                } else {
                    Toast.makeText(ChangeIngredientsActivity.this, R.string.ingredient_already_exists, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ChangeIngredientsActivity.this, "Необходимо добавить хотя бы один ингредиент", Toast.LENGTH_SHORT).show();
            }


        });
    }

    private boolean validateAndPrepareIngredients(Map<String, View> map) {
        boolean hasIngredients = false;

        for (Map.Entry<String, View> entry : map.entrySet()) {
            CardView cardView = (CardView) entry.getValue();

            TextInputEditText valueEditText = cardView.findViewById(R.id.set_value);
            TextInputEditText kgEditText = cardView.findViewById(R.id.countKg);
            TextInputEditText priceEditText = cardView.findViewById(R.id.set_price);

            if (isEmpty(valueEditText, kgEditText, priceEditText)) {
                Toast.makeText(ChangeIngredientsActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                return false;
            }
            hasIngredients = true;
        }
        return hasIngredients;
    }

    private boolean isEmpty(TextInputEditText... editTexts) {
        for (TextInputEditText editText : editTexts) {
            if (editText == null || TextUtils.isEmpty(editText.getText().toString().trim())) {
                return true;
            }
        }
        return false;
    }

    private void updateProcessIngredients(SharedPreferences sharedPreferences) {
        for (Map.Entry<String, View> entry : ingredientViewsMap2.entrySet()) {
            String ingredientTitle = entry.getKey();
            CardView cardView = (CardView) entry.getValue();

            TextInputEditText valueEditText = cardView.findViewById(R.id.set_value);
            TextInputEditText kgEditText = cardView.findViewById(R.id.countKg);
            TextInputEditText priceEditText = cardView.findViewById(R.id.set_price);

            try {
                String unit = sharedPreferences.getString(ingredientTitle, "");

                double value = Double.parseDouble(String.valueOf(valueEditText.getText()));
                double kg = Double.parseDouble(String.valueOf(kgEditText.getText()));
                double price = Double.parseDouble(String.valueOf(priceEditText.getText()));

                double adjustedValue = adjustValueBasedOnUnit(unit, value);
                double totalPrice = adjustedValue * price;

                updateIngredient(String.valueOf(this.id), ingredientTitle, value, price, kg, totalPrice, unit);
            } catch (NumberFormatException e) {
                Toast.makeText(ChangeIngredientsActivity.this, "Ошибка формата числа. Убедитесь, что используете точку.", Toast.LENGTH_SHORT).show();
            }


        }
    }
    private void insertProcessIngredients(SharedPreferences sharedPreferences) {
        for (Map.Entry<String, View> entry : ingredientViewsMap.entrySet()) {
            String ingredient = entry.getKey();
            CardView cardView = (CardView) entry.getValue();

            TextInputEditText valueEditText = cardView.findViewById(R.id.set_value);
            TextInputEditText kgEditText = cardView.findViewById(R.id.countKg);
            TextInputEditText priceEditText = cardView.findViewById(R.id.set_price);

            try {
                String unit = sharedPreferences.getString(ingredient, "");

                double value = Double.parseDouble(String.valueOf(valueEditText.getText()));
                double kg = Double.parseDouble(String.valueOf(kgEditText.getText()));
                double price = Double.parseDouble(String.valueOf(priceEditText.getText()));

                double adjustedValue = adjustValueBasedOnUnit(unit, value);
                double totalPrice = adjustedValue * price;

                insertIngredient(String.valueOf(this.id), ingredient, value, price, kg, totalPrice, dessert_id, unit);
            } catch (NumberFormatException e) {
                Toast.makeText(ChangeIngredientsActivity.this, "Ошибка формата числа. Убедитесь, что используете точку.", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private double adjustValueBasedOnUnit(String unit, double value) {
        if ("Кг".equals(unit)) {
            return value / 1000; // Convert from grams to kilograms
        } else if ("Штук".equals(unit)) {
            return value; // No conversion for pieces
        } else {
            return value / 1000; // Default to converting to kilograms
        }
    }


    private boolean updateTitleIfNotExists() {
        ContentValues contentValues = new ContentValues();
        String title = Objects.requireNonNull(edit_title.getText()).toString();
        contentValues.put("title", title);
        contentValues.put("units", autoComplete.getText().toString());

        Cursor cursor = database.query("dessert", new String[]{"COUNT(*)"}, "title=?", new String[]{title}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            if (count > 1) { // assuming you want to check for more than one occurrence
                return false;
            }
        }

        int updatedRows = database.update("list", contentValues, "id=" + id, null);
        return updatedRows > 0;
    }
    @SuppressLint("SetTextI18n")
    void get_and_set_intent_data(){

        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("price") && getIntent().hasExtra("image") && getIntent().hasExtra("value") && getIntent().hasExtra("units")&& getIntent().hasExtra("ingredientTitle") && getIntent().hasExtra("dessert_id")){

            id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));
            title = getIntent().getStringExtra("title");
            value = getIntent().getStringExtra("value");
            price = getIntent().getStringExtra("price");
            img = getIntent().getByteArrayExtra("image");
            units = getIntent().getStringExtra("units");


            edit_title.setText(title);

            setIngredientViews(getIntent().getStringExtra("ingredientTitle"), id);

        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }

    }
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void setIngredientViews(final String ingredientTitle, int listId) {
        if (ingredientViewsMap2.containsKey(ingredientTitle)) {
            return;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        List<IngredientsModel> ingredients = getIngredientsByListId(listId);
        SharedPreferences sharedPreferences = getSharedPreferences("ingredient_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (IngredientsModel ingredient : ingredients) {
            // Загрузка разметки карточки ингредиента
            CardView cardView = (CardView) inflater.inflate(R.layout.a, dynamicContainer, false);

            TextView ingredientTextView = cardView.findViewById(R.id.ingredientTextView);
            ingredientTextView.setText(ingredient.getTitle());

            ImageButton ingredientButton = cardView.findViewById(R.id.ingredientButton);

            TextInputEditText editValue = cardView.findViewById(R.id.set_value);
            editValue.setText(formatNumber(ingredient.getValue(), decimalFormat));

            TextInputEditText editPrice = cardView.findViewById(R.id.set_price);
            editPrice.setText(formatNumber(ingredient.getPrice(), decimalFormat));

            TextInputEditText editUnit = cardView.findViewById(R.id.countKg);
            editUnit.setText(formatNumber(ingredient.getKg(), decimalFormat));

            CheckBox kgCheckBox = cardView.findViewById(R.id.kgCheckBox);
            CheckBox piecesCheckBox = cardView.findViewById(R.id.piecesCheckBox);

            String savedUnit = sharedPreferences.getString(ingredient.getTitle(), "Кг");
            kgCheckBox.setChecked("Кг".equals(savedUnit));
            piecesCheckBox.setChecked("Штук".equals(savedUnit));

            kgCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    piecesCheckBox.setChecked(false);
                    editor.putString(ingredient.getTitle(), "Кг");
                    editor.apply();
                }
            });

            piecesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    kgCheckBox.setChecked(false);
                    editor.putString(ingredient.getTitle(), "Штук");
                    editor.apply();
                }
            });

            ingredientButton.setOnClickListener(v -> {
                dynamicContainer.removeView(cardView);
                ingredientViewsMap2.remove(ingredientTitle);
                database.delete("ingredients", "title =?", new String[]{ingredient.getTitle()});
                saveDataToSharedPreferences();
            });

            dynamicContainer.addView(cardView);
            ingredientViewsMap2.put(ingredient.getTitle(), cardView);
        }
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void addIngredientViews(final String ingredient) {
        if (ingredientViewsMap.containsKey(ingredient)) {
            return;
        }
        if (ingredientViewsMap2.containsKey(ingredient)) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        CardView cardView = (CardView) inflater.inflate(R.layout.a, dynamicContainer, false);

        TextView ingredientTextView = cardView.findViewById(R.id.ingredientTextView);
        ingredientTextView.setText(ingredient);

        ImageButton ingredientButton = cardView.findViewById(R.id.ingredientButton);

        CheckBox kgCheckBox = cardView.findViewById(R.id.kgCheckBox);
        CheckBox piecesCheckBox = cardView.findViewById(R.id.piecesCheckBox);

        SharedPreferences sharedPreferences = getSharedPreferences("ingredient_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        kgCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                piecesCheckBox.setChecked(false);
                editor.putString(ingredient, "Кг");
                editor.apply();
            }
        });

        piecesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                kgCheckBox.setChecked(false);
                editor.putString(ingredient, "Штук");
                editor.apply();
            }
        });

        ingredientButton.setOnClickListener(v -> {
            dynamicContainer.removeView(cardView);
            ingredientViewsMap.remove(ingredient);
        });

        dynamicContainer.addView(cardView);
        ingredientViewsMap.put(ingredient, cardView);
    }

    private void removeIngredientFromAutoComplete(String ingredient) {
        String currentText = autoComplete.getText().toString();
        if (currentText.contains(ingredient)) {
            currentText = currentText.replace(ingredient + ", ", "");
            currentText = currentText.replace(", " + ingredient, "");
            currentText = currentText.replace(ingredient, "");
            autoComplete.setText(currentText);
            autoComplete.setSelection(currentText.length());
        }
    }
    private void updateIngredient(String id, String ingredient, double amount, double price, double kg, double gram_price, String unit) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("value", amount);
        contentValues.put("price", price);
        contentValues.put("kg", kg);
        contentValues.put("gram_price", gram_price);
        contentValues.put("unit", unit);

        // Попробуем сначала выполнить update
        long result = database.update("ingredients", contentValues, "list_id=? AND title=?", new String[]{id, ingredient});

        if (result == -1) {
            Toast.makeText(ChangeIngredientsActivity.this, "Ошибка обновления ингредиента", Toast.LENGTH_SHORT).show();
        } else if (result == 0) {
            // Если update не обновил ни одной строки, попробуем вставить новый ингредиент
            long newResult = database.insert("ingredients", null, contentValues);
            if (newResult == -1) {
                Toast.makeText(ChangeIngredientsActivity.this, "Ошибка вставки нового ингредиента", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChangeIngredientsActivity.this, "Ингредиент успешно добавлен", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ChangeIngredientsActivity.this, "Ингредиент успешно обновлен", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertIngredient(String listId, String ingredient, double amount, double price, double gram_price, double kg, String dessertId, String unit) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_id", listId);
        contentValues.put("title", ingredient);
        contentValues.put("value", amount);
        contentValues.put("price", price);
        contentValues.put("gram_price", gram_price);
        contentValues.put("kg", kg);
        contentValues.put("dessert_id", dessertId);
        contentValues.put("unit", unit);

        long result = database.insert("ingredients", null, contentValues);
        if (result == -1) {
            Toast.makeText(ChangeIngredientsActivity.this, "Failed to insert ingredient: " + ingredient, Toast.LENGTH_SHORT).show();
        }
    }
    private byte [] ImageViewToByte(ImageView set_image){
        Bitmap bitmap =((BitmapDrawable)set_image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
        return stream.toByteArray();
    }

    private String formatNumber(double number, DecimalFormat decimalFormat) {
        if (number == (long) number) {
            return String.valueOf((long) number); // Если целое, возвращаем без дробной части
        }
        return decimalFormat.format(number); // Иначе с двумя знаками после запятой
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        Uri uri = data.getData();
        edit_image.setImageURI(uri);
    }

    private void setMode() {
        String mode = getSharedPreferences("Settings", MODE_PRIVATE).getString("mode", "light");

        // Применяем тему перед super.onCreate()
        switch (mode) {
            case "dark":
                setTheme(R.style.AppTheme_Dark);
                break;
            case "blue":
                setTheme(R.style.AppTheme_Blue);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }
    private List<IngredientsModel> getIngredientsByListId(int listId) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();
        return databaseAccess.getIngredientsByListId(listId);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void imagePick(){
        change_ingredient_img.setOnClickListener(v -> pickFromGallery());
        edit_image.setOnClickListener(view -> pickFromGallery());
    }
    private void pickFromGallery() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .crop(1f, 1f)
                .start();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (!ingredientViewsMap.isEmpty() || !ingredientViewsMap2.isEmpty()) {
            finish();
        } else {
            Toast.makeText(ChangeIngredientsActivity.this, "Необходимо добавить хотя бы один ингредиент", Toast.LENGTH_SHORT).show();

        }
    }
}