package com.example.myapplication;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Datebase.DatabaseAccess;

public class MainActivity3 extends AppCompatActivity {

    SQLiteDatabase database;
    EditText edit_title, edit_gram, edit_price, edit_gram_price;
    String title, id, gram, price;
    Button save_btn;
    Cursor c = null;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(this);
        databaseAccess.open();


        edit_title = findViewById(R.id.title);
        edit_gram = findViewById(R.id.gram_text);
        edit_price = findViewById(R.id.price_text);
        edit_gram_price = findViewById(R.id.edit_gram_price);
        save_btn = findViewById(R.id.save_product);


        get_and_set_intent_data();
        System.out.println(title + "2222222222222222222222222222222222222222");
        System.out.println(gram + "2222222222222222222222222222222222222222");
        System.out.println(price + "2222222222222222222222222222222222222222");

        save_btn.setOnClickListener(view -> {
//            DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
//            database = dbHelper.getWritableDatabase();
//
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("title", title);
//            contentValues.put("gram", gram);
//            contentValues.put("price", price);
//
//            long result = database.insert("list", null,contentValues);
//            if (result == -1){
//                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
//            }

            databaseAccess.updateData( title, gram, price);
        });
    }
    void get_and_set_intent_data(){

        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("gram") && getIntent().hasExtra("price")){
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            gram = getIntent().getStringExtra("gram");
            price = getIntent().getStringExtra("price");

            edit_title.setText(title);
            edit_gram.setText(gram);
            edit_price.setText(price);

        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }

//        c = database.rawQuery("select title from list where title = '" + title  + "';",null);
//        while (c.moveToNext()) {
//            String name = c.getString(0);
//            edit_title.setText(name);
//        }
//        c = database.rawQuery("select price from list where title = '" + title  + "';",null);
//        while (c.moveToNext()) {
//            String price = c.getString(0);
//            edit_price.setText(price);
//        }
//        c = database.rawQuery("select gram_price from list where title = '" + title  + "';",null);
//        while (c.moveToNext()) {
//            String gram_price = c.getString(0);
//            edit_gram_price.setText(gram_price);
//        }
//        c = database.rawQuery("select gram from list where title = '" + title  + "';",null);
//        while (c.moveToNext()) {
//            String gram = c.getString(0);
//            edit_gram.setText(gram);
//        }

    }
}