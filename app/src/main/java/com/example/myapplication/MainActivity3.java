package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;

public class MainActivity3 extends AppCompatActivity {

    SQLiteDatabase database;
    EditText edit_title, edit_gram, edit_price;
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
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        edit_title = findViewById(R.id.edit_title);
        edit_gram = findViewById(R.id.edit_gram);
        edit_price = findViewById(R.id.edit_price);
        save_btn = findViewById(R.id.save_product);


        get_and_set_intent_data();

        save_btn.setOnClickListener(view -> {

            ContentValues contentValues = new ContentValues();
            contentValues.put("title", edit_title.getText().toString());
            contentValues.put("gram", edit_gram.getText().toString());
            contentValues.put("price", edit_price.getText().toString());

            long result = database.update("list", contentValues,"id="+id ,null);
            if (result == -1){
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

            }
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

    }
}