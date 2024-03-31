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
import com.example.myapplication.Activities.IngredientActivity;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.Models.Model;
import com.example.myapplication.R;

import java.util.List;
import java.util.Objects;

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

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();



        String name = list.get(position).getTitle();
        if (name.isEmpty()){
            holder.title.setText("Untitled");
        }else {
            holder.title.setText(name);
        }

        double price = list.get(position).getPrice();
        if (price == 0){
            holder.price.setText(0 + " TMT ");
        }else {
            holder.price.setText(price + " TMT ");
        }

        int value = list.get(position).getValue();
        String units = list.get(position).getUnits();
        double gram_price = list.get(position).getGram_price();

        if (value == 0) {
            if (units == null) {
                holder.hint_unit.setText("unit");
                holder.value.setText("0");
                holder.hint_price.setText("unit/price");
                holder.gram_price.setText("0 TMT");
            }else {
                if (units.equals("gram")){
                    holder.hint_price.setText("1 kg /price");
                }else {
                    holder.hint_price.setText("1 " + units +"/price");
                }
                if (units.equals("milliliter")){
                    holder.hint_price.setText("1 liter/price");
                }else {
                    holder.hint_price.setText("1 " + units +"/price");
                }
                holder.hint_unit.setText(units);
                holder.value.setText(value + " " + name);
                holder.gram_price.setText(gram_price + " TMT ");
            }
        }else {
            if (Objects.equals(units, "gram")){
                holder.hint_price.setText("1 kg /price");
            }else {
                holder.hint_price.setText("1 " + units +"/price");
            }
            if (Objects.equals(units, "milliliter")){
                holder.hint_price.setText("1 liter/price");
            }else {
                holder.hint_price.setText("1 " + units +"/price");
            }
            holder.hint_unit.setText(units);
            holder.value.setText(value + units);
            holder.gram_price.setText(gram_price  + " TMT ");
        }




        byte[] image =  list.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        if (bitmap == null){
            holder.img.setVisibility(View.INVISIBLE);
            holder.empty_ingredient_img.setVisibility(View.VISIBLE);
        }else {
            holder.img.setVisibility(View.VISIBLE);
            holder.empty_ingredient_img.setVisibility(View.INVISIBLE);
            holder.img.setImageBitmap(bitmap);

        }

        holder.change_btn.setOnClickListener(view -> {

            Intent intent = new Intent(context, ChangeIngredientsActivity.class);
            intent.putExtra("id", String.valueOf(list.get(position).getId()));
            intent.putExtra("title", String.valueOf(list.get(position).getTitle()));
            intent.putExtra("value", String.valueOf(list.get(position).getValue()));
            intent.putExtra("price", String.valueOf(list.get(position).getPrice()));
            intent.putExtra("image", list.get(position).getImage());
            intent.putExtra("units", String.valueOf(list.get(position).getUnits()));

            context.startActivity(intent);


        });
        holder.delete_btn.setOnClickListener(v -> {

            database.delete("list","id="+list.get(position).getId(),null);
            list.remove(position);
            notifyItemRemoved(position);

            ((IngredientActivity) context).refresh();

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class CategoryViewHolder extends RecyclerView.ViewHolder{

       TextView title,  price, value, gram_price, hint_unit, hint_price, empty_ingredient_img;
       ImageView img;
       ImageButton change_btn, delete_btn;


        @SuppressLint("WrongViewCast")
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            hint_price = itemView.findViewById(R.id.hint_price);
            hint_unit = itemView.findViewById(R.id.hint_gram);
            title = itemView.findViewById(R.id.title_edit_text);
            price = itemView.findViewById(R.id.price);
            value = itemView.findViewById(R.id.gram);
            gram_price = itemView.findViewById(R.id.gram_price);
            change_btn = itemView.findViewById(R.id.change_btn);
            img = itemView.findViewById(R.id.avatar);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            empty_ingredient_img = itemView.findViewById(R.id.empty_ingredients_img);
        }
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
