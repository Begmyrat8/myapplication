package com.example.myapplication.Adaptors;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.ChangeDessertActivity;
import com.example.myapplication.Activities.IngredientActivity;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.Models.CategoryModel;
import com.example.myapplication.R;

import java.util.List;

public class CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.CategoryViewHolder> {

    Context context;
    List<CategoryModel> categories;

    SQLiteDatabase database;
    DatabaseOpenHelper databaseHelper;

    public CategoryAdaptor(Context context, List<CategoryModel> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(parent.getContext()).inflate(R.layout.dessert_model, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();


        String categoryName = categories.get(position).getTitle();
        holder.dessertTitle.setText(categoryName);

        holder.sum.setText(String.format("%s",databaseAccess.getSumPrice() + " TMT "));

        holder.weight.setText(String.format("%s", databaseAccess.getSumGram()  + " kg "));

        byte[] image =  categories.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        if (bitmap == null){
            holder.categoryImage.setImageResource(R.drawable.baseline_add_a_white_photo);
        }else {
            holder.categoryImage.setImageBitmap(bitmap);

        }
        holder.delete_btn.setOnClickListener(v -> {

            database.delete("category","id="+categories.get(position).getId(),null);
            categories.remove(position);
            notifyDataSetChanged();


        });

        holder.change_dessert.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChangeDessertActivity.class);
            intent.putExtra("id", String.valueOf(categories.get(position).getId()));
            intent.putExtra("title", String.valueOf(categories.get(position).getTitle()));
            intent.putExtra("image", categories.get(position).getImage());
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, IngredientActivity.class);
            intent.putExtra("dessertName", categoryName);
            intent.putExtra("dessertId", String.valueOf(categories.get(position).getId()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder{

        ImageView categoryImage;
        ImageButton delete_btn, change_dessert;
        TextView dessertTitle, sum, weight;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.word_lang);
            dessertTitle = itemView.findViewById(R.id.name);
            sum = itemView.findViewById(R.id.sum);
            weight = itemView.findViewById(R.id.weight);
            delete_btn = itemView.findViewById(R.id.words_fav);
            change_dessert = itemView.findViewById(R.id.change_dessert);
        }
    }
}
