package com.example.myapplication;

import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Datebase.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Model> List = new ArrayList<>();
    com.example.myapplication.Adaptor Adaptor;
    DatabaseAccess databaseAccess;
    SQLiteDatabase database;
    RecyclerView Recycler;
    Button new_button, sum_btn, delete_btn, save_btn;
    EditText title;
    String gram;
    EditText price;
    EditText gram_price;
    TextView result_tv, kg;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        Recycler = findViewById(R.id.categoryList);
        Recycler.setHasFixedSize(true);

        result_tv = findViewById(R.id.result_tv);
        sum_btn = findViewById(R.id.sum_btn);
        new_button = findViewById(R.id.new_btn);
        delete_btn = findViewById(R.id.delete_btn);
        save_btn = findViewById(R.id.save_btn);
        com.example.myapplication.Adaptor.CategoryViewHolder categoryViewHolder = new Adaptor.CategoryViewHolder(new View(this));
        gram = categoryViewHolder.gram.getText().toString();
        categoryViewHolder.title.getText().toString();
        categoryViewHolder.price.getText().toString();

        save_btn.setOnClickListener(v -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", title.getText().toString());
            contentValues.put("gram", gram.getText().toString());
            contentValues.put("kg", kg.getText().toString());
            contentValues.put("price", set_price.getText().toString());
            double a = Double.parseDouble(valueOf(set_price.getText()));
            double result = a / 1000;
            contentValues.put(COL_GRAM_PRICE, result);
            database.insert("list", null, contentValues);

            List = databaseAccess.getAllList();
            setRecycler(List);
        });


        new_button.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New")
                    .setCancelable(true)

                    .setPositiveButton("Yes", (dialog, which) -> {

                        Intent intent = new Intent(this,MainActivity2.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

                    })

                    .setNegativeButton("No", (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();


        });

        delete_btn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete all?")
                    .setCancelable(true)

                    .setPositiveButton("Yes", (dialog, which) -> {

                        databaseAccess.clearAllDataFromTable("list");
                        List = databaseAccess.getAllList();
                        setRecycler(List);
                    })

                    .setNegativeButton("No", (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        sum_btn.setOnClickListener(v -> {
            result_tv.setText(String.format("SUM : %s", databaseAccess.getSumPrice() + " Gram : " + databaseAccess.getSumGram()));
        });

        List = databaseAccess.getAllList();

        setRecycler(List);
    }
    private void setRecycler(List<Model> categoryList) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        Recycler.setLayoutManager(manager);

        Adaptor = new Adaptor(this, categoryList);
        Recycler.setAdapter(Adaptor);

    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();


        Recycler = findViewById(R.id.categoryList);
        Recycler.setHasFixedSize(true);

        List = databaseAccess.getAllList();

        setRecycler(List);
    }

}