package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Adaptors.FragmentAdapter;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    DatabaseAccess databaseAccess;
    ConstraintLayout empty;
    ImageView imageView, setting;
    Button add_dessert;
    ViewPager2 viewPager;
    SharedPreferences prefs = null;
    ActivityMainBinding binding;
    Toolbar toolbar;
    BottomNavigationView bnv;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"NonConstantResourceId", "MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onViewReady(Bundle savedInstanceState,Intent intent) {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "English");
        String mode = sharedPreferences.getString("mode", "light");

        // Apply language
        Locale locale;
        if (language.equals("English")) {
            locale = new Locale("en");
        } else if (language.equals("Русский")) {
            locale = new Locale("ru");
        } else {
            locale = new Locale(" ");
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        int color;
        // Apply mode
        if (mode.equals("dark")) {
            setTheme(R.style.AppTheme_Dark);
            color = ContextCompat.getColor(this, R.color.orange);
        } else if (mode.equals("blue")){
            setTheme(R.style.AppTheme_Blue);
            color = ContextCompat.getColor(this, R.color.green);
        }else {
            setTheme(R.style.AppTheme);
            color = ContextCompat.getColor(this, R.color.chocolate);
        }

        super.onViewReady(savedInstanceState, intent);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        updateStatusBarColor();
        setContentView(R.layout.activity_main);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            startActivity(new Intent(MainActivity.this, LanguageActivity.class));
            finish();
        }

        setting = findViewById(R.id.setting);
        toolbar = findViewById(R.id.toolbar);
        bnv = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.view_pager);
        add_dessert = findViewById(R.id.add_dessert);
        imageView = findViewById(R.id.imageView);
        empty = findViewById(R.id.empty);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(this, bnv);
        viewPager.setAdapter(fragmentAdapter);

        binding.bottomNavigationView.setBackground(null);

        bnv.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                viewPager.setCurrentItem(0, true);
                setIconColor(bnv.getOrCreateBadge(R.id.home), true, color); // Pass true to indicate selected
                setIconColor(bnv.getOrCreateBadge(R.id.library), false, color);
                toolbar.setSubtitle(R.string.app_name);
                return true;

            } else if (itemId == R.id.library) {
                viewPager.setCurrentItem(1, true);
                setIconColor(bnv.getOrCreateBadge(R.id.home), false, color); // Pass true to indicate selected
                setIconColor(bnv.getOrCreateBadge(R.id.library), true, color);
                toolbar.setSubtitle(R.string.liked);
                return true;
            } else if (itemId == R.id.add) {
                startActivity(new Intent(this, AddDessertActivity.class));
                return true;
            }
            return false;


        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        setIconColor(bnv.getOrCreateBadge(R.id.home), true, color); // Pass true to indicate selected
                        setIconColor(bnv.getOrCreateBadge(R.id.library), false, color);
                        toolbar.setSubtitle(R.string.app_name);
                        break;
                    case 1:
                        setIconColor(bnv.getOrCreateBadge(R.id.home), false, color); // Pass true to indicate selected
                        setIconColor(bnv.getOrCreateBadge(R.id.library), true, color);
                        toolbar.setSubtitle(R.string.liked);
                        break;
                }
            }
        });

        setting.setOnClickListener(view -> {

            startActivity(new Intent(this, SettingActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });

        imageView.setVisibility(View.INVISIBLE);

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


    public String getMyStyleId() {
        return getSharedPreferences("Setting",MODE_PRIVATE).getString("mode", "light");
    }


    private void setIconColor(BadgeDrawable badgeDrawable, boolean selected, int color) {
        if (badgeDrawable != null) {
            badgeDrawable.setBackgroundColor(selected ? color : Color.TRANSPARENT); // Set the color based on selection
        }
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // This will return the fragment currently displayed in your ViewPager2
        return fragmentManager.findFragmentByTag("f" + viewPager.getCurrentItem());

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", "light");

        if (mode.equals("dark")) {
            window.setStatusBarColor(getResources().getColor(R.color.brown, getTheme()));
        } else if (mode.equals("blue")){
            window.setStatusBarColor(getResources().getColor(R.color.blue, getTheme()));
        }else if (mode.equals("light")){
            window.setStatusBarColor(getResources().getColor(R.color.dark_chocolate, getTheme()));
        }

    }

}