package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;

public  class LanguageActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_language;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        setContentView(R.layout.activity_language);

        showLanguagePicker(this, true);
    }

}