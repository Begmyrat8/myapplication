package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;

import java.util.List;

public class
Adaptor extends RecyclerView.Adapter<Adaptor.CategoryViewHolder> {

    Context context;
    Activity activity;
    List<Model> list;
    SQLiteDatabase database;
    DatabaseOpenHelper databaseHelper;


    public Adaptor(Activity activity,Context context, List<Model> list) {
        this.activity = activity;
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
        holder.kg.setText(kg + " kg ");

        String price = String.valueOf(list.get(position).getPrice());
        holder.price.setText(price + " TMT ");

        String gram = String.valueOf(list.get(position).getGram());
        holder.gram.setText(gram + " g ");

        String gram_price = String.valueOf(list.get(position).getGram_price());
        holder.gram_price.setText(gram_price + " TMT ");

        holder.sum.setText(String.format("%s",databaseAccess.getSumPrice() + " TMT "));

        holder.weight.setText(String.format("%s", databaseAccess.getSumGram() + " g "));

        holder.change_btn.setOnClickListener(view -> {

            Intent intent = new Intent(context,MainActivity3.class);
            intent.putExtra("id", String.valueOf(list.get(position).getId()));
            intent.putExtra("title", String.valueOf(list.get(position).getTitle()));
            intent.putExtra("gram", String.valueOf(list.get(position).getGram()));
            intent.putExtra("price", String.valueOf(list.get(position).getPrice()));
            activity.startActivityForResult(intent, 1);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class CategoryViewHolder extends RecyclerView.ViewHolder{

       TextView title,  price, kg, gram, gram_price, sum, weight;
       Button change_btn;


        @SuppressLint("WrongViewCast")
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            kg = itemView.findViewById(R.id.kg);
            title = itemView.findViewById(R.id.title_edit_text);
            price = itemView.findViewById(R.id.price);
            gram = itemView.findViewById(R.id.gram);
            gram_price = itemView.findViewById(R.id.gram_price);
            sum = itemView.findViewById(R.id.sum);
            weight = itemView.findViewById(R.id.weight);
            change_btn = itemView.findViewById(R.id.change_btn);
        }
    }
}
