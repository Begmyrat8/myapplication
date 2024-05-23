package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ChangeDessertActivity extends AppCompatActivity {

    SQLiteDatabase database;
    TextInputEditText edit_title, update_dessert_size, update_portion_size;
    String title, id, dessert_size, portion_size;
    byte [] img;
    ImageView imageView, edit_image, setting;
    Toolbar toolbar;
    ImageButton change_dessert_img;
    Button save_btn;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_dessert);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(this);
        databaseAccess.open();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();
        
        findView();
        get_and_set_intent_data();
        imagePick();
        insertData();

        toolbar.setSubtitle(getString(R.string.change));
        setting.setVisibility(View.GONE);

        imageView.setOnClickListener(v -> finish());

    }
    private void findView(){
        setting =findViewById(R.id.setting);
        edit_image = findViewById(R.id.dessert_image);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        edit_title = findViewById(R.id.set_dessert_name);
        save_btn = findViewById(R.id.save_dessert);
        change_dessert_img = findViewById(R.id.change_dessert_img);
        update_dessert_size = findViewById(R.id.update_dessert_size);
        update_portion_size = findViewById(R.id.update_portion_size);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void imagePick(){
        change_dessert_img.setOnClickListener(v -> {pickFromGallery();});
        edit_image.setOnClickListener(view -> {pickFromGallery();});
    }
    private void pickFromGallery() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .crop(10f, 10f)
                .start();
    }
    void get_and_set_intent_data(){

        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")  && getIntent().hasExtra("image") && getIntent().hasExtra("portion_size") && getIntent().hasExtra("dessert_size")){


            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            img = getIntent().getByteArrayExtra("image");
            portion_size = getIntent().getStringExtra("portion_size");
            dessert_size = getIntent().getStringExtra("dessert_size");

            edit_title.setText(title);
            update_portion_size.setText(portion_size);
            update_dessert_size.setText(dessert_size);

            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            if (bitmap != null){
                edit_image.setImageBitmap(bitmap);
            }else {
                edit_image.setImageResource(R.drawable.noun_cake_6710939);
            }

        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }

    }

    private byte [] ImageViewToByte(ImageView set_image){
        Bitmap bitmap =((BitmapDrawable)set_image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
        return stream.toByteArray();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        Uri uri = data.getData();
        edit_image.setImageURI(uri);
    }
    private void insertData (){
        save_btn.setOnClickListener(view -> {
            boolean inserted = updateTitleIfNotExists();

            if (Objects.requireNonNull(edit_title.getText()).toString().isEmpty()){
                Toast.makeText(this, R.string.please_add_title, Toast.LENGTH_SHORT).show();
            }else {
                if (inserted) {
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.dessert_already_exists, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isValidNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean updateTitleIfNotExists() {

        ContentValues contentValues = new ContentValues();
        String title = Objects.requireNonNull(edit_title.getText()).toString();
        contentValues.put("title", title);

        if (Objects.requireNonNull(update_dessert_size.getText()).toString().isEmpty() || Objects.requireNonNull(update_portion_size.getText()).toString().isEmpty()) {
            if (update_dessert_size.getText().toString().isEmpty()) {
                contentValues.put("dessert_size", 0);
            } else {
                contentValues.put("portion_size", 0);
            }
        } else {
            contentValues.put("dessert_size", update_dessert_size.getText().toString());
            contentValues.put("portion_size", update_portion_size.getText().toString());

            if (isValidNumber(update_dessert_size.getText().toString()) && isValidNumber(update_portion_size.getText().toString())) {
                double a = Double.parseDouble(String.valueOf(update_dessert_size.getText()));
                double b = Double.parseDouble(String.valueOf(update_portion_size.getText()));
                contentValues.put("portion", a / b);
            } else {
                // Handle invalid input case, e.g., show a message to the user
                Toast.makeText(this, R.string.invalid_number_format, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        try {
            contentValues.put("image", ImageViewToByte(edit_image));
        } catch (Exception e) {
            contentValues.put("image", String.valueOf(edit_image));
        }

        if (title.isEmpty()){
            return false;
        }

// Check if the title already exists in the database
        Cursor cursor = database.query("dessert", new String[]{"COUNT(*)"}, "title=?", new String[]{title}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            if (count > 1) { // assuming you want to check for more than one occurrence
                return false;
            }
        }

// Proceed with the update
        database.update("dessert", contentValues, "id=" + id, null);
        return true;


    }
    private void setMode() {
        String mode = getSharedPreferences("Settings", MODE_PRIVATE).getString("mode", "light");

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
}