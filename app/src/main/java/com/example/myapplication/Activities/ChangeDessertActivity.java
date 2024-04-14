package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

public class ChangeDessertActivity extends AppCompatActivity {

    SQLiteDatabase database;
    TextInputEditText edit_title, update_dessert_size, update_portion_size;
    String title, id, dessert_size, portion_size;
    byte [] img;
    ImageView imageView, edit_image, lang, delete;
    Toolbar toolbar;
    ImageButton change_dessert_img;
    Button save_btn;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_dessert);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstances(this);
        databaseAccess.open();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(this);
        database = dbHelper.getWritableDatabase();
        
        lang = findViewById(R.id.lang);
        edit_image = findViewById(R.id.dessert_image);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        edit_title = findViewById(R.id.set_dessert_name);
        save_btn = findViewById(R.id.save_dessert);
        delete = findViewById(R.id.delete);
        change_dessert_img = findViewById(R.id.change_dessert_img);
        update_dessert_size = findViewById(R.id.update_dessert_size);
        update_portion_size = findViewById(R.id.update_portion_size);

        get_and_set_intent_data();

        lang.setVisibility(View.INVISIBLE);
        toolbar.setSubtitle(getString(R.string.change) + " " + title);

        imageView.setOnClickListener(v -> {
           finish();
        });
        change_dessert_img.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop(1f, 1f)
                    .start();
        });

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you want delete?")
                    .setCancelable(true)
                    .setPositiveButton(getText(R.string.Yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.delete("dessert","id="+ id,null);
                            databaseAccess.clearAllDataFromTable("list");
                            finish();
                        }
                    })

                    .setNegativeButton(getText(R.string.No), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        });

        save_btn.setOnClickListener(view -> {

            ContentValues contentValues = new ContentValues();
            contentValues.put("title", edit_title.getText().toString());
            if (update_dessert_size.getText().toString().isEmpty() || update_portion_size.getText().toString().isEmpty()){
                if (update_dessert_size.getText().toString().isEmpty()){
                    contentValues.put("dessert_size", 0);
                }else {
                    contentValues.put("portion_size", 0);
                }
            }else {
                contentValues.put("dessert_size", update_dessert_size.getText().toString());
                contentValues.put("portion_size", update_portion_size.getText().toString());

                double a = Double.parseDouble(String.valueOf(update_dessert_size.getText()));
                double b = Double.parseDouble(String.valueOf(update_portion_size.getText()));

                contentValues.put("portion", a / b);
            }

            try {
                contentValues.put("image", ImageViewToByte(edit_image));

            }catch (Exception e){
                contentValues.put("image", String.valueOf(edit_image));
            }

            long result = database.update("dessert", contentValues,"id="+id ,null);
            if (result == -1){
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    void get_and_set_intent_data(){

        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")  && getIntent().hasExtra("image") && getIntent().hasExtra("portion_size") && getIntent().hasExtra("dessert_size")){

            DecimalFormat decimalFormat = new DecimalFormat();

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