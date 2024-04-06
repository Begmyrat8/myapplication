package com.example.myapplication.Datebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.myapplication.Models.DessertModel;
import com.example.myapplication.Models.Model;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    private final SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instances;
    Cursor c = null;
    String COL_TITLE = "title";
    String COL_GRAM = "gram";
    String COL_KG = "kg";
    String COL_PRICE = "price";
    String COL_GRAM_PRICE = "gram_price";
    String TABLE_NAME = "list";
    String ID = "id";
    byte [] imageInBytes;
    Context context;

    public DatabaseAccess(Context context) {
        this.openHelper = new com.example.myapplication.Datebase.DatabaseOpenHelper(context);
        this.context = context;

    }

    public static DatabaseAccess getInstances(Context context) {
        if (instances == null) {
            instances = new DatabaseAccess(context);
        }
        return instances;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    private byte [] ImageViewToByte(ImageView set_image){
        Bitmap bitmap =((BitmapDrawable)set_image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
        byte [] bytes = stream.toByteArray();
        return bytes;
    }


    public String getWordByTitle(String title){
        String word = null;

        try {
            c = database.rawQuery("select title from words where title = '" + title + "';", null);
            if (c==null) return null;
            while (c.moveToNext()) {
                word = c.getString(0);

            }
        }catch (Exception e){

        }
        return word;
    }


    public void deleteWordFromTable(String word, String table){
        try {
            database.delete(table, "title" + "=\"" + word+"\"", null) ;
        }catch (Exception e){

        }
    }

    public void clearAllDataFromTable(String table){
        try {
            database.delete(table, null , null);
        }catch (Exception e){

        }
    }
    public String getSumPrice(String dessertId){
        String sAmount;
        String sQuery = "select sum(gram_price) from list where dessert_id ='" + dessertId + "';";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0));
        }else {
            sAmount = "0";
        }


        return sAmount;
    }
    public String getSumGram(String dessertId){
        String sAmount;
        String sQuery = "select sum(value) from list where dessert_id ='" + dessertId + "' and units != 'piece'";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0) /1000);
        }else {
            sAmount = "0";
        }

        return sAmount;
    }
    @SuppressLint("Range")
    public String getLastRowOfTable(String table){
        String selectQuery= "SELECT * FROM " + table +" ORDER BY id DESC LIMIT 1";

        String str = "";
        try {
            Cursor cursor = database.rawQuery(selectQuery, null);
            if(cursor.moveToFirst())
                str  =  cursor.getString( cursor.getColumnIndex("title") );
            cursor.close();
        }catch (Exception e){

        }
        return str;
    }

    public List<Model> getAllList() {
        c = database.rawQuery("select * from list", null);
        List<Model> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            double price = c.getDouble(2);
            String units = c.getString(3);
            double gram_price = c.getDouble(4);
            byte[] image = c.getBlob(5);
            int value = c.getInt(6);


            stringArrayList.add(new Model(id, title, price, units, gram_price, image, value));
        }
        return stringArrayList;
    }
    public List<DessertModel> getDessertList() {
        c = database.rawQuery("select * from dessert", null);
        List<DessertModel> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            byte[] image = c.getBlob(2);
            double weight = c.getDouble(3);
            double sum = c.getDouble(4);
            double dessert_size = c.getDouble(5);
            double portion = c.getDouble(6);
            double portion_size = c.getDouble(8);
            double portion_price = c.getDouble(7);

            stringArrayList.add(new DessertModel(id, title, sum, weight, image, dessert_size, portion, portion_size,portion_price));
        }
        return stringArrayList;
    }
    public List<Model> getDessertData(String dessertId) {
        c = database.rawQuery("select * from list where dessert_id ='" + dessertId + "';" , null);
        List<Model> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(2);
            double price = c.getDouble(3);
            String units = c.getString(4);
            double gram_price = c.getDouble(5);
            byte[] image = c.getBlob(6);
            int value = c.getInt(7);




            stringArrayList.add(new Model(id, title, price, units, gram_price, image, value));
        }
        return stringArrayList;
    }
}

