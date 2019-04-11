package com.example.shubhampc.englishdictionary;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.example.shubhampc.englishdictionary.fragments.FragmentAntonyms;
import com.example.shubhampc.englishdictionary.fragments.FragmentDefination;
import com.example.shubhampc.englishdictionary.fragments.FragmentExample;
import com.example.shubhampc.englishdictionary.fragments.FragmentSynonyms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WordMeaningActivity extends AppCompatActivity {

    private ViewPager viewPager;
    String enWord;
    DatabaseHelper myDbHelper;
    Cursor c =null;
    public String enDefination,example,synonyms,antonyms;

    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);


        // received values
        Bundle bundle = getIntent().getExtras();
        enWord = bundle.getString("en_word");

        myDbHelper = new DatabaseHelper(this);

        try{
            myDbHelper.openDatabase();

        }
        catch (SQLiteException e){
            Log.d("DRR",e.toString());
            throw e;
        }

        c= myDbHelper.getMeaning(enWord);
        if(c==null)
        {
            Log.d("nll","c is null");
        }
        if(c!=null)
        if(c.moveToFirst()){
            enDefination = c.getString(c.getColumnIndex("en_definition"));
            example = c.getString(c.getColumnIndex("example"));
            synonyms = c.getString(c.getColumnIndex("synonyms"));
            antonyms = c.getString(c.getColumnIndex("antonyms"));
        }

        myDbHelper.insertHistory(enWord);

        ImageButton btnSpeak = findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts = new TextToSpeech(WordMeaningActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            int result = tts.setLanguage(Locale.getDefault());

                            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                Log.e("error","This language is not supported");
                            }
                            else{
                                tts.speak(enWord,TextToSpeech.QUEUE_FLUSH,null);
                            }



                        }
                        else{
                            Log.e("error","Intialisation failed");
                        }
                    }
                });
            }
        });



        android.support.v7.widget.Toolbar toolbar  = findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(enWord);
        if(toolbar!=null)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);



        viewPager = findViewById(R.id.viewPager);
        if(viewPager!=null){
            setupViewPager(viewPager);
        }

        // Tablayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.home){          // Press back button
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }



    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int pos){
            return mFragmentTitleList.get(pos);
        }
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentDefination(),"Defination");
        adapter.addFrag(new FragmentSynonyms(),"Synonyms");
        adapter.addFrag(new FragmentAntonyms(),"Antonyms");
        adapter.addFrag(new FragmentExample(),"Example");
        viewPager.setAdapter(adapter);
    }


}
