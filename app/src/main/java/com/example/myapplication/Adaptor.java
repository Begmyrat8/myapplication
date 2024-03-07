package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;

import java.util.List;

public class
Adaptor extends RecyclerView.Adapter<Adaptor.CategoryViewHolder> {

    Context context;
    List<Model> list;
    SQLiteDatabase database;
    DatabaseOpenHelper databaseHelper;


    public Adaptor(Context context, List<Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();



        String Name = list.get(position).getTitle();
        holder.title.setText(Name);

        String kg = String.valueOf(list.get(position).getKg());
        holder.kg.setText(kg);

        String price = String.valueOf(list.get(position).getPrice());
        holder.price.setText(price);

        String gram = String.valueOf(list.get(position).getGram());
        holder.gram.setText(gram);

        String gram_price = String.valueOf(list.get(position).getGram_price());
        holder.gram_price.setText(gram_price);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class CategoryViewHolder extends RecyclerView.ViewHolder{

       EditText title,  price, gram, gram_price;
       TextView kg;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            kg = itemView.findViewById(R.id.kg);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            gram = itemView.findViewById(R.id.gram);
            gram_price = itemView.findViewById(R.id.gram_price);
        }
    }
}
