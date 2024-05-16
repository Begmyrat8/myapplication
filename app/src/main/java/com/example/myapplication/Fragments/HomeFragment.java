package com.example.myapplication.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.AddDessertActivity;
import com.example.myapplication.Activities.MainActivity;
import com.example.myapplication.Adaptors.DessertAdaptor;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Models.DessertModel;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView Recycler;
    java.util.List<DessertModel> List = new ArrayList<>();
    DessertAdaptor Adaptor;
    DatabaseAccess databaseAccess;
    ConstraintLayout empty;

    Button add_dessert;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseAccess = DatabaseAccess.getInstances(getContext().getApplicationContext());
        databaseAccess.open();

        empty = view.findViewById(R.id.empty);
        add_dessert = view.findViewById(R.id.add_dessert);
        Recycler = view.findViewById(R.id.category_list);
        Recycler.setHasFixedSize(true);

        add_dessert.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), AddDessertActivity.class).putExtra("style", ((MainActivity)getContext()).getMyStyleId()), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());

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

    @Override
    public void onResume() {
        super.onResume();
        databaseAccess = DatabaseAccess.getInstances(getContext().getApplicationContext());
        databaseAccess.open();

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

    private void setRecycler(List<DessertModel> categoryList) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        Recycler.setLayoutManager(manager);

        Adaptor = new DessertAdaptor(getContext(), categoryList,1);
        Recycler.setAdapter(Adaptor);

    }
}