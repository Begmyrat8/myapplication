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
import com.example.myapplication.Models.IngredientsModel;
import com.example.myapplication.Models.Model;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    private final SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    @SuppressLint("StaticFieldLeak")
    private static DatabaseAccess instances;
    Cursor c = null;
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
        String sQuery = "select sum(gram_price) from ingredients where dessert_id ='" + dessertId + "';";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0) );
        }else {
            sAmount = "0";
        }


        return sAmount;
    }
    public String getPortionWeight(String dessertId, int portions){
        String sAmount;
        String sQuery = "select sum(value) from ingredients where dessert_id ='" + dessertId + "';";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0) / portions);
        }else {
            sAmount = "0";
        }

        return sAmount;
    }
    public String getSumGram(String dessertId){
        String sAmount;
        String sQuery = "select sum(value) from ingredients where dessert_id ='" + dessertId + "';";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0) /1000);
        }else {
            sAmount = "0";
        }

        return sAmount;
    }
    public String getSumDessertId(String dessertId){
        String sAmount;
        String sQuery = "select count(dessert_id) from list where dessert_id ='" + dessertId + "';";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getInt(0));
        }else {
            sAmount = "0";
        }


        return sAmount;
    }

    public String getSumPrice2(String listId){
        String sAmount;
        String sQuery = "select sum(gram_price) from ingredients where list_id ='" + listId + "';";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0));
        }else {
            sAmount = "0";
        }


        return sAmount;
    }
    public String getSumGramPies(String listId){
        String sAmount;
        String sQuery = "select sum(value) from ingredients where list_id ='" + listId + "';";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0));
        }else {
            sAmount = "0";
        }

        return sAmount;
    }

    public String getSumPrice3(String listId){
        String sAmount;
        String sQuery = "select sum(price) from ingredients where list_id ='" + listId + "';";
        c = database.rawQuery(sQuery,null);
        if (c.moveToFirst()){
            sAmount = String.valueOf(c.getDouble(0));
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
            int desserts = c.getInt(9);
            double coefficient= c.getDouble(10);
            double new_dessert_height = c.getDouble(11);
            double dessert_width = c.getDouble(12);
            double dessert_height = c.getDouble(13);
            String shape_name = c.getString(14);
            String new_shape_name = c.getString(15);


            stringArrayList.add(new DessertModel(id, title, sum, weight, image, dessert_size, portion, portion_size, portion_price, desserts, coefficient, new_dessert_height, dessert_width, dessert_height, shape_name, new_shape_name));
        }
        return stringArrayList;
    }
    public List<DessertModel> getBookmarkList() {
        c = database.rawQuery("select * from bookmark", null);
        List<DessertModel> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String title = c.getString(1);
            byte[] image = c.getBlob(2);
            double weight = c.getDouble(8);
            double sum = c.getDouble(7);
            double dessert_size = c.getDouble(3);
            double portion = c.getDouble(5);
            double portion_size = c.getDouble(4);
            double portion_price = c.getDouble(6);
            int desserts = c.getInt(9);
            double coefficient = c.getDouble(10);
            double new_dessert_height = c.getDouble(11);
            double dessert_width = c.getDouble(12);
            double dessert_height = c.getDouble(13);
            String shape_name = c.getString(14);
            String new_shape_name = c.getString(15);

            stringArrayList.add(new DessertModel(id, title, sum, weight, image, dessert_size, portion, portion_size, portion_price, desserts, coefficient, new_dessert_height, dessert_width,dessert_height, shape_name, new_shape_name));
        }
        return stringArrayList;
    }
    public List<Model> getDessertData(String dessertId) {
        c = database.rawQuery("select * from list where dessert_id ='" + dessertId + "';" , null);
        List<Model> stringArrayList = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            int dessert_id = c.getInt(1);
            String title = c.getString(2);
            double price = c.getDouble(3);
            String units = c.getString(4);
            double gram_price = c.getDouble(5);
            byte[] image = c.getBlob(6);
            int value = c.getInt(7);



            stringArrayList.add(new Model(id, title, price, units, gram_price, image, value, dessert_id));
        }
        return stringArrayList;
    }

//    public List<IngredientsModel> getIngredientData(String list_id) {
//        c = database.rawQuery("select * from ingredients where list_id ='" + list_id + "';" , null);
//        List<IngredientsModel> stringArrayList = new ArrayList<>();
//        while (c.moveToNext()) {
//            int id = c.getInt(0);
//            String title = c.getString(2);
//            double price = c.getDouble(3);
//            double value = c.getDouble(5);
//            double gram_price = c.getDouble(6);
//            int kg = c.getInt(7);
//            int dessert_id = c.getInt(8);
//
//
//            stringArrayList.add(new IngredientsModel(id, title, price, value, gram_price, kg, dessert_id));
//        }
//        return stringArrayList;
//    }
    @SuppressLint("Range")
    public List<IngredientsModel> getIngredientsByListId(int listId) {
        List<IngredientsModel> ingredientsList = new ArrayList<>();
        String query = "SELECT id, title, price, value, gram_price, kg, dessert_id, unit FROM ingredients WHERE list_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(listId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                 String title = cursor.getString(cursor.getColumnIndex("title"));
                 double price = cursor.getDouble(cursor.getColumnIndex("price"));
                 double value = cursor.getInt(cursor.getColumnIndex("value"));
                double gram_price = cursor.getDouble(cursor.getColumnIndex("gram_price"));
                double kg = cursor.getInt(cursor.getColumnIndex("kg"));
                int dessert_id = cursor.getInt(cursor.getColumnIndex("dessert_id"));
                String unit = cursor.getString(cursor.getColumnIndex("unit"));
                IngredientsModel ingredient = new IngredientsModel(id, title, price, value, gram_price, kg, dessert_id, unit);
                ingredientsList.add(ingredient);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredientsList;
    }

}

