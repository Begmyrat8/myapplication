package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {

    RadioGroup lang, modes;
    ImageView setting, back;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findView();
        loadPreferences();

        toolbar.setSubtitle(R.string.setting);
        setting.setVisibility(View.GONE);

        // Set listeners for radio groups
        lang.setOnCheckedChangeListener((group, checkedId) -> {
            savePreferences();
            applyLanguage();
            recreate(); // Recreate the activity to apply new language
        });

        modes.setOnCheckedChangeListener((group, checkedId) -> {
            savePreferences();
            applyTheme(); // Apply theme without restarting the app
        });
        back.setOnClickListener(view -> {
            finish();
        });

    }

    private void findView() {
        back = findViewById(R.id.imageView);
        setting = findViewById(R.id.setting);
        toolbar = findViewById(R.id.toolbar);
        lang = findViewById(R.id.languageRadioGroup);
        modes = findViewById(R.id.ModeRadioGroup);
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "English");
        String mode = sharedPreferences.getString("mode", "light");

        if (language.equals("English")) {
            lang.check(R.id.radioEnglish);
        } else if (language.equals("Русский")) {
            lang.check(R.id.radioRus);
        } else {
            lang.check(R.id.radioTurkmen);
        }

        if (mode.equals("dark")) {
            modes.check(R.id.radioDark);
        } else if (mode.equals("blue")) {
            modes.check(R.id.radioBlue);
        } else {
            modes.check(R.id.radioLight);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int selectedLanguageId = lang.getCheckedRadioButtonId();
        RadioButton selectedLanguageButton = findViewById(selectedLanguageId);
        String selectedLanguage = selectedLanguageButton.getText().toString();

        int selectedModeId = modes.getCheckedRadioButtonId();
        String selectedMode = "light";
        if (selectedModeId == R.id.radioDark) {
            selectedMode = "dark";
        } else if (selectedModeId == R.id.radioBlue) {
            selectedMode = "blue";
        }

        editor.putString("language", selectedLanguage);
        editor.putString("mode", selectedMode);
        editor.apply();
    }

    private void applyLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "English");

        // Apply language
        Locale locale;
        if (language.equals("English")) {
            locale = new Locale("en");
        } else if (language.equals("Русский")) {
            locale = new Locale("ru");
        } else {
            locale = new Locale("tk");
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String mode = sharedPreferences.getString("mode", "light");

        // Apply mode
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
        recreate(); // Recreate the activity to apply new theme
    }
    private void applyPreferences() {
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
            locale = new Locale("tk");
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Apply mode
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
    }
    private String getCurrentLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        return sharedPreferences.getString("language", "English");
    }

    private String getCurrentMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        return sharedPreferences.getString("mode", "light");
    }

    private void setMode() {
        String mode = getSharedPreferences("Settings", MODE_PRIVATE).getString("mode", "light");

        // Apply theme before super.onCreate()
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
    }
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void previewLanguage(String language) {
        Locale locale;
        if (language.equals("English")) {
            locale = new Locale("en");
        } else if (language.equals("Русский")) {
            locale = new Locale("ru");
        } else {
            locale = new Locale("tk");
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        recreate(); // Recreate activity to apply language change
    }

    private void previewTheme(String mode) {
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
        recreate(); // Recreate activity to apply theme change
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Restore original settings if the user exits without saving
        savePreferences();
        applyPreferences();
        restartApp();
    }
}
