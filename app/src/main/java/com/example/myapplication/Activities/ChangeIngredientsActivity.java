package com.example.myapplication.Activities;

import static java.lang.Double.parseDouble;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
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
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;

public class ChangeIngredientsActivity extends AppCompatActivity {

    SQLiteDatabase database;
    EditText edit_title, edit_gram, edit_price;
    String title, id, gram, price, thing;
    byte [] img;
    AutoCompleteTextView autoComplete;
    TextInputLayout textInputLayout22;
    ArrayAdapter<String> adapterItem;
    String[] item = {"gram","thing"};
    ImageView imageView, edit_image, lang, delete;
    Toolbar toolbar;
    Button save_btn;


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

        textInputLayout22 = findViewById(R.id.textInputLayout22);
        autoComplete = findViewById(R.id.autoComplete2);
        lang = findViewById(R.id.lang);
        edit_image = findViewById(R.id.edit_image);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        edit_title = findViewById(R.id.edit_title);
        edit_gram = findViewById(R.id.edit_gram);
        edit_price = findViewById(R.id.edit_price);
        save_btn = findViewById(R.id.save_product);
        delete = findViewById(R.id.delete);

        get_and_set_intent_data();

        lang.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        toolbar.setSubtitle(getString(R.string.change));

        adapterItem = new ArrayAdapter<String>(this,R.layout.item_list,item);
        autoComplete.setAdapter(adapterItem);


        imageView.setOnClickListener(v -> {
            finish();
        });
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                textInputLayout22.setHint(item);
            }
        });

        edit_image.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });


        save_btn.setOnClickListener(view -> {

            ContentValues contentValues = new ContentValues();
            contentValues.put("title", edit_title.getText().toString());
            contentValues.put("price", edit_price.getText().toString());
            try {
                contentValues.put("image", ImageViewToByte(edit_image));

            } catch (Exception e) {
                edit_image.setImageResource(R.drawable.baseline_add_a_photo_24);
                contentValues.put("image", String.valueOf(edit_image));
            }

            if (edit_price.getText().toString().isEmpty() || edit_gram.getText().toString().isEmpty()) {
                contentValues.put("gram", 0);
                contentValues.put("price", 0);
            } else {
                double a = parseDouble(valueOf(edit_price.getText()));
                if (autoComplete.getText().toString().equals("thing")) {
                    contentValues.put("thing", edit_gram.getText().toString());
                    contentValues.put("gram", 0);

                    double b = parseDouble(valueOf(edit_gram.getText()));
                    double c = a * b;
                    contentValues.put("gram_price", c);

                } else {
                    contentValues.put("gram_price", a / 1000);
                    contentValues.put("gram", edit_gram.getText().toString());
                    contentValues.put("thing", 0);
                }
            }


            long result = database.update("list", contentValues, "id=" + id, null);
            if (result == -1) {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

            }

        });
    }
    @SuppressLint("SetTextI18n")
    void get_and_set_intent_data(){

        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("gram") && getIntent().hasExtra("price") && getIntent().hasExtra("image") && getIntent().hasExtra("thing")){

            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            gram = getIntent().getStringExtra("gram");
            price = getIntent().getStringExtra("price");
            img = getIntent().getByteArrayExtra("image");
            thing = getIntent().getStringExtra("thing");

            if (gram.equals("0")){
                edit_gram.setText(thing);
                autoComplete.setText(item[0],false);
            }else {
                edit_gram.setText(gram);
                autoComplete.setText(item[1],true);
            }


            edit_title.setText(title);
            edit_price.setText(price);

            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            if (bitmap == null){
                edit_image.setImageResource(R.drawable.baseline_add_a_photo_24);
            }else {
                edit_image.setImageBitmap(bitmap);
            }

        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }

    }
    private byte [] ImageViewToByte(ImageView set_image){
        Bitmap bitmap =((BitmapDrawable)set_image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
        byte [] bytes = stream.toByteArray();
        return bytes;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        edit_image.setImageURI(uri);
    }
}