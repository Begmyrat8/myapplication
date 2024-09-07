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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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
import com.google.android.material.textfield.TextInputLayout;

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
    private final Map<String, LinearLayout> ingredientViewsMap2 = new HashMap<String, LinearLayout>();
    private final Map<String, TextInputEditText> ingredientValueMap = new HashMap<>();
    private final Map<String, TextInputEditText> ingredientKgMap = new HashMap<>();
    private final Map<String, TextInputEditText> ingredientPriceMap = new HashMap<>();
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

            for (Map.Entry<String, LinearLayout> entry : ingredientViewsMap2.entrySet()) {
                String ingredientTitle = entry.getKey();
                TextInputEditText valueEditText = ingredientValueMap.get(ingredientTitle);
                TextInputEditText kgEditText = ingredientKgMap.get(ingredientTitle);
                TextInputEditText priceEditText = ingredientPriceMap.get(ingredientTitle);

                if (valueEditText == null || kgEditText == null || priceEditText == null ||
                        TextUtils.isEmpty(Objects.requireNonNull(valueEditText.getText()).toString().trim()) ||
                        TextUtils.isEmpty(Objects.requireNonNull(priceEditText.getText()).toString().trim()) ||
                        TextUtils.isEmpty(Objects.requireNonNull(kgEditText.getText()).toString().trim())) {
                    Toast.makeText(ChangeIngredientsActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                    return;
                }
                hasIngredients = true;
            }

            if (hasIngredients) {
                boolean inserted = updateTitleIfNotExists();
                if (inserted) {
                    Toast.makeText(ChangeIngredientsActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();

                    for (Map.Entry<String, LinearLayout> entry : ingredientViewsMap2.entrySet()) {
                        String ingredientTitle = entry.getKey();
                        TextInputEditText valueEditText = ingredientValueMap.get(ingredientTitle);
                        TextInputEditText kgEditText = ingredientKgMap.get(ingredientTitle);
                        TextInputEditText priceEditText = ingredientPriceMap.get(ingredientTitle);

                        if (valueEditText == null || kgEditText == null || priceEditText == null ||
                                TextUtils.isEmpty(Objects.requireNonNull(valueEditText.getText()).toString().trim()) ||
                                TextUtils.isEmpty(Objects.requireNonNull(priceEditText.getText()).toString().trim()) ||
                                TextUtils.isEmpty(Objects.requireNonNull(kgEditText.getText()).toString().trim())) {
                            Toast.makeText(ChangeIngredientsActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            String unit = sharedPreferences.getString(ingredientTitle, ""); // Получение сохраненного значения (Кг или Штук)

                            double value = Double.parseDouble(Objects.requireNonNull(valueEditText.getText()).toString());
                            double kg = Double.parseDouble(Objects.requireNonNull(kgEditText.getText()).toString());
                            double priceStr = Double.parseDouble(Objects.requireNonNull(priceEditText.getText()).toString());
                            double price = kg * priceStr;
                            double v;

                            // Проверяем, что сохранено в SharedPreferences: Кг или Штук
                            if ("Кг".equals(unit)) {
                                v = value / 1000; // Если выбран "Кг", делим на 1000
                            } else if ("Штук".equals(unit)) {
                                v = value; // Если выбран "Штук", не делим
                            } else {
                                v = value / 1000; // По умолчанию делим на 1000
                            }

                            double r = v * price;
                            // Call the updateIngredient method for each ingredient
                            updateIngredient(String.valueOf(this.id), ingredientTitle, value, price, kg, r, unit);
                        } catch (NumberFormatException e) {
                            Toast.makeText(ChangeIngredientsActivity.this, "Ошибка формата числа", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    finish();
                } else {
                    Toast.makeText(ChangeIngredientsActivity.this, R.string.ingredient_already_exists, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ChangeIngredientsActivity.this, "Необходимо добавить хотя бы один ингредиент", Toast.LENGTH_SHORT).show();
            }

            for (Map.Entry<String, View> entry : ingredientViewsMap.entrySet()) {
                LinearLayout container = (LinearLayout) entry.getValue();

                // Find the TextInputLayouts
                LinearLayout detailsContainer = (LinearLayout) container.getChildAt(1);
                TextInputLayout countInputLayout = (TextInputLayout) detailsContainer.getChildAt(0);

                LinearLayout detailsContainer2 = (LinearLayout) detailsContainer.getChildAt(2);
                TextInputLayout countInputLayout2 = (TextInputLayout) detailsContainer2.getChildAt(0);
                TextInputLayout priceInputLayout = (TextInputLayout) detailsContainer2.getChildAt(1);

                // Get TextInputEditTexts from TextInputLayouts
                TextInputEditText amountEditText = (TextInputEditText) countInputLayout.getEditText();
                TextInputEditText amountEditText2 = (TextInputEditText) countInputLayout2.getEditText();
                TextInputEditText priceEditText = (TextInputEditText) priceInputLayout.getEditText();

                if (TextUtils.isEmpty(Objects.requireNonNull(Objects.requireNonNull(amountEditText).getText()).toString().trim()) ||
                        TextUtils.isEmpty(Objects.requireNonNull(Objects.requireNonNull(priceEditText).getText()).toString().trim()) ||
                        TextUtils.isEmpty(Objects.requireNonNull(Objects.requireNonNull(amountEditText2).getText()).toString().trim())) {
                    Toast.makeText(ChangeIngredientsActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                    return;
                }

                hasIngredients = true;
            }

            if (hasIngredients) {
                boolean inserted = updateTitleIfNotExists();
                if (inserted) {
                    dessert_id = (Objects.requireNonNull(getIntent().getStringExtra("dessert_id")));
                    Toast.makeText(ChangeIngredientsActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();

                    for (Map.Entry<String, View> entry : ingredientViewsMap.entrySet()) {
                        String ingredient = entry.getKey();
                        LinearLayout container = (LinearLayout) entry.getValue();

                        // Find the TextInputLayouts
                        LinearLayout detailsContainer = (LinearLayout) container.getChildAt(1);
                        TextInputLayout countInputLayout = (TextInputLayout) detailsContainer.getChildAt(0);

                        LinearLayout detailsContainer2 = (LinearLayout) detailsContainer.getChildAt(2);
                        TextInputLayout countInputLayout2 = (TextInputLayout) detailsContainer2.getChildAt(0);
                        TextInputLayout priceInputLayout = (TextInputLayout) detailsContainer2.getChildAt(1);

                        // Get TextInputEditTexts from TextInputLayouts
                        TextInputEditText amountEditText = (TextInputEditText) countInputLayout.getEditText();
                        TextInputEditText amountEditText2 = (TextInputEditText) countInputLayout2.getEditText();
                        TextInputEditText priceEditText = (TextInputEditText) priceInputLayout.getEditText();

                        if (TextUtils.isEmpty(Objects.requireNonNull(Objects.requireNonNull(amountEditText).getText()).toString().trim()) ||
                                TextUtils.isEmpty(Objects.requireNonNull(Objects.requireNonNull(priceEditText).getText()).toString().trim())) {
                            Toast.makeText(ChangeIngredientsActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String unit = sharedPreferences.getString(ingredient, "");
                        // Get text from TextInputEditTexts
                        double amountStr = Double.parseDouble(Objects.requireNonNull(amountEditText.getText()).toString());
                        int amountStr2 = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(amountEditText2).getText()).toString());
                        double priceStr = Double.parseDouble(Objects.requireNonNull(priceEditText.getText()).toString());
                        double price = amountStr2 * priceStr;
                        double v;

                        // Проверяем, что сохранено в SharedPreferences: Кг или Штук
                        if ("Кг".equals(unit)) {
                            v = amountStr / 1000; // Если выбран "Кг", делим на 1000
                        } else if ("Штук".equals(unit)) {
                            v = amountStr; // Если выбран "Штук", не делим
                        } else {
                            v = amountStr / 1000; // По умолчанию делим на 1000
                        }

                        double r = v * price;

                        insertIngredient(String.valueOf(this.id), ingredient, amountStr, price, r, amountStr2, dessert_id, unit);
                    }
                    finish();

                } else {
                    Toast.makeText(ChangeIngredientsActivity.this, R.string.ingredient_already_exists, Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(ChangeIngredientsActivity.this, "Необходимо добавить хотя бы один ингредиент", Toast.LENGTH_SHORT).show();
            }
        });
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
            // Если ингредиент уже существует, нет необходимости добавлять его снова
            return;
        }

        List<IngredientsModel> ingredients = getIngredientsByListId(listId);
        DecimalFormat format = new DecimalFormat();

        SharedPreferences sharedPreferences = getSharedPreferences("ingredient_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        for (IngredientsModel ingredient : ingredients) {
            // Создаем CardView для ингредиента
            CardView cardView = new CardView(this);
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(20, 20, 20, 20); // Установка отступов
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(12); // Установка радиуса углов CardView
            cardView.setCardBackgroundColor(getResources().getColor(R.color.white)); // Установка цвета фона CardView


            // Создаем контейнер для ImageButton и EditText внутри CardView
            final LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(16, 16, 16, 16); // Padding для контейнера

            // Создаем RelativeLayout для TextView и ImageButton
            RelativeLayout buttonContainer = new RelativeLayout(this);

            // Создаем TextView для отображения названия ингредиента
            TextView ingredientTextView = new TextView(this);
            ingredientTextView.setText(ingredient.getTitle());
            ingredientTextView.setPadding(0, 16, 16, 16);
            ingredientTextView.setTextSize(16);
            ingredientTextView.setTextColor(getResources().getColor(android.R.color.black));

            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            ingredientTextView.setLayoutParams(textParams);

            // Создаем ImageButton для ингредиента
            ImageButton ingredientButton = new ImageButton(this);
            ingredientButton.setImageResource(R.drawable.ic_delete);
            ingredientButton.setBackgroundResource(android.R.color.transparent);

            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            ingredientButton.setLayoutParams(buttonParams);

            // Добавляем TextView и ImageButton в buttonContainer
            buttonContainer.addView(ingredientTextView);
            buttonContainer.addView(ingredientButton);

            // Добавляем buttonContainer в основной контейнер
            container.addView(buttonContainer);

            // Создаем LinearLayout для ввода количества и цены
            LinearLayout detailsContainer = new LinearLayout(this);
            detailsContainer.setOrientation(LinearLayout.VERTICAL);

            LinearLayout detailsContainer3 = new LinearLayout(this);
            detailsContainer3.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            detailsContainer3.setLayoutParams(detailsParams);

            // Создаем TextInputLayout для первого количества (или количество)
            TextInputLayout countInputLayout = new TextInputLayout(this, null, R.style.TextInput);
            countInputLayout.setHint("Количество");
            countInputLayout.setBoxStrokeColor(getResources().getColor(R.color.chocolate)); // Установка цвета обводки
            countInputLayout.setLayoutParams(detailsParams);

            TextInputEditText editValue = new TextInputEditText(countInputLayout.getContext());
            editValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editValue.setText(format.format(ingredient.getValue()));
            countInputLayout.addView(editValue, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Сохранение ссылки на edit_value
            ingredientValueMap.put(ingredient.getTitle(), editValue);

            // Создаем контейнер для CheckBox
            LinearLayout checkBoxContainer = new LinearLayout(this);
            checkBoxContainer.setOrientation(LinearLayout.HORIZONTAL);
            checkBoxContainer.setGravity(Gravity.START);
            checkBoxContainer.setPadding(0, 10, 0, 20);
            LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            checkBoxContainer.setLayoutParams(checkBoxParams);

            // Создаем CheckBox для "Кг"
            CheckBox kgCheckBox = new CheckBox(this);
            kgCheckBox.setText("Кг");
            kgCheckBox.setChecked(true);
            checkBoxContainer.addView(kgCheckBox);

            // Создаем CheckBox для "Штук"
            CheckBox piecesCheckBox = new CheckBox(this);
            piecesCheckBox.setText("Штук");
            checkBoxContainer.addView(piecesCheckBox);

            String savedUnit = sharedPreferences.getString(ingredient.getTitle(), "Кг");
            if ("Кг".equals(savedUnit)) {
                kgCheckBox.setChecked(true);
                piecesCheckBox.setChecked(false);
            } else {
                kgCheckBox.setChecked(false);
                piecesCheckBox.setChecked(true);
            }


            // Слушатель для CheckBox "Кг"
            kgCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    piecesCheckBox.setChecked(false);
                    editor.putString(ingredient.getTitle(), "Кг");
                    editor.apply();
                }
            });

            // Слушатель для CheckBox "Штук"
            piecesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    kgCheckBox.setChecked(false);
                    editor.putString(ingredient.getTitle(), "Штук");
                    editor.apply();
                }
            });

            // Создаем LinearLayout для ввода количества, цены
            LinearLayout detailsContainer2 = new LinearLayout(this);
            detailsContainer2.setOrientation(LinearLayout.HORIZONTAL);
            detailsContainer2.setLayoutParams(detailsParams);

            // Создаем TextInputLayout для кг/шт
            TextInputLayout kgInputLayout = new TextInputLayout(this, null, R.style.TextInput);
            TextInputEditText countKg = new TextInputEditText(kgInputLayout.getContext());
            countKg.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            countKg.setHint("Кг/шт");
            countKg.setWidth(150);
            countKg.setText(format.format(ingredient.getKg()));
            kgInputLayout.addView(countKg, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            ingredientKgMap.put(ingredient.getTitle(), countKg);

            // Создаем TextInputLayout для цены
            TextInputLayout priceInputLayout = new TextInputLayout(this, null, R.style.TextInput);
            priceInputLayout.setHint("Цена");
            priceInputLayout.setLayoutParams(detailsParams);

            TextInputEditText editPrice = new TextInputEditText(priceInputLayout.getContext());
            editPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editPrice.setText(format.format(ingredient.getPrice() / ingredient.getKg()));
            priceInputLayout.addView(editPrice, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            ingredientPriceMap.put(ingredient.getTitle(), editPrice);

            // Добавляем все элементы в контейнеры
            detailsContainer.addView(countInputLayout);
            detailsContainer.addView(checkBoxContainer);
            detailsContainer2.addView(kgInputLayout);
            detailsContainer2.addView(priceInputLayout);
            detailsContainer.addView(detailsContainer2);
            container.addView(detailsContainer);

            // Добавляем контейнер в CardView
            cardView.addView(container);

            // Добавляем CardView в динамический контейнер
            dynamicContainer.addView(cardView);

            // Сохраняем контейнер в карте
            ingredientViewsMap2.put(ingredient.getTitle(), container);

            // Устанавливаем обработчик нажатия для ImageButton
            ingredientButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(this, v);
                popupMenu.getMenuInflater().inflate(R.menu.mode_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_setting) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                        builder.setTitle(ingredient.getTitle())
                                .setCancelable(true)
                                .setPositiveButton(R.string.yes, (dialog, which) -> {
                                    ArrayList<String> selectedList = ingredientCategories.get(ingredient.getTitle());
                                    if (selectedList != null) {
                                        selectedList.remove(ingredient.getTitle());
                                        items.remove(ingredient.getTitle());
                                        if (selectedList.isEmpty()) {
                                            ingredientCategories.remove(ingredient.getTitle());
                                            items.remove(ingredient.getTitle());
                                        }
                                        adapterItem.notifyDataSetChanged(); // Update the adapter to refresh the list
                                    }
                                    dynamicContainer.removeView(cardView);
                                    ingredientViewsMap2.remove(ingredientTitle);
                                    removeIngredientFromAutoComplete(ingredientTitle);
                                    database.delete("ingredients", "title =?", new String[]{ingredient.getTitle()});
                                    saveDataToSharedPreferences();
                                })
                                .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

                        android.app.AlertDialog alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                        return true;
                    } else if (itemId == R.id.delete_all) {
                        dynamicContainer.removeView(cardView);
                        ingredientViewsMap2.remove(ingredientTitle);
                        removeIngredientFromAutoComplete(ingredientTitle);
                        database.delete("ingredients", "title =?", new String[]{ingredient.getTitle()});
                        saveDataToSharedPreferences();
                    }
                    return true;
                });
                popupMenu.show();
                saveDataToSharedPreferences();
            });
        }
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void addIngredientViews(final String ingredient) {
        if (ingredientViewsMap.containsKey(ingredient)) {
            // Если ингредиент уже существует, нет необходимости добавлять его снова
            return;
        }
        if (ingredientViewsMap2.containsKey(ingredient)) {
            // Если ингредиент уже существует, нет необходимости добавлять его снова
            return;
        }

        CardView cardView = new CardView(this);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(20, 20, 20, 20); // Установка отступов
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(12); // Установка радиуса углов CardView
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white)); // Установка цвета фона CardView

        // Создаем контейнер для ImageButton и EditText
        final LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 16, 16, 16); // Padding для контейнера

        // Создаем LinearLayout для TextView и ImageButton
        RelativeLayout buttonContainer = new RelativeLayout(this);


        // Создаем TextView для отображения названия ингредиента
        TextView ingredientTextView = new TextView(this);
        ingredientTextView.setText(ingredient);
        ingredientTextView.setPadding(0,16,16,16);
        ingredientTextView.setTextSize(16);
        ingredientTextView.setTextColor(getResources().getColor(android.R.color.black)); // Установка цвета текста через ресурсы

        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        ingredientTextView.setLayoutParams(textParams);

        // Создаем ImageButton для ингредиента
        ImageButton ingredientButton = new ImageButton(this);
        ingredientButton.setImageResource(R.drawable.ic_delete); // Установка соответствующей иконки
        ingredientButton.setBackgroundResource(android.R.color.transparent); // Прозрачный фон

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END); // Выравнивание ImageButton в конце
        ingredientButton.setLayoutParams(buttonParams);


        // Добавляем TextView и ImageButton в buttonContainer
        buttonContainer.addView(ingredientTextView);
        buttonContainer.addView(ingredientButton);

        // Добавляем buttonContainer в основной контейнер
        container.addView(buttonContainer);

        // Создаем LinearLayout для ввода количества, цены
        LinearLayout detailsContainer = new LinearLayout(this);
        detailsContainer.setOrientation(LinearLayout.VERTICAL);

        LinearLayout detailsContainer3 = new LinearLayout(this);
        detailsContainer3.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams detialsParms = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        detailsContainer3.setLayoutParams(detialsParms);

        // Создаем TextInputLayout для первого количества (или количество)
        TextInputLayout countInputLayout = new TextInputLayout(this, null, R.style.TextInput);
        countInputLayout.setHint("Количество");
        countInputLayout.setBoxStrokeColor(getResources().getColor(R.color.chocolate)); // Установка цвета обводки
        countInputLayout.setLayoutParams(detialsParms);
        edit_value = new TextInputEditText(countInputLayout.getContext());
        edit_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        countInputLayout.addView(edit_value, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

//        TextInputLayout set_unit = new TextInputLayout(this, null, R.style.TextInputWithDropDown);
//
//        AutoCompleteTextView unit = new AutoCompleteTextView(set_unit.getContext());
//        unit.setHint("Единица");
//        unit.setInputType(InputType.TYPE_NULL);
//        unit.setWidth(200);
//        set_unit.addView(unit, new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        ));

        // Создаем контейнер для CheckBox
        LinearLayout checkBoxContainer = new LinearLayout(this);
        checkBoxContainer.setOrientation(LinearLayout.HORIZONTAL);
        checkBoxContainer.setGravity(Gravity.START);
        checkBoxContainer.setPadding(0, 10, 0, 20);
        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        checkBoxContainer.setLayoutParams(checkBoxParams);

        // Создаем CheckBox для "Кг"
        CheckBox kgCheckBox = new CheckBox(this);
        kgCheckBox.setText("Кг");
        kgCheckBox.setChecked(true);
        checkBoxContainer.addView(kgCheckBox);

        // Создаем CheckBox для "Штук"
        CheckBox piecesCheckBox = new CheckBox(this);
        piecesCheckBox.setText("Штук");
        checkBoxContainer.addView(piecesCheckBox);

        SharedPreferences sharedPreferences = getSharedPreferences("ingredient_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Слушатель для CheckBox "Кг"
        kgCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                piecesCheckBox.setChecked(false);
                editor.putString(ingredient, "Кг");
                editor.apply();
            }
        });

        // Слушатель для CheckBox "Штук"
        piecesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                kgCheckBox.setChecked(false);
                editor.putString(ingredient, "Штук");
                editor.apply();
            }
        });

        // Создаем LinearLayout для ввода количества, цены
        LinearLayout detailsContainer2 = new LinearLayout(this);
        detailsContainer2.setOrientation(LinearLayout.HORIZONTAL);
        detailsContainer2.setLayoutParams(detialsParms);

        // Создаем TextInputLayout для кг
        TextInputLayout priceInputLayout2 = new TextInputLayout(this, null, R.style.TextInput);

        TextInputEditText countKg = new TextInputEditText(priceInputLayout2.getContext());
        countKg.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        countKg.setHint("Кг/шт");
        countKg.setText("1");
        countKg.setWidth(150);
        countKg.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        priceInputLayout2.addView(countKg, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));


        // Создаем TextInputLayout для цены
        TextInputLayout priceInputLayout = new TextInputLayout(this, null, R.style.TextInput);
        priceInputLayout.setHint("Цена");
        priceInputLayout.setLayoutParams(detialsParms);

        edit_price = new TextInputEditText(priceInputLayout.getContext());
        edit_price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceInputLayout.addView(edit_price, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));


        detailsContainer.addView(countInputLayout);
        detailsContainer.addView(checkBoxContainer);
        detailsContainer2.addView(priceInputLayout2);
        detailsContainer2.addView(priceInputLayout);
        detailsContainer.addView(detailsContainer2);


        // Добавляем вертикальный LinearLayout в основной контейнер
        container.addView(detailsContainer);
        cardView.addView(container);

        // Добавляем контейнер в динамический контейнер
        dynamicContainer.addView(cardView);

        // Добавляем контейнер в карту
        ingredientViewsMap.put(ingredient, container);

        ingredientButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.mode_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_setting) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle(ingredient)
                            .setCancelable(true)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                ArrayList<String> selectedList = ingredientCategories.get(ingredient);
                                if (selectedList != null) {
                                    selectedList.remove(ingredient);
                                    items.remove(ingredient);
                                    if (selectedList.isEmpty()) {
                                        ingredientCategories.remove(ingredient);
                                        items.remove(ingredient);
                                    }
                                    adapterItem.notifyDataSetChanged(); // Обновляем адаптер
                                }
                                // Удаляем контейнер из пользовательского интерфейса
                                dynamicContainer.removeView(cardView);
                                // Удаляем ингредиент из карты
                                ingredientViewsMap.remove(ingredient);
                                // Удаляем ингредиент из автодополнения
                                removeIngredientFromAutoComplete(ingredient);

                                int rowsDeleted = database.delete("ingredients", "title =?", new String[]{ingredient});
                                if (rowsDeleted > 0) {
                                    saveDataToSharedPreferences(); // Сохраняем изменения только если удаление прошло успешно
                                } else {
                                    // Можно добавить обработку ошибки или вывод логов
                                    Log.e("DeleteError", "Ingredient not deleted from database: " + ingredient);
                                }
                                saveDataToSharedPreferences();
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    return true;
                } else if (itemId == R.id.delete_all) {
                    // Удаляем контейнер из пользовательского интерфейса
                    dynamicContainer.removeView(cardView);
                    // Удаляем ингредиент из карты
                    ingredientViewsMap.remove(ingredient);
                    // Удаляем ингредиент из автодополнения
                    removeIngredientFromAutoComplete(ingredient);

                    int rowsDeleted = database.delete("ingredients", "title =?", new String[]{ingredient});
                    if (rowsDeleted > 0) {
                        saveDataToSharedPreferences(); // Сохраняем изменения только если удаление прошло успешно
                    } else {
                        // Можно добавить обработку ошибки или вывод логов
                        Log.e("DeleteError", "Ingredient not deleted from database: " + ingredient);
                    }
                }

                return true;
            });

            popupMenu.show();
        });



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

    private void insertIngredient(String listId, String ingredient, double amount, double price, double gram_price, int kg, String dessertId, String unit) {
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