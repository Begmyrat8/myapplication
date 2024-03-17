package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptors.Adaptor;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Models.Model;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class IngredientActivity extends AppCompatActivity {

    List<Model> List = new ArrayList<>();
    com.example.myapplication.Adaptors.Adaptor Adaptor;
    DatabaseAccess databaseAccess;
    RecyclerView Recycler;
    String categoryName;
    String categoryId;
    Toolbar toolbar;
    ImageView lang, imageView;
    ImageButton add_button, delete, home;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        Recycler = findViewById(R.id.List);
        Recycler.setHasFixedSize(true);

        imageView = findViewById(R.id.imageView);
        lang = findViewById(R.id.lang);
        home = findViewById(R.id.home_btn);
        delete = findViewById(R.id.delete);
        toolbar = findViewById(R.id.toolbar);
        add_button = findViewById(R.id.add_btn);

        lang.setVisibility(View.INVISIBLE);
        imageView.setOnClickListener(v -> {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });

        categoryName = getIntent().getStringExtra("dessertName");
        categoryId = getIntent().getStringExtra("dessertId");
        System.out.println(categoryId + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete all?")
                    .setCancelable(true)

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseAccess.clearAllDataFromTable("list");

                            List = databaseAccess.getCategoryData(categoryId);
                            setRecycler(List);
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

        toolbar.setSubtitle(categoryName);


        add_button.setOnClickListener(view -> {

            Intent intent = new Intent(this, AddIngredientsActivity.class);
            intent.putExtra("categoryId", categoryId);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


        });



        List = databaseAccess.getCategoryData(categoryId);

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


        Recycler = findViewById(R.id.List);
        Recycler.setHasFixedSize(true);

        List = databaseAccess.getCategoryData(categoryId);

        setRecycler(List);
    }
}