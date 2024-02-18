package com.example.myapplication.Datebase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    private final SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instances;
    Cursor c = null;
    String COL_TITLE = "title";
    String COL_LANG = "lang";


    public DatabaseAccess(Context context) {
        this.openHelper = new com.example.myapplication.Datebase.DatabaseOpenHelper(context);
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
            c = database.rawQuery("select value from words where title = '" + title + "';", null);
            if (c==null) return null;
            while (c.moveToNext()) {
                word = c.getString(0);

            }
        }catch (Exception e){

        }
        return word;
    }

    public void insertWordIntoTable(String word, String lang, String table){

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_TITLE, word);
            contentValues.put(COL_LANG, lang);
            database.insert(table, null, contentValues);
        }catch (Exception e){

        }
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

    public List<CategoryModel> getAllList() {
        c = database.rawQuery("select * from list", null);
        List<CategoryModel> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            int kg = c.getInt(2);
            int price = c.getInt(3);
            int gram = c.getInt(4);

            stringArrayList.add(new CategoryModel(id, title, kg, price,gram));
        }
        return stringArrayList;
    }
}

