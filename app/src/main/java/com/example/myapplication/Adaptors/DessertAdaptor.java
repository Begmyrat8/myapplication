package com.example.myapplication.Adaptors;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.ChangeDessertActivity;
import com.example.myapplication.Activities.IngredientActivity;
import com.example.myapplication.Activities.MainActivity;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.Models.DessertModel;
import com.example.myapplication.R;

import java.text.DecimalFormat;
import java.util.List;

public class DessertAdaptor extends RecyclerView.Adapter<DessertAdaptor.CategoryViewHolder> {

    Context context;
    List<DessertModel> desserts;
    SQLiteDatabase database;
    DatabaseOpenHelper databaseHelper;
     SharedPreferences sharedPreferences;
     SharedPreferences.Editor editor;
     int currentFragment;

    public DessertAdaptor(Context context, List<DessertModel> desserts, int currentFragment) {
        this.context = context;
        this.desserts = desserts;
        this.currentFragment = currentFragment;
        this.sharedPreferences = context.getSharedPreferences("like_prefs", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_model, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DessertModel dessert = desserts.get(position);
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();
        DecimalFormat decimalFormat = new DecimalFormat();

        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String currency = sharedPreferences.getString("currency", "TMT");

        boolean isLiked = this.sharedPreferences.getBoolean(dessert.getTitle(), false);

        // Установить ресурс изображения на основе состояния "нравится"
        holder.like.setImageResource(isLiked ? R.drawable.like : R.drawable.like_border);

        String id = String.valueOf(dessert.getId());
        String dessertName = desserts.get(position).getTitle();
        holder.dessertTitle.setText(dessertName);

        double dessert_size = Double.parseDouble(String.valueOf(dessert.getNew_dessert_width()));


        double sum = Double.parseDouble(String.format("%s", databaseAccess.getSumPrice(id)));

        double weight = Double.parseDouble(String.format("%s", databaseAccess.getSumGram(id)));

        double portion_size = Double.parseDouble(String.valueOf(dessert.getPortion_size()));

        double portion = Double.parseDouble(String.valueOf((weight * 1000) / portion_size));


        holder.portion_weight.setText(decimalFormat.format(portion_size) + " " + holder.gram);
        holder.portion.setText(decimalFormat.format(portion));

        holder.sum.setText(decimalFormat.format(sum )+ " " + currency);

        holder.weight.setText(decimalFormat.format(weight)  + " " + holder.kg);



        if (portion == 0 && sum == 0){
            holder.portion_price.setText("0 " + currency);
        } else {
            double portion_price = sum / portion;
            holder.portion_price.setText(decimalFormat.format(portion_price) + " " + currency);
        }

        holder.dessert_size.setText(decimalFormat.format(dessert_size) + " "+ holder.sm);

        byte[] image =  desserts.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        if (bitmap == null){
            holder.dessertImage.setVisibility(View.INVISIBLE);
            holder.empty_img.setVisibility(View.VISIBLE);
        } else {
            holder.empty_img.setVisibility(View.INVISIBLE);
            holder.dessertImage.setVisibility(View.VISIBLE);
            holder.dessertImage.setImageBitmap(bitmap);
        }

        boolean isExpanded = dessert.isExpanded();
//        holder.portion_img.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.constraintLayout3.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.result.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.imageView3.setImageResource(isExpanded ? R.drawable.up : R.drawable.down);

        holder.change_dessert.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.do_you_want_delete)
                            .setCancelable(true)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                int dessertId = dessert.getId();
                                if (currentFragment == 0) {
                                    // Удаляем закладку
                                    database.delete("bookmark", "id=" + dessertId, null);
                                } else if (currentFragment == 1) {
                                    // Удаляем десерт и соответствующую закладку
                                    database.delete("dessert", "id=" + dessertId, null);
                                    database.delete("bookmark", "id=" + dessertId, null);
                                }

                                // Удаляем десерт из списка и уведомляем адаптер
                                desserts.remove(position);
                                notifyItemRemoved(position);

                                // Обновляем главную активность
                                ((MainActivity)context).refresh();
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    return true;
                } else if (itemId == R.id.nav_edit) {
                    Intent intent = new Intent(context, ChangeDessertActivity.class);
                    intent.putExtra("id", String.valueOf(dessert.getId()));
                    intent.putExtra("title", dessert.getTitle());
                    intent.putExtra("image", dessert.getImage());
                    intent.putExtra("portion_size", String.valueOf(dessert.getPortion_size()));
                    intent.putExtra("new_dessert_width", String.valueOf(dessert.getNew_dessert_width()));
                    intent.putExtra("desserts", String.valueOf(dessert.getDesserts()));
                    intent.putExtra("new_dessert_height", String.valueOf(dessert.getNew_dessert_height()));
                    intent.putExtra("dessert_height", String.valueOf(dessert.getDessert_height()));
                    intent.putExtra("dessert_width", String.valueOf(dessert.getDessert_width()));
                    intent.putExtra("shape_name", String.valueOf(dessert.getShape_name()));
                    intent.putExtra("new_shape_name", String.valueOf(dessert.getNew_shape_name()));
                    context.startActivity(intent);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        holder.like.setOnClickListener(v -> {
            String title = holder.dessertTitle.getText().toString();
            Cursor cursor = database.query("bookmark", null, "title = ?", new String[]{title}, null, null, null);

            if (cursor.getCount() == 0) {
                // Title not found, proceed to insert bookmark
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", title);
                contentValues.put("sum", holder.sum.getText().toString());
                contentValues.put("new_dessert_width", holder.dessert_size.getText().toString());
//                contentValues.put("portion_size", holder.portion_size.getText().toString());
                contentValues.put("portion_price", holder.portion_price.getText().toString());
                contentValues.put("weight", holder.weight.getText().toString());
                contentValues.put("portion", holder.portion.getText().toString());
                contentValues.put("image",image);
                contentValues.put("desserts", 0);
                contentValues.put("new_dessert_height", String.valueOf(dessert.getNew_dessert_height()));
                contentValues.put("dessert_height", String.valueOf(dessert.getDessert_height()));
                contentValues.put("dessert_width", String.valueOf(dessert.getDessert_width()));
                contentValues.put("shape_name", String.valueOf(dessert.getShape_name()));
                contentValues.put("new_shape_name", String.valueOf(dessert.getNew_shape_name()));

                database.insert("bookmark", null, contentValues);
                holder.like.setImageResource(R.drawable.like);
                editor.putBoolean(desserts.get(position).getTitle(), true);
            } else {
                // Title found, remove bookmark
                database.delete("bookmark", "title = ?", new String[]{title});
                holder.like.setImageResource(R.drawable.like_border);
                editor.putBoolean(desserts.get(position).getTitle(), false);
            }

            // Apply the changes to SharedPreferences
            editor.apply();

            // Close the cursor to avoid memory leaks
            cursor.close();
        });

        holder.ingredients.setText(databaseAccess.getSumDessertId(id));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, IngredientActivity.class);
            intent.putExtra("dessertName", dessertName);
            intent.putExtra("dessertId", String.valueOf(desserts.get(position).getId()));
            intent.putExtra("style",((MainActivity)context).getMyStyleId());
            intent.putExtra("coefficient", String.valueOf(desserts.get(position).getCoefficient()));
            intent.putExtra("originalWidth", String.valueOf(dessert.getDessert_width()));
            intent.putExtra("originalHeight", String.valueOf(dessert.getDessert_height()));
            intent.putExtra("newWidth", String.valueOf(dessert.getNew_dessert_width()));
            intent.putExtra("newHeight", String.valueOf(dessert.getNew_dessert_height()));
            intent.putExtra("originalShape", String.valueOf(dessert.getShape_name()));
            intent.putExtra("newShape", String.valueOf(dessert.getNew_shape_name()));

            context.startActivity(intent);
        });

        holder.imageView3.setOnClickListener(v -> {
            boolean expanded = dessert.isExpanded();
            dessert.setExpanded(!expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return desserts.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder{

        ImageView dessertImage, empty_img,like ,imageView3, portion_img;
        ImageButton  change_dessert;
        ConstraintLayout result, constraintLayout2, constraintLayout3;
        TextView dessertTitle, sum, weight, portion, portion_price, dessert_size, portion_size, ingredients, portion_weight;
        String kg, gram, sm;

        @SuppressLint("ResourceType")
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

//            portion_img = itemView.findViewById(R.id.portion_img);
            constraintLayout3 = itemView.findViewById(R.id.constraintLayout3);
            constraintLayout2 = itemView.findViewById(R.id.constraintLayout2);
            result = itemView.findViewById(R.id.result);
            imageView3 = itemView.findViewById(R.id.imageView3);
            like = itemView.findViewById(R.id.like_dessert);
            empty_img = itemView.findViewById(R.id.empty_img);
            dessertImage = itemView.findViewById(R.id.word_lang);
            dessertTitle = itemView.findViewById(R.id.name);
            sum = itemView.findViewById(R.id.sum);
            weight = itemView.findViewById(R.id.weight);
            change_dessert = itemView.findViewById(R.id.change_dessert);
            portion = itemView.findViewById(R.id.portion);
            portion_price = itemView.findViewById(R.id.portion_price);
            dessert_size = itemView.findViewById(R.id.new_dessert_width);
            portion_size = itemView.findViewById(R.id.portion_size);
            ingredients = itemView.findViewById(R.id.ingredients);
            portion_weight = itemView.findViewById(R.id.portion_weight);
            kg = itemView.getResources().getString(R.string.kg);
            gram = itemView.getResources().getString(R.string.gram);
            sm = itemView.getResources().getString(R.string.sm);

        }
    }

}
