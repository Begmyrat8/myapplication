package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    ImageView imageView, image;
    EditText set_name, set_portion_size, originalCakeWidth, originalCakeHeight, newCakeWidth, newCakeHeight, desserts;
    Toolbar toolbar;
    ImageButton add_img;
    RadioGroup shapeGroup, a;
    RadioButton shapeCircle, shapeRectangle, shapeSquare, myCircle, myRectangle, mySquare;
    private SQLiteDatabase database;
    DatabaseAccess databaseAccess;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setMode();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dessert);

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        findView();
        insertData();
        imagePick();

        toolbar.setSubtitle(R.string.add_dessert);

        imageView.setOnClickListener(v -> finish());
    }

    private void setMode() {
        String mode = getSharedPreferences("Settings", MODE_PRIVATE).getString("mode", "light");

        // Применяем тему перед super.onCreate()
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

    private void findView() {
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        set_name = findViewById(R.id.set_name);
        add_btn = findViewById(R.id.set);
        image = findViewById(R.id.image);
        add_img = findViewById(R.id.add_dessert_img);
        set_portion_size = findViewById(R.id.set_portion_size);
        originalCakeWidth = findViewById(R.id.original_cake_width);
        originalCakeHeight = findViewById(R.id.original_cake_height);
        newCakeWidth = findViewById(R.id.new_cake_width);
        newCakeHeight = findViewById(R.id.new_cake_height);
        desserts = findViewById(R.id.dessert_model);
        shapeGroup = findViewById(R.id.cake_shape_group);
        shapeCircle = findViewById(R.id.shape_circle);
        shapeRectangle = findViewById(R.id.shape_rectangle);
        shapeSquare = findViewById(R.id.shape_square);
        a = findViewById(R.id.a);
        myCircle = findViewById(R.id.my_circle);
        myRectangle = findViewById(R.id.my_rectangle);
        mySquare = findViewById(R.id.my_square);
    }

    private byte[] ImageViewToByte(ImageView set_image) {
        Bitmap bitmap = ((BitmapDrawable) set_image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void imagePick() {
        add_img.setOnClickListener(v -> pickFromGallery());
        image.setOnClickListener(view -> pickFromGallery());
    }

    private void pickFromGallery() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        image.setImageURI(uri);
    }

    private boolean insertTitleIfNotExists() {
        // Check if the title exists in the database
        Cursor cursor = database.rawQuery("SELECT * FROM " + "dessert" + " WHERE " + "title" + " = ?", new String[]{set_name.getText().toString()});
        if (cursor.getCount() > 0) {
            Toast.makeText(this, R.string.dessert_already_exists, Toast.LENGTH_SHORT).show();
            cursor.close();
            return false; // Title already exists
        }

        // Insert the title
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, set_name.getText().toString());
        contentValues.put("desserts", desserts.getText().toString());

        if (set_name.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.please_add_title, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newCakeWidth.getText().toString().isEmpty() || set_portion_size.getText().toString().isEmpty() || desserts.getText().toString().isEmpty()) {
            if (newCakeWidth.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.please_add_dessert_size, Toast.LENGTH_SHORT).show();
            } else if (set_portion_size.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.please_add_portion_size, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.please_add_dessert_number, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        if (newCakeWidth.getText().toString().equals("0") || set_portion_size.getText().toString().equals("0") || desserts.getText().toString().equals("0")) {
            Toast.makeText(this, R.string.please_add_number_except_0, Toast.LENGTH_SHORT).show();
            return false;
        } else if (newCakeWidth.getText().toString().equals(".") || set_portion_size.getText().toString().equals(".") || desserts.getText().toString().equals(".")) {
            Toast.makeText(this, R.string.invalid_number_format, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            contentValues.put("dessert_size", newCakeWidth.getText().toString());
            contentValues.put("portion_size", set_portion_size.getText().toString());
            double a = Double.parseDouble(newCakeWidth.getText().toString());
            double b = Double.parseDouble(set_portion_size.getText().toString());
            contentValues.put("portion", a / b);
        }

        // Add shape-specific coefficient
        double coefficient = 1.0;
        if (shapeCircle.isChecked()) {
            String originalDiameterStr = originalCakeWidth.getText().toString();
            String newDiameterStr = newCakeWidth.getText().toString();
            if (originalDiameterStr.isEmpty() || newDiameterStr.isEmpty()) {
                Toast.makeText(this, "Please enter values for both original and new cake diameter", Toast.LENGTH_SHORT).show();
                return false;
            }
            double originalDiameter = Double.parseDouble(originalDiameterStr);
            double newDiameter = Double.parseDouble(newDiameterStr);
            coefficient = Math.pow(newDiameter, 2) / Math.pow(originalDiameter, 2);
        } else if (shapeRectangle.isChecked()) {
            String originalWidthStr = originalCakeWidth.getText().toString();
            String originalHeightStr = originalCakeHeight.getText().toString();
            String newWidthStr = newCakeWidth.getText().toString();
            String newHeightStr = newCakeHeight.getText().toString();
            if (originalWidthStr.isEmpty() || originalHeightStr.isEmpty() || newWidthStr.isEmpty() || newHeightStr.isEmpty()) {
                Toast.makeText(this, "Please enter values for all dimensions of original and new cakes", Toast.LENGTH_SHORT).show();
                return false;
            }
            double originalWidth = Double.parseDouble(originalWidthStr);
            double originalHeight = Double.parseDouble(originalHeightStr);
            double newWidth = Double.parseDouble(newWidthStr);
            double newHeight = Double.parseDouble(newHeightStr);
            double originalArea = originalWidth * originalHeight;
            double newArea = newWidth * newHeight;
            coefficient = newArea / originalArea;
        } else if (shapeSquare.isChecked()) {
            String originalSideStr = originalCakeWidth.getText().toString();
            String newSideStr = newCakeWidth.getText().toString();
            if (originalSideStr.isEmpty() || newSideStr.isEmpty()) {
                Toast.makeText(this, "Please enter values for both original and new cake side length", Toast.LENGTH_SHORT).show();
                return false;
            }
            double originalSide = Double.parseDouble(originalSideStr);
            double newSide = Double.parseDouble(newSideStr);
            coefficient = Math.pow(newSide, 2) / Math.pow(originalSide, 2);
        }

        try {
            contentValues.put(COL_IMAGE, ImageViewToByte(image));
        } catch (Exception e) {
            contentValues.put(COL_IMAGE, String.valueOf(image));
        }

        database.insert("dessert", null, contentValues);
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        cursor.close();
        return true; // Title inserted successfully
    }

    private void insertData() {
        add_btn.setOnClickListener(view -> {
            boolean inserted = insertTitleIfNotExists();
            if (inserted) {
                finish();
            }
        });
    }
}

