package com.example.myapplication.Activities;

import static java.lang.Double.parseDouble;
import static java.lang.String.valueOf;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

public class AddIngredientsActivity extends AppCompatActivity {

    Button add_product;
    String COL_TITLE = "title";
    String COL_VALUE = "value";
    String COL_PRICE = "price";
    String COL_GRAM_PRICE = "gram_price";
    String COL_IMAGE = "image";
    String COL_CATEGORY_ID = "dessert_id";
    String COL_UNIT = "units";
    String category_id;
    ImageView imageView, set_image, lang, delete;
    TextInputEditText set_title, set_value, set_price;
    TextView  textInputLayout11;
    AutoCompleteTextView autoComplete;
    ArrayAdapter<String> adapterItem;
    TextView textview7;
    String[] item = {"gram","piece","milliliter"};
    Toolbar toolbar;
    ImageButton add_ingredient_img;
    private SQLiteDatabase database;
    DatabaseAccess databaseAccess;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);


        category_id = getIntent().getStringExtra("dessertId");

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();


        findView();
        insertData();
        imagePick();

        delete.setVisibility(View.GONE);
        lang.setVisibility(View.GONE);
        adapterItem = new ArrayAdapter<String>(this,R.layout.item_list,item);
        autoComplete.setAdapter(adapterItem);

        toolbar.setSubtitle(getString(R.string.add));

        imageView.setOnClickListener(v -> {
            finish();

        });
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                textview7.setText("how much " + item);
                if (autoComplete.getText().toString().equals("gram")){
                    textInputLayout11.setText("1 kg price");
                }
                if (autoComplete.getText().toString().equals("milliliter")){
                    textInputLayout11.setText("1 liter price");
                }
                if (autoComplete.getText().toString().equals("piece")){
                    textInputLayout11.setText("1 piece price");
                }
            }
        });



    }
    private void insertData (){
        add_product.setOnClickListener(view -> {
            boolean inserted = insertTitleIfNotExists(set_title.getText().toString());

            if(inserted) {
                Toast.makeText(this, "Save successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ingredient already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void findView(){

        textInputLayout11 = findViewById(R.id.textView99);
        textview7 = findViewById(R.id.textView7);
        lang = findViewById(R.id.lang);
        autoComplete = findViewById(R.id.autoComplete);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        set_title = findViewById(R.id.set_title);
        set_value = findViewById(R.id.set_gram);
        set_price = findViewById(R.id.set_price);
        add_product = findViewById(R.id.add_product);
        set_image = findViewById(R.id.set_image);
        delete = findViewById(R.id.delete);
        add_ingredient_img = findViewById(R.id.add_ingredient_img);

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
        add_ingredient_img.setOnClickListener(v -> {

            pickFromGallery();

        });
    }



    private void pickFromGallery() {
        ImagePicker.with(this)
                .crop(1f,1f)
                .start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        set_image.setImageURI(uri);
    }
    private boolean insertTitleIfNotExists(String title) {
        // Check if the title exists in the database
        Cursor cursor = database.rawQuery("SELECT * FROM " + "list" + " WHERE " + "title" + " = ?", new String[]{title});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // Title already exists
        }

        // Insert
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, set_title.getText().toString());
        contentValues.put(COL_CATEGORY_ID, category_id);

        try {
            contentValues.put(COL_IMAGE, ImageViewToByte(set_image));

        } catch (Exception e) {

            contentValues.put(COL_IMAGE, valueOf(set_image));
        }

        if (set_price.getText().toString().isEmpty() || set_value.getText().toString().isEmpty()){

            if (set_price.getText().toString().isEmpty()){
                contentValues.put(COL_PRICE, 0);
            }else {
                contentValues.put(COL_PRICE, set_price.getText().toString());
            }
            if (set_value.getText().toString().isEmpty()){
                contentValues.put(COL_VALUE, 0);
            }else {
                contentValues.put(COL_VALUE, set_value.getText().toString());
            }
            if (autoComplete.getText().toString().equals("piece")) {
                contentValues.put(COL_UNIT, item[1]);

            } else if (autoComplete.getText().toString().equals("gram")){
                contentValues.put(COL_UNIT, item[0]);
            }else if (autoComplete.getText().toString().equals("milliliter")){
                contentValues.put(COL_UNIT, item[2]);
            }
        }else {
            contentValues.put(COL_PRICE, set_price.getText().toString());
            contentValues.put(COL_VALUE, set_value.getText().toString());
            double a = parseDouble(valueOf(set_price.getText()));
            double b = parseDouble(valueOf(set_value.getText()));
            double c = a * b;
            if (autoComplete.getText().toString().equals("piece")) {
                contentValues.put(COL_GRAM_PRICE, c);
                contentValues.put(COL_UNIT, item[1]);

            } else if (autoComplete.getText().toString().equals("gram")){
                double v = b /1000;
                double r = v * a ;
                contentValues.put(COL_GRAM_PRICE, r);
                contentValues.put(COL_UNIT, item[0]);
            }else if (autoComplete.getText().toString().equals("milliliter")){
                double v = b /1000;
                double r = v * a ;
                contentValues.put(COL_GRAM_PRICE, r);
                contentValues.put(COL_UNIT, item[2]);
            }
        }



        database.insert("list", null, contentValues);

        cursor.close();
        return true; // Title inserted successfully
    }

}