package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.myapplication.Datebase.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    java.util.List<CategoryModel> List = new ArrayList<>();
    CategoryAdaptor Adaptor;
    DatabaseAccess databaseAccess;
    RecyclerView Recycler;
    ImageView imageView;
    ImageButton add_button, delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        Recycler = findViewById(R.id.category_list);
        Recycler.setHasFixedSize(true);

        delete = findViewById(R.id.delete);
        imageView = findViewById(R.id.imageView);
        add_button = findViewById(R.id.add_category_btn);

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete all?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseAccess.clearAllDataFromTable("category");

                            List = databaseAccess.getCategoryList();

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

        imageView.setVisibility(View.INVISIBLE);

        add_button.setOnClickListener(view -> {

            Intent intent = new Intent(this, Activity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


        });



        List = databaseAccess.getCategoryList();

        setRecycler(List);
    }
    private void setRecycler(List<CategoryModel> categoryList) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        Recycler.setLayoutManager(manager);

        Adaptor = new CategoryAdaptor(this, categoryList);
        Recycler.setAdapter(Adaptor);

    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();


        Recycler = findViewById(R.id.category_list);
        Recycler.setHasFixedSize(true);

        List = databaseAccess.getCategoryList();

        setRecycler(List);
    }
}