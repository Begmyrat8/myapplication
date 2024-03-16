package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;

public class AddDessertActivity extends AppCompatActivity {

    Button add_btn;
    String COL_TITLE = "title";
    String COL_IMAGE = "image";

    ImageView imageView, image, lang;
    EditText set_name;
    Toolbar toolbar;
    private SQLiteDatabase database;
    DatabaseAccess databaseAccess;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dessert);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        findView();
        insertData();
        imagePick();

        lang.setVisibility(View.INVISIBLE);
        toolbar.setSubtitle(getString(R.string.add));

        imageView.setOnClickListener(v -> {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });


    }
    private void insertData (){
        add_btn.setOnClickListener(view -> {

            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_TITLE, set_name.getText().toString());

            try {
                contentValues.put(COL_IMAGE, ImageViewToByte(image));

            }catch (Exception e){
                image.setImageResource(R.drawable.baseline_cake_24);
                contentValues.put(COL_IMAGE, String.valueOf(image));
            }

            Long result = database.insert("category", null, contentValues);
            if (result != null){
                Toast.makeText(this, getText(R.string.saved), Toast.LENGTH_SHORT).show();
            }

        });
    }
    private void findView(){
        lang = findViewById(R.id.lang);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        set_name = findViewById(R.id.set_name);
        add_btn = findViewById(R.id.set);
        image = findViewById(R.id.image);

    }
    private byte [] ImageViewToByte(ImageView set_image){
        Bitmap bitmap =((BitmapDrawable)set_image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
        byte [] bytes = stream.toByteArray();
        return bytes;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void imagePick(){
        image.setOnClickListener(v -> {

            pickFromGallery();

        });
    }



    private void pickFromGallery() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        image.setImageURI(uri);
    }

}