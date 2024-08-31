package com.example.myapplication.Fragments;

import android.annotation.SuppressLint;
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

import com.example.myapplication.Adaptors.DessertAdaptor;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Models.DessertModel;
import com.example.myapplication.R;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment {
    RecyclerView recycler;
    java.util.List<DessertModel> List = new ArrayList<>();
    DessertAdaptor adaptor;
    DatabaseAccess databaseAccess;
    ConstraintLayout bookmark_empty;

    Button add_bookmark;

    public BookmarkFragment() {
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
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseAccess = DatabaseAccess.getInstances(getContext().getApplicationContext());
        databaseAccess.open();

        bookmark_empty = view.findViewById(R.id.bookmark_empty);
        recycler = view.findViewById(R.id.bookmark_list);
        recycler.setHasFixedSize(true);

        List = databaseAccess.getBookmarkList();

        setRecycler(List);
        if (List.isEmpty()) {
            recycler.setVisibility(View.GONE);
            bookmark_empty.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            bookmark_empty.setVisibility(View.GONE);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        databaseAccess = DatabaseAccess.getInstances(getContext().getApplicationContext());
        databaseAccess.open();


        recycler.setHasFixedSize(true);

        List = databaseAccess.getBookmarkList();
        setRecycler(List);

        if (List.isEmpty()) {
            recycler.setVisibility(View.GONE);
            bookmark_empty.setVisibility(View.VISIBLE);
        }else {
            recycler.setVisibility(View.VISIBLE);
            bookmark_empty.setVisibility(View.GONE);
        }
        adaptor.notifyDataSetChanged();
    }

    private void setRecycler(java.util.List<DessertModel> bookmarkList) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recycler.setLayoutManager(manager);

        adaptor = new DessertAdaptor(getContext(), bookmarkList,0);
        recycler.setAdapter(adaptor);

    }
}