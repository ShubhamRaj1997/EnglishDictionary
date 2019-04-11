package com.example.shubhampc.englishdictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DB_PATH =null;
    private static String DB_NAME = "eng_dictionary.db";
    private SQLiteDatabase myDatabase;
    private final Context myContext;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null,1);
        this.myContext=context;
        this.DB_PATH="/data/data/" + context.getPackageName()  + "/" + "databases/";
        Log.e("path 1",DB_PATH);


    }


    public void createDatabase() throws IOException{
        boolean dbExist = checkDatabase();
        if(!dbExist){
            this.getReadableDatabase();
            try {
                copyDatabase();
            }
            catch (IOException e){
                throw new Error("Error Copying database");
            }
        }
    }




    public boolean checkDatabase(){
        /*
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH+DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);

        }
        catch (SQLException e){

        }

        if(checkDB != null){
            checkDB.close();
        }
        return checkDB!=null ? true:false;
        */

        File databasePath = myContext.getDatabasePath(DB_NAME);
        if(!databasePath.exists()){
            Log.d("DATT","doesnt exist");
        }
        else{
            Log.d("DATT","do exist");
        }
        //Log.d("DATT",databasePath.getName());
        return databasePath.exists();



    }

    private void copyDatabase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;

        while ((length=myInput.read(buffer)) > 0){
            myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myInput.close();
        myInput.close();

        Log.i("copyDatabase","Copied!");

    }

    public void openDatabase() throws SQLiteException{
        String myPath = DB_PATH + DB_NAME;
        myDatabase  = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {
        if(myDatabase!=null){
            myDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            this.getReadableDatabase();
            myContext.deleteDatabase(DB_NAME);
            copyDatabase();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public Cursor getMeaning(String text){

        Cursor c= null;
        try{
            c = myDatabase.rawQuery("SELECT en_definition,example,synonyms,antonyms FROM words WHERE en_word == UPPER('"+text+"')",null);

        }
        catch (SQLiteException e){
            Log.d("DEKH",e.toString());
            return  c;
        }
        return  c;
    }

    public Cursor getSuggestion(String text){
        Cursor c = null;
        try{
            c = myDatabase.rawQuery("SELECT _id,en_word FROM words WHERE en_word LIKE '"+text+"%' LIMIT 40",null);

        }

        catch (SQLiteException e){
            return c;
        }

        return  c;
    }

    public void insertHistory(String text){
        myDatabase.execSQL("INSERT INTO history(word) VALUES (UPPER('"+text+"'))");
    }






}
