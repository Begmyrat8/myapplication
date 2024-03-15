package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

        holder.kg.setText(1 + " kg ");

        String price = String.valueOf(list.get(position).getPrice());
        holder.price.setText(price + " TMT ");

        double gram = Double.valueOf(list.get(position).getGram());
        if (gram >= 1000){
            holder.gram.setText(gram / 1000 +"kg");
        }else {
            holder.gram.setText(gram + " g ");
        }
        double gram_price = Double.valueOf(list.get(position).getGram_price());
        holder.gram_price.setText(gram_price / 1000 + " TMT ");



        byte[] image =  list.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        if (bitmap == null){
            holder.img.setImageResource(R.drawable.baseline_cake_24);
        }else {
            holder.img.setImageBitmap(bitmap);

        }

        holder.change_btn.setOnClickListener(view -> {

            Intent intent = new Intent(context, ChangeActivity.class);
            intent.putExtra("id", String.valueOf(list.get(position).getId()));
            intent.putExtra("title", String.valueOf(list.get(position).getTitle()));
            intent.putExtra("gram", String.valueOf(list.get(position).getGram()));
            intent.putExtra("price", String.valueOf(list.get(position).getPrice()));
            intent.putExtra("image", list.get(position).getImage());
            context.startActivity(intent);


        });
        holder.delete_btn.setOnClickListener(v -> {

            database.delete("list","id="+list.get(position).getId(),null);
            list.remove(position);
            notifyDataSetChanged();


        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class CategoryViewHolder extends RecyclerView.ViewHolder{

       TextView title,  price, kg, gram, gram_price;
       ImageView img;
       Button change_btn, delete_btn;


        @SuppressLint("WrongViewCast")
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            kg = itemView.findViewById(R.id.kg);
            title = itemView.findViewById(R.id.title_edit_text);
            price = itemView.findViewById(R.id.price);
            gram = itemView.findViewById(R.id.gram);
            gram_price = itemView.findViewById(R.id.gram_price);
            change_btn = itemView.findViewById(R.id.change_btn);
            img = itemView.findViewById(R.id.avatar);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }
    }
}
