package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<CategoryModel> categoryList = new ArrayList<>();
    CategoryAdaptor categoryAdaptor;
    DatabaseAccess databaseAccess;
    RecyclerView categoryRecycler;
    Button new_button;
    TextView result;
    EditText kg;
    private SQLiteDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        categoryRecycler = findViewById(R.id.categoryList);
        categoryRecycler.setHasFixedSize(true);
        categoryList = databaseAccess.getAllCategory();

        setCategoryRecycler(categoryList);

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        new_button = findViewById(R.id.button);

        new_button.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New")
                    .setCancelable(true)

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            ContentValues contentValues = new ContentValues();
//                            database.insert("list", null, contentValues);
                        }
                    })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        kg = findViewById(R.id.kg);
       result = findViewById(R.id.result_tv);


    }
    private void setCategoryRecycler(List<CategoryModel> categoryList) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        categoryRecycler.setLayoutManager(manager);

        categoryAdaptor = new CategoryAdaptor(this, categoryList);
        categoryRecycler.setAdapter(categoryAdaptor);
    }
}