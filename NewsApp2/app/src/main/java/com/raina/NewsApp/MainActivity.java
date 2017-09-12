package com.raina.NewsApp;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.Menu;
import android.content.Intent;
import android.os.StrictMode;
import android.widget.Button;
import android.support.v4.view.MenuItemCompat;
import java.lang.reflect.Method;
import android.util.Log;
import android.graphics.Typeface;
import android.view.SubMenu;
import java.io.*;

public class MainActivity extends AppCompatActivity {

    public static News currentNews;
    public static NewsSystem newsSystem;

    private News[] newsList;
    private NewsAdapter adapter;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ListView newsListView;
    private SearchView searchView;
    private News[] prevNewsList;
    boolean isNight;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        translucentStatusBar();
        setContentView(R.layout.activity_main);
        isNight = false;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        initDrawer();
        initNewsList();
        initMark();



    }

    @Override
    protected void onStart() {
        super.onStart();
        initNavigationView();
    }

    private void initDrawer() {
        initToolbar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        initNavigationView();
    }

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void initNewsList(){
        Toast.makeText(MainActivity.this, "news list", Toast.LENGTH_SHORT).show();
        newsListView = (ListView) findViewById(R.id.listview_news);
        //ImageView headerImage = (ImageView) LayoutInflater.from(this).inflate(R.layout.list_view_header_layout, newsListView, false);
        //newsListView.addHeaderView(headerImage);

        initNews();
        prevNewsList = (News[]) newsList.clone();
        adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, newsList);
        newsListView.setAdapter(adapter);
        Toast.makeText(MainActivity.this, "~", Toast.LENGTH_SHORT).show();

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                String title = ((News)parent.getItemAtPosition(pos)).getNewsTitle();
                String body;
                try{
                    body = ((News)parent.getItemAtPosition(pos)).getWholeNews();
                }catch(Exception e) {
                    body = "connection failed";
                }
                intent.putExtra("Title", title);
                intent.putExtra("Body", body);
                currentNews = (News)parent.getItemAtPosition(pos);
                MainActivity.this.startActivity(intent);
            }
        });

        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/PingFangLight.ttf");
        try {
            ((TextView) findViewById(R.id.news_title)).setTypeface(tf1);
        }catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    /* show latest news */
    private void updateNewsList() {
        adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, newsList);
        newsListView.setAdapter(adapter);
    }

    /* show news in a category */
    private void updateNewsList(int type) {
        News[] categoryNewsList = new News[0];
        try {
            newsSystem.getCategoryNews(type);
            categoryNewsList = newsSystem.getCategoryNewsList(type);
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        prevNewsList = (News[]) categoryNewsList.clone();
        adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, categoryNewsList);
        newsListView.setAdapter(adapter);
    }

    /* show search results */
    private void updateNewsList(String query) {
        News[] searchNewsList = new News[0];
        newsSystem.searchInit(query);
        try{
            newsSystem.searchNews();
            searchNewsList = newsSystem.getSearchNewsList();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, searchNewsList);
        newsListView.setAdapter(adapter);
    }

    /* show favorite news */
    private void updateFavouriteNewsList() {
        News[] favouriteNewsList = new News[0];
        try{
            favouriteNewsList = newsSystem.getMarkNewsList();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        prevNewsList = (News[]) favouriteNewsList.clone();
        adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, favouriteNewsList);
        newsListView.setAdapter(adapter);
    }

    private void restoreNewsList() {
        adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, prevNewsList);
        newsListView.setAdapter(adapter);
    }

    private void translucentStatusBar() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);
        getMenuInflater().inflate(R.menu.search_view_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_view);
        //searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView = new SearchView(this);
        //MenuItem item = menu.getItem(0);
        //searchView = new SearchView(this);
        //设置展开后图标的样式,false时ICON在搜索框外,true为在搜索框内，无法修改
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");
        searchView.setSubmitButtonEnabled(true);//设置最右侧的提交按钮
        searchItem.setActionView(searchView);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(searchView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                    updateNewsList(query);
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                    searchView.clearFocus();
                }
                return true;
            }



            @Override
            public boolean onQueryTextChange(String newText) {
                //updateNewsList(newText);
                return false;
            }
        });

        /*
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                System.out.print("close");
                restoreNewsList();
                // This is where you can be notified when the `SearchView` is closed
                // and change your views you see fit.
                return true;
            }
        });*/

        /*
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //DO SOMETHING WHEN THE SEARCHVIEW IS CLOSING
                return true;
            }
        });*/

        return true;
    }

    private void initNews() {
        try {
            newsSystem = new NewsSystem();
            newsSystem.getLatestNews();
            newsList = newsSystem.getLatestNewsList();
            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();

        }catch (Exception e) {
            newsList = new News[0];
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    void initMark() {
        String path = this.getFilesDir().getPath()+"//";
        File file = null;

        file = new File(path+"markList.txt");
        if(!file.exists()){
            try {
                FileOutputStream fw = openFileOutput("markList.txt", Context.MODE_PRIVATE);
                String s = "";
                fw.write(s.getBytes());
                fw.close();
            } catch (Exception e) {}
        }

        file = new File(path+"savedList.txt");
        if(!file.exists()){
            try {
                FileOutputStream fw = openFileOutput("savedList.txt", Context.MODE_PRIVATE);
                String s = "";
                fw.write(s.getBytes());
                fw.close();
            } catch (Exception e) {}
        }

        file = new File(path+"contentList.txt");
        if(!file.exists()){
            try {
                FileOutputStream fw = openFileOutput("contentList.txt", Context.MODE_PRIVATE);
                String s = "{\"foo\":\"foo\"}";
                fw.write(s.getBytes());
                fw.close();
            } catch (Exception e) {}
        }
        try {
            newsSystem.updateMarkNewsList(getApplicationContext());
        } catch (Exception e) {}
    }
    private void initNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_drawer);
        Menu navMenu = navigationView.getMenu();
        SubMenu menu = navMenu.findItem(R.id.nav_category).getSubMenu();
        menu.clear();
        String[] categories = getResources().getStringArray(R.array.categories);
        for(int i = 0; i< 12; i++){
            if(Category.getCategory().get(i)){
                menu.add(Menu.NONE,Menu.FIRST+i,i,categories[i]);
            }
        }
        menu.add(Menu.NONE, Menu.FIRST + 12, 12, "Edit");
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Toast.makeText(MainActivity.this,"你点击我了",Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.nav_favourite:
                        updateFavouriteNewsList();
                        break;
                    case R.id.nav_all:
                        updateNewsList();
                        break;
                    case R.id.nav_day_mode:
                        AppContext.me().setTheme(MainActivity.this, false);
                        Toast.makeText(MainActivity.this, (isNight)? "Night" : "Day", Toast.LENGTH_SHORT).show();
                        isNight = !isNight;
                        break;
                    case R.id.nav_night_mode:
                        AppContext.me().setTheme(MainActivity.this, true);
                        isNight = !isNight;
                        break;
                    case Menu.FIRST + 12:
                        Intent intent = new Intent(MainActivity.this, EditCategoryActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        updateNewsList(item.getItemId());
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        AppContext.me().refreshResources(this);
    }
}