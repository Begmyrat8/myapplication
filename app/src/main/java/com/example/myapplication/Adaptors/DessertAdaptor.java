package com.example.myapplication.Adaptors;

import android.annotation.SuppressLint;
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
import com.example.myapplication.Models.DessertModel;
import com.example.myapplication.R;

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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();


        String dessertName = desserts.get(position).getTitle();
        if (dessertName.isEmpty()){
            holder.dessertTitle.setText("Untitled");
        }else {
            holder.dessertTitle.setText(dessertName);
        }

        holder.sum.setText(String.format("%s",databaseAccess.getSumPrice() + " TMT"));

        holder.weight.setText(String.format("%s", databaseAccess.getSumGram()  + " kg"));

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
            Intent intent = new Intent(context, ChangeDessertActivity.class);
            intent.putExtra("id", String.valueOf(desserts.get(position).getId()));
            intent.putExtra("title", String.valueOf(desserts.get(position).getTitle()));
            intent.putExtra("image", desserts.get(position).getImage());
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, IngredientActivity.class);
            intent.putExtra("dessertName", dessertName);
            intent.putExtra("dessertId", String.valueOf(desserts.get(position).getId()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return desserts.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder{

        ImageView dessertImage;
        ImageButton  change_dessert;
        TextView dessertTitle, sum, weight, empty_img;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            empty_img = itemView.findViewById(R.id.empty_img);
            dessertImage = itemView.findViewById(R.id.word_lang);
            dessertTitle = itemView.findViewById(R.id.name);
            sum = itemView.findViewById(R.id.sum);
            weight = itemView.findViewById(R.id.weight);
            change_dessert = itemView.findViewById(R.id.change_dessert);
        }
    }
}
