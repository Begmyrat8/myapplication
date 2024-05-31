package com.example.myapplication.Adaptors;

import static android.view.View.VISIBLE;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.ChangeIngredientsActivity;
import com.example.myapplication.Activities.IngredientActivity;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.Models.Model;
import com.example.myapplication.R;

import java.text.DecimalFormat;
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
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        databaseHelper = new DatabaseOpenHelper(context);
        database = databaseHelper.getWritableDatabase();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(context.getApplicationContext());
        databaseAccess.open();

        DecimalFormat decimalFormat = new DecimalFormat();


        String name = list.get(position).getTitle();
        if (name.isEmpty()){
            holder.title.setText("Untitled");
        }else {
            holder.title.setText(name);
        }

        double price = list.get(position).getPrice();
        if (price == 0){
            holder.price.setText(0 + " TMT");
        }else {
            holder.price.setText(decimalFormat.format(price) + " TMT");
        }

        double value = list.get(position).getValue();
        String units = list.get(position).getUnits();
        double gram_price = list.get(position).getGram_price();


        if (Objects.equals(units, holder.items[0])){
            holder.hint_price.setText("1 " + holder.kg + " " + holder.small_price);
            holder.hint_unit_price.setText( holder.gram + " " + holder.small_price);
            holder.hint_gram_price_c.setText( holder.gram + " " + holder.small_price);
            holder.hint_unit.setText(holder.gram);
            holder.hint_gram_c.setText(holder.gram);
        }
        if (Objects.equals(units, holder.items[1])){
            holder.hint_price.setText("1 " + holder.piece + " " + holder.small_price);
            holder.hint_unit_price.setText(holder.piece + " " + holder.small_price);
            holder.hint_gram_price_c.setText(holder.piece + " " + holder.small_price);
            holder.hint_unit.setText(holder.piece);
            holder.hint_gram_c.setText(holder.piece);
        }
        if (Objects.equals(units, holder.items[2])){
            holder.hint_price.setText("1 " + holder.liter + " " + holder.small_price);
            holder.hint_unit_price.setText(holder.milliliter + " " + holder.small_price);
            holder.hint_gram_price_c.setText(holder.milliliter + " " + holder.small_price);
            holder.hint_unit.setText(holder.milliliter);
            holder.hint_gram_c.setText(holder.milliliter);
        }
        double a =(value / list.get(position).getCoefficient());
        holder.value.setText(decimalFormat.format(value));
        holder.gram_c.setText(decimalFormat.format(a));
        holder.gram_price.setText(decimalFormat.format(gram_price)  + " TMT");




        byte[] image =  list.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        if (bitmap == null){
            holder.img.setVisibility(View.INVISIBLE);
            holder.empty_avatar.setVisibility(VISIBLE);
        }else {
            holder.empty_avatar.setVisibility(View.INVISIBLE);
            holder.img.setVisibility(VISIBLE);
            holder.img.setImageBitmap(bitmap);

        }

        holder.change_btn.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            // Set a click listener for each menu item
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.do_you_want_delete)
                            .setCancelable(true)
                            .setPositiveButton((R.string.yes), (dialog, which) -> {
                                database.delete("list","id=" + list.get(position).getId(),null);
                                list.remove(position);
                                notifyItemRemoved(position);

                                ((IngredientActivity) context).refresh();
                            })

                            .setNegativeButton((R.string.no), (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    return true;
                } else if (itemId == R.id.nav_edit) {
                    Intent intent = new Intent(context, ChangeIngredientsActivity.class);
                    intent.putExtra("id", String.valueOf(list.get(position).getId()));
                    intent.putExtra("title", String.valueOf(list.get(position).getTitle()));
                    intent.putExtra("value", String.valueOf(list.get(position).getValue()));
                    intent.putExtra("price", String.valueOf(list.get(position).getPrice()));
                    intent.putExtra("image", list.get(position).getImage());
                    intent.putExtra("units", String.valueOf(list.get(position).getUnits()));
                    intent.putExtra("styles",((IngredientActivity) context).setMode());

                    context.startActivity(intent);
                    return true;
                }
                return false;
            });

            // Show the popup menu
            popupMenu.show();
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static  class CategoryViewHolder extends RecyclerView.ViewHolder{

       TextView title,  price, value, gram_price, hint_unit, hint_price, hint_unit_price, gram_price_c, gram_c, hint_gram_c,hint_gram_price_c;
       ImageView img, empty_avatar, imageView4;
       ImageButton change_btn;
       String kg, small_price, piece, liter, unit, gram, milliliter;
       String[] items;
       ConstraintLayout c, gram_price_container_c, price_container;

        @SuppressLint("WrongViewCast")
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            hint_gram_price_c = itemView.findViewById(R.id.hint_gram_price_c);
            gram_price_c = itemView.findViewById(R.id.gram_price_c);
            hint_gram_c = itemView.findViewById(R.id.hint_gram_c);
            gram_c = itemView.findViewById(R.id.gram_c);
            price_container = itemView.findViewById(R.id.price_container);
            gram_price_container_c = itemView.findViewById(R.id.gram_price_container_c);
            c = itemView.findViewById(R.id.c);
            imageView4 = itemView.findViewById(R.id.imageView4);
            hint_price = itemView.findViewById(R.id.hint_price);
            hint_unit = itemView.findViewById(R.id.hint_gram);
            title = itemView.findViewById(R.id.title_edit_text);
            price = itemView.findViewById(R.id.price);
            value = itemView.findViewById(R.id.gram);
            gram_price = itemView.findViewById(R.id.gram_price);
            hint_unit_price = itemView.findViewById(R.id.hint_gram_price);
            change_btn = itemView.findViewById(R.id.change_btn);
            img = itemView.findViewById(R.id.avatar);
            empty_avatar = itemView.findViewById(R.id.empty_avatar);
            items = itemView.getResources().getStringArray(R.array.items);

            kg = itemView.getResources().getString(R.string.kg);
            small_price = itemView.getResources().getString(R.string.small_price);
            liter = itemView.getResources().getString(R.string.liter);
            piece = itemView.getResources().getString(R.string.piece);
            unit = itemView.getResources().getString(R.string.unit);
            gram = itemView.getResources().getString(R.string.gram);
            milliliter = itemView.getResources().getString(R.string.milliliter);


        }
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
