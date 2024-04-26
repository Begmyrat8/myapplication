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
import java.util.Objects;

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
    String[] item = {"Gram","Piece","Milliliter"};
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

        String[] items = getResources().getStringArray(R.array.items);
        adapterItem = new ArrayAdapter<>(this, R.layout.item_list, items);
        autoComplete.setAdapter(adapterItem);

        toolbar.setSubtitle(getString(R.string.add));

        imageView.setOnClickListener(v -> finish());


        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                handleSelectedItem(item);
            }
        });

    }

    private void insertData (){
        add_product.setOnClickListener(view -> {
            boolean inserted = insertTitleIfNotExists(Objects.requireNonNull(set_title.getText()).toString());

            if(inserted) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.dessert_already_exists, Toast.LENGTH_SHORT).show();
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
        return stream.toByteArray();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void imagePick(){
        add_ingredient_img.setOnClickListener(v -> pickFromGallery());
    }

    private void pickFromGallery() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .crop(1f, 1f)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        Uri uri = data.getData();
        set_image.setImageURI(uri);
    }
    private boolean insertTitleIfNotExists(String title) {
        try (Cursor cursor = database.rawQuery("SELECT * FROM list WHERE title = ?", new String[]{title})) {

            if (cursor.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COL_TITLE, Objects.requireNonNull(set_title.getText()).toString());
                contentValues.put(COL_CATEGORY_ID, category_id);
                String[] items = getResources().getStringArray(R.array.items);

                try {
                    contentValues.put(COL_IMAGE, ImageViewToByte(set_image));

                } catch (Exception e) {

                    contentValues.put(COL_IMAGE, valueOf(set_image));
                }

                if (Objects.requireNonNull(set_price.getText()).toString().isEmpty() || Objects.requireNonNull(set_value.getText()).toString().isEmpty()) {


                    if (set_price.getText().toString().isEmpty()) {
                        contentValues.put(COL_PRICE, 0);
                    } else {
                        contentValues.put(COL_PRICE, set_price.getText().toString());
                    }
                    if (Objects.requireNonNull(set_value.getText()).toString().isEmpty()) {
                        contentValues.put(COL_VALUE, 0);
                    } else {
                        contentValues.put(COL_VALUE, set_value.getText().toString());
                    }
                    if (autoComplete.getText().toString().equals(items[1])) {
                        contentValues.put(COL_UNIT, item[1]);

                    } else if (autoComplete.getText().toString().equals(items[0])) {
                        contentValues.put(COL_UNIT, item[0]);
                    } else if (autoComplete.getText().toString().equals(items[2])) {
                        contentValues.put(COL_UNIT, item[2]);
                    }
                } else {
                    contentValues.put(COL_PRICE, set_price.getText().toString());
                    contentValues.put(COL_VALUE, set_value.getText().toString());
                    double a = parseDouble(valueOf(set_price.getText()));
                    double b = parseDouble(valueOf(set_value.getText()));
                    double c = a * b;

                    if (autoComplete.getText().toString().equals(items[1])) {
                        contentValues.put(COL_GRAM_PRICE, c);
                        contentValues.put(COL_UNIT, item[1]);

                    } else if (autoComplete.getText().toString().equals(items[0])) {
                        double v = b / 1000;
                        double r = v * a;
                        contentValues.put(COL_GRAM_PRICE, r);
                        contentValues.put(COL_UNIT, item[0]);
                    } else if (autoComplete.getText().toString().equals(items[2])) {
                        double v = b / 1000;
                        double r = v * a;
                        contentValues.put(COL_GRAM_PRICE, r);
                        contentValues.put(COL_UNIT, item[2]);
                    }
                }

                database.insert("list", null, contentValues);
                return true;
            } else {
                return false;
            }
        }
        // Закройте курсор после завершения операции

    }

    @SuppressLint("SetTextI18n")
    private void handleSelectedItem(String selectedItem) {
        String[] items = getResources().getStringArray(R.array.items);
        String kg = getResources().getString(R.string.kg);
        String price = getResources().getString(R.string.small_price);
        String liter = getResources().getString(R.string.liter);
        String piece = getResources().getString(R.string.piece);
        String how_many = getResources().getString(R.string.how_many);
        String used = getResources().getString(R.string.used);

        textview7.setText(how_many + " " + selectedItem + " " + used);

        if (selectedItem.equals(items[0])) {
            textInputLayout11.setText("1 " + kg + " " + price);
        } else if (selectedItem.equals(items[1])) {
            textInputLayout11.setText("1 " + liter + " " + price);
        } else if (selectedItem.equals(items[2])) {
            textInputLayout11.setText("1 " + piece + " " + price);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearAutoCompleteText();
    }
    private void clearAutoCompleteText() {
        if (autoComplete != null) {
            autoComplete.setText(""); // Clear the text
        }
    }
}