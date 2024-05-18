package com.example.myapplication.Activities;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Adaptors.FragmentAdapter;
import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Fragments.BookmarkFragment;
import com.example.myapplication.Fragments.HomeFragment;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends BaseActivity {

    DatabaseAccess databaseAccess;
    ConstraintLayout empty;
    ImageView imageView, lang, delete, modes;
    FloatingActionButton add_button;
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
        int theme = getSharedPreferences("a", MODE_PRIVATE).getInt("theme", 0);
        int mode = getSharedPreferences("mode", MODE_PRIVATE).getInt("mode", 0);

        setDefaultNightMode(mode);
        setTheme(theme);
        super.onViewReady(savedInstanceState, intent);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            startActivity(new Intent(MainActivity.this, LanguageActivity.class));
            finish();
        }

        toolbar = findViewById(R.id.toolbar);
        bnv = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.view_pager);
        add_dessert = findViewById(R.id.add_dessert);
        delete = findViewById(R.id.delete);
        lang = findViewById(R.id.lang);
        modes = findViewById(R.id.mode);
        imageView = findViewById(R.id.imageView);
        add_button = findViewById(R.id.buttons2);
        empty = findViewById(R.id.empty);

        lang.setOnClickListener(v -> showLanguagePicker(this, false));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(this, bnv);
        viewPager.setAdapter(fragmentAdapter);


        binding.bottomNavigationView.setBackground(null);

        int color;
        if (theme == R.style.AppTheme){
            color = ContextCompat.getColor(this, R.color.chocolate);
            modes.setImageResource(R.drawable.sun);
        }else if (theme == R.style.AppTheme_Dark){
            color = ContextCompat.getColor(this, R.color.orange);
            modes.setImageResource(R.drawable.moon);
        }else if (theme == R.style.AppTheme_Blue) {
            color = ContextCompat.getColor(this, R.color.green);
            modes.setImageResource(R.drawable.clouds);
        }else {
            color = 0;
        }


        bnv.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                viewPager.setCurrentItem(0, false);
                setIconColor(bnv.getOrCreateBadge(R.id.home), true, color); // Pass true to indicate selected
                setIconColor(bnv.getOrCreateBadge(R.id.library), false, color);
                toolbar.setSubtitle(R.string.app_name);
                return true;
            } else if (itemId == R.id.library) {
                viewPager.setCurrentItem(1, false);
                setIconColor(bnv.getOrCreateBadge(R.id.home), false, color); // Pass true to indicate selected
                setIconColor(bnv.getOrCreateBadge(R.id.library), true, color);
                toolbar.setSubtitle(R.string.liked);
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

        modes.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.mode_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.light_mode) {
                    setAppTheme(R.style.AppTheme);
                    setDefaultNightMode(MODE_NIGHT_NO);
                    modes.setImageResource(R.drawable.sun);

                    saveMyTheme(R.style.AppTheme);
                    saveMyMode(MODE_NIGHT_NO);
                    recreate(); // Recreate the activity to apply the new theme
                    return true;
                } else if (itemId == R.id.night_mode) {
                    setAppTheme(R.style.AppTheme_Dark);
                    setDefaultNightMode(MODE_NIGHT_YES);
                    modes.setImageResource(R.drawable.moon);
                    saveMyTheme(R.style.AppTheme_Dark);
                    saveMyMode(MODE_NIGHT_YES);
                    recreate(); // Recreate the activity to apply the new theme
                    return true;
                } else if (itemId == R.id.blue_mode) {
                    setAppTheme(R.style.AppTheme_Blue);
                    setDefaultNightMode(MODE_NIGHT_NO);
                    modes.setImageResource(R.drawable.clouds);
                    saveMyTheme(R.style.AppTheme_Blue);
                    saveMyMode(MODE_NIGHT_NO);
                    recreate(); // Recreate the activity to apply the new theme
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        delete.setOnClickListener(v -> {
            Fragment currentFragment = getCurrentFragment();

            if (currentFragment instanceof HomeFragment) {
                showAlertDialog("dessert");
            } else if (currentFragment instanceof BookmarkFragment) {
                showAlertDialog("bookmark");
            }

        });

        imageView.setVisibility(View.INVISIBLE);

        add_button.setOnClickListener(view -> startActivity(new Intent(this, AddDessertActivity.class).putExtra("style",getMyStyleId()), ActivityOptions.makeSceneTransitionAnimation(this).toBundle()));

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

    @Override
    public Resources.Theme getTheme() {
        int themes = getSharedPreferences("a",MODE_PRIVATE).getInt("theme", 0);
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(saveMyTheme(themes), true);
        return theme;
    }

    @SuppressLint("UnsafeIntentLaunch")
    public void setAppTheme(int themeId) {
        // Пересоздание активности для применения изменений
        Intent intent = getIntent();
        intent.putExtra("themeId", themeId);
        finish();
        startActivity(intent);
    }

    public int getMyStyleId() {
        return getIntent().getIntExtra("themeId", R.style.AppTheme);
    }

    public int saveMyTheme(int theme){

        SharedPreferences.Editor editor = getSharedPreferences("a", MODE_PRIVATE).edit();
        editor.putInt("theme", theme);
        editor.apply();
        return theme;
    }
    public void saveMyMode(int mode){

        SharedPreferences.Editor editor = getSharedPreferences("mode", MODE_PRIVATE).edit();
        editor.putInt("mode", mode);
        editor.apply();
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
    private void showAlertDialog(String tableName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_all))
                .setCancelable(true)
                .setPositiveButton(getText(R.string.yes), (dialog, which) -> {
                    databaseAccess.clearAllDataFromTable(tableName);
                    refresh(); // Assuming you have a method to refresh the UI
                })
                .setNegativeButton(getText(R.string.no), (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}