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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.Datebase.DatabaseAccess;
import com.example.myapplication.Datebase.DatabaseOpenHelper;
import com.example.myapplication.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;

public class ChangeDessertActivity extends AppCompatActivity {

    SQLiteDatabase database;
    EditText edit_title;
    String title, id;
    byte [] img;
    ImageView imageView, edit_image, lang, delete;
    Toolbar toolbar;
    TextView empty_change_img;
    ImageButton delete_btn, change_dessert_img;
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

        empty_change_img = findViewById(R.id.empty_change_img);
        delete_btn = findViewById(R.id.words_fav);
        lang = findViewById(R.id.lang);
        edit_image = findViewById(R.id.dessert_image);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        edit_title = findViewById(R.id.set_dessert_name);
        save_btn = findViewById(R.id.save_dessert);
        delete = findViewById(R.id.delete);
        change_dessert_img = findViewById(R.id.change_dessert_img);

        get_and_set_intent_data();

        delete.setVisibility(View.GONE);
        lang.setVisibility(View.INVISIBLE);
        toolbar.setSubtitle(getString(R.string.change) + " " + title);

        imageView.setOnClickListener(v -> {
           finish();
        });
        change_dessert_img.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        delete_btn.setOnClickListener(v -> {
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
            try {
                contentValues.put("image", ImageViewToByte(edit_image));

            }catch (Exception e){
                edit_image.setImageResource(R.drawable.baseline_add_a_photo_24);
                contentValues.put("image", String.valueOf(edit_image));
            }

            long result = database.update("list", contentValues,"id="+id ,null);
            if (result == -1){
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

            }
        });
    }
    void get_and_set_intent_data(){

        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")  && getIntent().hasExtra("image")){

            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            img = getIntent().getByteArrayExtra("image");


            edit_title.setText(title);
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            if (bitmap == null){
                edit_image.setVisibility(View.INVISIBLE);
                empty_change_img.setVisibility(View.VISIBLE);
            }else {
                empty_change_img.setVisibility(View.INVISIBLE);
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