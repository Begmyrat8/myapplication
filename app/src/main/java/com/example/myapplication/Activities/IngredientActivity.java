package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.myapplication.Datebase.DatabaseOpenHelper;
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
    String dessertName;
    String dessertId;
    Toolbar toolbar;
    ImageView imageView, setting;
    ImageButton add_button;
    Button add_ingredient;
    SQLiteDatabase database;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();



        Recycler = findViewById(R.id.List);
        Recycler.setHasFixedSize(true);

        add_ingredient = findViewById(R.id.add_ingredients);
        imageView = findViewById(R.id.imageView);
        setting = findViewById(R.id.setting);
        toolbar = findViewById(R.id.toolbar);
        add_button = findViewById(R.id.buttons);
        empty = findViewById(R.id.empty_ingredients);


        setting.setVisibility(View.GONE);
        imageView.setOnClickListener(v -> finish());


        toolbar.setSubtitle("Части десерта");

        dessertName = getIntent().getStringExtra("dessertName");
        dessertId = getIntent().getStringExtra("dessertId");


        add_button.setOnClickListener(view -> {

            Intent intent = new Intent(this, AddIngredientsActivity.class);
            intent.putExtra("dessertId", dessertId);
            intent.putExtra("style", setMode());
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


        });
        add_ingredient.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddIngredientsActivity.class);
            intent.putExtra("dessertId", dessertId);
            intent.putExtra("style", setMode());
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });


        List = databaseAccess.getDessertData(dessertId);

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

        List = databaseAccess.getDessertData(dessertId);

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

    public String setMode() {
        String mode = getSharedPreferences("Settings", MODE_PRIVATE).getString("mode", "light");

        // Применяем тему перед super.onCreate()
        switch (mode) {
            case "dark":
                setTheme(R.style.AppTheme_Dark);
                break;
            case "blue":
                setTheme(R.style.AppTheme_Blue);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
        return mode;
    }
    public double getCoif(){
        String coefficient = getIntent().getStringExtra("coefficient");
        if (coefficient != null){
            return Double.parseDouble(coefficient);
        }else {
            return 0.0;
        }

    }
    public String getShape(){
        return getIntent().getStringExtra("originalShape");
    }
    public String getNewShape(){
        return getIntent().getStringExtra("newShape");
    }

    public double getNewWidth(){
        String newWidth = getIntent().getStringExtra("newWidth");
        if (newWidth != null){
            return Double.parseDouble(newWidth);
        }else {
            return 0.0;
        }
    }
    public double getOriginalWidth(){
        String originalWidth = getIntent().getStringExtra("originalWidth");
        if (originalWidth != null){
            return Double.parseDouble(originalWidth);
        }else {
            return 0.0;
        }
    }
    public double getNewHeight(){
        String newHeight = getIntent().getStringExtra("newHeight");
        if (newHeight != null){
            return Double.parseDouble(newHeight);
        }else {
            return 0.0;
        }
    }
    public double getOriginalHeight(){
        String originalHeight = getIntent().getStringExtra("originalHeight");
        if (originalHeight != null){
            return Double.parseDouble(originalHeight);
        }else {
            return 0.0;
        }
    }

}