package com.raina.NewsApp;

import android.content.Context;
import android.os.Handler;
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
import android.support.design.internal.NavigationMenuView;
import android.graphics.Typeface;
import android.view.SubMenu;
import java.io.*;
import java.util.*;

public class MainActivity extends AppCompatActivity implements RefreshListView.OnRefreshListener{

    public static News currentNews;
    public static NewsSystem newsSystem;
    public static boolean picMode = true;
    public static boolean nightMode;
    private int news_type = -1;
    private ArrayList<News> newsList;
    private NewsAdapter adapter;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    //private ListView newsListView;
    private RefreshListView newsListView;
    private SearchView searchView;
    private ArrayList<News> prevNewsList;
    public static Context context;
    private static String THEME_KEY = "theme_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        translucentStatusBar();
        setContentView(R.layout.activity_main);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        nightMode = ConfigUtil.getBoolean(THEME_KEY, false);

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
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/Bodoni 72.ttc");
        ((TextView) findViewById(R.id.toolbar_title)).setTypeface(tf1);
    }

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ((TextView) findViewById(R.id.toolbar_title)).setText("Nouvelle");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/Bodoni 72.ttc");
        ((TextView) findViewById(R.id.toolbar_title)).setTypeface(tf1);

    }

    private void initNewsList(){
        Toast.makeText(MainActivity.this, "news list", Toast.LENGTH_SHORT).show();
        //newsListView = (ListView) findViewById(R.id.listview_news);
        newsListView = (RefreshListView) findViewById(R.id.listview_news);
        newsListView.setOnRefreshListener(this);
        //ImageView headerImage = (ImageView) LayoutInflater.from(this).inflate(R.layout.list_view_header_layout, newsListView, false);
        //newsListView.addHeaderView(headerImage);

        initNews();
        prevNewsList = (ArrayList<News>) newsList.clone();
        //adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, newsList);
        adapter = new NewsAdapter(this, newsList);
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
                intent.putExtra("keywords", ((News)parent.getItemAtPosition(pos)).getKeyWords());
                currentNews = (News)parent.getItemAtPosition(pos);
                MainActivity.this.startActivity(intent);
            }
        });

        /*
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/Avenir Next Condensed.ttc");
        try {
            ((TextView) findViewById(R.id.news_title)).setTypeface(tf1);
        }catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        */
    }

    /* show latest news */
    private void updateNewsList() {
        //adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, newsList);
        newsList = new ArrayList<>();
        try {
            if (newsSystem.getLatestNewsList().size() == 0)
                newsSystem.getLatestNews();
            newsList = newsSystem.getLatestNewsList();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        prevNewsList = (ArrayList<News>) newsList.clone();
        adapter = new NewsAdapter(MainActivity.this, newsList);
        newsListView.setAdapter(adapter);
    }

    /* show news in a category */
    private void updateNewsList(int type) {
        type--;
        newsList = new ArrayList<>();
        try {
            if (newsSystem.getCategoryNewsList(type).size() == 0)
                newsSystem.getCategoryNews(type);
            newsList = newsSystem.getCategoryNewsList(type);
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        prevNewsList = (ArrayList<News>) newsList.clone();
        //adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, Arrays.asList(categoryNewsList));
        adapter = new NewsAdapter(MainActivity.this, newsList);
        newsListView.setAdapter(adapter);
    }


    int tmp_news_type = 1;
    /* show search results */
    private void updateNewsList(String query) {
        if (news_type != 13) {
            tmp_news_type = news_type;
            news_type = 13;
        }
        newsSystem.searchInit(query);
        newsList = new ArrayList<>();
        try {
            if (newsSystem.getSearchNewsList().size() == 0)
                newsSystem.searchNews();
            newsList = newsSystem.getSearchNewsList();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        //prevNewsList = (ArrayList<News>) newsList.clone();
        //adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, Arrays.asList(searchNewsList));
        adapter = new NewsAdapter(MainActivity.this, newsList);
        newsListView.setAdapter(adapter);
    }

    /* show favorite news */
    private void updateFavouriteNewsList() {
        ArrayList<News> favouriteNewsList = new ArrayList<>();
        try{
            favouriteNewsList = newsSystem.getMarkNewsList();
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        newsList = favouriteNewsList;
        prevNewsList = (ArrayList<News>) favouriteNewsList.clone();
        //adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, Arrays.asList(favouriteNewsList));
        adapter = new NewsAdapter(MainActivity.this, newsList);
        newsListView.setAdapter(adapter);
    }

    private void restoreNewsList() {
        if (news_type == 13) news_type = tmp_news_type;
        newsList = new ArrayList<>();
        for (int i = 0; i < prevNewsList.size(); i++)
            newsList.add(prevNewsList.get(i));
        //prevNewsList = (ArrayList<News>) newsList.clone();
        //adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, Arrays.asList(categoryNewsList));
        adapter = new NewsAdapter(MainActivity.this, newsList);
        newsListView.setAdapter(adapter);
        //adapter = new NewsAdapter(MainActivity.this, R.layout.news_item_layout, Arrays.asList(prevNewsList));
        //adapter = new NewsAdapter(MainActivity.this, prevNewsList);
        //newsListView.setAdapter(adapter);
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
        //searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
                if(newText.equals(""))
                    restoreNewsList();
                return false;
            }
        });

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

        if (navigationView != null){
            NavigationMenuView navigationMenuView =  (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null){
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }

        Menu navMenu = navigationView.getMenu();
        SubMenu menu = navMenu.findItem(R.id.nav_category).getSubMenu();
        menu.clear();
        String[] categories = getResources().getStringArray(R.array.categories);
        for(int i = 0; i< 12; i++){
            if(Category.getCategory().get(i)){
                menu.add(Menu.NONE,Menu.FIRST+i,i,categories[i]);
            }
        }
        /*
        if(nightMode) {
            navigationView.getMenu().getItem(R.id.nav_color_mode).setIcon(R.drawable.icon_day);
            navigationView.getMenu().getItem(R.id.nav_color_mode).setTitle("Day");
        } else {
            navigationView.getMenu().getItem(R.id.nav_color_mode).setIcon(R.drawable.icon_night);
            navigationView.getMenu().getItem(R.id.nav_color_mode).setTitle("Night");
        }
        */
        //menu.add(Menu.NONE, Menu.FIRST + 12, 12, "Edit");
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_favourite:
                        updateFavouriteNewsList();
                        news_type = -2;
                        break;
                    case R.id.nav_home:
                        updateNewsList();
                        news_type = -1;
                        break;
                    case R.id.nav_edit:
                        Intent intent = new Intent(MainActivity.this, EditCategoryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_color_mode:
                        if(!nightMode) {
                            AppContext.me().setTheme(MainActivity.this, true);
                            Toast.makeText(MainActivity.this, "Day", Toast.LENGTH_SHORT).show();
                            nightMode = true;
                            item.setIcon(R.drawable.icon_day);
                            item.setTitle("Day");
                        } else {
                            AppContext.me().setTheme(MainActivity.this, false);
                            Toast.makeText(MainActivity.this, "Night", Toast.LENGTH_SHORT).show();
                            nightMode = false;
                            item.setIcon(R.drawable.icon_night);
                            item.setTitle("Night");
                        }
                        break;
                    case R.id.nav_text_mode:
                        if(picMode) {
                            picMode = false;
                            item.setIcon(R.drawable.icon_text);
                            item.setTitle("Text");
                        } else {
                            picMode = true;
                            item.setIcon(R.drawable.icon_picture);
                            item.setTitle("Picture");
                        }
                        break;
                    default:
                        news_type = item.getItemId()-1;
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
//

    /**
     * 下拉刷新的接口方法
     */
    @Override
    public void onDownPullRefresh() {
        Toast.makeText(this,"下拉",Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                newsListView.onRefreshComplete();
            }
        }, 1000);

//        //因为本例中没有从网络获取数据，因此这里使用Handler演示4秒延迟来从服务器获取数据的延迟现象，以便于大家
//        // 能够看到listView正在刷新的状态。大家在现实使用时只需要使用run（）{}方法中的代码就行了。
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //获取最新的list数据
//                getLoadMoreData();
//                //通知界面显示，
//                adapter.notifyDataSetChanged();
//                // 通知listview刷新数据完毕
//                newsListView.onRefreshComplete();
//            }
//        }, 400);
    }

    /**
     * 上拉加载更多的接口方法
     */
    @Override
    public void onLoadingMore() {
        Toast.makeText(this,"上拉",Toast.LENGTH_SHORT).show();
        if (news_type == -2) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //showListView(newsList);
                    //通知listview加载完毕
                    newsListView.loadMoreComplete();
                }
            }, 0);
        } else {
            //因为本例中没有从网络获取数据，因此这里使用Handler演示4秒延迟来从服务器获取数据的延迟现象，以便于大家
            // 能够看到listView正在刷新的状态。大家在现实使用时只需要使用run（）{}方法中的代码就行了。
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //获取更多的数据
                    getLoadMoreData();
                    //更新listview显示
                    showListView(newsList);
                    //通知listview加载完毕
                    newsListView.loadMoreComplete();
                }
            }, 1000);
        }
    }

    private void getLoadMoreData() {
        //这里只是模拟3个列表项数据，在现实开发中，listview中的数据都是从服务器获取的。
        ArrayList<News> news = new ArrayList<>();
        try {
            switch (news_type) {
                case -1:
                    newsSystem.getLatestNews();
                    news = newsSystem.getLatestNewsList();
                    break;
                case 13:
                    newsSystem.searchNews();
                    news = newsSystem.getSearchNewsList();
                    break;
                default:
                    newsSystem.getCategoryNews(news_type);
                    news = newsSystem.getCategoryNewsList(news_type);
                    break;
            }
        } catch (Exception e) {}
        int size = newsList.size();

        for(int i = size; i < news.size(); i++){
            newsList.add(news.get(i));
        }
    }

    private void showListView(List<News> listViewItems) {
        if (adapter == null) {
            //为listview配置adapter
            adapter = new NewsAdapter(this, newsList);
            newsListView.setAdapter(adapter);
        } else {
            //当有数据变化时，自动刷新界面
            adapter.onDateChange(newsList);
        }
    }
}