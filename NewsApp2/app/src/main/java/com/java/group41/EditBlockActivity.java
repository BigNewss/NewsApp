package com.java.group41;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileOutputStream;


public class EditBlockActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private BlockAdapter adapter;
    private ListView blockListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_block);
        initToolBar();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null)
            adapter.notifyDataSetChanged();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            FileOutputStream fw = openFileOutput("blockList.txt", Context.MODE_PRIVATE);
            String s = Block.saveBlock();
            fw.write(s.getBytes());
            fw.close();
        } catch (Exception e) {}
        switch (MainActivity.news_type) {
            case -1:
                //newsSystem.getLatestNews();
                MainActivity.newsList = MainActivity.newsSystem.getLatestNewsList();
                break;
            case 13:
                //newsSystem.searchNews();
                MainActivity.newsList = MainActivity.newsSystem.getSearchNewsList();
                break;
            default:
                //newsSystem.getCategoryNews(news_type);
                try {
                    MainActivity.newsList = MainActivity.newsSystem.getCategoryNewsList(MainActivity.news_type);
                } catch (Exception e) {}
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.block_view_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.block_view);
        searchView = new SearchView(this);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Add your Block");
        searchView.setSubmitButtonEnabled(true);//设置最右侧的提交按钮
        searchItem.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(searchView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                        addBlock(query);
                    }
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }
    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.block_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ((TextView) findViewById(R.id.toolbar_title)).setText("Block");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/Avenir Next.ttc");
        ((TextView) findViewById(R.id.toolbar_title)).setTypeface(tf1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (MainActivity.news_type) {
                    case -1:
                        //newsSystem.getLatestNews();
                        MainActivity.newsList = MainActivity.newsSystem.getLatestNewsList();
                        break;
                    case 13:
                        //newsSystem.searchNews();
                        MainActivity.newsList = MainActivity.newsSystem.getSearchNewsList();
                        break;
                    default:
                        //newsSystem.getCategoryNews(news_type);
                        try {
                            MainActivity.newsList = MainActivity.newsSystem.getCategoryNewsList(MainActivity.news_type);
                        } catch (Exception e) {}
                        break;
                }
                finish();
            }
        });
    }
    private void initView(){
        blockListView = (ListView) findViewById(R.id.block_list_view);
        adapter = new BlockAdapter(this, Block.blockList);
        blockListView.setAdapter(adapter);
        blockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                String s = (String)parent.getItemAtPosition(pos);
               removeBlock(s);
            }
        });
    }
    private void addBlock(String query){
        if(!Block.blockList.contains(query)) {
            Block.addBlock(query);
            adapter = new BlockAdapter(this, Block.blockList);
            blockListView.setAdapter(adapter);
        }
    }
    private void removeBlock(String query){
        if(Block.blockList.contains(query)) {
            Block.removeBlock(query);
            adapter = new BlockAdapter(this, Block.blockList);
            blockListView.setAdapter(adapter);
        }
    }
}
