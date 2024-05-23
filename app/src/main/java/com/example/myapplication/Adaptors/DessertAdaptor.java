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
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int currentFragment;

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
        View categoryItems = LayoutInflater.from(parent.getContext()).inflate(R.layout.dessert_model, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DessertModel dessert = desserts.get(position);
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();
        DecimalFormat decimalFormat = new DecimalFormat();

        boolean isLiked = sharedPreferences.getBoolean(dessert.getTitle(), false);

        // Set the tag and image resource based on the liked state
        holder.like.setTag(isLiked ? 1 : 0);
        holder.like.setImageResource(isLiked ? R.drawable.like : R.drawable.like_border);

        String id = String.valueOf(dessert.getId());
        String dessertName = desserts.get(position).getTitle();
        holder.dessertTitle.setText(dessertName);

        double dessert_size = Double.parseDouble(String.valueOf(dessert.getDessert_size()));

        double sum = Double.parseDouble(String.format("%s", databaseAccess.getSumPrice(id)));

        double weight = Double.parseDouble(String.format("%s", databaseAccess.getSumGram(id)));

        double portion_size = Double.parseDouble(String.valueOf(dessert.getPortion_size()));

        double portion = Double.parseDouble(String.valueOf(dessert.getPortion()));

        double portion_weight = Double.parseDouble(databaseAccess.getPortionWeight(id,portion));

        holder.portion_weight.setText(portion_weight + " g");

        holder.sum.setText(decimalFormat.format(sum )+ " TMT");

        holder.weight.setText(decimalFormat.format(weight)  + " kg");

        holder.portion_size.setText(decimalFormat.format(portion_size) + " cm");

        holder.portion.setText(decimalFormat.format(portion));


        if (portion == 0 && sum == 0){
            holder.portion_price.setText("0 TMT");
        }else {
            double portion_price = sum / portion;
            holder.portion_price.setText(decimalFormat.format(portion_price) + " TMT");

        }



        holder.dessert_size.setText(decimalFormat.format(dessert_size) + " cm");

        byte[] image =  desserts.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        if (bitmap == null){
            holder.dessertImage.setVisibility(View.INVISIBLE);
            holder.empty_img.setVisibility(View.VISIBLE);
        }else {
            holder.empty_img.setVisibility(View.INVISIBLE);
            holder.dessertImage.setVisibility(View.VISIBLE);
            holder.dessertImage.setImageBitmap(bitmap);

        }


        holder.change_dessert.setOnClickListener(v -> {
            // Create a PopupMenu
            PopupMenu popupMenu = new PopupMenu(context, v);
            // Inflate the menu resource
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            // Set a click listener for each menu item
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.do_you_want_delete)
                            .setCancelable(true)
                            .setPositiveButton((R.string.yes), (dialog, which) -> {
                                if (currentFragment == 0) { // If in bookmark fragment
                                    // Handle bookmark fragment deletion
                                    database.delete("bookmark", "id=" + dessert.getId(), null);
                                    // Update the RecyclerView
                                    desserts.remove(position);
                                    notifyItemRemoved(position);
                                    ((MainActivity)context).refresh();
                                } else if (currentFragment == 1) { // If in home fragment
                                    // Handle home fragment deletion
                                    database.delete("dessert", "id=" + dessert.getId(), null);
                                    // Update the RecyclerView
                                    desserts.remove(position);
                                    notifyItemRemoved(position);
                                    ((MainActivity)context).refresh();
                                }
                            })
                            .setNegativeButton((R.string.no), (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    return true;
                } else if (itemId == R.id.nav_edit) {
                    Intent intent = new Intent(context, ChangeDessertActivity.class);
                    intent.putExtra("id", String.valueOf(dessert.getId()));
                    intent.putExtra("title", String.valueOf(dessert.getTitle()));
                    intent.putExtra("image", dessert.getImage());
                    intent.putExtra("portion_size", String.valueOf(dessert.getPortion_size()));
                    intent.putExtra("dessert_size", String.valueOf(dessert.getDessert_size()));
                    intent.putExtra("style",((MainActivity)context).getMyStyleId());
                    context.startActivity(intent);
                    return true;
                }
                return false;
            });

            // Show the popup menu
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
                contentValues.put("dessert_size", holder.dessert_size.getText().toString());
                contentValues.put("portion_size", holder.portion_size.getText().toString());
                contentValues.put("portion_price", holder.portion_price.getText().toString());
                contentValues.put("weight", holder.weight.getText().toString());
                contentValues.put("portion", holder.portion.getText().toString());
                contentValues.put("image", image);

                database.insert("bookmark", null, contentValues);
                holder.like.setImageResource(R.drawable.like);
                holder.like.setTag(1); // Update tag to liked state
                editor.putBoolean(desserts.get(position).getTitle(), true);
            } else {
                // Title found, remove bookmark
                database.delete("bookmark", "title = ?", new String[]{title});
                holder.like.setImageResource(R.drawable.like_border);
                holder.like.setTag(0); // Update tag to unliked state
                editor.putBoolean(desserts.get(position).getTitle(), false);
                desserts.remove(position);
                notifyItemRemoved(position);
                ((MainActivity)context).refresh();
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
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return desserts.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder{

        ImageView dessertImage, empty_img,like;
        ImageButton  change_dessert;
        TextView dessertTitle, sum, weight, portion, portion_price, dessert_size, portion_size, ingredients, portion_weight;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.like_dessert);
            empty_img = itemView.findViewById(R.id.empty_img);
            dessertImage = itemView.findViewById(R.id.word_lang);
            dessertTitle = itemView.findViewById(R.id.name);
            sum = itemView.findViewById(R.id.sum);
            weight = itemView.findViewById(R.id.weight);
            change_dessert = itemView.findViewById(R.id.change_dessert);
            portion = itemView.findViewById(R.id.portion);
            portion_price = itemView.findViewById(R.id.portion_price);
            dessert_size = itemView.findViewById(R.id.dessert_size);
            portion_size = itemView.findViewById(R.id.portion_size);
            ingredients = itemView.findViewById(R.id.ingredients);
            portion_weight = itemView.findViewById(R.id.portion_weight);
        }
    }

}
