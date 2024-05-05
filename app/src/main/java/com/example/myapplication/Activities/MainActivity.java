package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptors.DessertAdaptor;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Models.DessertModel;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    java.util.List<DessertModel> List = new ArrayList<>();
    DessertAdaptor Adaptor;
    DatabaseAccess databaseAccess;
    ConstraintLayout empty;
    RecyclerView Recycler;
    ImageView imageView, lang, delete;
    FloatingActionButton add_button;
    Button add_dessert;
    SharedPreferences prefs = null;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
    @Override
    protected void onViewReady(Bundle savedInstanceState,Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        setContentView(R.layout.activity_main);
        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            startActivity(new Intent(MainActivity.this, LanguageActivity.class));
            finish();
        }


        Recycler = findViewById(R.id.category_list);
        Recycler.setHasFixedSize(true);

        add_dessert = findViewById(R.id.add_dessert);
        delete = findViewById(R.id.delete);
        lang = findViewById(R.id.lang);
        imageView = findViewById(R.id.imageView);
        add_button = findViewById(R.id.buttons2);
        empty = findViewById(R.id.empty);

        lang.setOnClickListener(v -> showLanguagePicker(this, false));

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_all))
                    .setCancelable(true)
                    .setPositiveButton(getText(R.string.yes), (dialog, which) -> {
                        databaseAccess.clearAllDataFromTable("dessert");
                        databaseAccess.clearAllDataFromTable("list");

                        List = databaseAccess.getDessertList();
                        setRecycler(List);

                        if (List.isEmpty()) {
                            Recycler.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                        } else {
                            Recycler.setVisibility(View.VISIBLE);
                            empty.setVisibility(View.GONE);
                        }
                    })

                    .setNegativeButton(getText(R.string.no), (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        });

        imageView.setVisibility(View.INVISIBLE);

        add_button.setOnClickListener(view -> {


            startActivity(new Intent(this, AddDessertActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


        });

        add_dessert.setOnClickListener(view -> {

            startActivity(new Intent(this, AddDessertActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });


        List = databaseAccess.getDessertList();

        setRecycler(List);
        if (List.isEmpty()) {
            Recycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            Recycler.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }
    private void setRecycler(List<DessertModel> categoryList) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        Recycler.setLayoutManager(manager);

        Adaptor = new DessertAdaptor(this, categoryList);
        Recycler.setAdapter(Adaptor);

    }



    @Override
    protected void onResume() {
        super.onResume();
        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();


        Recycler = findViewById(R.id.category_list);
        Recycler.setHasFixedSize(true);

        List = databaseAccess.getDessertList();
        setRecycler(List);

        if (List.isEmpty()) {
            Recycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }else {
            Recycler.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit);
        builder.setMessage(R.string.do_you_really_want_exit);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> finishAffinity());
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void refresh(){
        recreate();
    }

}