package com.example.myapplication.Adaptors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.example.myapplication.Fragments.BookmarkFragment;
import com.example.myapplication.Fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    private final Fragment[] fragments;
    BottomNavigationView bottomNavigationView;


    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, BottomNavigationView bottomNavigationView) {
        super(fragmentActivity);
        this.bottomNavigationView = bottomNavigationView;
        fragments = new Fragment[NUM_PAGES];
        fragments[0] = new HomeFragment();
        fragments[1] = new BookmarkFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(viewHolder, position, payloads);

    }
}
