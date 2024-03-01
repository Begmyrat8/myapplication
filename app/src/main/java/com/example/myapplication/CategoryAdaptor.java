package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Datebase.DatabaseAccess;

import java.util.List;

public class
CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.CategoryViewHolder> {

    Context context;
    List<CategoryModel> categories;
    SQLiteDatabase database;

    public CategoryAdaptor(Context context, List<CategoryModel> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {


        String categoryName = categories.get(position).getTitle();
        holder.title.setText(categoryName);

        String kg = String.valueOf(categories.get(position).getKg());
        holder.kg.setText(kg);

        String price = String.valueOf(categories.get(position).getPrice());
        holder.price.setText(price);

        String gram = String.valueOf(categories.get(position).getGram());
        holder.gram.setText(gram);

        String gram_price = String.valueOf(categories.get(position).getGram_price());
        holder.gram_price.setText(gram_price);

        holder.delete_btn.setOnClickListener(v -> {
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder{

       TextView title, kg, price, gram, gram_price;
       Button delete_btn;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            kg = itemView.findViewById(R.id.kg);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            gram = itemView.findViewById(R.id.gram);
            gram_price = itemView.findViewById(R.id.gram_price);
            delete_btn = itemView.findViewById(R.id.delete_btn);

        }
    }
}
