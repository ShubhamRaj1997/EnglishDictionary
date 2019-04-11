package com.example.shubhampc.englishdictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    android.support.v7.widget.SearchView searchView;
    static DatabaseHelper myDbHelper;
    static boolean databaseOpened = false;
    android.support.v4.widget.SimpleCursorAdapter suggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.search_view);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 searchView.setIconified(false);

            }
        });
        myDbHelper = new DatabaseHelper(this);
        if(myDbHelper.checkDatabase()){
            openDatabse();
        }
        else{
            LoadDatabaseAsync task = new LoadDatabaseAsync(this);
            task.execute();
        }

        // setup simplecursoradapter

        final String[] from = new String [] {"en_word"};
        final int [] to = new int [] {R.id.suggestion_text};

        suggestionAdapter = new android.support.v4.widget.SimpleCursorAdapter(this,R.layout.suggestion_row,null,from,to,0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.changeCursor(cursor);
            }
        };
        searchView.setSuggestionsAdapter(suggestionAdapter);

        searchView.setOnSuggestionListener(new android.support.v7.widget.SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {






                return true;
            }

            @Override
            public boolean onSuggestionClick(int i) {

                // Add clicked text to search box
                android.support.v4.widget.CursorAdapter ca = searchView.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(i);
                String clicked_word = cursor.getString(cursor.getColumnIndex("en_word"));
                searchView.setQuery(clicked_word,false);
                searchView.clearFocus();
                searchView.setFocusable(false);

                Intent intent= new Intent(MainActivity.this,WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word",clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);


                return true;
            }
        });


        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String text = searchView.getQuery().toString();
                Cursor c = myDbHelper.getMeaning(text);

                if(c == null){
                    searchView.setQuery("",false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
                    builder.setTitle("word not found");
                    builder.setMessage("Please search again!");

                    String positiveText= getString(android.R.string.ok);

                    builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Positive button clicked
                        }
                    });

                    String negativeText= getString(android.R.string.cancel);

                    builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Negative button clicked
                            searchView.clearFocus();
                        }
                    });

                    AlertDialog dialog = builder.create();

                    dialog.show();

                }
                else{
                    // have word in database

                    searchView.clearFocus();
                    searchView.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this,WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word",text);
                    intent.putExtras(bundle);
                    startActivity(intent);


                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {


                searchView.setIconified(false);  // give suggestion list margins
                Cursor cursorSuggestion = myDbHelper.getSuggestion(s);
                suggestionAdapter.changeCursor(cursorSuggestion);



                return false;
            }
        });



    }

    protected static void openDatabse(){
        try{
            myDbHelper.openDatabase();
            databaseOpened=true;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu_main; this adds items to action bar if present
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle actionbar items click here
        int id = item.getItemId();

        if(id == R.id.action_settings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_exit){
            System.exit(0);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
