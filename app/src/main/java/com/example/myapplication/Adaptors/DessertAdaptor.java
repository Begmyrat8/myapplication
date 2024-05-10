package com.example.myapplication.Adaptors;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

    public DessertAdaptor(Context context, List<DessertModel> desserts) {
        this.context = context;
        this.desserts = desserts;
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
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();
        DecimalFormat decimalFormat = new DecimalFormat();


        String id = String.valueOf(desserts.get(position).getId());
        String dessertName = desserts.get(position).getTitle();
        if (dessertName.isEmpty()){
            holder.dessertTitle.setText("Untitled");
        }else {
            holder.dessertTitle.setText(dessertName);
        }



        double sum = Double.parseDouble(String.format("%s", databaseAccess.getSumPrice(id)));
        holder.sum.setText(decimalFormat.format(sum )+ " TMT");

        double weight = Double.parseDouble(String.format("%s", databaseAccess.getSumGram(id)));
        holder.weight.setText(decimalFormat.format(weight)  + " kg");

        double portion_size = Double.parseDouble(String.valueOf(desserts.get(position).getPortion_size()));
        holder.portion_size.setText(decimalFormat.format(portion_size) + " cm");

        double portion = Double.parseDouble(String.valueOf(desserts.get(position).getPortion()));
        holder.portion.setText(decimalFormat.format(portion));

        if (portion == 0 && sum == 0){
            holder.portion_price.setText("0 TMT");
        }else {
            double portion_price = sum / portion;
            holder.portion_price.setText(decimalFormat.format(portion_price) + " TMT");

        }


        double dessert_size = Double.parseDouble(String.valueOf(desserts.get(position).getDessert_size()));
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
                                database.delete("dessert","id=" + desserts.get(position).getId(),null);
                                desserts.remove(position);
                                notifyItemRemoved(position);

                                ((MainActivity)context).refresh();
                            })

                            .setNegativeButton((R.string.no), (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    return true;
                } else if (itemId == R.id.nav_edit) {
                    Intent intent = new Intent(context, ChangeDessertActivity.class);
                    intent.putExtra("id", String.valueOf(desserts.get(position).getId()));
                    intent.putExtra("title", String.valueOf(desserts.get(position).getTitle()));
                    intent.putExtra("image", desserts.get(position).getImage());
                    intent.putExtra("portion_size", String.valueOf(desserts.get(position).getPortion_size()));
                    intent.putExtra("dessert_size", String.valueOf(desserts.get(position).getDessert_size()));
                    intent.putExtra("style",((MainActivity)context).getMyStyleId());
                    context.startActivity(intent);
                    return true;
                }
                return false;
            });

            // Show the popup menu
            popupMenu.show();
        });



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

        ImageView dessertImage, empty_img;
        ImageButton  change_dessert;
        TextView dessertTitle, sum, weight, portion, portion_price, dessert_size, portion_size;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

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
        }
    }
}
