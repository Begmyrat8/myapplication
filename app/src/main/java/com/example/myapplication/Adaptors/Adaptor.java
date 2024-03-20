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

import com.example.myapplication.Activities.ChangeIngredientsActivity;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.Models.Model;
import com.example.myapplication.R;

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
        View categoryItems = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredients_model, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();



        String Name = list.get(position).getTitle();
        holder.title.setText(Name);



        double price = list.get(position).getPrice();
        if (price == 0){
            holder.price.setText(0 + " TMT ");
        }else {
            holder.price.setText(price + " TMT ");
        }

        int gram = list.get(position).getGram();
        int thing = list.get(position).getThing();
        double gram_price = list.get(position).getGram_price();
        if (gram == 0) {
            if (thing == 0) {
                holder.hint_gram.setText("gram/ " + Name);
                holder.gram.setText(0 + " gram/ " + Name);
                holder.hint_price.setText("kg/ " + Name);
            }else {
                holder.hint_gram.setText(Name);
                holder.gram.setText(thing + " " + Name);
                holder.hint_price.setText("1 " + Name +"/price");
                holder.gram_price.setText(gram_price + " TMT ");
            }
        }else {
            holder.gram.setText(gram + " g ");
            holder.gram_price.setText(gram_price  + " TMT ");
        }





        byte[] image =  list.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        if (bitmap == null){
            holder.img.setImageResource(R.drawable.baseline_add_a_photo_24);
        }else {
            holder.img.setImageBitmap(bitmap);

        }

        holder.change_btn.setOnClickListener(view -> {

            Intent intent = new Intent(context, ChangeIngredientsActivity.class);
            intent.putExtra("id", String.valueOf(list.get(position).getId()));
            intent.putExtra("title", String.valueOf(list.get(position).getTitle()));
            intent.putExtra("price", String.valueOf(list.get(position).getPrice()));
            intent.putExtra("image", list.get(position).getImage());
            intent.putExtra("thing", String.valueOf(list.get(position).getThing()));
            intent.putExtra("gram", String.valueOf(list.get(position).getGram()) );

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

       TextView title,  price, kg, gram, gram_price, hint_gram, hint_price;
       ImageView img;
       ImageButton change_btn, delete_btn;


        @SuppressLint("WrongViewCast")
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            hint_price = itemView.findViewById(R.id.hint_price);
            hint_gram = itemView.findViewById(R.id.hint_gram);
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
