package com.example.myapplication;

import static java.lang.String.*;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Datebase.DatabaseOpenHelper;

public class MainActivity2 extends AppCompatActivity {

    Button add_product;
    String COL_TITLE = "title";
    String COL_GRAM = "gram";
    String COL_KG = "kg";
    String COL_Price = "price";
    String COL_GRAM_PRICE = "gram_price";

    EditText set_title, set_gram,  set_price;
    TextView set_kg;
    private SQLiteDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        set_title = findViewById(R.id.set_title);
        set_gram = findViewById(R.id.set_gram);
        set_kg = findViewById(R.id.set_kg);
        set_price = findViewById(R.id.set_price);



        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        add_product = findViewById(R.id.add_product);
        add_product.setOnClickListener(view -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_TITLE, set_title.getText().toString());
            contentValues.put(COL_GRAM, set_gram.getText().toString());
            contentValues.put(COL_KG, set_kg.getText().toString());
            contentValues.put(COL_Price, set_price.getText().toString());
            double a = Double.parseDouble(valueOf(set_price.getText()));
            double result = a / 1000;
            contentValues.put(COL_GRAM_PRICE, result);
            database.insert("list", null, contentValues);


            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });
    }


}