package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
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

public class AddIngredientsActivity extends AppCompatActivity {

    Button add_product;
    String COL_TITLE = "title";
    String COL_GRAM = "gram";
    String COL_KG = "kg";
    String COL_Price = "price";
    String COL_GRAM_PRICE = "gram_price";
    String COL_IMAGE = "image";
    String COL_CATEGORY_ID = "dessert_id";
    String category_id;
    ImageView imageView, set_image, lang;
    TextInputEditText set_title, set_gram, set_price;
    AutoCompleteTextView autoComplete;
    ArrayAdapter<String> adapterItem;
    String[] item = {"gram","thing"};
    Toolbar toolbar;
    private SQLiteDatabase database;
    DatabaseAccess databaseAccess;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);


        category_id = getIntent().getStringExtra("categoryId");

        databaseAccess = DatabaseAccess.getInstances(this.getApplicationContext());
        databaseAccess.open();

        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();


        findView();
        insertData();
        imagePick();

        lang.setVisibility(View.INVISIBLE);
        adapterItem = new ArrayAdapter<String>(this,R.layout.item_list,item);
        autoComplete.setAdapter(adapterItem);

        toolbar.setSubtitle(getString(R.string.add));

        imageView.setOnClickListener(v -> {
            finish();

        });
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                set_gram.setHint(item);
            }
        });


    }
    private void insertData (){
        add_product.setOnClickListener(view -> {

            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_TITLE, set_title.getText().toString());
            contentValues.put(COL_Price, set_price.getText().toString());
            contentValues.put(COL_GRAM_PRICE, set_price.getText().toString());
            contentValues.put(COL_CATEGORY_ID, category_id);
            if (autoComplete.getText().toString().equals("thing")){
                contentValues.put("thing", set_gram.getText().toString());
            }else {
                contentValues.put(COL_GRAM, set_gram.getText().toString());
            }
            try {
                contentValues.put(COL_IMAGE, ImageViewToByte(set_image));

            }catch (Exception e){
                set_image.setImageResource(R.drawable.baseline_add_a_white_photo);
                contentValues.put(COL_IMAGE, String.valueOf(set_image));
            }

            Long result = database.insert("list", null, contentValues);
            if (result != null){
                Toast.makeText(this, getText(R.string.saved), Toast.LENGTH_SHORT).show();
            }

        });
    }
    private void findView(){

        lang = findViewById(R.id.lang);
        autoComplete = findViewById(R.id.autoComplete);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        set_title = findViewById(R.id.set_title);
        set_gram = findViewById(R.id.set_gram);
        set_price = findViewById(R.id.set_price);
        add_product = findViewById(R.id.add_product);
        set_image = findViewById(R.id.set_image);

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
        set_image.setOnClickListener(v -> {

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
        set_image.setImageURI(uri);
    }

}