package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
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
    RecyclerView Recycler;
    Button delete_btn;
    ImageButton add_button;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        Recycler = findViewById(R.id.categoryList);
        Recycler.setHasFixedSize(true);

        add_button = findViewById(R.id.add_btn);
        delete_btn = findViewById(R.id.delete_btn);

        add_button.setOnClickListener(view -> {
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