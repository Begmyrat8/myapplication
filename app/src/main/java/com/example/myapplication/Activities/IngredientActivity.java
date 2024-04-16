package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    ImageView lang, imageView, delete;
    FloatingActionButton add_button;
    Button add_ingredient;
    SQLiteDatabase database;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        lang = findViewById(R.id.lang);
        delete = findViewById(R.id.delete);
        toolbar = findViewById(R.id.toolbar);
        add_button = findViewById(R.id.buttons);
        empty = findViewById(R.id.empty_ingredients);

        lang.setVisibility(View.GONE);
        imageView.setOnClickListener(v -> {
            finish();
        });

        dessertName = getIntent().getStringExtra("dessertName");
        dessertId = getIntent().getStringExtra("dessertId");

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.delete_all)
                    .setCancelable(true)

                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.delete("list","dessert_id=" + dessertId, null);

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
                    })

                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        });

        toolbar.setSubtitle(R.string.ingredients);


        add_button.setOnClickListener(view -> {

            Intent intent = new Intent(this, AddIngredientsActivity.class);
            intent.putExtra("dessertId", dessertId);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


        });
        add_ingredient.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddIngredientsActivity.class);
            intent.putExtra("dessertId", dessertId);
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

}