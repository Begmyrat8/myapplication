package com.example.myapplication.Activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptors.DessertAdaptor;
import com.example.myapplication.Models.DessertModel;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    java.util.List<DessertModel> List = new ArrayList<>();
    DessertAdaptor Adaptor;
    DatabaseAccess databaseAccess;
    ConstraintLayout empty;
    RecyclerView Recycler;
    ImageView imageView, lang, delete;
    ImageButton add_button;
    Button add_dessert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();
        loadLocale();

        Recycler = findViewById(R.id.category_list);
        Recycler.setHasFixedSize(true);

        add_dessert = findViewById(R.id.add_dessert);
        delete = findViewById(R.id.delete);
        lang = findViewById(R.id.lang);
        imageView = findViewById(R.id.imageView);
        add_button = findViewById(R.id.add_category_btn);
        empty = findViewById(R.id.empty);

        lang.setVisibility(View.GONE);
        lang.setOnClickListener(v -> {
            showChangeLanguageDialog();
        });

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_all))
                    .setCancelable(true)
                    .setPositiveButton(getText(R.string.Yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseAccess.clearAllDataFromTable("dessert");
                            databaseAccess.clearAllDataFromTable("list");

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
                    })

                    .setNegativeButton(getText(R.string.No), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        imageView.setVisibility(View.INVISIBLE);

        add_button.setOnClickListener(view -> {

            Intent intent = new Intent(this, AddDessertActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


        });

        add_dessert.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddDessertActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });


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

    private void showChangeLanguageDialog() {
        String[] genders = {"ENG","RUS"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Select_language));
        builder .setSingleChoiceItems(genders, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0){
                    setLocale("en");
                    recreate();
                }
                if (i == 1){
                    setLocale("ru");
                    recreate();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config =  new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Setting",MODE_PRIVATE).edit();
        editor.putString("My_lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences pref = getSharedPreferences("Setting", android.app.Activity.MODE_PRIVATE);
        String language = pref.getString("My_lang","");
        setLocale(language);
    }
    public void refreshMainActivity(){
        recreate();
    }

}