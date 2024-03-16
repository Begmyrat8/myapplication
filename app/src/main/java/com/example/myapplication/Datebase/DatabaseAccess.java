package com.example.myapplication.Datebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.Models.CategoryModel;
import com.example.myapplication.Models.Model;

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
    public String getSumPrice(){
        String sAmount;
        String sQuery = "select sum(gram_price) from list";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0) / 1000);
        }else {
            sAmount = "0";
        }


        return sAmount;
    }
    public String getSumGram(){
        String sAmount;
        String sQuery = "select sum(gram) from list";
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
            int kg = c.getInt(2);
            double price = c.getDouble(3);
            int gram = c.getInt(4);
            double gram_price = c.getDouble(5);
            byte[] image = c.getBlob(6);


            stringArrayList.add(new Model(id, title, kg, price, gram, gram_price, image));
        }
        return stringArrayList;
    }
    public List<CategoryModel> getCategoryList() {
        c = database.rawQuery("select * from category", null);
        List<CategoryModel> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            byte[] image = c.getBlob(2);
            double weight = c.getDouble(3);
            double sum = c.getDouble(4);


            stringArrayList.add(new CategoryModel(id, title, sum, weight, image));
        }
        return stringArrayList;
    }
    public List<Model> getCategoryData(String categoryName) {
        c = database.rawQuery("select * from list where category_name ='" + categoryName + "';" , null);
        List<Model> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            int kg = c.getInt(2);
            double price = c.getDouble(3);
            int gram = c.getInt(4);
            double gram_price = c.getDouble(5);
            byte[] image = c.getBlob(6);



            stringArrayList.add(new Model(id, title, kg, price, gram, gram_price, image));
        }
        return stringArrayList;
    }
}

