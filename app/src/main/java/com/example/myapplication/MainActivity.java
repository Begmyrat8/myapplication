package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Datebase.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<CategoryModel> categoryList = new ArrayList<>();
    CategoryAdaptor categoryAdaptor;
    DatabaseAccess databaseAccess;
    RecyclerView categoryRecycler;
    Button new_button;
    TextView title , result_tv;
    EditText search;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        categoryRecycler = findViewById(R.id.categoryList);
        categoryRecycler.setHasFixedSize(true);

        title = findViewById(R.id.title_txt);
        result_tv = findViewById(R.id.result_tv);
        search = findViewById(R.id.dictionary_input_search);
        new_button = findViewById(R.id.button);

        new_button.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New")
                    .setCancelable(true)

                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(this,MainActivity2.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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

        categoryList = databaseAccess.getAllList();

        setCategoryRecycler(categoryList);
    }
    private void setCategoryRecycler(List<CategoryModel> categoryList) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        categoryRecycler.setLayoutManager(manager);

        categoryAdaptor = new CategoryAdaptor(this, categoryList);
        categoryRecycler.setAdapter(categoryAdaptor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        categoryRecycler = findViewById(R.id.categoryList);
        categoryRecycler.setHasFixedSize(true);

        categoryList = databaseAccess.getAllList();

        setCategoryRecycler(categoryList);
        Toast.makeText(this, "aaaaaaaaaaa", Toast.LENGTH_SHORT).show();
    }

}