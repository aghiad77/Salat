package com.example.salat.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SALAT_DB";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE user\n" +
                "(\n" +
                "userId TEXT Not NULL,\n" +
                "userName TEXT Not NULL,\n" +
                "password TEXT NOT NULL,\n" +
                "email TEXT NOT NULL,\n" +
                "date TEXT NOT NULL,\n" +
                "status TEXT NOT NULL\n" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE pray\n" +
                "(\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "pray TEXT NOT NULL,\n" +
                "date TEXT DEFAULT CURRENT_TIMESTAMP\n" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE location\n" +
                "(\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "lat DOUBLE NOT NULL,\n" +
                "long DOUBLE NOT NULL\n" +
                ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try
        {
            // Drop older table if existed
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS pray");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS location");

            // Create tables again
            onCreate(sqLiteDatabase);

        } catch (Exception ex) {

        }
    }

    public void saveLogin(String id,String name, String password, String email,String date,String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor cursor = db.rawQuery("SELECT userName FROM user WHERE userName=?", new String[]{name});
            ContentValues values = new ContentValues();
            if (cursor == null || cursor.getCount() == 0 )
            {
                values.put("userId", id);
                values.put("userName", name);
                values.put("password", password);
                values.put("email", email);
                values.put("date", date);
                values.put("status", status);
                db.insert("user", null, values);
            }
        } catch (Exception ex) {

        }
        db.close();
    }
    public boolean getIsUserLogged() {
        String result = getSetting();

        if (result == "" || result == null || !result.equals("logged"))
            return false;
        else
            return true;
    }
    public String getSetting() {

        SQLiteDatabase db = this.getWritableDatabase();
        String result="";
        try
        {
            Cursor cursor = db.rawQuery("SELECT status FROM user",null);

            if (cursor == null || cursor.getCount() == 0 || !cursor.moveToFirst())
                return "";
            else
            {
                result = cursor.getString(0);
            }
        } catch (Exception ex) {

        }

        db.close();
        return result;
    }
    public String getPassword(){
        SQLiteDatabase db = this.getWritableDatabase();
        String result="";
        try
        {
            Cursor cursor = db.rawQuery("SELECT password FROM user",null);

            if (cursor == null || cursor.getCount() == 0 || !cursor.moveToFirst())
                return "";
            else
            {
                result = cursor.getString(0);
            }
        } catch (Exception ex) {

        }
        db.close();
        return result;
    }
    public User getUser(){

        SQLiteDatabase db = this.getWritableDatabase();
        User user = new User();
        try
        {

            Cursor cursor = db.rawQuery("SELECT * FROM user", null);


            if (cursor == null || cursor.getCount() == 0 || !cursor.moveToFirst())
                return null;
            else
            {
                user = new User(cursor.getString(0),cursor.getString(1),cursor.getString(3),cursor.getString(4));
            }
        } catch (Exception ex) {

        }
        db.close();
        return user;
    }


    public void addPray(String pray,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put("pray", pray);
            values.put("date", date);
            db.insert("pray", null, values);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        db.close();
    }
    public List<Pray> getPray(String date){

        SQLiteDatabase db = this.getWritableDatabase();
        String condition = "'%" + date + "%'";
        List<Pray> prayList=new ArrayList<>();
        try
        {

            Cursor cursor = db.rawQuery("SELECT * FROM pray WHERE date LIKE" + condition, null);


            if (cursor == null || cursor.getCount() == 0 || !cursor.moveToFirst())
                return prayList;
            else if (cursor != null && cursor.moveToFirst())
            {
                do
                {
                    Pray noti=new Pray(cursor.getInt(0) , cursor.getString(1),cursor.getString(2));
                    prayList.add(noti);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return prayList;
        }
        db.close();
        return prayList;
    }
    public List<Pray> getPrays(){

        SQLiteDatabase db = this.getWritableDatabase();

        List<Pray> prayList=new ArrayList<>();
        try
        {

            Cursor cursor = db.rawQuery("SELECT * FROM pray", null);


            if (cursor == null || cursor.getCount() == 0 || !cursor.moveToFirst())
                return prayList;
            else if (cursor != null && cursor.moveToFirst())
            {
                do
                {
                    Pray noti=new Pray(cursor.getInt(0) , cursor.getString(1),cursor.getString(2));
                    prayList.add(noti);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return prayList;
        }
        db.close();
        return prayList;
    }
    public int deletePray(int Id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete("pray", "id=" + Integer.toString(Id), null);
        db.close();

        return result;
    }

    public void saveLocation(double lat,double longt) {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues values = new ContentValues();
            values.put("lat", lat);
            values.put("long", longt);
            db.insert("location", null, values);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        db.close();
    }
    public lastLocation getLocation(){

        SQLiteDatabase db = this.getWritableDatabase();
         lastLocation location = new lastLocation();
        try
        {

            Cursor cursor = db.rawQuery("SELECT * FROM location", null);


            if (cursor == null || cursor.getCount() == 0 || !cursor.moveToFirst())
                return null;
            else
            {
                location = new lastLocation(cursor.getInt(0),cursor.getDouble(1),cursor.getDouble(2));
            }
        } catch (Exception ex) {

        }
        db.close();
        return location;
    }
    public int deleteLocation(int Id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete("location", "id=" + Integer.toString(Id), null);
        db.close();

        return result;
    }
}
