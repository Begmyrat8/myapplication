package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    ConstraintLayout empty;
    String categoryName;
    String categoryId;
    Toolbar toolbar;
    ImageView lang, imageView, delete;
    ImageButton add_button;
    Button add_ingredient;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        Recycler = findViewById(R.id.List);
        Recycler.setHasFixedSize(true);

        add_ingredient = findViewById(R.id.add_ingredients);
        imageView = findViewById(R.id.imageView);
        lang = findViewById(R.id.lang);
        delete = findViewById(R.id.delete);
        toolbar = findViewById(R.id.toolbar);
        add_button = findViewById(R.id.add_btn);
        empty = findViewById(R.id.empty_ingredients);

        lang.setVisibility(View.GONE);
        imageView.setOnClickListener(v -> {
            finish();
        });

        categoryName = getIntent().getStringExtra("dessertName");
        categoryId = getIntent().getStringExtra("dessertId");

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
                            if (Adaptor.isEmpty()) {
                                Recycler.setVisibility(View.GONE);
                                empty.setVisibility(View.VISIBLE);
                            }else {
                                Recycler.setVisibility(View.VISIBLE);
                                empty.setVisibility(View.GONE);
                            }
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

        toolbar.setSubtitle("Ingredients for " + categoryName);


        add_button.setOnClickListener(view -> {

            Intent intent = new Intent(this, AddIngredientsActivity.class);
            intent.putExtra("categoryId", categoryId);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


        });
        add_ingredient.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddDessertActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });



        List = databaseAccess.getCategoryData(categoryId);
        setRecycler(List);

        if (Adaptor.isEmpty()) {
            Recycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }else {
            Recycler.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }
    @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
    private void setRecycler(List<Model> categoryList) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        Recycler.setLayoutManager(manager);

        Adaptor = new Adaptor(this, categoryList);
        Recycler.setAdapter(Adaptor);
        Adaptor.notifyDataSetChanged();


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
        if (Adaptor.isEmpty()) {
            Recycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }else {
            Recycler.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }
    public void refresh(){
        recreate();
    }

}