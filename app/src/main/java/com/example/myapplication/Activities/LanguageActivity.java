package com.example.myapplication.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        showLanguagePicker();
    }
    private void showLanguagePicker() {

        final String langs[] = {"ENG","RUS","TKM"};

        AlertDialog.Builder builder = new AlertDialog.Builder(LanguageActivity.this);
        builder.setTitle(R.string.select_language);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(langs, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("en");
                    changeFirstTime();
                } else if (which == 1) {
                    setLocale("ru");
                    changeFirstTime();
                }else if (which == 2) {
                    setLocale("tkm");
                    changeFirstTime();
                }
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("lang", lang);
        editor.apply();
    }

    private void changeFirstTime(){
        SharedPreferences.Editor editor = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE).edit();
        editor.putBoolean("first_run", false).apply();
        startActivity(new Intent(LanguageActivity.this, MainActivity.class));
    }
}