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
import com.example.myapplication.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddIngredientsActivity extends AppCompatActivity {

    Button add_product;
    ImageButton  button, button2;
    String COL_TITLE = "title";
    String COL_VALUE = "value";
    String COL_PRICE = "price";
    String COL_GRAM_PRICE = "gram_price";
    String COL_IMAGE = "image";
    String COL_DESSERT_ID = "dessert_id";
    String COL_UNIT = "units";
    int dessert_id;
    ImageView imageView, set_image, setting;
    TextInputEditText set_title;
    EditText set_value, set_price;
    AutoCompleteTextView autoComplete, autoComplete2, autoCompleteDelete, autoCompleteDelete2;
    ArrayAdapter<String> adapterItem;
    Toolbar toolbar;
    ImageButton add_ingredient_img;
    private SQLiteDatabase database;
    DatabaseAccess databaseAccess;
    private LinearLayout dynamicContainer;
    private final Map<String, View> ingredientViewsMap = new HashMap<>();
    private final Map<String, ArrayList<String>> ingredientCategories = new HashMap<>();
    ArrayList<String> items;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);


        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        findView();
        insertData();
        imagePick();
        loadSavedData();

        adapterItem = new ArrayAdapter<>(this, R.layout.item_list, items);
        autoComplete.setAdapter(adapterItem);
        autoCompleteDelete.setAdapter(adapterItem);

        toolbar.setSubtitle(getString(R.string.add));
        setting.setVisibility(View.GONE);

        imageView.setOnClickListener(v -> finish());

        button2.setOnClickListener(view -> showAddCategoryIngredientDialog());
        button.setOnClickListener(view -> showAddIngredientDialog());
        
//        autoComplete.setOnClickListener(view -> {
//            if (autoComplete.getText().toString().isEmpty()){
//                Toast.makeText(this, "Список пуст, пожалйуста добавьте категорию", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        autoComplete2.setOnClickListener(view -> {
//            if (autoComplete2.getText().toString().isEmpty()){
//                Toast.makeText(this, "Список пуст, пожалйуста добавьте ингредиент", Toast.LENGTH_SHORT).show();
//            }
//        });

        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (ingredientCategories.containsKey(selectedItem)) {
                    ArrayList<String> selectedList = ingredientCategories.get(selectedItem);
                    adapterItem = new ArrayAdapter<>(AddIngredientsActivity.this, R.layout.item_list, Objects.requireNonNull(selectedList));
                } else {
                    adapterItem = new ArrayAdapter<>(AddIngredientsActivity.this, R.layout.item_list, new ArrayList<>());
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
                        adapterItem = new ArrayAdapter<>(AddIngredientsActivity.this, R.layout.item_list, selectedList);
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


    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void addIngredientViews(final String ingredient) {
        if (ingredientViewsMap.containsKey(ingredient)) {
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
        set_value = new TextInputEditText(countInputLayout.getContext());
        set_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        countInputLayout.addView(set_value, new LinearLayout.LayoutParams(
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

        // Создаем LinearLayout для ввода количества, цены
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

        set_price = new TextInputEditText(priceInputLayout.getContext());
        set_price.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceInputLayout.addView(set_price, new LinearLayout.LayoutParams(
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
        String currentText = autoComplete2.getText().toString();
        if (currentText.contains(ingredient)) {
            currentText = currentText.replace(ingredient + ", ", "");
            currentText = currentText.replace(", " + ingredient, "");
            currentText = currentText.replace(ingredient, "");
            autoComplete2.setText(currentText);
            autoComplete2.setSelection(currentText.length());
        }
    }

    // Inside insertData() method
    private void insertData() {
        add_product.setOnClickListener(view -> {
            boolean hasIngredients = false;

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
                    Toast.makeText(AddIngredientsActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                    return;
                }

                hasIngredients = true;
            }

            if (hasIngredients) {
                boolean inserted = insertTitleIfNotExists(Objects.requireNonNull(set_title.getText()).toString());
                if (inserted) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
                    String ingredient_id = sharedPreferences.getString("id", "1");
                    dessert_id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("dessertId")));
                    Toast.makeText(AddIngredientsActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(AddIngredientsActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences sharedPreferences2 = getSharedPreferences("ingredient_preferences", MODE_PRIVATE);
                        String unit = sharedPreferences2.getString(ingredient, "");
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

                        insertIngredient(ingredient_id, ingredient, amountStr, price, r, amountStr2, dessert_id);
                    }
                    finish();

                } else {
                    Toast.makeText(AddIngredientsActivity.this, R.string.ingredient_already_exists, Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(AddIngredientsActivity.this, "Необходимо добавить хотя бы один ингредиент", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void findView() {

        autoCompleteDelete = findViewById(R.id.autoCompleteDelete);
        button2 = findViewById(R.id.button2);
        autoCompleteDelete2 = findViewById(R.id.autoCompleteDelete2);
        set_image = findViewById(R.id.set_image);
        set_title = findViewById(R.id.set_title);
        autoComplete = findViewById(R.id.autoComplete);
        autoComplete2 = findViewById(R.id.autoComplete2);
        button = findViewById(R.id.button);
        add_product = findViewById(R.id.add_product);
        imageView = findViewById(R.id.imageView);
        setting = findViewById(R.id.setting);
        toolbar = findViewById(R.id.toolbar);
        add_ingredient_img = findViewById(R.id.add_ingredient_img);
        dynamicContainer = findViewById(R.id.dynamic_container);
    }
    private boolean insertTitleIfNotExists(String title) {
        try (Cursor cursor = database.rawQuery("SELECT * FROM list WHERE title = ?", new String[]{title})) {
            dessert_id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("dessertId")));
            if (cursor.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COL_TITLE, Objects.requireNonNull(set_title.getText()).toString());
                contentValues.put(COL_DESSERT_ID, dessert_id);
                contentValues.put(COL_UNIT, autoComplete.getText().toString());

                if (set_title.getText().toString().isEmpty()) {
                    return false;
                }

                database.insert("list", null, contentValues);
                return true;
            } else {
                return false;
            }
        }
    }
    private void insertIngredient(String listId, String ingredient, double amount, double price, double gram_price, int kg, int dessertId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("list_id", listId);
        contentValues.put(COL_TITLE, ingredient);
        contentValues.put(COL_VALUE, amount);
        contentValues.put(COL_PRICE, price);
        contentValues.put(COL_GRAM_PRICE, gram_price);
        contentValues.put("kg", kg);
        contentValues.put(COL_DESSERT_ID, dessertId);

        long result = database.insert("ingredients", null, contentValues);
        if (result == -1) {
            Toast.makeText(AddIngredientsActivity.this, "Failed to insert ingredient: " + ingredient, Toast.LENGTH_SHORT).show();
        }
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


    private byte[] imageViewToByte(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    @SuppressLint("NonConstantResourceId")
    private void setMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("night", 0);
        boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);

        if (isNightMode) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void imagePick() {
        add_ingredient_img.setOnClickListener(v -> ImagePicker.with(AddIngredientsActivity.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            set_image.setImageURI(uri);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseAccess.close();
    }

}
