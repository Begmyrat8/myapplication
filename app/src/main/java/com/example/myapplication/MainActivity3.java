package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;

import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    SQLiteDatabase database;
    EditText titles, edit_gram, edit_price, edit_gram_price;
    String id, name, price, gram_price, kg;
    Cursor c = null;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(this);
        databaseAccess.open();

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        titles = findViewById(R.id.title);
        edit_gram = findViewById(R.id.gram_text);
        edit_price = findViewById(R.id.price_text);
        edit_gram_price = findViewById(R.id.edit_gram_price);

        name = getIntent().getStringExtra("name");

        c = database.rawQuery("select title from list where title = '" + name  + "';",null);
        while (c.moveToNext()) {
            String title = c.getString(0);
            titles.setText(title);
        }
        c = database.rawQuery("select price from list where title = '" + name  + "';",null);
        while (c.moveToNext()) {
            String price = c.getString(0);
            edit_price.setText(price);
        }
        c = database.rawQuery("select gram_price from list where title = '" + name  + "';",null);
        while (c.moveToNext()) {
            String gram_price = c.getString(0);
            edit_gram_price.setText(gram_price);
        }
        c = database.rawQuery("select gram from list where title = '" + name  + "';",null);
        while (c.moveToNext()) {
            String gram = c.getString(0);
            edit_gram.setText(gram);
        }
    }
}