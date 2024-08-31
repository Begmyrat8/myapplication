package com.example.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Datebase.DatabaseAccess;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    DatabaseAccess databaseAccess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
        loadLocale();
        onViewReady(savedInstanceState, getIntent());
    }

    protected abstract int getContentView();
    @CallSuper
    protected void onViewReady(Bundle savedInstanceState, Intent intent){
        //To be used by child activities
    }
    public void showLanguagePicker(Activity activity, boolean isFirstRun) {

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        setLocale("ru");


        if(isFirstRun) changeFirstTime(activity, MainActivity.class);
        else restartActivity();

//        final String[] langs = {"Türkmen", "Русский", "English"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle(R.string.select_language);
//        builder.setCancelable(true);
//        builder.setSingleChoiceItems(langs, -1, (dialog, which) -> {
//            if (which == 0) {
//                setLocale("tk");
//
//                if(isFirstRun) changeFirstTime(activity, MainActivity.class);
//                else restartActivity();
//            } else if (which == 1) {
//                setLocale("ru");
//
//                if(isFirstRun) changeFirstTime(activity, MainActivity.class);
//                else restartActivity();
//            }else if (which == 2) {
//                setLocale("en");
//
//                if(isFirstRun) changeFirstTime(activity, MainActivity.class);
//                else restartActivity();
//            }
//            dialog.dismiss();
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();

    }

    public void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = sharedPreferences.getString("lang", "");
        setLocale(lang);
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("lang", lang);
        editor.apply();
    }

    public void changeFirstTime(Activity from, Class<?> to){
        SharedPreferences.Editor editor = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE).edit();
        editor.putBoolean("firstrun", false).apply();
        startActivity(new Intent(from, to));
    }

    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }
}
