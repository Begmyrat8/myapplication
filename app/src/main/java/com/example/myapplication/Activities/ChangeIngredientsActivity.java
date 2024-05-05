package com.example.myapplication.Activities;

import static java.lang.Double.parseDouble;
import static java.lang.String.valueOf;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ChangeIngredientsActivity extends AppCompatActivity {

    SQLiteDatabase database;
    EditText edit_title, edit_value, edit_price;
    String title, id,  value, price, units;
    byte [] img;
    AutoCompleteTextView autoComplete;
    TextView textInputLayout;
    ArrayAdapter<String> adapterItem;

    ImageView imageView, edit_image, lang, delete;
    Toolbar toolbar;
    ImageButton change_ingredient_img;
    Button save_btn;
    TextView textView77;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_ingredients);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(this);
        databaseAccess.open();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        findView();
        get_and_set_intent_data();
        imagePick();
        insertData();

        lang.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        toolbar.setSubtitle(getString(R.string.change));

        String[] items = getResources().getStringArray(R.array.items);
        adapterItem = new ArrayAdapter<>(this, R.layout.item_list, items);
        autoComplete.setAdapter(adapterItem);

        String kg = getResources().getString(R.string.kg);
        String price = getResources().getString(R.string.small_price);
        String liter = getResources().getString(R.string.liter);
        String piece = getResources().getString(R.string.piece);
        String used = getResources().getString(R.string.used);

        imageView.setOnClickListener(v -> finish());
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                textView77.setText(item + " " + used);
                if (autoComplete.getText().toString().equals(items[0])){
                    textInputLayout.setText("1 " + kg + " " + price);
                }
                if (autoComplete.getText().toString().equals(items[2])){
                    textInputLayout.setText("1 " + liter + " " + price);
                }
                if (autoComplete.getText().toString().equals(items[1])){
                    textInputLayout.setText("1 " + piece + " " + price);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void imagePick(){
        change_ingredient_img.setOnClickListener(v -> {
            pickFromGallery();
        });
        edit_image.setOnClickListener(view -> {
            pickFromGallery();
        });
    }
    private void pickFromGallery() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .crop(1f, 1f)
                .start();
    }
    private void findView() {
        textInputLayout = findViewById(R.id.textView9);
        textView77 = findViewById(R.id.textView77);
        autoComplete = findViewById(R.id.autoComplete2);
        lang = findViewById(R.id.lang);
        edit_image = findViewById(R.id.edit_image);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        edit_title = findViewById(R.id.edit_title);
        edit_value = findViewById(R.id.edit_gram);
        edit_price = findViewById(R.id.edit_price);
        save_btn = findViewById(R.id.save_product);
        delete = findViewById(R.id.delete);
        change_ingredient_img = findViewById(R.id.change_ingredient_img);

    }

    private void insertData (){
        save_btn.setOnClickListener(view -> {
            boolean inserted = updateTitleIfNotExists(Objects.requireNonNull(edit_title.getText()).toString());

            if(inserted) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this, R.string.dessert_already_exists, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean updateTitleIfNotExists(String title) {
        try (Cursor cursor = database.rawQuery("SELECT * FROM list WHERE title = ?", new String[]{title})) {

            if (cursor.getCount() == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", edit_title.getText().toString());
                contentValues.put("price", edit_price.getText().toString());
                String[] items = getResources().getStringArray(R.array.items);
                try {
                    contentValues.put("image", ImageViewToByte(edit_image));

                } catch (Exception e) {
                    contentValues.put("image", String.valueOf(edit_image));
                }

                if (edit_price.getText().toString().isEmpty() || edit_value.getText().toString().isEmpty()) {

                    if (edit_price.getText().toString().isEmpty()){
                        contentValues.put("price", 0);
                    }else if (edit_value.getText().toString().isEmpty()){
                        contentValues.put("value", 0);
                    }
                } else {

                    double a = parseDouble(valueOf(edit_price.getText()));
                    double b = parseDouble(valueOf(edit_value.getText()));
                    double c = a * b;
                    if (autoComplete.getText().toString().equals(items[1])) {

                        contentValues.put("value", edit_value.getText().toString());
                        contentValues.put("units", items[1]);
                        contentValues.put("gram_price", c);

                    } else if (autoComplete.getText().toString().equals(items[0])){
                        double v = b /1000;
                        double r = v * a ;
                        contentValues.put("gram_price",r);
                        contentValues.put("value", edit_value.getText().toString());
                        contentValues.put("units", items[0]);
                    }else {
                        double v = b /1000;
                        double r = v * a ;
                        contentValues.put("gram_price", r);
                        contentValues.put("value", edit_value.getText().toString());
                        contentValues.put("units", items[2]);
                    }
                }
                if (autoComplete.getText().toString().isEmpty()){
                    Toast.makeText(this, R.string.please_select_unit, Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (edit_title.getText().toString().isEmpty()){
                    Toast.makeText(this, R.string.please_add_title, Toast.LENGTH_SHORT).show();
                    return false;
                }

                database.update("list", contentValues, "id=" + id, null);
                return true;

            } else {
                return false;
            }
        }

    }
    @SuppressLint("SetTextI18n")
    void get_and_set_intent_data(){

        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("price") && getIntent().hasExtra("image") && getIntent().hasExtra("value") && getIntent().hasExtra("units")){

            String[] items = getResources().getStringArray(R.array.items);
            String how_many = getResources().getString(R.string.how_many);
            String used = getResources().getString(R.string.used);

            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            value = getIntent().getStringExtra("value");
            price = getIntent().getStringExtra("price");
            img = getIntent().getByteArrayExtra("image");
            units = getIntent().getStringExtra("units");

            if (value.equals("0")){
                if (units == null){
                    textView77.setText(how_many);
                    autoComplete.setText(" ");
                }else if (units.equals(how_many + " " + items[1])){
                    textView77.setText(items[1]);
                    edit_value.setText(units);
                    autoComplete.setText(items[1],false);

                }else if (units.equals(how_many + " " + items[2])){
                    textView77.setText( how_many + " " + items[2]);
                    edit_value.setText(value);
                    autoComplete.setText(items[2],false);
                }
            }else{
                textView77.setText(how_many + " " + units + " " + used);
                edit_value.setText(value);
                autoComplete.setText(units,false);
            }


            edit_title.setText(title);

            if (!Objects.equals(price, "0.0")){
                edit_price.setText(price);
            }

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