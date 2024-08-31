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
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ChangeDessertActivity extends AppCompatActivity {

    SQLiteDatabase database;
    TextInputEditText edit_title, update_portion_size, originalCakeWidth, originalCakeHeight, newCakeWidth, newCakeHeight;
    String title, id, dessert_size, portion_size, desserts;
    byte [] img;
    ImageView imageView, edit_image, setting;
    Toolbar toolbar;
    ImageButton change_dessert_img;
    RadioGroup shapeGroup, a;
    RadioButton shapeCircle, shapeRectangle, shapeSquare, myCircle, myRectangle, mySquare;
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
        insertData();

        toolbar.setSubtitle(getString(R.string.change));
        setting.setVisibility(View.GONE);

        imageView.setOnClickListener(v -> finish());




    }
    private void findView(){
        setting = findViewById(R.id.setting);
        edit_image = findViewById(R.id.dessert_image);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        edit_title = findViewById(R.id.set_dessert_name);
        save_btn = findViewById(R.id.save_dessert);
        change_dessert_img = findViewById(R.id.change_dessert_img);
        update_portion_size = findViewById(R.id.update_portion_size);
        originalCakeWidth = findViewById(R.id.original_cake_width2);
        originalCakeHeight = findViewById(R.id.original_cake_height2);
        newCakeWidth = findViewById(R.id.new_cake_width2);
        newCakeHeight = findViewById(R.id.new_cake_height2);
        shapeGroup = findViewById(R.id.cake_shape_group2);
        shapeCircle = findViewById(R.id.shape_circle2);
        shapeRectangle = findViewById(R.id.shape_rectangle2);
        shapeSquare = findViewById(R.id.shape_square2);
        a = findViewById(R.id.a2);
        myCircle = findViewById(R.id.my_circle2);
        myRectangle = findViewById(R.id.my_rectangle2);
        mySquare = findViewById(R.id.my_square2);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void imagePick(){
        change_dessert_img.setOnClickListener(v -> pickFromGallery());
        edit_image.setOnClickListener(view -> pickFromGallery());
    }
    private void pickFromGallery() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .crop(1f, 1f)
                .start();
    }
    void get_and_set_intent_data(){

        if (getIntent().hasExtra("id")
                && getIntent().hasExtra("title")
                && getIntent().hasExtra("image")
                && getIntent().hasExtra("portion_size")
                && getIntent().hasExtra("new_dessert_width")
                && getIntent().hasExtra("desserts")
                && getIntent().hasExtra("new_dessert_height")
                && getIntent().hasExtra("dessert_height")
                && getIntent().hasExtra("dessert_width")
                && getIntent().hasExtra("shape_name")
                && getIntent().hasExtra("new_shape_name")){


            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            img = getIntent().getByteArrayExtra("image");
            portion_size = getIntent().getStringExtra("portion_size");
            dessert_size = getIntent().getStringExtra("new_dessert_width");
            desserts = getIntent().getStringExtra("desserts");


            edit_title.setText(title);
            update_portion_size.setText(portion_size);
            newCakeWidth.setText(dessert_size);
            newCakeHeight.setText(getIntent().getStringExtra("new_dessert_height"));
            originalCakeWidth.setText(getIntent().getStringExtra("dessert_height"));
            originalCakeHeight.setText(getIntent().getStringExtra("dessert_width"));

            if (Objects.equals(getIntent().getStringExtra("new_shape_name"), "circle")){
                shapeGroup.check(R.id.shape_circle2);
                originalCakeHeight.setVisibility(View.INVISIBLE);

            }else if (Objects.equals(getIntent().getStringExtra("new_shape_name"), "rectangle")){
                shapeGroup.check(R.id.shape_rectangle2);
                originalCakeHeight.setVisibility(View.VISIBLE);
            }else {
                shapeGroup.check(R.id.shape_square2);
                originalCakeHeight.setVisibility(View.INVISIBLE);
            }

            if (Objects.equals(getIntent().getStringExtra("shape_name"), "circle")){
                a.check(R.id.my_circle2);
                newCakeHeight.setVisibility(View.INVISIBLE);
            }else if (Objects.equals(getIntent().getStringExtra("shape_name"), "rectangle")){
                a.check(R.id.my_rectangle2);
                newCakeHeight.setVisibility(View.VISIBLE);
            }else {
                newCakeHeight.setVisibility(View.INVISIBLE);
                a.check(R.id.my_square2);
            }
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

//            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//            if (bitmap != null){
//                edit_image.setImageBitmap(bitmap);
//            }else {
//                edit_image.setImageResource(R.drawable.noun_cake_6710939);
//            }

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

                if (inserted) {
                    finish();
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

        if (Objects.requireNonNull(edit_title.getText()).toString().isEmpty()){
            Toast.makeText(this, R.string.please_add_title, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Objects.requireNonNull(newCakeWidth.getText()).toString().isEmpty() || Objects.requireNonNull(update_portion_size.getText()).toString().isEmpty()) {
            if (newCakeWidth.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.please_add_dessert_size, Toast.LENGTH_SHORT).show();
            } else if (Objects.requireNonNull(update_portion_size.getText()).toString().isEmpty()) {
                Toast.makeText(this, R.string.please_add_portion_size, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.please_add_dessert_number, Toast.LENGTH_SHORT).show();
            }
            return false;
        } else if (newCakeWidth.getText().toString().equals("0") || update_portion_size.getText().toString().equals("0") ) {
            Toast.makeText(this, R.string.please_add_number_except_0, Toast.LENGTH_SHORT).show();
            return false;

        } else if (newCakeWidth.getText().toString().equals(".") || update_portion_size.getText().toString().equals(".")) {
            Toast.makeText(this, R.string.invalid_number_format, Toast.LENGTH_SHORT).show();
            return false;

        } else {
            contentValues.put("new_dessert_width", newCakeWidth.getText().toString());
            contentValues.put("portion_size", update_portion_size.getText().toString());

            if (isValidNumber(newCakeWidth.getText().toString()) && isValidNumber(update_portion_size.getText().toString())) {
                double a = Double.parseDouble(String.valueOf(newCakeWidth.getText()));
                double b = Double.parseDouble(String.valueOf(update_portion_size.getText()));
                contentValues.put("portion", a / b);
            } else {
                Toast.makeText(this, R.string.invalid_number_format, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        double coefficient = 1.0;
        String originalShape = "";
        String newShape = "";

        if (shapeCircle.isChecked()) {
            originalShape = "circle";
            double originalDiameter = Double.parseDouble(Objects.requireNonNull(originalCakeWidth.getText()).toString());
            contentValues.put("dessert_width", originalDiameter);
        } else if (shapeRectangle.isChecked()) {
            originalShape = "rectangle";
            double originalWidth = Double.parseDouble(Objects.requireNonNull(originalCakeWidth.getText()).toString());
            double originalHeight = Double.parseDouble(Objects.requireNonNull(originalCakeHeight.getText()).toString());
            contentValues.put("dessert_width", originalWidth);
            contentValues.put("dessert_height", originalHeight);
        } else if (shapeSquare.isChecked()) {
            originalShape = "square";
            double originalSide = Double.parseDouble(Objects.requireNonNull(originalCakeWidth.getText()).toString());
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
                coefficient = newArea / originalArea;
            }
            contentValues.put("new_dessert_width", newDiameter);
        } else if (myRectangle.isChecked()) {
            newShape = "rectangle";
            double newWidth = Double.parseDouble(newCakeWidth.getText().toString());
            double newHeight = Double.parseDouble(Objects.requireNonNull(newCakeHeight.getText()).toString());
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

//        try {
//            contentValues.put("image", ImageViewToByte(edit_image));
//        } catch (Exception e) {
//            contentValues.put("image", String.valueOf(edit_image));
//        }

        Cursor cursor = database.query("dessert", new String[]{"COUNT(*)"}, "title=?", new String[]{title}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            if (count > 1) {
                Toast.makeText(this, R.string.dessert_already_exists, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        database.update("dessert", contentValues, "id=" + id, null);
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
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