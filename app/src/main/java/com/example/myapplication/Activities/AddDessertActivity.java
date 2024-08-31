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
import android.view.View;
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
    EditText set_name, set_portion_size, originalCakeWidth, originalCakeHeight, newCakeWidth, newCakeHeight;
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

        originalCakeHeight.setVisibility(View.INVISIBLE);
        newCakeHeight.setVisibility(View.INVISIBLE);

        shapeRectangle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                originalCakeHeight.setVisibility(View.VISIBLE);
            } else {
                originalCakeHeight.setVisibility(View.INVISIBLE);
            }
        });
        myRectangle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newCakeHeight.setVisibility(View.VISIBLE);
            } else {
                newCakeHeight.setVisibility(View.INVISIBLE);
            }
        });
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
        // Проверка существования заголовка в базе данных
        Cursor cursor = database.rawQuery("SELECT * FROM dessert WHERE title = ?", new String[]{set_name.getText().toString()});
        if (cursor.getCount() > 0) {
            Toast.makeText(this, R.string.dessert_already_exists, Toast.LENGTH_SHORT).show();
            cursor.close();
            return false; // Заголовок уже существует
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, set_name.getText().toString());

        // Проверка на пустые значения
        if (set_name.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.please_add_title, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newCakeWidth.getText().toString().isEmpty() || set_portion_size.getText().toString().isEmpty()) {
            if (newCakeWidth.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.please_add_dessert_size, Toast.LENGTH_SHORT).show();
            } else if (set_portion_size.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.please_add_portion_size, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.please_add_dessert_number, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        if (newCakeWidth.getText().toString().equals("0") || set_portion_size.getText().toString().equals("0")) {
            Toast.makeText(this, R.string.please_add_number_except_0, Toast.LENGTH_SHORT).show();
            return false;
        } else if (newCakeWidth.getText().toString().equals(".") || set_portion_size.getText().toString().equals(".")) {
            Toast.makeText(this, R.string.invalid_number_format, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            contentValues.put("new_dessert_width", newCakeWidth.getText().toString());
            contentValues.put("portion_size", set_portion_size.getText().toString());
            double a = Double.parseDouble(newCakeWidth.getText().toString());
            double b = Double.parseDouble(set_portion_size.getText().toString());
            contentValues.put("portion", a / b);
        }

        double coefficient = 1.0;
        String originalShape = "";
        String newShape = "";

        if (shapeCircle.isChecked()) {
            originalShape = "circle";
            double originalDiameter = Double.parseDouble(originalCakeWidth.getText().toString());
            contentValues.put("dessert_width", originalDiameter);
        } else if (shapeRectangle.isChecked()) {
            originalShape = "rectangle";
            double originalWidth = Double.parseDouble(originalCakeWidth.getText().toString());
            double originalHeight = Double.parseDouble(originalCakeHeight.getText().toString());
            contentValues.put("dessert_width", originalWidth);
            contentValues.put("dessert_height", originalHeight);
        } else if (shapeSquare.isChecked()) {
            originalShape = "square";
            double originalSide = Double.parseDouble(originalCakeWidth.getText().toString());
            contentValues.put("dessert_width", originalSide);
        }

        if (myCircle.isChecked()) {
            newShape = "circle";
            double newDiameter = Double.parseDouble(newCakeWidth.getText().toString());
            if (originalShape.equals("circle")) {
                double originalDiameter = Double.parseDouble(originalCakeWidth.getText().toString());
                coefficient = Math.pow(newDiameter / originalDiameter, 2);
            } else if (originalShape.equals("rectangle") || originalShape.equals("square")) {
                double originalArea = originalShape.equals("rectangle") ?
                        Double.parseDouble(originalCakeWidth.getText().toString()) * Double.parseDouble(originalCakeHeight.getText().toString()) :
                        Math.pow(Double.parseDouble(originalCakeWidth.getText().toString()), 2);
                double newArea = Math.PI * Math.pow(newDiameter / 2, 2);
                if (originalArea < newArea) {
                    coefficient = newArea / originalArea;
                }else {
                    coefficient = originalArea / newArea;
                }
            }
            contentValues.put("new_dessert_width", newDiameter);
        } else if (myRectangle.isChecked()) {
            newShape = "rectangle";
            double newWidth = Double.parseDouble(newCakeWidth.getText().toString());
            double newHeight = Double.parseDouble(newCakeHeight.getText().toString());
            if (originalShape.equals("circle")) {
                double originalDiameter = Double.parseDouble(originalCakeWidth.getText().toString());
                double originalArea = Math.PI * Math.pow(originalDiameter / 2, 2);
                double newArea = newWidth * newHeight;
                coefficient = newArea / originalArea;
            } else if (originalShape.equals("rectangle") || originalShape.equals("square")) {
                double originalArea = originalShape.equals("rectangle") ?
                        Double.parseDouble(originalCakeWidth.getText().toString()) * Double.parseDouble(originalCakeHeight.getText().toString()) :
                        Math.pow(Double.parseDouble(originalCakeWidth.getText().toString()), 2);
                double newArea = newWidth * newHeight;
                coefficient = newArea / originalArea;
            }
            contentValues.put("new_dessert_width", newWidth);
            contentValues.put("new_dessert_height", newHeight);
        } else if (mySquare.isChecked()) {
            newShape = "square";
            double newSide = Double.parseDouble(newCakeWidth.getText().toString());
            if (originalShape.equals("circle")) {
                double originalDiameter = Double.parseDouble(originalCakeWidth.getText().toString());
                double originalArea = Math.PI * Math.pow(originalDiameter / 2, 2);
                double newArea = Math.pow(newSide, 2);
                coefficient = newArea / originalArea;
            } else if (originalShape.equals("rectangle") || originalShape.equals("square")) {
                double originalArea = originalShape.equals("rectangle") ?
                        Double.parseDouble(originalCakeWidth.getText().toString()) * Double.parseDouble(originalCakeHeight.getText().toString()) :
                        Math.pow(Double.parseDouble(originalCakeWidth.getText().toString()), 2);
                double newArea = Math.pow(newSide, 2);
                coefficient = newArea / originalArea;
            }
            contentValues.put("new_dessert_width", newSide);
        }

        contentValues.put("shape_name", originalShape);
        contentValues.put("new_shape_name", newShape);
        contentValues.put("coefficient", coefficient);

        // Сохранение изображения и других данных
        try {
            contentValues.put(COL_IMAGE, ImageViewToByte(image));
        } catch (Exception e) {
            contentValues.put(COL_IMAGE, String.valueOf(image));
        }

        database.insert("dessert", null, contentValues);
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        cursor.close();
        return true;
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

