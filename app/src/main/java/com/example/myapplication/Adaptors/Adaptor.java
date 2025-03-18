package com.example.myapplication.Adaptors;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.ChangeIngredientsActivity;
import com.example.myapplication.Activities.IngredientActivity;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.Models.IngredientsModel;
import com.example.myapplication.Models.Model;
import com.example.myapplication.R;

import java.text.DecimalFormat;
import java.util.List;

public class Adaptor extends RecyclerView.Adapter<Adaptor.CategoryViewHolder> {

    private final Context context;
    private final List<Model> list;
    private SQLiteDatabase database;

    public Adaptor(Context context, List<Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredients_model, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DatabaseOpenHelper databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();


        // Сохраняем id текущего элемента в настройках
        SharedPreferences preferences = context.getSharedPreferences("Setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", String.valueOf(list.get(position).getId() + 1));
        editor.apply();

        // Устанавливаем название элемента
        String name = list.get(position).getTitle();
        holder.title.setText(name);

        // Обработчик для кнопки изменения (показываем меню)
        holder.change_btn.setOnClickListener(v -> showPopupMenu(v, position));

        // Обработчик для кнопки сворачивания/разворачивания деталей
        holder.imageView4.setOnClickListener(v -> toggleDetailVisibility(holder));

        // Добавляем динамические TextView в контейнеры
        addDynamicTextViews(holder.fromRecipeContainer, list.get(position).getId());
        addDynamicTextViews2(holder.yourContainer, list.get(position).getId());
        addDynamicTextViews4(holder.resultContainer2,list.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView title, textView5;
        ImageView imageView4;
        ImageButton change_btn;
        String kg, small_price, piece, liter, unit, gram, milliliter;
        String[] items;
        ConstraintLayout c, gram_price_container_c, price_container_c;
        LinearLayout fromRecipeContainer, yourContainer, resultContainer, resultContainer2;

        @SuppressLint("WrongViewCast")
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView5 = itemView.findViewById(R.id.textView5);
            c = itemView.findViewById(R.id.c);
            imageView4 = itemView.findViewById(R.id.imageView4);
            title = itemView.findViewById(R.id.title_edit_text);
            change_btn = itemView.findViewById(R.id.change_btn);
            items = itemView.getResources().getStringArray(R.array.items);
            kg = itemView.getResources().getString(R.string.kg);
            small_price = itemView.getResources().getString(R.string.small_price);
            liter = itemView.getResources().getString(R.string.liter);
            piece = itemView.getResources().getString(R.string.piece);
            unit = itemView.getResources().getString(R.string.unit);
            gram = itemView.getResources().getString(R.string.gram);
            milliliter = itemView.getResources().getString(R.string.milliliter);
            fromRecipeContainer = itemView.findViewById(R.id.fromRecipeContainer);
            yourContainer = itemView.findViewById(R.id.yourContainer);
            resultContainer = itemView.findViewById(R.id.resultContainer);
            resultContainer2 = itemView.findViewById(R.id.resultContainer2);

        }
    }

    private void calculateShapeConversion(double value, TextView textView) {
        IngredientActivity activity = (IngredientActivity) context;
        String shape = activity.getShape();
        String newShape = activity.getNewShape();
        double newWidth = activity.getNewWidth();
        double newHeight = activity.getNewHeight();
        double originalWidth = activity.getOriginalWidth();
        double originalHeight = activity.getOriginalHeight();
        double coefficient = activity.getCoif();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        if (shape.equals("circle") && (newShape.equals("rectangle") || newShape.equals("square"))) {
            double originalArea = newShape.equals("rectangle") ?
                    newWidth * newHeight :
                    Math.pow(newWidth, 2);
            double newArea = Math.PI * Math.pow(newWidth / 2, 2);
            if (originalArea < newArea) {
                coefficient = newArea / originalArea;
                textView.setText(formatNumber((coefficient * value),decimalFormat));
            } else {
                coefficient = originalArea / newArea;
                textView.setText(formatNumber((coefficient / value),decimalFormat));
            }
        } else if (newShape.equals("circle") && (shape.equals("rectangle") || shape.equals("square"))) {
            double originalArea = shape.equals("rectangle") ?
                    originalWidth * originalHeight :
                    Math.pow(originalWidth, 2);
            double newArea = Math.PI * Math.pow(newWidth / 2, 2);
            if (originalArea < newArea) {
                coefficient = newArea / originalArea;
                textView.setText(formatNumber((coefficient * value),decimalFormat));
            } else {
                coefficient = originalArea / newArea;
                textView.setText(formatNumber((coefficient / value),decimalFormat));
            }
        } else {
            textView.setText(formatNumber((coefficient * value),decimalFormat));
        }
    }

    private void showPopupMenu(View v, int listPosition) {
        List<IngredientsModel> ingredients = getIngredientsByListId(list.get(listPosition).getId());

        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_delete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.do_you_want_delete)
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            database.delete("list", "id=" + list.get(listPosition).getId(), null);
                            database.delete("ingredients", "id=" + list.get(listPosition).getId(), null);
                            list.remove(listPosition);
                            notifyItemRemoved(listPosition);

                            ((IngredientActivity) context).refresh();
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                return true;
            } else if (itemId == R.id.nav_edit) {
                // Ensure the position is valid for the ingredients list
                IngredientsModel ingredient;
                if (listPosition < ingredients.size()) {
                    ingredient = ingredients.get(listPosition);
                } else {
                    // Default to the first ingredient if position is out of bounds
                    ingredient = ingredients.get(0);
                }

                Intent intent = new Intent(context, ChangeIngredientsActivity.class);
                intent.putExtra("id", String.valueOf(list.get(listPosition).getId()));
                intent.putExtra("title", String.valueOf(list.get(listPosition).getTitle()));
                intent.putExtra("value", String.valueOf(ingredient.getValue()));
                intent.putExtra("price", String.valueOf(ingredient.getPrice()));
                intent.putExtra("image", list.get(listPosition).getImage());
                intent.putExtra("units", String.valueOf(list.get(listPosition).getUnits()));
                intent.putExtra("styles", ((IngredientActivity) context).setMode());
                intent.putExtra("ingredientTitle", String.valueOf(ingredient.getTitle()));
                intent.putExtra("dessert_id", String.valueOf(list.get(listPosition).getDessert_id()));

                context.startActivity(intent);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
    private void toggleDetailVisibility(CategoryViewHolder holder) {
        if (holder.c.getVisibility() == View.GONE) {
            holder.c.setVisibility(View.VISIBLE);
            holder.gram_price_container_c.setVisibility(VISIBLE);
            holder.price_container_c.setVisibility(VISIBLE);
            holder.textView5.setVisibility(VISIBLE);
            holder.imageView4.setImageResource(R.drawable.up);
        } else {
            holder.c.setVisibility(View.GONE);
            holder.gram_price_container_c.setVisibility(View.GONE);
            holder.price_container_c.setVisibility(View.GONE);
            holder.textView5.setVisibility(View.GONE);
            holder.imageView4.setImageResource(R.drawable.down);
        }
    }
    private String formatNumber(double number, DecimalFormat decimalFormat) {
        if (number == (long) number) {
            return String.valueOf((long) number); // Если целое, возвращаем без дробной части
        }
        return decimalFormat.format(number); // Иначе с двумя знаками после запятой
    }


    @SuppressLint("SetTextI18n")
    private void addDynamicTextViews(LinearLayout container, int listId) {
        List<IngredientsModel> ingredients = getIngredientsByListId(listId);
        container.removeAllViews();

        DecimalFormat format = new DecimalFormat("#.##");
        // Create TableLayout
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tableLayout.setStretchAllColumns(true);

        // Header row
        TableRow headerRow = new TableRow(context);
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        headerRow.setPadding(0, 16, 16, 16);  // Padding for headers

        TextView nameHint = new TextView(context);
        nameHint.setText("Ингредиенты");
        nameHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        nameHint.setTextColor(ContextCompat.getColor(context, R.color.grey));
        nameHint.setGravity(Gravity.CENTER);
        headerRow.addView(nameHint);

        TextView amountHint = new TextView(context);
        amountHint.setText("Вес");
        amountHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        amountHint.setTextColor(ContextCompat.getColor(context, R.color.grey));
        amountHint.setGravity(Gravity.CENTER);
        headerRow.addView(amountHint);

        TextView kg_priceHint = new TextView(context);
        kg_priceHint.setText("Кг/цена");
        kg_priceHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        kg_priceHint.setTextColor(ContextCompat.getColor(context, R.color.grey));
        kg_priceHint.setGravity(Gravity.CENTER);
        headerRow.addView(kg_priceHint);

        TextView priceHint = new TextView(context);
        priceHint.setText("Цена");
        priceHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        priceHint.setTextColor(ContextCompat.getColor(context, R.color.green));
        priceHint.setGravity(Gravity.CENTER);
        headerRow.addView(priceHint);

        tableLayout.addView(headerRow);

        // Add rows for each ingredient
        for (IngredientsModel ingredient : ingredients) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setGravity(Gravity.CENTER_VERTICAL);  // Center elements vertically

            TextView nameTextView = new TextView(context);
            nameTextView.setText(ingredient.getTitle());
            nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            nameTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            nameTextView.setGravity(Gravity.CENTER);
            nameTextView.setPadding(0, 16, 16, 16);
            row.addView(nameTextView);

            TextView quantityTextView = new TextView(context);
            quantityTextView.setText(formatNumber(ingredient.getValue(), format));
            quantityTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            quantityTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            quantityTextView.setGravity(Gravity.CENTER);
            row.addView(quantityTextView);

            TextView kgPriceTextView = new TextView(context);
            kgPriceTextView.setText(formatNumber(ingredient.getPrice(),format));
            kgPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            kgPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            kgPriceTextView.setGravity(Gravity.CENTER);
            row.addView(kgPriceTextView);

            TextView actualPriceTextView = new TextView(context);
            actualPriceTextView.setText(formatNumber(ingredient.getGram_price(),format));
            actualPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            actualPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
            actualPriceTextView.setGravity(Gravity.CENTER);
            row.addView(actualPriceTextView);

            tableLayout.addView(row);
        }

        // Retrieve total price and weight
        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();

        double totalPrice = Double.parseDouble(databaseAccess.getSumPrice2(String.valueOf(listId)));
        double totalWeight = Double.parseDouble(databaseAccess.getSumGramPies(String.valueOf(listId)));
        double totalKgPrice = Double.parseDouble(databaseAccess.getSumPrice3(String.valueOf(listId)));

        // Bottom row for total values
        TableRow bottomRow = new TableRow(context);
        bottomRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        bottomRow.setGravity(Gravity.CENTER_VERTICAL);

        // Empty cell for the first column (Ingredients)
        TextView total = new TextView(context);
        total.setText("Итого :");
        total.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        total.setTextColor(ContextCompat.getColor(context, R.color.red));
        total.setGravity(Gravity.CENTER);
        bottomRow.addView(total);

        // Total weight under the "Вес" column
        TextView totalWeightTextView = new TextView(context);
        totalWeightTextView.setText(formatNumber(totalWeight,format));
        totalWeightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        totalWeightTextView.setTextColor(ContextCompat.getColor(context, R.color.orange));
        totalWeightTextView.setGravity(Gravity.CENTER);
        bottomRow.addView(totalWeightTextView);

        // Empty cell for the "Кг/цена" column
        TextView totalKgPriceTextView = new TextView(context);
        totalKgPriceTextView.setText(formatNumber(totalKgPrice,format));  // No text, just a placeholder
        totalKgPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        totalKgPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
        totalKgPriceTextView.setGravity(Gravity.CENTER);
        bottomRow.addView(totalKgPriceTextView);

        // Total price under the "Цена" column
        TextView totalPriceTextView = new TextView(context);
        totalPriceTextView.setText(formatNumber(totalPrice,format));
        totalPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        totalPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        totalPriceTextView.setGravity(Gravity.CENTER);
        bottomRow.addView(totalPriceTextView);

        tableLayout.addView(bottomRow);

        container.addView(tableLayout);  // Add the TableLayout to the main container
    }


    @SuppressLint("SetTextI18n")
    private void addDynamicTextViews2(LinearLayout container, int listId) {
        List<IngredientsModel> ingredients = getIngredientsByListId(listId);
        container.removeAllViews();

        DecimalFormat format = new DecimalFormat("#.##");

        // Create TableLayout
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tableLayout.setStretchAllColumns(true);

        // Header row
        TableRow headerRow = new TableRow(context);
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        headerRow.setPadding(0, 16, 16, 8);  // Padding for headers

        TextView nameHint = new TextView(context);
        nameHint.setText("Ингредиенты");
        nameHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        nameHint.setTextColor(ContextCompat.getColor(context, R.color.grey));
        nameHint.setGravity(Gravity.CENTER);
        headerRow.addView(nameHint);

        TextView amountHint = new TextView(context);
        amountHint.setText("Вес");
        amountHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        amountHint.setTextColor(ContextCompat.getColor(context, R.color.grey));
        amountHint.setGravity(Gravity.CENTER);
        headerRow.addView(amountHint);

        TextView kg_priceHint = new TextView(context);
        kg_priceHint.setText("Кг/цена");
        kg_priceHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        kg_priceHint.setTextColor(ContextCompat.getColor(context, R.color.grey));
        kg_priceHint.setGravity(Gravity.CENTER);
        headerRow.addView(kg_priceHint);

        TextView priceHint = new TextView(context);
        priceHint.setText("Цена");
        priceHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        priceHint.setTextColor(ContextCompat.getColor(context, R.color.green));
        priceHint.setGravity(Gravity.CENTER);
        headerRow.addView(priceHint);

        tableLayout.addView(headerRow);

        for (IngredientsModel ingredient : ingredients) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setGravity(Gravity.CENTER_VERTICAL);  // Center elements vertically

            TextView nameTextView = new TextView(context);
            nameTextView.setText(ingredient.getTitle());
            nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            nameTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            nameTextView.setPadding(0, 16, 16, 16);
            nameTextView.setGravity(Gravity.CENTER);
            row.addView(nameTextView);

            TextView quantityTextView = new TextView(context);
            calculateShapeConversion(ingredient.getValue(), quantityTextView);
            quantityTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            quantityTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            quantityTextView.setGravity(Gravity.CENTER);
            row.addView(quantityTextView);

            TextView kgPriceTextView = new TextView(context);
            kgPriceTextView.setText(formatNumber(ingredient.getPrice(),format));
            kgPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            kgPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
            kgPriceTextView.setGravity(Gravity.CENTER);
            row.addView(kgPriceTextView);


            TextView actualPriceTextView = new TextView(context);
            actualPriceTextView.setText(formatNumber(ingredient.getGram_price(),format));
            actualPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            actualPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
            actualPriceTextView.setGravity(Gravity.CENTER);
            row.addView(actualPriceTextView);

            tableLayout.addView(row);
        }

        // Retrieve total price and weight
        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();

        double totalPrice = Double.parseDouble(databaseAccess.getSumPrice2(String.valueOf(listId)));
        double totalWeight = Double.parseDouble(databaseAccess.getSumGramPies(String.valueOf(listId)));
        double totalKgPrice = Double.parseDouble(databaseAccess.getSumPrice3(String.valueOf(listId)));

        // Bottom row for total values
        TableRow bottomRow = new TableRow(context);
        bottomRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        bottomRow.setGravity(Gravity.CENTER_VERTICAL);

        // TextView для Итого
        TextView allpriceTextView = new TextView(context);
        allpriceTextView.setText("Итого: ");
        allpriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        allpriceTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
        allpriceTextView.setGravity(Gravity.CENTER);
        bottomRow.addView(allpriceTextView);

        // Значение вес ингредиента
        TextView allactualPriceTextView = new TextView(context);
        allactualPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        allactualPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.orange));
        allactualPriceTextView.setGravity(Gravity.CENTER);
        bottomRow.addView(allactualPriceTextView);

        // Установка значения цены
        calculateShapeConversion(totalWeight, allactualPriceTextView);

        // TextView для текста кг/цены
        TextView allkgPriceTextView = new TextView(context);
        allkgPriceTextView.setText(format.format(totalKgPrice));
        allkgPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        allkgPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
        allkgPriceTextView.setGravity(Gravity.CENTER);
        bottomRow.addView(allkgPriceTextView);

        // Значение цены ингредиента
        TextView allactualKgPriceTextView = new TextView(context);
        allactualKgPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        allactualKgPriceTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        allactualKgPriceTextView.setGravity(Gravity.CENTER);
        bottomRow.addView(allactualKgPriceTextView);

        // Установка значения веса
        calculateShapeConversion(totalPrice, allactualKgPriceTextView);

        // Добавляем горизонтальный layout для кг/цены в основной контейнер
        tableLayout.addView(bottomRow);

        container.addView(tableLayout);
    }


    @SuppressLint("SetTextI18n")
    private void addDynamicTextViews4(LinearLayout container, int listId){
        container.removeAllViews();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();

        double price = Double.parseDouble(databaseAccess.getSumPrice2(String.valueOf(listId)));
        double weight = Double.parseDouble(databaseAccess.getSumGramPies(String.valueOf(listId)));

        DecimalFormat format = new DecimalFormat("#0.0");

        // Horizontal LinearLayout для цены
        LinearLayout allpriceLayout = new LinearLayout(context);
        allpriceLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        allpriceLayout.setOrientation(LinearLayout.HORIZONTAL);
        allpriceLayout.setGravity(Gravity.CENTER_VERTICAL); // Выравнивание по вертикали


    }

    private List<IngredientsModel> getIngredientsByListId(int listId) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();
        return databaseAccess.getIngredientsByListId(listId);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}

