package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class
CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.CategoryViewHolder> {

    Context context;
    List<CategoryModel> categories;

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


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder{

       TextView title, kg, price, gram;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            kg = itemView.findViewById(R.id.kg);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            gram = itemView.findViewById(R.id.gram);
        }
    }
}
